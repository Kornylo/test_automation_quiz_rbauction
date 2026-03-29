package com.rbauction.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchResultsPage extends BasePage {

    private static final Logger log = LoggerFactory.getLogger(SearchResultsPage.class);

    private static final By RESULTS_HEADING = By.cssSelector("p[data-testid='search-count-header']");
    private static final By RESULT_TITLE_LINK = By.cssSelector("a[data-testid='item-card-title-link']");

    public SearchResultsPage(WebDriver driver) {
        super(driver);
        dismissDialogsIfPresent();
        wait.until(ExpectedConditions.visibilityOfElementLocated(RESULTS_HEADING));
    }

    public int getTotalResultCount() {
        WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(RESULTS_HEADING));
        log.info("Parsing result count: {}", heading.getText());
        Matcher matcher = Pattern.compile("([\\d,]+) results").matcher(heading.getText());
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1).replace(",", ""));
        }
        throw new RuntimeException("Could not parse result count from: " + heading.getText());
    }

    public String getFirstResultTitle() {
        WebElement link = wait.until(ExpectedConditions.visibilityOfElementLocated(RESULT_TITLE_LINK));
        String title = link.getText().trim();
        log.info("First result title: {}", title);
        return title;
    }

    public boolean firstResultContains(String text) {
        return getFirstResultTitle().toLowerCase().contains(text.toLowerCase());
    }

    public SearchResultsPage applyYearFilterViaUrl(int fromYear, int toYear) {
        log.info("Applying year filter: {}-{}", fromYear, toYear);
        int countBefore = getTotalResultCount();

        String url = driver.getCurrentUrl();
        url += (url.contains("?") ? "&" : "?") + "manufactureYearRange=" + fromYear + "-" + toYear;
        driver.get(url);

        dismissDialogsIfPresent();
        wait.until(ExpectedConditions.visibilityOfElementLocated(RESULTS_HEADING));

        new WebDriverWait(driver, Duration.ofSeconds(config.getExplicitWaitTimeout()))
                .until(d -> getTotalResultCount() != countBefore);

        return this;
    }
}
