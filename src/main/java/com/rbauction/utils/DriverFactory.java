package com.rbauction.utils;

import com.rbauction.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public final class DriverFactory {

    private static final Logger log = LoggerFactory.getLogger(DriverFactory.class);
    private static final ConfigReader config = ConfigReader.getInstance();

    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        log.info("Creating chrome driver (headless={})", config.isHeadless());

        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver(buildChromeOptions());

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(config.getPageLoadTimeout()));
        return driver;
    }

    private static ChromeOptions buildChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-search-engine-choice-screen");

        if (config.isHeadless()) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("--user-agent=Mozilla/5.0 (X11; Linux x86_64) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36");
        }

        return options;
    }
}
