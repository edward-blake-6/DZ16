package com.example.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DownloadPage extends BasePage {

    @FindBy(xpath = "//a[contains(@href, 'webdrivermanager.png') and contains(text(), 'logo')]")
    private WebElement webDriverManagerLogoBtn;

    @FindBy(xpath = "//a[contains(@href, 'webdrivermanager.pdf') and contains(text(), 'doc')]")
    private WebElement webDriverManagerDocBtn;

    @FindBy(xpath = "//a[contains(@href, 'selenium-jupiter.png') and contains(text(), 'logo')]")
    private WebElement seleniumJupiterLogoBtn;

    @FindBy(xpath = "//a[contains(@href, 'selenium-jupiter.pdf') and contains(text(), 'doc')]")
    private WebElement seleniumJupiterDocBtn;

    public DownloadPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageLoaded() {
        return webDriverManagerLogoBtn.isDisplayed();
    }

    public void downloadWebDriverManagerLogo() {
        webDriverManagerLogoBtn.click();
    }

    public void downloadWebDriverManagerDoc() {
        webDriverManagerDocBtn.click();
    }

    public void downloadSeleniumJupiterLogo() {
        seleniumJupiterLogoBtn.click();
    }

    public void downloadSeleniumJupiterDoc() {
        seleniumJupiterDocBtn.click();
    }

    public void waitForFileDownload(String fileName, long timeoutMillis) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        String downloadPath = System.getProperty("user.home") + "/Downloads/";
        Path filePath = Paths.get(downloadPath, fileName);

        while (System.currentTimeMillis() - startTime < timeoutMillis) {
            if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
                long size1 = Files.size(filePath);
                Thread.sleep(1000);
                long size2 = Files.size(filePath);

                if (size1 == size2 && size1 > 0) {
                    return;
                }
            }
            Thread.sleep(500);
        }

        throw new RuntimeException("Файл " + fileName + " не загрузился за " + timeoutMillis + " мс");
    }
}