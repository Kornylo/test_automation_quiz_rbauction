package com.rbauction.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.rbauction.base.BaseTest;
import com.rbauction.utils.ScreenshotUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    private static final Logger log = LoggerFactory.getLogger(TestListener.class);
    private static final ExtentReports extent = createExtent();
    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    public static void step(String message) {
        ExtentTest extentTest = test.get();
        if (extentTest != null) {
            extentTest.log(Status.PASS, message);
        }
    }

    public static void setTestName(String displayName) {
        ExtentTest extentTest = test.get();
        if (extentTest != null) {
            extentTest.getModel().setName(displayName);
        }
    }

    private static ExtentReports createExtent() {
        ExtentReports ext = new ExtentReports();
        ExtentSparkReporter spark = new ExtentSparkReporter("test-reports/ExtentReport.html");
        spark.config().setDocumentTitle("RB Auction Search Tests");
        spark.config().setReportName("Test Results");
        ext.attachReporter(spark);
        return ext;
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = resolveTestName(result);
        log.info("Test started: {}", testName);
        ExtentTest extentTest = extent.createTest(
                testName,
                result.getMethod().getDescription());
        test.set(extentTest);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("Test passed: {}", resolveTestName(result));
        ExtentTest extentTest = test.get();
        if (extentTest != null) {
            extentTest.log(Status.PASS, "Test passed");
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.info("Test failed: {}", resolveTestName(result));
        ExtentTest extentTest = test.get();
        if (extentTest != null) {
            extentTest.fail(result.getThrowable());

            Object testInstance = result.getInstance();
            if (testInstance instanceof BaseTest baseTest) {
                WebDriver driver = baseTest.getDriver();
                if (driver != null) {
                    String base64 = ScreenshotUtils.captureAsBase64(driver);
                    extentTest.fail("Screenshot",
                            MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build());
                }
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.info("Test skipped: {}", resolveTestName(result));
        ExtentTest extentTest = test.get();
        if (extentTest != null) {
            extentTest.log(Status.SKIP, "Test skipped: " + result.getThrowable());
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("Test execution finished");
        extent.flush();
    }

    private String resolveTestName(ITestResult result) {
        Object[] params = result.getParameters();
        if (params != null && params.length > 0 && params[0] instanceof String firstParam) {
            if (firstParam.contains(" | ")) {
                return firstParam;
            }
        }
        return result.getMethod().getMethodName();
    }
}
