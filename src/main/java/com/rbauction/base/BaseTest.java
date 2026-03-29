package com.rbauction.base;

import com.rbauction.pages.HomePage;
import com.rbauction.utils.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

public abstract class BaseTest {

    private static final Logger log = LoggerFactory.getLogger(BaseTest.class);
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    public WebDriver getDriver() {
        return DRIVER.get();
    }

    @BeforeMethod
    public void setUp(Method method) {
        log.info("Starting browser for test: {}", method.getName());
        DRIVER.set(DriverFactory.createDriver());
    }

    @AfterMethod
    public void tearDown() {
        WebDriver wd = DRIVER.get();
        if (wd != null) {
            wd.quit();
            DRIVER.remove();
            log.info("Browser closed");
        }
    }

    protected HomePage openHomePage() {
        return new HomePage(getDriver()).open();
    }
}
