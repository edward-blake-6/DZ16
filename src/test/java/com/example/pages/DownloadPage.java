package com.example.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.io.File;
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
            if (Files.exists(filePath)) {
                try {
                    // Проверяем что файл полностью загружен (перестал меняться размер)
                    long size1 = Files.size(filePath);
                    Thread.sleep(1000);
                    long size2 = Files.size(filePath);

                    if (size1 == size2 && size1 > 0) {
                        return;
                    }
                } catch (Exception e) {
                    Thread.sleep(500);
                }
            }
            Thread.sleep(500);
        }

        throw new RuntimeException("Файл " + fileName + " не загрузился за " + timeoutMillis + " мс");
    }

    public boolean isFileDownloaded(String fileName, long timeoutMillis) {
        long startTime = System.currentTimeMillis();
        String downloadPath = System.getProperty("user.home") + "/Downloads/";
        Path filePath = Paths.get(downloadPath, fileName);

        while (System.currentTimeMillis() - startTime < timeoutMillis) {
            if (Files.exists(filePath)) {
                try {
                    long size1 = Files.size(filePath);
                    Thread.sleep(1000);
                    long size2 = Files.size(filePath);

                    if (size1 == size2 && size1 > 0) {
                        return true;
                    }
                } catch (Exception e) {
                    // Игнорируем ошибки и продолжаем проверку
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

    public Path getDownloadedFilePath(String fileName) {
        String downloadPath = System.getProperty("user.home") + "/Downloads/";
        return Paths.get(downloadPath, fileName);
    }

    public boolean fileExists(String fileName) {
        Path filePath = getDownloadedFilePath(fileName);
        return Files.exists(filePath);
    }

    public long getFileSize(String fileName) throws Exception {
        Path filePath = getDownloadedFilePath(fileName);
        if (Files.exists(filePath)) {
            return Files.size(filePath);
        }
        return 0;
    }
}