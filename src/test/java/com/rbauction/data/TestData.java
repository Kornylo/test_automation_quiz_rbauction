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
        Object[][] data = new Object[array.size()][2];
        for (int i = 0; i < array.size(); i++) {
            JsonObject obj = array.get(i).getAsJsonObject();
            data[i][0] = obj.get("searchTerm").getAsString();
            data[i][1] = obj.get("expectedInFirstResult").getAsString();
        }
        return data;
    }

    @DataProvider(name = "yearFilter")
    public static Object[][] yearFilter() {
        JsonObject obj = readJsonObject("testdata/year-filter.json");
        return new Object[][]{
                {obj.get("searchTerm").getAsString(), obj.get("fromYear").getAsInt()}
        };
    }

    private static JsonArray readJsonArray(String resourcePath) {
        InputStream is = TestData.class.getClassLoader().getResourceAsStream(resourcePath);
        if (is == null) throw new RuntimeException("Test data not found: " + resourcePath);
        return JsonParser.parseReader(new InputStreamReader(is, StandardCharsets.UTF_8)).getAsJsonArray();
    }

    private static JsonObject readJsonObject(String resourcePath) {
        InputStream is = TestData.class.getClassLoader().getResourceAsStream(resourcePath);
        if (is == null) throw new RuntimeException("Test data not found: " + resourcePath);
        return JsonParser.parseReader(new InputStreamReader(is, StandardCharsets.UTF_8)).getAsJsonObject();
    }
}
