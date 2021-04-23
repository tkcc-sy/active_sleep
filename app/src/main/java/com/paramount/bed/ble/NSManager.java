package com.paramount.bed.ble;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.common.util.ArrayUtils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.paramount.bed.ble.interfaces.NSAutomaticOperationDelegate;
import com.paramount.bed.ble.interfaces.NSBaseDelegate;
import com.paramount.bed.ble.interfaces.NSBedDelegate;
import com.paramount.bed.ble.interfaces.NSConnectionDelegate;
import com.paramount.bed.ble.interfaces.NSFWUpdateDelegate;
import com.paramount.bed.ble.interfaces.NSMattressDelegate;
import com.paramount.bed.ble.interfaces.NSOperationDelegate;
import com.paramount.bed.ble.interfaces.NSRealtimeDelegate;
import com.paramount.bed.ble.interfaces.NSScanDelegate;
import com.paramount.bed.ble.interfaces.NSSettingDelegate;
import com.paramount.bed.ble.pojo.NSBedPosition;
import com.paramount.bed.ble.pojo.NSBedSetting;
import com.paramount.bed.ble.pojo.NSBedSpec;
import com.paramount.bed.ble.pojo.NSMattressPosition;
import com.paramount.bed.ble.pojo.NSMattressStatus;
import com.paramount.bed.ble.pojo.NSRealtimeFeed;
import com.paramount.bed.ble.pojo.NSSpec;
import com.paramount.bed.ble.pojo.NSWifiSetting;
import com.paramount.bed.data.model.DeviceTemplateBedModel;
import com.paramount.bed.data.model.FirmwareFileModel;
import com.paramount.bed.data.model.NemuriConstantsModel;
import com.paramount.bed.data.model.PermissionRequestModel;
import com.paramount.bed.util.BluetoothUtil;
import com.paramount.bed.util.NSCommandReceiver;
import com.paramount.bed.util.PermissionUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import static com.paramount.bed.util.LogUtil.Logx;

//REMOTE CONTROL BLE CLASS
public class NSManager {
    //singleton vars
    public static NSManager instance;
    public Context contextInstance;
    public Context broadcastContext;
    //ble vars
    private int lastButtonCode;
    private BluetoothAdapter bleAdapter;
    private BluetoothLeScanner bleScanner;
    private BluetoothGatt bleGatt;
    private Handler bleScanStopTimer;
    private Handler operationTimeoutTimer;

    private boolean isBLECurrentlyScanning = false;
    private NSBaseDelegate delegate;
    private BluetoothGattCharacteristic targetCharacteristics;
    private int connectionStatus = 0;
    private ArrayList<Byte> responseDataBuffer;
    private ArrayList<byte[]> requestDataBuffer;
    private Queue<CommandData> commandQueue = new LinkedList<>();
    private Queue<NSOperation.BedOperationType> commandTypeQueue = new LinkedList<>();
    private boolean isWritingCommand = false;
    private boolean shouldSplitPacket = false;
    private int MAX_PACKET_SPLIT_SIZE = 19;
    private CommandData currentCommand;
    private Runnable stopScanRunnable = this::stopScan;

    private Runnable operationTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            Logger.e("NSManager operation timeout triggered, try to resend command with split method");
            if (delegate != null && delegate instanceof NSOperationDelegate) {
                ((NSOperationDelegate) delegate).onOperationTimeout();
            }
        }
    };

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (delegate instanceof NSScanDelegate) {
                ((NSScanDelegate) delegate).onScanResult(result);
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };
    private final BroadcastReceiver mBluetoothStateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
            final int previousState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, BluetoothAdapter.STATE_OFF);
            Logger.v("NSManager BLE State receiver changed from "+previousState + " to "+state);

            switch (state) {
                case BluetoothAdapter.STATE_TURNING_OFF:
                case BluetoothAdapter.STATE_OFF:
                    if(previousState == BluetoothAdapter.STATE_TURNING_OFF && state == BluetoothAdapter.STATE_OFF){
                        closeConnection();
                    }
                    break;
            }
        }
    };
    private BluetoothGattCallback bleGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int
                newState) {
            Logger.v("NSManager BLE Connection state changed from "+connectionStatus + " to "+newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                if (delegate instanceof NSConnectionDelegate) {
                    Logger.v("NSManager calling onDisconnect delegate");
                    commandQueue.clear();
                    commandTypeQueue.clear();
                    isWritingCommand = false;
                    if(connectionStatus == 0 && newState == 0){
                        ((NSConnectionDelegate) delegate).onConnectionStalled(status);
                    }
                    ((NSConnectionDelegate) delegate).onDisconnect();
                }
                bleGatt.close();
                bleGatt = null;
            }
            Logger.i("NSManager onConnectionStateChange from " + connectionStatus + " to " + newState);
            connectionStatus = newState;
            super.onConnectionStateChange(gatt, status, newState);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Logger.v("NSManager onDescriptorWrite write result " + status);

            if (delegate instanceof NSConnectionDelegate) {
                ((NSConnectionDelegate) delegate).onConnectionEstablished();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            BluetoothGattService service = gatt.getService(UUID.fromString(NSConstants.SERVICE_UUID));
            if (service != null) {
                targetCharacteristics =
                        service.getCharacteristic(UUID.fromString(NSConstants.CHARACTERISTICS_UUID));
                gatt.setCharacteristicNotification(targetCharacteristics, true);
                BluetoothGattDescriptor descriptor = targetCharacteristics.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                boolean descWriteREsult = gatt.writeDescriptor(descriptor);
                Logger.v("NSManager descriptor write result " + descWriteREsult);
                if (isBLECurrentlyScanning) {
                    stopScan();
                }
            }
            super.onServicesDiscovered(gatt, status);
        }
        private boolean isGetWifi = false;
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            byte[] readValue = characteristic.getValue();
            byte commandCode = readValue[0];
            if(commandCode == (byte)0x03){
                isGetWifi = true;
            }
            int dataLength = 0;
            if (readValue.length > 1) {
                dataLength = unsigned2sComplement(readValue[1]);
            }
            Logger.v("NSManager commandCode " + commandCode + " datalength " + dataLength);
            if (commandCode != (byte) 0x8F) {
                if (dataLength > readValue.length) {
                    responseDataBuffer = new ArrayList<>();
                    responseDataBuffer.addAll(toObjectBytes(readValue));
                    Logger.v("NSManager buffering response");
                } else {
                    Logger.v("NSManager parse response");
                    parseResponse(readValue);
                }
            } else {
                if(isGetWifi){
                    isGetWifi = false;
                    parseResponse(toPrimitiveBytes(responseDataBuffer));
                }else {
                    //continous packet command
                    byte[] trimmedData = Arrays.copyOfRange(readValue, 1, readValue.length); //remove "8F" command code
                    byte tail = trimmedData[trimmedData.length - 1];
                    trimmedData = Arrays.copyOfRange(trimmedData, 0, trimmedData.length - 1);//remove tail to calculate bcc

                    responseDataBuffer.addAll(toObjectBytes(trimmedData));

                    byte calculatedBCC = calculateBCC(responseDataBuffer);
                    if (tail == calculatedBCC) {
                        //last packet received,parse
                        parseResponse(toPrimitiveBytes(responseDataBuffer));
                    } else {
                        //nope, not it, reappend tail
                        responseDataBuffer.add(tail);
                    }
                }

            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Logger.v("onCharacteristicWrite callback");
            operationTimeoutTimer.removeCallbacks(operationTimeoutRunnable);
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Logger.e("onCharacteristicWrite error " + status + " (catchball)");
            } else {
                if(requestDataBuffer.size() > 0 ){
                    byte[] reqData = requestDataBuffer.remove(0);
                    characteristic.setValue(reqData);
                    if (writeCharacteristicFilter(contextInstance, characteristic)) {
                        Logger.v("NSManager Sending data packet with payload "+bytesToHex(reqData)+" ("+reqData.length+" bytes of data)");
                        gatt.writeCharacteristic(characteristic);
                    }
                }else {
                    if (delegate != null && currentCommand != null) {
                        delegate.onCommandWritten(currentCommand.nsOperationType);
                    }
                    Logger.v("onCharacteristicWrite success queue size " + commandQueue.size() + " current type " + currentCommand.operationType + " (catchball)");
                    isWritingCommand = false;
                    writeDequeCommand();
                }
            }
        }
    };
    //end of ble vars

    //singleton functions
    private NSManager() {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(0)         // (Optional) How many method line to show. Default 2
                .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
                .tag("NSManager")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();

        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
    }

    public static NSManager getInstance(Context context, NSScanDelegate delegate) {
        if (instance == null) {
            instance = new NSManager();
            instance.initBLE(context);
            if (instance.broadcastContext != null) {
                //unreg ble on/off broadcast receiver to avoid leakage
                instance.broadcastContext.unregisterReceiver(instance.mBluetoothStateBroadcastReceiver);
            }
            instance.broadcastContext = context;

            //register ble on/off broadcast receiver
            IntentFilter myIntentFilter = new IntentFilter();
            myIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            myIntentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
            instance.broadcastContext.registerReceiver(instance.mBluetoothStateBroadcastReceiver, myIntentFilter);
        }

        instance.delegate = delegate;
        return instance;
    }

    public void setDelegate(NSBaseDelegate delegate) {
        this.delegate = delegate;
    }

    public void setShouldSplitPacket(boolean shouldSplitPacket) {
        this.shouldSplitPacket = shouldSplitPacket;
    }

    public boolean isShouldSplitPacket() {
        return shouldSplitPacket;
    }

    //connections
    private void initBLE(Context context) {
        BluetoothManager bleManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        contextInstance = context;
        if (bleManager != null) {
            bleAdapter = bleManager.getAdapter();
        }
    }

    public void connectToDevice(BluetoothDevice device, Context context) {
        Logger.v("NSManager Connect To Device");
        if (bleGatt == null) {
            Logger.v("NSManager Connecting To Device");
            bleGatt = device.connectGatt(context, false, bleGattCallback);
        }
    }

    public void disconnectCurrentDevice() {
        try {
            Logger.w("NSManager Disconnect Current Device");
            if (bleGatt != null) {
                Logger.w("NSManager Disconnecting");
                bleGatt.disconnect();
            } else {
                Logger.w("NSManager Disconnecting failed");
            }
        }catch (NullPointerException ignored){

        }
    }

    //scanning
    public void startScan(Activity baseActivity) {
        if (!hasLocationPermissions(baseActivity)) {
            requestLocationPermission(baseActivity);
            return;
        }

        if (!PermissionUtil.isLocationServiceEnable(baseActivity)) {
            locationServiceDisabled();
            return;
        }

        Logger.v("NSManager Start Scan");
        if (!hasBLEPermissions(baseActivity) || isBLECurrentlyScanning) {
            return;
        }
        bleScanner = bleAdapter.getBluetoothLeScanner();
        if (bleScanner != null) {
            final ScanFilter scanFilter = new ScanFilter.Builder().build();
            ScanSettings settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_BALANCED).build();
            bleScanner.startScan(Collections.singletonList(scanFilter), settings, scanCallback);

            isBLECurrentlyScanning = true;

            bleScanStopTimer = new Handler(Looper.getMainLooper());
            bleScanStopTimer.postDelayed(stopScanRunnable, (NemuriConstantsModel.get().nsScanTime) * 1000);
            if (delegate instanceof NSScanDelegate) {
                ((NSScanDelegate) delegate).onStartScan();
            }
        }
    }

    public boolean startSilentScan(Activity baseActivity) {
        if (!hasLocationPermissions(baseActivity)) {
            return false;
        }

        if (!PermissionUtil.isLocationServiceEnable(baseActivity)) {
            return false;
        }

        Logger.v("NSManager Start Scan");
        if (!hasBLEPermissionsNoRequest(baseActivity) || isBLECurrentlyScanning) {
            return false;
        }
        bleScanner = bleAdapter.getBluetoothLeScanner();
        if (bleScanner != null) {
            final ScanFilter scanFilter = new ScanFilter.Builder().build();
            ScanSettings settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_BALANCED).build();
            bleScanner.startScan(Collections.singletonList(scanFilter), settings, scanCallback);

            isBLECurrentlyScanning = true;

            bleScanStopTimer = new Handler(Looper.getMainLooper());
            bleScanStopTimer.postDelayed(stopScanRunnable, (NemuriConstantsModel.get().nsScanTime) * 1000);
            if (delegate instanceof NSScanDelegate) {
                ((NSScanDelegate) delegate).onStartScan();
            }
            return true;
        }
        return false;
    }

    public void locationPermissionDenied() {
        if (delegate instanceof NSScanDelegate) {
            ((NSScanDelegate) delegate).onLocationPermissionDenied();
        }
    }

    public void locationServiceDisabled() {
        if (delegate instanceof NSScanDelegate) {
            ((NSScanDelegate) delegate).onLocationServiceDisabled();
        }
    }

    public void stopScan() {
        Logger.v("NSManager Stop Scan");
        if (isBLECurrentlyScanning && bleAdapter != null && bleAdapter.isEnabled() && bleScanner != null) {
            bleScanner.stopScan(scanCallback);
        }
        if (bleScanStopTimer != null) {
            bleScanStopTimer.removeCallbacks(stopScanRunnable);
        }
        bleScanner = null;
        isBLECurrentlyScanning = false;
        bleScanStopTimer = null;
        if (delegate instanceof NSScanDelegate) {
            ((NSScanDelegate) delegate).onStopScan();
        }
    }

    private void enqueueCommand(CommandData commandData) {
        commandQueue.add(commandData);
        commandTypeQueue.add(commandData.operationType);
//        Logger.v("enqueing command size " + commandQueue.size()+" (catchball)");
        writeDequeCommand();
    }

    private void writeDequeCommand() {
        if (isWritingCommand) {
            return;
        }
        if (commandQueue.size() <= 0) {
            return;
        }

        isWritingCommand = true;
        currentCommand = commandQueue.poll();
        //split
        int maxPacketLength = shouldSplitPacket ? MAX_PACKET_SPLIT_SIZE : Integer.MAX_VALUE -1;
        Logger.v("NSManager Max packet length set to : "+(maxPacketLength+1)+" bytes, trying to send "+currentCommand.data.length+" byte of data");
        Logger.v("NSManager Data to sent : "+bytesToHex(currentCommand.data));
        requestDataBuffer = new ArrayList<>();
        if(currentCommand.data.length > maxPacketLength)
        {
            byte[] wholePacket = currentCommand.data;
            int seekPos = 0;
            do{
                int copyEnd = seekPos == 0 ? maxPacketLength+1 : maxPacketLength;
                if(copyEnd + seekPos > currentCommand.data.length){
                    //remainder
                    copyEnd = currentCommand.data.length - seekPos;
                }
                byte[] singlePacket = Arrays.copyOfRange(wholePacket,0, copyEnd);
                byte[] preparedPacket = singlePacket;
                if(seekPos != 0){
                    //append 8F to subsequent packets
                    preparedPacket = new byte[singlePacket.length + 1];
                    preparedPacket[0] = (byte)0x8F;
                    System.arraycopy(singlePacket, 0, preparedPacket, 1, singlePacket.length);
                }
                requestDataBuffer.add(preparedPacket);
                seekPos += singlePacket.length;
                wholePacket = Arrays.copyOfRange(currentCommand.data,seekPos, currentCommand.data.length);
                //Logger.v("NSManager Splitting payload "+bytesToHex(preparedPacket)+" ("+preparedPacket.length+" bytes of data), remainder "+bytesToHex(wholePacket));

            }while (seekPos < currentCommand.data.length);

            Logger.v("NSManager packet exceeding max size, splitting into "+requestDataBuffer.size()+" packets");
            if(requestDataBuffer.size() > 0){
                byte[] reqData = requestDataBuffer.remove(0);
                targetCharacteristics.setValue(reqData);
                if (writeCharacteristicFilter(contextInstance, targetCharacteristics) && bleGatt != null) {
                    Logger.v("NSManager Sending data packet with payload "+bytesToHex(reqData)+" ("+reqData.length+" bytes of data)");
                    bleGatt.writeCharacteristic(targetCharacteristics);
                }
            }
        }else{
            targetCharacteristics.setValue(currentCommand.data);
            if (writeCharacteristicFilter(contextInstance, targetCharacteristics) && bleGatt != null) {
                bleGatt.writeCharacteristic(targetCharacteristics);
                operationTimeoutTimer = new Handler(Looper.getMainLooper());
                float splitByteTimeout = NemuriConstantsModel.get().getUnmanaged().splitByteTimeout;
                operationTimeoutTimer.postDelayed(operationTimeoutRunnable, (long) (splitByteTimeout) * 1000);
            }
        }
//        Logger.w("dequeing command "+currentCommand.operationType+" size " + commandQueue.size()+" (catchball)");
    }

    public boolean writeCharacteristicFilter(Context context, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        //TODO : AFH
        byte[] readValue = bluetoothGattCharacteristic.getValue();
        byte commandCode = readValue[0];
        if (context != null) {
            NSCommandReceiver.sendCommand(context, commandCode);
        }
        //Prevent Sending Command If Bluetooth Id Disabled
        if (!BluetoothUtil.isBluetoothEnable()) return false;
        //Prevent Sending Command If Alert Dialog Is Showing in Bed Free
//        if (IOSDialogRight.getDialogVisibility() && commandCode == (byte) 0xB4) return false;
        return true;
    }

    //commands
    public void getSerialNumber() {
        if (isBLEReady()) {
            Logger.d("NSManager request GET_SERIAL_NUMBER (catchball)");
            byte[] commandData = {NSOperation.GET_SERIAL_NUMBER.getCommandCode(), 0, NSOperation.GET_SERIAL_NUMBER.getCommandCode()};
            enqueueCommand(new CommandData(commandData, NSOperation.GET_SERIAL_NUMBER));
        }
    }

    public void getWifiSettings() {
        if (isBLEReady()) {
            Logger.d("NSManager request GET_WIFI (catchball)");
            byte[] commandData = {NSOperation.GET_WIFI.getCommandCode(), 0, NSOperation.GET_WIFI.getCommandCode()};
            enqueueCommand(new CommandData(commandData, NSOperation.GET_WIFI));
        }
    }

    public void getNSStatus() {
        if (isBLEReady()) {
            Logger.d("NSManager request GET_NS_STATUS (catchball)");
            byte[] commandData = {NSOperation.GET_NS_STATUS.getCommandCode(), 0, NSOperation.GET_NS_STATUS.getCommandCode()};
            enqueueCommand(new CommandData(commandData, NSOperation.GET_NS_STATUS));
        }
    }

    public void getNSSpec() {
        if (isBLEReady()) {
            Logger.d("NSManager request GET_NS_SPEC (catchball)");
            byte[] commandData = {NSOperation.GET_NS_SPEC.getCommandCode(), 0, NSOperation.GET_NS_SPEC.getCommandCode()};
            enqueueCommand(new CommandData(commandData, NSOperation.GET_NS_SPEC));
        }
    }

    public void getBedSpec() {
        if (isBLEReady()) {
            Logger.d("NSManager request GET_BED_SPEC (catchball)");
            byte[] commandData = {NSOperation.GET_BED_SPEC.getCommandCode(), 0, NSOperation.GET_BED_SPEC.getCommandCode()};
            enqueueCommand(new CommandData(commandData, NSOperation.GET_BED_SPEC));
        }
    }

    public void getBedPosition() {
        if (isBLEReady()) {
            Logger.d("NSManager request GET_BED_POSITION (catchball)");
            byte[] commandData = {NSOperation.GET_BED_POSITION.getCommandCode(), 0, NSOperation.GET_BED_POSITION.getCommandCode()};
            enqueueCommand(new CommandData(commandData, NSOperation.GET_BED_POSITION));
        }
    }

    public void setBedSetting(NSBedSetting newSetting) {
        if (isBLEReady()) {
            Logger.d("NSManager request SET_BED_SETTING (catchball)");
            byte commandCode = NSOperation.SET_BED_SETTING.getCommandCode();
            byte dataLength = (byte) 3;

            //header
            List<Byte> commandData = new ArrayList<>();
            commandData.add(commandCode);
            commandData.add(dataLength);

            //data
            //line1, 4 zeroes unused data (lock setting)
            String line1 = "0000";
            line1 += (newSetting.isCombiLocked() ? "1" : "0");
            line1 += (newSetting.isHeightLocked() ? "1" : "0");
            line1 += (newSetting.isLegLocked() ? "1" : "0");
            line1 += (newSetting.isHeadLocked() ? "1" : "0");

            commandData.add(Byte.parseByte(line1, 2));

            //line 2, unused
            commandData.add((byte) 0);

            //line 3, speed setting
            commandData.add((byte) (newSetting.isFastMode() ? 1 : 0));

            //bcc
            commandData.add(calculateBCC(commandData));

            enqueueCommand(new CommandData(toPrimitiveBytes(commandData), NSOperation.SET_BED_SETTING));
        }
    }

    public void requestAuthentication(String serverId) {
        if (isBLEReady()) {
            Logger.d("NSManager request AUTHENTICATE " + serverId + " (catchball)");
            byte commandCode = NSOperation.AUTHENTICATE.getCommandCode();
            byte dataLength = 16;

            //header
            List<Byte> commandData = new ArrayList<>();
            commandData.add(commandCode);
            commandData.add(dataLength);

            //data
            byte[] rawData = serverId.getBytes();
            commandData.addAll(toObjectBytes(rawData));

            commandData.add(calculateBCC(commandData));

            enqueueCommand(new CommandData(toPrimitiveBytes(commandData), NSOperation.AUTHENTICATE));
        }
    }

    public void setServerURL(String serverURL) {
        if (isBLEReady()) {
            Logger.d("NSManager request SET_SERVER_URL serverURL : " + serverURL + " (catchball)");
            byte commandCode = NSOperation.SET_SERVER_URL.getCommandCode();
            byte dataLength = (byte) serverURL.length();

            //header
            List<Byte> commandData = new ArrayList<>();
            commandData.add(commandCode);
            commandData.add(dataLength);

            //data
            byte[] rawData = serverURL.getBytes();
            commandData.addAll(toObjectBytes(rawData));

            commandData.add(calculateBCC(commandData));

            enqueueCommand(new CommandData(toPrimitiveBytes(commandData), NSOperation.SET_SERVER_URL));
        }
    }

    public void notifyAutomaticOperationChange() {
        if (isBLEReady()) {
            Logger.d("NSManager request NOTIFY_AUTOMATIC_OPERATION_CHANGE (catchball)");
            byte commandCode = NSOperation.NOTIFY_AUTOMATIC_OPERATION_CHANGE.getCommandCode();
            byte dataLength = 0;

            //header
            List<Byte> commandData = new ArrayList<>();
            commandData.add(commandCode);
            commandData.add(dataLength);
            commandData.add(calculateBCC(commandData));

            enqueueCommand(new CommandData(toPrimitiveBytes(commandData), NSOperation.NOTIFY_AUTOMATIC_OPERATION_CHANGE));
        }
    }

    public void setWifiSetting(NSWifiSetting wifiSetting) {
        if (isBLEReady()) {
            Logger.d("NSManager request SET_WIFI serverURL : " + wifiSetting.toString() + " (catchball)");
            byte commandCode = NSOperation.SET_WIFI.getCommandCode();
            byte dataLength = 125;
            //header
            List<Byte> commandData = new ArrayList<>();
            commandData.add(commandCode);
            commandData.add(dataLength);

            //data
            commandData.add((byte) wifiSetting.getIsWifiEnabled());
            commandData.add((byte) wifiSetting.getWifiType());
            commandData.add((byte) wifiSetting.getOperationMode());
            commandData.add((byte) wifiSetting.getChannel());
            commandData.addAll(toObjectBytes(wifiSetting.getSsid().getBytes()));
            commandData.add((byte) 0);//null terminator for string
            for (int i = 0; i < 32 - wifiSetting.getSsid().length(); i++) {
                commandData.add((byte) 0);//fill in 32 bytes

            }
            commandData.add((byte) wifiSetting.getEncryptionMethod());
            commandData.addAll(toObjectBytes(wifiSetting.getPassword().getBytes()));
            commandData.add((byte) 0);//null terminator for string
            for (int i = 0; i < 64 - wifiSetting.getPassword().length(); i++) {
                commandData.add((byte) 0);//fill in 64 bytes

            }
            commandData.add((byte) wifiSetting.getDhcpStatus());
            commandData.add((byte) wifiSetting.getAutoDNSStatus());
            commandData.addAll(toObjectBytes(intArrayToByteArray(wifiSetting.getIpAddress())));
            commandData.addAll(toObjectBytes(intArrayToByteArray(wifiSetting.getSubnetMask())));
            commandData.addAll(toObjectBytes(intArrayToByteArray(wifiSetting.getDefaultGateway())));
            commandData.addAll(toObjectBytes(intArrayToByteArray(wifiSetting.getDns1())));
            commandData.addAll(toObjectBytes(intArrayToByteArray(wifiSetting.getDns2())));

            commandData.add(calculateBCC(commandData));

            enqueueCommand(new CommandData(toPrimitiveBytes(commandData), NSOperation.SET_WIFI));
        }
    }

    public void sendArrowCommand(NSOperation operation) {
        byte buttonCode;
        NSOperation.BedOperationType bedOperationType = NSOperation.BedOperationType.FREE;
        switch (operation) {
            case FREE_INCREASE_COMBI:
                buttonCode = 8;
                break;
            case FREE_DECREASE_COMBI:
                buttonCode = 9;
                break;
            case FREE_INCREASE_HEAD:
                buttonCode = 2;
                break;
            case FREE_DECREASE_HEAD:
                buttonCode = 3;
                break;
            case FREE_INCREASE_HEIGHT:
                buttonCode = 6;
                break;
            case FREE_DECREASE_HEIGHT:
                buttonCode = 7;
                break;
            case FREE_INCREASE_LEG:
                buttonCode = 4;
                break;
            case FREE_DECREASE_LEG:
                buttonCode = 5;
                break;
            case FREE_MULTI_BUTTON:
                buttonCode = 1;
                break;
            case FREE_TERMINATE:
                bedOperationType = NSOperation.BedOperationType.TERMINATE;
                buttonCode = 0;
                break;
            default:
                //invalid command
                return;
        }
        lastButtonCode = buttonCode;
        if (isBLEReady()) {
            Logger.d("NSManager request SEND_ARROW_COMMAND type : " + operation + " (catchball)");
            byte commandCode = operation.getCommandCode();
            byte dataLength = (byte) 5;

            //header
            List<Byte> commandData = new ArrayList<>();
            commandData.add(commandCode);
            commandData.add(dataLength);

            //data
            commandData.add(buttonCode);
            commandData.add(Byte.MAX_VALUE);//head, should be 255
            commandData.add(Byte.MAX_VALUE);//leg, should be 255
            commandData.add(Byte.MAX_VALUE);//height, should be 255
            commandData.add(Byte.MAX_VALUE);//tilt, should be 255

            //bcc
            commandData.add(calculateBCC(commandData));

            enqueueCommand(new CommandData(toPrimitiveBytes(commandData), bedOperationType, operation));
        }

    }

    public void getMattressPosition() {
        if (isBLEReady()) {
            Logger.d("NSManager request GET_MATTRESS_POSITION (catchball)");
            byte[] commandData = {NSOperation.GET_MATTRESS_POSITION.getCommandCode(), 0, NSOperation.GET_MATTRESS_POSITION.getCommandCode()};
            enqueueCommand(new CommandData(commandData, NSOperation.GET_MATTRESS_POSITION));
        }
    }

    public void setMattressPosition(NSMattressPosition mattressPosition) {
        if (isBLEReady()) {
            Logger.d("NSManager request SET_MATTRESS_POSITION (catchball)");
            byte commandCode = NSOperation.SET_MATTRESS_POSITION.getCommandCode();
            byte dataLength = (byte) 4;

            //header
            List<Byte> commandData = new ArrayList<>();
            commandData.add(commandCode);
            commandData.add(dataLength);

            //data
            String dataHead = intToPaddedBinaryString(mattressPosition.getHead()).substring(4, 8);
            String dataShoulder = intToPaddedBinaryString(mattressPosition.getShoulder()).substring(4, 8);
            String dataHip = intToPaddedBinaryString(mattressPosition.getHip()).substring(4, 8);
            String dataThigh = intToPaddedBinaryString(mattressPosition.getThigh()).substring(4, 8);
            String dataCalf = intToPaddedBinaryString(mattressPosition.getCalf()).substring(4, 8);
            String dataFeet = intToPaddedBinaryString(mattressPosition.getFeet()).substring(4, 8);

            String line1 = dataShoulder + dataHead;
            String line2 = dataThigh + dataHip;
            String line3 = dataFeet + dataCalf;
            String line4 = "";
            line4 += mattressPosition.getMattressSound();
            line4 += mattressPosition.getDehumidifierOperation();
            line4 += mattressPosition.isFukattoOn() ? "01" : "00";
            line4 += intToPaddedBinaryString(mattressPosition.getOperationMode()).substring(4, 8);

            commandData.add((byte) (Integer.parseInt(line1, 2) & 0xFF));
            commandData.add((byte) (Integer.parseInt(line2, 2) & 0xFF));
            commandData.add((byte) (Integer.parseInt(line3, 2) & 0xFF));
            commandData.add((byte) (Integer.parseInt(line4, 2) & 0xFF));

            //bcc
            commandData.add(calculateBCC(commandData));

            enqueueCommand(new CommandData(toPrimitiveBytes(commandData), NSOperation.SET_MATTRESS_POSITION));
        }
    }

    public void sendPresetCommand(NSBedPosition bedPosition, int currentHeight) {
        if (isBLEReady()) {
            NSOperation.BedOperationType bedOperationType = NSOperation.BedOperationType.PRESET;
            Logger.d("NSManager request SEND_PRESET_COMMAND type : " + NSOperation.PRESET_SET_POSITION + " with param " + bedPosition.toString() + " (catchball)");
            byte commandCode = NSOperation.PRESET_SET_POSITION.getCommandCode();
            byte dataLength = (byte) 5;

            //header
            List<Byte> commandData = new ArrayList<>();
            commandData.add(commandCode);
            commandData.add(dataLength);

            //data
            lastButtonCode = 22;
            commandData.add((byte) lastButtonCode);//button code
            commandData.add((byte) bedPosition.getHead());
            commandData.add((byte) bedPosition.getLeg());
            // 「高さ目標値がベッドタイプ=3（INTIME_COMFORT）のデフォルト値（254想定）」の場合は、現在高さを目標値として送信する
            if (bedPosition.getHeight() == DeviceTemplateBedModel.heightAndTiltDefaultValue_Comfort) {
                commandData.add((byte) currentHeight);
            } else {
                commandData.add((byte) bedPosition.getHeight());
            }
            commandData.add((byte) bedPosition.getTilt());

            //bcc
            commandData.add(calculateBCC(commandData));

            enqueueCommand(new CommandData(toPrimitiveBytes(commandData), bedOperationType, NSOperation.PRESET_SET_POSITION));
        }

    }

    public void getRealtimeFeed() {
        if (isBLEReady()) {
            Logger.d("NSManager request REALTIME_FEED (catchball)");
            byte[] commandData = {NSOperation.REALTIME_FEED.getCommandCode(), 0, NSOperation.REALTIME_FEED.getCommandCode()};
            enqueueCommand(new CommandData(commandData, NSOperation.REALTIME_FEED));
        }
    }

    public void switchFirmwareMode() {
        if (isBLEReady()) {
            Logger.d("NSManager request SWITCH_FIRMWARE_MODE (catchball)");
            byte[] commandData = {
                    NSOperation.SWITCH_FIRMWARE_MODE.getCommandCode(),
                    0,
                    NSOperation.SWITCH_FIRMWARE_MODE.getCommandCode()
            };
            enqueueCommand(new CommandData(commandData, NSOperation.SWITCH_FIRMWARE_MODE));
        }
    }

    public void enterFirmwareMode() {
        if (isBLEReady()) {
            Logger.d("NSManager request ENTER_FIRMWARE_MODE (catchball)");

            List<Byte> commandData = new ArrayList<>();
            commandData.add(NSOperation.ENTER_FIRMWARE_MODE.getCommandCode());
            commandData.add((byte)2);
            commandData.add((byte)1);
            commandData.add((byte)45);
            commandData.add(calculateBCC(commandData));

            enqueueCommand(new CommandData(toPrimitiveBytes(commandData), NSOperation.ENTER_FIRMWARE_MODE));
        }
    }

    public void writeFirmware(FirmwareFileModel.FirmwareBLEPacket firmwareBLEPacket) {
        if (isBLEReady()) {
            Logger.d("NSManager request WRITE_FIRMWARE data : " + firmwareBLEPacket.toString());
            byte commandCode = NSOperation.WRITE_FIRMWARE.getCommandCode();
            byte dataLength = (byte) 132;
            byte[] addressData = hexStringToByteArray(firmwareBLEPacket.getAddress());
            byte[] fwData = hexStringToByteArray(firmwareBLEPacket.getData());
            byte[] rawData = ArrayUtils.concatByteArrays(addressData, fwData);
            //header
            List<Byte> commandData = new ArrayList<>();
            commandData.add(commandCode);
            commandData.add(dataLength);
            //data
            commandData.addAll(toObjectBytes(rawData));
            commandData.add(calculateBCC(commandData));

            enqueueCommand(new CommandData(toPrimitiveBytes(commandData), NSOperation.WRITE_FIRMWARE));
        }
    }

    public void exitFirmwareMode(String checksum){
        if (isBLEReady()) {
            Logger.d("NSManager request EXIT_FIRMWARE_MODE checksum : "+checksum);
            byte commandCode = NSOperation.EXIT_FIRMWARE_MODE.getCommandCode();
            byte[] rawData = hexStringToByteArray(checksum);
            byte dataLength = (byte) rawData.length;
            //header
            List<Byte> commandData = new ArrayList<>();
            commandData.add(commandCode);
            commandData.add(dataLength);
            //data
            commandData.addAll(toObjectBytes(rawData));
            commandData.add(calculateBCC(commandData));

            enqueueCommand(new CommandData(toPrimitiveBytes(commandData), NSOperation.EXIT_FIRMWARE_MODE));
        }
    }
    private void parseResponse(byte[] response) {
        byte commandCode = response[0];
        NSOperation.BedOperationType bedOperationType = commandTypeQueue.poll();
        if (commandCode == NSOperation.GET_SERIAL_NUMBER.getResponseCode()) {
            Logger.i("NSManager response GET_SERIAL_NUMBER (catchball)");
            byte[] serialNumberInBytes = {response[2], response[3], response[4], response[5], response[6], response[7], response[8], response[9]};
            String serialNumber = new String(serialNumberInBytes);
            if (response[2] == 0 && response[3] == 0 && response[4] == 0 && response[5] == 0 && response[6] == 0 && response[7] == 0 && response[8] == 0 && response[9] == 0) {
                serialNumber = "AMS0NG";
            }
            if (delegate instanceof NSConnectionDelegate) {
                ((NSConnectionDelegate) delegate).onSerialNumberReceived(serialNumber);
            }
        } else if (commandCode == NSOperation.AUTHENTICATE.getResponseCode()) {
            Logger.i("NSManager response AUTHENTICATE (catchball)");
            int result = response[2];

            if (delegate instanceof NSConnectionDelegate) {
                ((NSConnectionDelegate) delegate).onAuthenticationFinished(result);
            }
        } else if (commandCode == NSOperation.SET_SERVER_URL.getResponseCode()) {
            Logger.i("NSManager response SET_SERVER_URL (catchball)");
            int result = response[2];

            if (delegate instanceof NSSettingDelegate) {
                ((NSSettingDelegate) delegate).onSetNSURLFinished(result == 0);
            }
        } else if (commandCode == NSOperation.GET_WIFI.getResponseCode()) {
            Logger.i("NSManager response GET_WIFI (catchball)");
            NSWifiSetting getWifiPojo = new NSWifiSetting(response);

            if (delegate instanceof NSSettingDelegate) {
                ((NSSettingDelegate) delegate).onGetWifiReceived(getWifiPojo);
            }
        } else if (commandCode == NSOperation.SET_WIFI.getResponseCode()) {
            Logger.i("NSManager response SET_WIFI (catchball)");
            int result = response[2];

            if (delegate instanceof NSSettingDelegate) {
                ((NSSettingDelegate) delegate).onSetWifiFinished(result == 0);
            }
        } else if (commandCode == NSOperation.GET_NS_STATUS.getResponseCode()) {
            Logger.i("NSManager response GET_NS_STATUS (catchball)");
            int systemStatus = response[2];
            int bleStatus = response[3];
            int wifiStatus = response[4];

            if (delegate instanceof NSConnectionDelegate) {
                ((NSConnectionDelegate) delegate).onNSStatusReceived(systemStatus, bleStatus, wifiStatus);
            }
        } else if (commandCode == NSOperation.GET_NS_SPEC.getResponseCode()) {
            Logger.i("NSManager response GET_NS_SPEC (catchball)");
            String nsDataBitString = intToPaddedBinaryString(response[2]);
            String nsVersionBitString = intToPaddedBinaryString(response[3]);
            String nsVersionBitString2 = intToPaddedBinaryString(response[4]);
            String bedDataBitString = intToPaddedBinaryString(response[5]);
            String mattressDataBitString = intToPaddedBinaryString(response[8]);

            int verMinor = Integer.parseInt(nsVersionBitString.substring(0, 4), 2);
            int verRevision = Integer.parseInt(nsVersionBitString.substring(4, 8), 2);

            int deviceMode = Integer.parseInt(nsVersionBitString2.substring(0, 4), 2);
            int verMajor = Integer.parseInt(nsVersionBitString2.substring(4, 8), 2);

            int nsTypeValue = Integer.parseInt(nsDataBitString.substring(0, 4), 2);
            int nsModel = Integer.parseInt(nsDataBitString.substring(4, 8), 2);

            int bedTypeValue = Integer.parseInt(bedDataBitString.substring(0, 4), 2);
            int bedModel = Integer.parseInt(bedDataBitString.substring(4, 8), 2);
            Logger.i("NSManager response GET_NS_SPEC bedModel = %d", bedModel);

            int mattressTypeValue = Integer.parseInt(mattressDataBitString.substring(0, 4), 2);
            int mattModel = Integer.parseInt(mattressDataBitString.substring(4, 8), 2);

            if (delegate instanceof NSConnectionDelegate) {
                NSSpec newSpec = new NSSpec(nsTypeValue ==3, bedTypeValue == 1,mattressTypeValue == 2,bedModel
                        ,nsModel,mattModel,deviceMode,verRevision,verMinor,verMajor);
                ((NSConnectionDelegate) delegate).onNSSpecReceived(newSpec);
            }
        } else if (commandCode == NSOperation.GET_BED_SPEC.getResponseCode()) {
            Logger.i("NSManager response GET_BED_SPEC (catchball)");

            if (delegate instanceof NSBedDelegate) {
                int headLowerLimitData = response[2];
                int headUpperLimitData = response[3];
                int legLowerLimitData = response[4];
                int legUpperLimitData = response[5];
                int heightLowerLimitData = response[6];
                int heightUpperLimitData = response[7];
                int tiltLowerLimitData = response[8];
                int tiltUpperLimitData = response[9];
                String lockSettingString = intToPaddedBinaryString(response[10]);

                boolean lockCombiSupported = lockSettingString.charAt(4) == '1';
                boolean lockHeightSupported = lockSettingString.charAt(5) == '1';
                boolean lockLegSupported = lockSettingString.charAt(6) == '1';
                boolean lockHeadSupported = lockSettingString.charAt(7) == '1';

                NSBedSpec bedSpec = new NSBedSpec(headLowerLimitData, headUpperLimitData, legLowerLimitData, legUpperLimitData,
                        heightLowerLimitData, heightUpperLimitData, tiltLowerLimitData, tiltUpperLimitData, lockHeightSupported, lockHeadSupported,
                        lockLegSupported, lockCombiSupported);

                ((NSBedDelegate) delegate).onBedSpecReceived(bedSpec);
            }
        } else if (commandCode == NSOperation.GET_BED_POSITION.getResponseCode()) {
            Logger.i("NSManager response GET_BED_POSITION (catchball)");
            int headPos = response[2];
            int legPos = response[3];
            int heightPos = response[4];
            int tiltPos = response[5];
            Logger.i("NSManager response GET_BED_POSITION tiltPos = %d", tiltPos);
            int speedSetting = response[8];
            int failCode = response[9];

            NSBedPosition bedPosition = new NSBedPosition(headPos, legPos, heightPos, tiltPos);

            String lockSettingString = intToPaddedBinaryString(response[6]);
            lockSettingString = lockSettingString.substring(4, 8);


            NSBedSetting bedSetting = new NSBedSetting(
                    lockSettingString.charAt(0) == '1',
                    lockSettingString.charAt(1) == '1',
                    lockSettingString.charAt(2) == '1',
                    lockSettingString.charAt(3) == '1',
                    speedSetting == 1);

            if (delegate instanceof NSBedDelegate) {
                ((NSBedDelegate) delegate).onBedPositionReceived(bedPosition, failCode);
                ((NSBedDelegate) delegate).onBedSettingReceived(bedSetting);
            }
        } else if (commandCode == NSOperation.FREE_DECREASE_HEAD.getResponseCode()) { //one code for all
            if (delegate instanceof NSBedDelegate) {
                int buttonCode = response[2];
                int headPos = response[3];
                int legPos = response[4];
                int heightPos = response[5];
                int tiltPos = response[6];
                int failCode = response[7];

                NSBedPosition bedPosition = new NSBedPosition(headPos, legPos, heightPos, tiltPos);
                Logger.i("NSManager response BED_POS " + bedOperationType + " (catchball) " + lastButtonCode + "-" + buttonCode);
                Logger.i("NSManager response BED_POS tiltPos = %d", tiltPos);
                switch (bedOperationType) {
                    case NONE:
                        break;
                    case FREE:
                        ((NSBedDelegate) delegate).onBedFreePositionReceived(bedPosition, failCode, buttonCode, lastButtonCode == buttonCode || buttonCode == 0);
                        break;
                    case PRESET:
                        ((NSBedDelegate) delegate).onBedPresetPositionReceived(bedPosition, failCode, buttonCode, lastButtonCode == buttonCode || buttonCode == 0);
                        break;
                    case MULTI_BUTTON:
                        break;
                    case TERMINATE:
                        break;
                }
            }

        } else if (commandCode == NSOperation.REALTIME_FEED.getResponseCode()) {
            Logger.i("NSManager response REALTIME_FEED (catchball)");
            int sequence = unsigned2sComplement(response[2]);
            int data1 = unsigned2sComplement(response[3]);
            int data2 = unsigned2sComplement(response[4]);
            int data3 = unsigned2sComplement(response[5]);
            int data4 = unsigned2sComplement(response[6]);
            int data5 = unsigned2sComplement(response[7]);
            int data6 = unsigned2sComplement(response[8]);
            int data7 = unsigned2sComplement(response[9]);
            int data8 = unsigned2sComplement(response[10]);
            int patientStat = unsigned2sComplement(response[11]);
            int respRate = unsigned2sComplement(response[12]);
            int heartRate = unsigned2sComplement(response[13]);
            NSRealtimeFeed realtimeFeed = new NSRealtimeFeed(sequence, data1, data2, data3, data4, data5, data6, data7, data8, patientStat, respRate, heartRate);

            if (delegate instanceof NSRealtimeDelegate) {
                ((NSRealtimeDelegate) delegate).onRealTimeFeedReceived(realtimeFeed);
            }
        } else if (commandCode == NSOperation.NOTIFY_AUTOMATIC_OPERATION_CHANGE.getResponseCode()) {
            Logger.i("NSManager response NOTIFY_AUTOMATIC_OPERATION_CHANGE (catchball)");
            if (delegate instanceof NSAutomaticOperationDelegate) {
                ((NSAutomaticOperationDelegate) delegate).onNotifyAutomaticOperationFinished();
            }
        } else if (commandCode == NSOperation.GET_MATTRESS_POSITION.getResponseCode()) {
            Logger.i("NSManager response GET_MATTRESS_POSITION (catchball)");

            if (delegate instanceof NSMattressDelegate) {
                String mattressValLine1 = intToPaddedBinaryString(response[2]);
                String mattressValLine2 = intToPaddedBinaryString(response[3]);
                String mattressValLine3 = intToPaddedBinaryString(response[4]);
                String mattressValLine4 = intToPaddedBinaryString(response[5]);
                int mattressValLine5 = response[6];
                String mattressValLine7 = intToPaddedBinaryString(response[8]);
                Logger.d("mattress raw data " + mattressValLine1 + "(" + response[2] + ")," + mattressValLine2 + "(" + response[3] +
                        ")," + mattressValLine3 + "," + "(" + response[4] + ")," + mattressValLine4 + "(" + response[5] + "),");
                int headVal = Byte.parseByte("0000" + mattressValLine1.substring(4, 8), 2);
                int shoulderVal = Byte.parseByte("0000" + mattressValLine1.substring(0, 4), 2);
                int hipVal = Byte.parseByte("0000" + mattressValLine2.substring(4, 8), 2);
                int thighVal = Byte.parseByte("0000" + mattressValLine2.substring(0, 4), 2);
                int calfVal = Byte.parseByte("0000" + mattressValLine3.substring(4, 8), 2);
                int feetVal = Byte.parseByte("0000" + mattressValLine3.substring(0, 4), 2);

                boolean isDehumidifierBusy = mattressValLine7.charAt(1) == '1';
                boolean isFeetBusy = mattressValLine7.charAt(2) == '1';
                boolean isCalfBusy = mattressValLine7.charAt(3) == '1';
                boolean isThighBusy = mattressValLine7.charAt(4) == '1';
                boolean isHipBusy = mattressValLine7.charAt(5) == '1';
                boolean isShoulderBuys = mattressValLine7.charAt(6) == '1';
                boolean isHeadBusy = mattressValLine7.charAt(7) == '1';

                int mattressSound = Character.getNumericValue(mattressValLine4.charAt(0));
                int mattressDehumOp = Character.getNumericValue(mattressValLine4.charAt(1));
                boolean mattressFukatto = !mattressValLine4.substring(2, 4).equalsIgnoreCase("00");
                int mattressOpMode = Byte.parseByte("0000" + mattressValLine4.substring(4, 8), 2);
                //TODO:MATRESS FAIL CODE
                int failCode = response[17];

                NSMattressPosition nsMattressPosition = new NSMattressPosition();
                nsMattressPosition.setHead(headVal);
                nsMattressPosition.setShoulder(shoulderVal);
                nsMattressPosition.setHip(hipVal);
                nsMattressPosition.setThigh(thighVal);
                nsMattressPosition.setCalf(calfVal);
                nsMattressPosition.setFeet(feetVal);

                nsMattressPosition.setDehumidifierOperation(mattressDehumOp);
                nsMattressPosition.setDehumidifierTime(mattressValLine5);
                nsMattressPosition.setOperationMode(mattressOpMode);
                nsMattressPosition.setMattressSound(mattressSound);
                nsMattressPosition.setFukatto(mattressFukatto);

                NSMattressStatus nsMattressStatus = new NSMattressStatus();
                nsMattressStatus.setHeadBusy(isHeadBusy);
                nsMattressStatus.setShoulderBusy(isShoulderBuys);
                nsMattressStatus.setHipBusy(isHipBusy);
                nsMattressStatus.setThighBusy(isThighBusy);
                nsMattressStatus.setCalfBusy(isCalfBusy);
                nsMattressStatus.setFeetBusy(isFeetBusy);
                nsMattressStatus.setDehumidifierBusy(isDehumidifierBusy);
                //TODO:MATRESS FAIL CODE
                final Set<Integer> ERROR_CODE_H = new HashSet<Integer>(Arrays.asList(2, 3, 7, 8));
                nsMattressStatus.setFailCodeH(ERROR_CODE_H.contains(failCode));

                ((NSMattressDelegate) delegate).onMattressPositionReceived(nsMattressPosition, nsMattressStatus, failCode);
            }
        } else if (commandCode == NSOperation.SET_MATTRESS_POSITION.getResponseCode()) {
            Logger.i("NSManager response SET_MATTRESS_POSITION (catchball)");
            boolean isSuccess = true;//response[2] == 1; //commented out no proper response from ASA
            if (delegate instanceof NSMattressDelegate) {
                ((NSMattressDelegate) delegate).onMattressResultReceived(isSuccess);
            }
        } else if (commandCode == NSOperation.SET_BED_SETTING.getResponseCode()) {
            Logger.i("NSManager response SET_BED_SETTING (catchball)");

            if (delegate instanceof NSBedDelegate) {
                ((NSBedDelegate) delegate).onBedSettingResult();
            }

        }else if (commandCode == NSOperation.SWITCH_FIRMWARE_MODE.getResponseCode()) {
            Logger.i("NSManager response SWITCH_FIRMWARE_MODE (catchball)");
            int result = unsigned2sComplement(response[2]);
            if (delegate instanceof NSFWUpdateDelegate) {
                ((NSFWUpdateDelegate) delegate).onSwitchFirmwareMode(result == 0);
            }
        }else if (commandCode == NSOperation.ENTER_FIRMWARE_MODE.getResponseCode()) {
            Logger.i("NSManager response ENTER_FIRMWARE_MODE (catchball)");
            int result = unsigned2sComplement(response[2]);
            if (delegate instanceof NSFWUpdateDelegate) {
                ((NSFWUpdateDelegate) delegate).onEnterFirmwareMode(result == 0);
            }
        }else if (commandCode == NSOperation.WRITE_FIRMWARE.getResponseCode()) {
            Logger.i("NSManager WRITE_FIRMWARE (catchball)");
            int result = unsigned2sComplement(response[2]);
            if (delegate instanceof NSFWUpdateDelegate) {
                ((NSFWUpdateDelegate) delegate).onWriteFirmware(result == 0);
            }
        }else if (commandCode == NSOperation.EXIT_FIRMWARE_MODE.getResponseCode()) {
            Logger.i("NSManager response EXIT_FIRMWARE_MODE (catchball)");
            int result = unsigned2sComplement(response[2]);
            if (delegate instanceof NSFWUpdateDelegate) {
                ((NSFWUpdateDelegate) delegate).onExitFirmwareMode(result == 0);
            }
        } else {
            Logger.e("NSManager parse response rogue command code " + commandCode + " (catchball)");
        }
    }

    //Utils
    private boolean hasBLEPermissions(Activity activity) {
        if (!BluetoothUtil.isBluetoothEnable()) {
            requestBluetoothEnable(activity);
            return false;
        } else if (!hasLocationPermissions(activity)) {
            requestLocationPermission(activity);
            return false;
        }
        return true;
    }

    private boolean hasBLEPermissionsNoRequest(Activity activity) {
        if (!BluetoothUtil.isBluetoothEnable()) {
            return false;
        } else if (!hasLocationPermissions(activity)) {
            return false;
        }
        return true;
    }


    public boolean isBLEReady() {
        return bleGatt != null && connectionStatus == BluetoothProfile.STATE_CONNECTED;
    }

    private List<Byte> toObjectBytes(byte[] rawData) {
        ArrayList<Byte> data = new ArrayList<>();
        for (byte rawElement : rawData
        ) {
            data.add(rawElement);
        }
        return data;
    }

    private byte[] intArrayToByteArray(int[] input) {
        byte[] output = new byte[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = (byte) input[i];
        }
        return output;
    }

    private byte[] toPrimitiveBytes(List<Byte> data) {
        byte[] convertedData = new byte[data.size()];
        int index = 0;
        for (Byte element : data
        ) {
            convertedData[index] = element;
            index++;
        }
        return convertedData;
    }

    private byte calculateBCC(List<Byte> data) {
        byte bcc = 0x0;
        for (Byte element : data
        ) {
            bcc = (byte) (bcc ^ element);
        }
        return bcc;
    }

    private int unsigned2sComplement(int signed2sComplement) {
        if (signed2sComplement < 0) {
            signed2sComplement = 256 + signed2sComplement;
        }
        return signed2sComplement;
    }

    private String intToPaddedBinaryString(int data) {
        data = unsigned2sComplement(data);
        return String.format("%8s", Integer.toBinaryString(data)).replace(' ', '0');
    }

    public void requestBluetoothEnable(Activity activity) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent, NSConstants.REQUEST_ENABLE_BT);
    }

    private boolean hasLocationPermissions(Activity activity) {
        return activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestLocationPermission(Activity activity) {
        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        PermissionUtil.showLocationServiceDialogAlert(activity, new PermissionUtil.LocationServiceDialogueListener() {
                            @Override
                            public void onDisabled(DialogInterface dialogInterface) {
                                dialogInterface.dismiss();
                                locationPermissionDenied();
                            }

                            @Override
                            public void onEnabled() {
                                startScan(activity);
                                Logx("requestLocationPermission", "onPermissionGranted");
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
//                        hasBLEPermissions(activity);
                        locationPermissionDenied();
                        Logx("requestLocationPermission", "onPermissionDenied");
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
//                        locationPermissionDenied();
                        token.continuePermissionRequest();
                        Logx("requestLocationPermission", "onPermissionRationaleShouldBeShown");
                    }
                }).check();
    }
    public void closeConnection(){
        Logger.v("NSManager force closing connection");
        if(bleGatt != null) {
            bleGatt.disconnect();
            bleGatt.close();
            bleGatt = null;
            if(delegate instanceof NSConnectionDelegate){
                Logger.v("NSManager ondisconnect delegate called");
                ((NSConnectionDelegate)delegate).onDisconnect();
            }
        }
    }

    public boolean isBLECurrentlyScanning() {
        return isBLECurrentlyScanning;
    }

    public BluetoothDevice getCurrentDevice() {
        if (bleGatt != null) {
            return bleGatt.getDevice();
        }
        return null;
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len/2];

        for(int i = 0; i < len; i+=2){
            data[i/2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }

        return data;
    }
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
    class CommandData {
        byte[] data;
        NSOperation.BedOperationType operationType = NSOperation.BedOperationType.OTHER;
        NSOperation nsOperationType;

        public CommandData(byte[] data, NSOperation nsOperationType) {
            this.data = data;
            this.nsOperationType = nsOperationType;
        }

        public CommandData(byte[] data, NSOperation.BedOperationType operationType, NSOperation nsOperationType) {
            this.data = data;
            this.operationType = operationType;
            this.nsOperationType = nsOperationType;
        }
    }
}