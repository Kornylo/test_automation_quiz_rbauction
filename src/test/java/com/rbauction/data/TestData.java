package com.rbauction.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.testng.annotations.DataProvider;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class TestData {

    @DataProvider(name = "searchTerms")
    public static Object[][] searchTerms() {
        JsonArray array = readJsonArray("testdata/search-terms.json");
        Object[][] data = new Object[array.size()][3];
        for (int i = 0; i < array.size(); i++) {
            JsonObject obj = array.get(i).getAsJsonObject();
            data[i][0] = buildDisplayName(obj);
            data[i][1] = obj.get("searchTerm").getAsString();
            data[i][2] = obj.get("expectedInFirstResult").getAsString();
        }
        return data;
    }

    @DataProvider(name = "yearFilter")
    public static Object[][] yearFilter() {
        JsonArray array = readJsonArray("testdata/year-filter.json");
        Object[][] data = new Object[array.size()][3];
        for (int i = 0; i < array.size(); i++) {
            JsonObject obj = array.get(i).getAsJsonObject();
            data[i][0] = buildDisplayName(obj);
            data[i][1] = obj.get("searchTerm").getAsString();
            data[i][2] = obj.get("fromYear").getAsInt();
        }
        return data;
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
