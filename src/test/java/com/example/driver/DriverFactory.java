package com.example.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class DriverFactory {

    public enum BrowserType {
        CHROME,
        FIREFOX
    }

    private DriverFactory() {
    }

    public static WebDriver createDriver(BrowserType browserType) {
        WebDriver driver;

        switch (browserType) {
            case FIREFOX:
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;
            case CHROME:
            default:
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
                break;
        }

        driver.manage().window().maximize();
        return driver;
    }

    public static WebDriver createDefaultDriver() {
        return createDriver(BrowserType.CHROME);
    }
}