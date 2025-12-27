package com.example.listeners;

import com.example.pages.BasePage;
import io.qameta.allure.Attachment;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import java.lang.reflect.Field;


public class TestListener implements AfterTestExecutionCallback {

    @Override
    public void afterTestExecution(ExtensionContext context) {
        if (context.getExecutionException().isPresent()) {
            Object testInstance = context.getTestInstance().orElse(null);
            if (testInstance != null) {
                try {
                    WebDriver driver = getDriverFromTestInstance(testInstance);
                    if (driver != null) {
                        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                        attachScreenshot(screenshot, context.getDisplayName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private WebDriver getDriverFromTestInstance(Object testInstance) throws Exception {
        for (Field field : testInstance.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object fieldValue = field.get(testInstance);

            if (fieldValue instanceof WebDriver) {
                return (WebDriver) fieldValue;
            } else if (fieldValue instanceof BasePage) {
                return ((BasePage) fieldValue).driver;
            }
        }
        return null;
    }

    @Attachment(value = "Screenshot on failure", type = "image/png")
    private byte[] attachScreenshot(byte[] screenshot, String testName) {
        return screenshot;
    }
}