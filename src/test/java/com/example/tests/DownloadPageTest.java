package com.example.tests;

import com.example.driver.DriverFactory;
import com.example.listeners.TestListener;
import com.example.manager.PageManager;
import io.qameta.allure.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(TestListener.class)
@Epic("Функциональность загрузки файлов")
@Feature("Загрузка файлов с сайта")
public class DownloadPageTest {

    private PageManager pageManager;
    private WebDriver driver;
    private String downloadDir;

    @BeforeEach
    @Step("Настройка окружения для тестов загрузки")
    public void setup() throws IOException {
        downloadDir = System.getProperty("user.dir") + "/target/downloads/" + System.currentTimeMillis();
        Files.createDirectories(Paths.get(downloadDir));

        // Настраиваем Chrome для загрузки в указанную папку
        ChromeOptions options = new ChromeOptions();
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", downloadDir);
        prefs.put("download.prompt_for_download", false);
        prefs.put("download.directory_upgrade", true);
        prefs.put("safebrowsing.enabled", true);
        options.setExperimentalOption("prefs", prefs);

        driver = DriverFactory.createDriverWithOptions(options);
        pageManager = new PageManager(driver);
    }

    @AfterEach
    @Step("Очистка после теста")
    public void teardown() throws IOException {
        pageManager.quit();
        copyDownloadsToAllure();
    }

    @Test
    @Story("Загрузка логотипа WebDriverManager")
    @Description("Тест проверяет загрузку PNG файла с логотипом WebDriverManager")
    @Severity(SeverityLevel.CRITICAL)
    public void testDownloadWebDriverManagerLogo() throws Exception {
        String fileName = "webdrivermanager.png";
        Path expectedFile = Paths.get(downloadDir, fileName);

        pageManager.openDownloadPage("https://bonigarcia.dev/selenium-webdriver-java/download.html");
        pageManager.getDownloadPage().downloadWebDriverManagerLogo();

        waitForFileDownload(expectedFile, 10000);
        assertTrue(Files.exists(expectedFile), "Файл " + fileName + " должен быть загружен");
        assertTrue(Files.size(expectedFile) > 0, "Файл не должен быть пустым");

        attachFileToAllure(expectedFile, "Загруженный файл: " + fileName);

        Allure.step("Проверка файла " + fileName, () -> {
            Allure.addAttachment("Размер файла", "text/plain",
                    String.valueOf(Files.size(expectedFile)));
        });
    }

    @Test
    @Story("Загрузка документации WebDriverManager")
    @Description("Тест проверяет загрузку PDF файла с документацией WebDriverManager")
    @Severity(SeverityLevel.NORMAL)
    public void testDownloadWebDriverManagerDoc() throws Exception {
        String fileName = "webdrivermanager.pdf";
        Path expectedFile = Paths.get(downloadDir, fileName);

        pageManager.openDownloadPage("https://bonigarcia.dev/selenium-webdriver-java/download.html");
        pageManager.getDownloadPage().downloadWebDriverManagerDoc();

        waitForFileDownload(expectedFile, 10000);
        assertTrue(Files.exists(expectedFile), "Файл " + fileName + " должен быть загружен");
        assertTrue(Files.size(expectedFile) > 0, "Файл не должен быть пустым");

        attachFileToAllure(expectedFile, "Загруженный файл: " + fileName);
    }

    @Test
    @Story("Загрузка логотипа Selenium-Jupiter")
    @Description("Тест проверяет загрузку PNG файла с логотипом Selenium-Jupiter")
    @Severity(SeverityLevel.CRITICAL)
    public void testDownloadSeleniumJupiterLogo() throws Exception {
        String fileName = "selenium-jupiter.png";
        Path expectedFile = Paths.get(downloadDir, fileName);

        pageManager.openDownloadPage("https://bonigarcia.dev/selenium-webdriver-java/download.html");
        pageManager.getDownloadPage().downloadSeleniumJupiterLogo();

        waitForFileDownload(expectedFile, 10000);
        assertTrue(Files.exists(expectedFile), "Файл " + fileName + " должен быть загружен");
        assertTrue(Files.size(expectedFile) > 0, "Файл не должен быть пустым");

        attachFileToAllure(expectedFile, "Загруженный файл: " + fileName);
    }

    @Test
    @Story("Загрузка документации Selenium-Jupiter")
    @Description("Тест проверяет загрузку PDF файла с документацией Selenium-Jupiter")
    @Severity(SeverityLevel.NORMAL)
    public void testDownloadSeleniumJupiterDoc() throws Exception {
        String fileName = "selenium-jupiter.pdf";
        Path expectedFile = Paths.get(downloadDir, fileName);

        pageManager.openDownloadPage("https://bonigarcia.dev/selenium-webdriver-java/download.html");
        pageManager.getDownloadPage().downloadSeleniumJupiterDoc();

        waitForFileDownload(expectedFile, 10000);
        assertTrue(Files.exists(expectedFile), "Файл " + fileName + " должен быть загружен");
        assertTrue(Files.size(expectedFile) > 0, "Файл не должен быть пустым");

        attachFileToAllure(expectedFile, "Загруженный файл: " + fileName);
    }

    private void waitForFileDownload(Path filePath, long timeoutMillis) throws InterruptedException {
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeoutMillis) {
            if (Files.exists(filePath)) {
                long size1 = Files.size(filePath);
                Thread.sleep(1000);
                long size2 = Files.size(filePath);

                if (size1 == size2 && size1 > 0) {
                    return;
                }
            }
            Thread.sleep(500);
        }

        throw new RuntimeException("Файл не загрузился за " + timeoutMillis + " мс");
    }

    @Attachment(value = "{attachmentName}", type = "application/octet-stream")
    private byte[] attachFileToAllure(Path filePath, String attachmentName) throws IOException {
        return Files.readAllBytes(filePath);
    }

    private void copyDownloadsToAllure() throws IOException {
        Path allureResults = Paths.get(System.getProperty("user.dir"), "target", "allure-results");
        Files.createDirectories(allureResults);

        File downloadFolder = new File(downloadDir);
        if (downloadFolder.exists() && downloadFolder.isDirectory()) {
            File[] files = downloadFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        Path destination = allureResults.resolve(file.getName());
                        Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        }
    }
}