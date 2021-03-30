package com.paramount.bed.util;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paramount.bed.data.remote.response.DeviceTemplateResponse;

import java.io.IOException;

public class DataDefaultUtil {
    public static DeviceTemplateResponse getDeviceTempate() {

        DeviceTemplateResponse data = null;
        ObjectMapper mapper = new ObjectMapper();

        try {
            // Convert JSON string to Object
            String jsonInString = "{\n" +
                    "\t\"bed\": [{\n" +
                    "\t\t\t\"id\": 5,\n" +
                    "\t\t\t\"head\": 15,\n" +
                    "\t\t\t\"leg\": 12,\n" +
                    "\t\t\t\"tilt\": null,\n" +
                    "\t\t\t\"height\": null,\n" +
                    "\t\t\t\"tilt_default\": 255,\n" +
                    "\t\t\t\"height_default\": 255\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"id\": 8,\n" +
                    "\t\t\t\"head\": 0,\n" +
                    "\t\t\t\"leg\": 0,\n" +
                    "\t\t\t\"tilt\": null,\n" +
                    "\t\t\t\"height\": null,\n" +
                    "\t\t\t\"tilt_default\": 255,\n" +
                    "\t\t\t\"height_default\": 255\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"id\": 7,\n" +
                    "\t\t\t\"head\": 45,\n" +
                    "\t\t\t\"leg\": 16,\n" +
                    "\t\t\t\"tilt\": null,\n" +
                    "\t\t\t\"height\": null,\n" +
                    "\t\t\t\"tilt_default\": 255,\n" +
                    "\t\t\t\"height_default\": 255\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"id\": 4,\n" +
                    "\t\t\t\"head\": 20,\n" +
                    "\t\t\t\"leg\": 10,\n" +
                    "\t\t\t\"tilt\": 0,\n" +
                    "\t\t\t\"height\": 0,\n" +
                    "\t\t\t\"tilt_default\": 255,\n" +
                    "\t\t\t\"height_default\": 255\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"id\": 2,\n" +
                    "\t\t\t\"head\": 0,\n" +
                    "\t\t\t\"leg\": 15,\n" +
                    "\t\t\t\"tilt\": 0,\n" +
                    "\t\t\t\"height\": 0,\n" +
                    "\t\t\t\"tilt_default\": 255,\n" +
                    "\t\t\t\"height_default\": 255\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"id\": 6,\n" +
                    "\t\t\t\"head\": 15,\n" +
                    "\t\t\t\"leg\": 10,\n" +
                    "\t\t\t\"tilt\": 0,\n" +
                    "\t\t\t\"height\": 0,\n" +
                    "\t\t\t\"tilt_default\": 255,\n" +
                    "\t\t\t\"height_default\": 255\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"id\": 9,\n" +
                    "\t\t\t\"head\": 45,\n" +
                    "\t\t\t\"leg\": 5,\n" +
                    "\t\t\t\"tilt\": 0,\n" +
                    "\t\t\t\"height\": 0,\n" +
                    "\t\t\t\"tilt_default\": 255,\n" +
                    "\t\t\t\"height_default\": 255\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"id\": 3,\n" +
                    "\t\t\t\"head\": 35,\n" +
                    "\t\t\t\"leg\": 17,\n" +
                    "\t\t\t\"tilt\": 0,\n" +
                    "\t\t\t\"height\": 0,\n" +
                    "\t\t\t\"tilt_default\": 255,\n" +
                    "\t\t\t\"height_default\": 255\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"id\": 1,\n" +
                    "\t\t\t\"head\": 40,\n" +
                    "\t\t\t\"leg\": 10,\n" +
                    "\t\t\t\"tilt\": 1,\n" +
                    "\t\t\t\"height\": 0,\n" +
                    "\t\t\t\"tilt_default\": 255,\n" +
                    "\t\t\t\"height_default\": 255\n" +
                    "\t\t}\n" +
                    "\t],\n" +
                    "\t\"mattress\": [{\n" +
                    "\t\t\t\"id\": 2,\n" +
                    "\t\t\t\"head\": 2,\n" +
                    "\t\t\t\"shoulder\": 4,\n" +
                    "\t\t\t\"hip\": 3,\n" +
                    "\t\t\t\"thigh\": 2,\n" +
                    "\t\t\t\"calf\": 1,\n" +
                    "\t\t\t\"feet\": 6\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"id\": 3,\n" +
                    "\t\t\t\"head\": 3,\n" +
                    "\t\t\t\"shoulder\": 3,\n" +
                    "\t\t\t\"hip\": 4,\n" +
                    "\t\t\t\"thigh\": 1,\n" +
                    "\t\t\t\"calf\": 3,\n" +
                    "\t\t\t\"feet\": 1\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"id\": 4,\n" +
                    "\t\t\t\"head\": 4,\n" +
                    "\t\t\t\"shoulder\": 2,\n" +
                    "\t\t\t\"hip\": 1,\n" +
                    "\t\t\t\"thigh\": 2,\n" +
                    "\t\t\t\"calf\": 1,\n" +
                    "\t\t\t\"feet\": 1\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"id\": 5,\n" +
                    "\t\t\t\"head\": 5,\n" +
                    "\t\t\t\"shoulder\": 1,\n" +
                    "\t\t\t\"hip\": 5,\n" +
                    "\t\t\t\"thigh\": 1,\n" +
                    "\t\t\t\"calf\": 1,\n" +
                    "\t\t\t\"feet\": 4\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"id\": 1,\n" +
                    "\t\t\t\"head\": 1,\n" +
                    "\t\t\t\"shoulder\": 5,\n" +
                    "\t\t\t\"hip\": 2,\n" +
                    "\t\t\t\"thigh\": 1,\n" +
                    "\t\t\t\"calf\": 1,\n" +
                    "\t\t\t\"feet\": 1\n" +
                    "\t\t}\n" +
                    "\t]\n" +
                    "}";
            data = mapper.readValue(jsonInString, DeviceTemplateResponse.class);

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
