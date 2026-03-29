package com.rbauction.pages;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomePage extends BasePage {

    private static final Logger log = LoggerFactory.getLogger(HomePage.class);

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public HomePage open() {
        String baseUrl = config.getBaseUrl();
        log.info("Navigating to: {}", baseUrl);
        driver.get(baseUrl);
        dismissDialogsIfPresent();
        return this;
    }

    public SearchResultsPage searchFor(String term) {
        String searchUrl = config.getBaseUrl() + "/search?freeText=" + term.replace(" ", "+");
        log.info("Searching for: {}", term);
        driver.get(searchUrl);
        return new SearchResultsPage(driver);
    }
}
