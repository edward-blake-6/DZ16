package com.example.pages;

import com.example.components.Footer;
import com.example.components.Header;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class BasePage {
    public WebDriver driver;

    private Header header;
    private Footer footer;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        this.header = new Header(driver);
        this.footer = new Footer(driver);
    }

    public Header getHeader() {
        return header;
    }

    public Footer getFooter() {
        return footer;
    }

    public abstract boolean isPageLoaded();

    public byte[] takeScreenshot() {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    public String takeScreenshotAndSave(String testName) {
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path destination = Paths.get("target/screenshots/" + testName + "_" + System.currentTimeMillis() + ".png");
            Files.createDirectories(destination.getParent());
            Files.copy(screenshot.toPath(), destination);
            return destination.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}