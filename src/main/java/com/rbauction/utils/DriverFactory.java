package com.rbauction.utils;

import com.rbauction.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public final class DriverFactory {

    private static final Logger log = LoggerFactory.getLogger(DriverFactory.class);
    private static final ConfigReader config = ConfigReader.getInstance();

    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        String browser = config.getBrowser().toLowerCase();
        boolean remote = config.isRemote();
        log.info("Creating {} driver (headless={}, remote={})", browser, config.isHeadless(), remote);

        WebDriver driver;
        if (remote) {
            driver = createRemoteDriver(browser);
        } else {
            driver = switch (browser) {
                case "firefox" -> createFirefox();
                default -> createChrome();
            };
        }

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(config.getPageLoadTimeout()));
        return driver;
    }

    // ---- Local drivers ----

    private static WebDriver createChrome() {
        WebDriverManager.chromedriver().setup();
        return new ChromeDriver(buildChromeOptions());
    }

    private static WebDriver createFirefox() {
        WebDriverManager.firefoxdriver().setup();
        return new FirefoxDriver(buildFirefoxOptions());
    }

    // ---- Remote / Selenoid ----

    private static WebDriver createRemoteDriver(String browser) {
        String remoteUrl = config.getRemoteUrl();
        log.info("Connecting to remote WebDriver: {}", remoteUrl);

        URL url;
        try {
            url = new URL(remoteUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid remote.url: " + remoteUrl, e);
        }

        return switch (browser) {
            case "firefox" -> {
                FirefoxOptions options = buildFirefoxOptions();
                applySelenoidCapabilities(options);
                yield new RemoteWebDriver(url, options);
            }
            default -> {
                ChromeOptions options = buildChromeOptions();
                applySelenoidCapabilities(options);
                yield new RemoteWebDriver(url, options);
            }
        };
    }

    // ---- Options builders ----

    private static ChromeOptions buildChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-search-engine-choice-screen");

        String version = config.getBrowserVersion();
        if (version != null && !version.isBlank()) {
            options.setBrowserVersion(version);
        }

        if (config.isHeadless()) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("--user-agent=Mozilla/5.0 (X11; Linux x86_64) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36");
        }

        return options;
    }

    private static FirefoxOptions buildFirefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();

        String version = config.getBrowserVersion();
        if (version != null && !version.isBlank()) {
            options.setBrowserVersion(version);
        }

        if (config.isHeadless()) {
            options.addArguments("--headless");
        }

        return options;
    }

    private static void applySelenoidCapabilities(org.openqa.selenium.MutableCapabilities options) {
        Map<String, Object> selenoidOptions = new HashMap<>();
        selenoidOptions.put("enableVNC", config.isSelenoidVnc());
        selenoidOptions.put("enableVideo", config.isSelenoidVideo());

        log.info("Selenoid options: enableVNC={}, enableVideo={}",
                config.isSelenoidVnc(), config.isSelenoidVideo());

        options.setCapability("selenoid:options", selenoidOptions);
    }
}
