package com.rbauction.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.testng.annotations.DataProvider;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TestData {

    private static final String DATA_FILE = "testdata/test-data.json";

    @DataProvider(name = "searchTerms")
    public static Object[][] searchTerms() {
        List<JsonObject> entries = filterByType("search");
        Object[][] data = new Object[entries.size()][3];
        for (int i = 0; i < entries.size(); i++) {
            JsonObject obj = entries.get(i);
            data[i][0] = buildDisplayName(obj);
            data[i][1] = obj.get("searchTerm").getAsString();
            data[i][2] = obj.get("expectedInFirstResult").getAsString();
        }
        return data;
    }

    @DataProvider(name = "yearFilter")
    public static Object[][] yearFilter() {
        List<JsonObject> entries = filterByType("yearFilter");
        Object[][] data = new Object[entries.size()][3];
        for (int i = 0; i < entries.size(); i++) {
            JsonObject obj = entries.get(i);
            data[i][0] = buildDisplayName(obj);
            data[i][1] = obj.get("searchTerm").getAsString();
            data[i][2] = obj.get("fromYear").getAsInt();
        }
        return data;
    }

    private static List<JsonObject> filterByType(String type) {
        JsonArray array = readJsonArray(DATA_FILE);
        List<JsonObject> filtered = new ArrayList<>();
        for (JsonElement element : array) {
            JsonObject obj = element.getAsJsonObject();
            if (type.equals(obj.get("type").getAsString())) {
                filtered.add(obj);
            }
        }
        return filtered;
    }

    private static String buildDisplayName(JsonObject obj) {
        String testPlanId = obj.get("testPlanId").getAsString();
        String testCaseId = obj.get("testCaseId").getAsString();
        String name = obj.get("name").getAsString();
        return testPlanId + " | " + testCaseId + " | " + name;
    }

    private static JsonArray readJsonArray(String resourcePath) {
        InputStream is = TestData.class.getClassLoader().getResourceAsStream(resourcePath);
        if (is == null) throw new RuntimeException("Test data not found: " + resourcePath);
        return JsonParser.parseReader(new InputStreamReader(is, StandardCharsets.UTF_8)).getAsJsonArray();
    }
}
