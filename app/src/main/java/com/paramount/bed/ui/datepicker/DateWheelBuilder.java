package com.paramount.bed.ui.datepicker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.configure.PickerOptions;
import com.bigkoo.pickerview.listener.OnOptionsSelectChangeListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.util.DialogUtil;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DateWheelBuilder extends OptionsPickerBuilder {

    private PickerOptions mPickerOptions;
    DateWheelPicker instance;
    OnDateWheelSelectListener dateWheelListener;

    List<String> monthsList;
    List<Integer> dateList;
    List<Integer> yearLists;
    Context context;

    int yearNow;
    List<String> monthsListNow;
    List<Integer> dateListNow;

    public DateWheelBuilder(Context context, OnDateWheelSelectListener listener) {
        super(context, null);

        this.context = context;
        mPickerOptions = new PickerOptions(PickerOptions.TYPE_PICKER_OPTIONS);
        mPickerOptions.context = context;
        dateWheelListener = listener;

        mPickerOptions.cyclic1 = false;
        mPickerOptions.cyclic2 = false;
        mPickerOptions.cyclic3 = false;

        yearNow = Calendar.getInstance().get(Calendar.YEAR);
        int monthNow = Calendar.getInstance().get(Calendar.MONTH);
        int dateNow = Calendar.getInstance().get(Calendar.DATE);

        monthsList = new ArrayList<>();
        String[] months = new DateFormatSymbols().getMonths();
        for (int i = 0; i < months.length; i++) {
            monthsList.add(months[i]);
        }

        monthsListNow = new ArrayList<>();
        for (int i = 0; i <= monthNow; i++) {
            monthsListNow.add(months[i]);
        }

        dateList = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            dateList.add(i);
        }

        dateListNow = new ArrayList<>();
        for (int i = 1; i <= dateNow; i++) {
            dateListNow.add(i);
        }

        yearLists = new ArrayList<>();

        for (int i = 1870; i <= yearNow; i++) {
            yearLists.add(i);
        }

    }

    public DateWheelBuilder setRangeDate(Calendar startDate, Calendar endDate) {
        mPickerOptions.startDate = startDate;
        mPickerOptions.endDate = endDate;
        return this;
    }

    public DateWheelBuilder setDate(Calendar date) {
        mPickerOptions.date = date;
        int yearIndex = yearLists.indexOf(date.get(Calendar.YEAR));
        //super.setSelectOptions(date.get(Calendar.MONTH), date.get(Calendar.DATE), yearIndex);
        mPickerOptions.option2 = date.get(Calendar.MONTH) - 1;
        mPickerOptions.option3 = date.get(Calendar.DATE) - 1;
        mPickerOptions.option1 = yearIndex;
        return this;
    }

    public DateWheelBuilder setCancelText(String textCancel) {
        mPickerOptions.textContentCancel = textCancel;
        return this;
    }

    public DateWheelBuilder setConfirmText(String textConfirm) {
        mPickerOptions.textContentConfirm = textConfirm;
        return this;
    }

    @Override
    public DateWheelBuilder setSelectOptions(int options1, int options2, int options3) {
        super.setSelectOptions(options1, options2, options3);
        return this;
    }

    int status;

    @Override
    public DateWheelPicker build() {
        super.build();
        status = 0;

        mPickerOptions.optionsSelectChangeListener = new OnOptionsSelectChangeListener() {
            @Override
            public void onOptionsSelectChanged(int options1, int options2, int options3) {
                Log.d("month", Calendar.getInstance().get(Calendar.MONTH) + " " + options1);

                //instance.setNPicker(monthsList, fakeList, yearLists);
                int year = yearLists.get(options1);
                int month = options2;
                Calendar cal = Calendar.getInstance();
                cal.setLenient(false);
                cal.set(yearLists.get(options1), options2, options3 + 1);
                try {
                    cal.getTime();

                    if (yearLists.get(options1) == Calendar.getInstance().get(Calendar.YEAR)) {
//                        if (status == 0) {
                        if (options2 == Calendar.getInstance().get(Calendar.MONTH)) {
//                            instance.setNPicker(monthsListNow, dateListNow, yearLists);
                            instance.setNPicker(yearLists, monthsListNow, dateListNow);
                        } else {
//                            instance.setNPicker(monthsListNow, dateList, yearLists);
                            instance.setNPicker(yearLists, monthsListNow, dateList);
                        }

                        instance.setSelectOptions(options1, options2, options3);

//                            status = 1;
//                        }
                    } else if (yearLists.get(options1) != Calendar.getInstance().get(Calendar.YEAR)) {
//                        if (status == 1) {
                        setDateList(year, month);
                        instance.setNPicker(yearLists, monthsList, dateList);

//                            int lastDate =  cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                        instance.setSelectOptions(options1, options2, options3);

//                            status = 0;
//                        }
                    }
                } catch (Exception e) {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    int lastDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    setDateList(year, month);
                    instance.setNPicker(yearLists, monthsList, dateList);
                    instance.setSelectOptions(options3, lastDate - 1, options2);
//                    Log.d("montCatch", calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + " " + calendar.getActualMaximum(Calendar.DAY_OF_YEAR) + " " + calendar.getActualMaximum(Calendar.DAY_OF_WEEK));
                }
            }
        };

        mPickerOptions.backgroundId = 0;

        mPickerOptions.optionsSelectListener = new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                Calendar date = Calendar.getInstance();
                date.set(yearLists.get(options1), options2, options3 + 1);

                dateWheelListener.onDatateWheelSelect(date.getTime(), v);
            }

        };

        instance = new DateWheelPicker(mPickerOptions);

        if (UserLogin.getUserLogin() != null && UserLogin.getUserLogin().getBirthDate() != null && !UserLogin.getUserLogin().getBirthDate().isEmpty()) {
            String yearGet = UserLogin.getUserLogin().getBirthDate().trim().replace("-", "/");
            int yearGetInt = Integer.parseInt(yearGet.substring(0, 4));
            int monthGetInt = Integer.parseInt(yearGet.substring(5, 7));

            Log.d("month", monthGetInt + " " + Calendar.getInstance().get(Calendar.MONTH));

            if (yearGetInt == Calendar.getInstance().get(Calendar.YEAR)) {
                if (monthGetInt - 1 == Calendar.getInstance().get(Calendar.MONTH)) {
                    instance.setNPicker(yearLists, monthsListNow, dateListNow);
                } else {
                    instance.setNPicker(yearLists, monthsListNow, dateList);
                }
            } else {
                instance.setNPicker(yearLists, monthsList, dateList);
            }
        } else {
            instance.setNPicker(yearLists, monthsList, dateList);
        }

        return instance;
    }

    public void setDateList(int iYear, int iMonth) {
        // Create a calendar object and set year and month
        Calendar mycal = new GregorianCalendar(iYear, iMonth, 1);
        // Get the number of days in that month
        int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH); // 28
        dateList = new ArrayList<>();
        for (int i = 1; i <= daysInMonth; i++) {
            dateList.add(i);
        }

    }
}
