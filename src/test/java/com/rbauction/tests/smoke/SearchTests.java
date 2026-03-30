package com.rbauction.tests.smoke;

import com.rbauction.base.BaseTest;
import com.rbauction.data.TestData;
import com.rbauction.pages.SearchResultsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Year;

import static com.rbauction.listeners.TestListener.step;
import static com.rbauction.listeners.TestListener.setTestName;

public class SearchTests extends BaseTest {

    @Test(dataProvider = "searchTerms", dataProviderClass = TestData.class,
            description = "Verify search results relevance for a given search term")
    public void shouldValidateSearchResultRelevance(String displayName, String searchTerm, String expectedInFirstResult) {
        setTestName(displayName);

        step("Navigate to www.rbauction.com");
        step("Searches for \"" + searchTerm + "\"");
        SearchResultsPage results = openHomePage().searchFor(searchTerm);

        step("Captures the total number of results returned");
        int totalResults = results.getTotalResultCount();
        Assert.assertTrue(totalResults > 0,
                "Expected results for '" + searchTerm + "', got " + totalResults);

        step("This should be represented as an integer: " + totalResults);

        step("Verifies that the first result on the page has the word \"" + expectedInFirstResult + "\" in it");
        Assert.assertTrue(results.firstResultContains(expectedInFirstResult),
                "First result should contain '" + expectedInFirstResult + "', got: " + results.getFirstResultTitle());
    }

    @Test(dataProvider = "yearFilter", dataProviderClass = TestData.class,
            description = "Verify that applying a year filter changes the result count")
    public void shouldUpdateResultsWhenYearFilterApplied(String displayName, String searchTerm, int fromYear) {
        setTestName(displayName);
        int currentYear = Year.now().getValue();

        step("Navigate to www.rbauction.com");
        step("Searches for \"" + searchTerm + "\"");
        SearchResultsPage results = openHomePage().searchFor(searchTerm);

        step("Captures the total number of results returned");
        int initialCount = results.getTotalResultCount();
        Assert.assertTrue(initialCount > 0,
                "Expected results for '" + searchTerm + "', got " + initialCount);

        step("This should be represented as an integer: " + initialCount);

        step("Applies the \"Year\" filter with a range from " + fromYear + " to " + currentYear);
        results.applyYearFilterViaUrl(fromYear, currentYear);

        int filteredCount = results.getTotalResultCount();
        step("Verifies the number of results is different using numerical comparison: " + initialCount + " → " + filteredCount);
        Assert.assertNotEquals(filteredCount, initialCount,
                "Result count should change after year filter. Before: " + initialCount + ", After: " + filteredCount);
    }
}
