package com.rbauction.pages;

import com.rbauction.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public abstract class BasePage {

    private static final Logger log = LoggerFactory.getLogger(BasePage.class);

    private static final By LANGUAGE_DIALOG = By.xpath("//h1[contains(text(),'Confirm your language')]");
    private static final By ENGLISH_BUTTON = By.xpath("//button[.//div[text()='English']]");
    private static final By COOKIE_ACCEPT_BUTTON = By.xpath("//button[text()='I understand']");
    private static final By MODAL_BACKDROP = By.cssSelector(".MuiBackdrop-root.MuiModal-backdrop");

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final ConfigReader config;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.config = ConfigReader.getInstance();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(config.getExplicitWaitTimeout()));
    }

    protected void dismissDialogsIfPresent() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(config.getDialogWaitTimeout()));

        try {
            shortWait.until(d -> {
                try {
                    WebElement el = d.findElement(LANGUAGE_DIALOG);
                    return el.isDisplayed() ? el : null;
                } catch (Exception e) {
                    return null;
                }
            });
            log.info("Dismissing language dialog");
            shortWait.until(ExpectedConditions.elementToBeClickable(ENGLISH_BUTTON)).click();
            shortWait.until(ExpectedConditions.invisibilityOfElementLocated(LANGUAGE_DIALOG));
        } catch (Exception ignored) {
        }

        try {
            WebElement cookieBtn = driver.findElement(COOKIE_ACCEPT_BUTTON);
            if (cookieBtn.isDisplayed()) {
                cookieBtn.click();
            }
        } catch (Exception ignored) {
        }

        try {
            new WebDriverWait(driver, Duration.ofSeconds(config.getDialogWaitTimeout()))
                    .until(d -> d.findElements(MODAL_BACKDROP).stream().noneMatch(WebElement::isDisplayed));
        } catch (Exception ignored) {
        }
    }
}
