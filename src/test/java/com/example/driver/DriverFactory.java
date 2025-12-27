package com.example.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.HashMap;
import java.util.Map;

public class DriverFactory {

    public enum BrowserType {
        CHROME,
        FIREFOX
    }

    private DriverFactory() {
    }

    public static WebDriver createDriver(BrowserType browserType) {
        return createDriver(browserType, null);
    }

    public static WebDriver createDriver(BrowserType browserType, Object options) {
        WebDriver driver;

        switch (browserType) {
            case FIREFOX:
                WebDriverManager.firefoxdriver().setup();
                if (options instanceof FirefoxOptions) {
                    driver = new FirefoxDriver((FirefoxOptions) options);
                } else {
                    driver = new FirefoxDriver();
                }
                break;
            case CHROME:
            default:
                WebDriverManager.chromedriver().setup();
                if (options instanceof ChromeOptions) {
                    driver = new ChromeDriver((ChromeOptions) options);
                } else {
                    driver = new ChromeDriver();
                }
                break;
        }

        driver.manage().window().maximize();
        return driver;
    }

    public static WebDriver createDriverWithOptions(ChromeOptions options) {
        return createDriver(BrowserType.CHROME, options);
    }

    public static WebDriver createDefaultDriver() {
        return createDriver(BrowserType.CHROME);
    }

    public static ChromeOptions getChromeOptionsForDownload(String downloadPath) {
        ChromeOptions options = new ChromeOptions();
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", downloadPath);
        prefs.put("download.prompt_for_download", false);
        prefs.put("download.directory_upgrade", true);
        prefs.put("safebrowsing.enabled", true);
        prefs.put("profile.default_content_settings.popups", 0);
        options.setExperimentalOption("prefs", prefs);
        return options;
    }
}