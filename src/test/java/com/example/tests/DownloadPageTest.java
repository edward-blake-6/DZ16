package com.example.tests;

import com.example.driver.DriverFactory;
import com.example.listeners.TestListener;
import com.example.manager.PageManager;
import io.qameta.allure.*;
import io.qameta.allure.model.Status;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;

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
@Story("Скачивание различных типов файлов")
@Tag("download")
@Tag("file")
@Tag("smoke")
public class DownloadPageTest {

    private PageManager pageManager;
    private WebDriver driver;
    private String downloadDir;

    @BeforeEach
    @Step("Настройка окружения для тестов загрузки")
    @Description("Создание временной директории и настройка Chrome для загрузки файлов")
    public void setup() throws IOException {
        Allure.label("layer", "ui");
        Allure.label("component", "file-download");

        // Создаем временную папку для загрузок
        downloadDir = createDownloadDirectoryStep();

        // Настраиваем Chrome для загрузки в указанную папку
        ChromeOptions options = createChromeOptionsStep(downloadDir);

        // Используем DriverFactory с кастомными опциями
        driver = DriverFactory.createDriverWithOptions(options);
        pageManager = new PageManager(driver);

        Allure.addAttachment("Директория загрузки", "text/plain", downloadDir);
    }

    @AfterEach
    @Step("Очистка после теста")
    @Description("Закрытие браузера и копирование файлов в Allure отчет")
    public void teardown() throws IOException {
        closeBrowserStep();
        copyDownloadsToAllureStep();

        Allure.step("Ресурсы освобождены");
    }

    @Test
    @Story("Загрузка логотипа WebDriverManager")
    @Description("Тест проверяет загрузку PNG файла с логотипом WebDriverManager")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("UI: Загрузка PNG логотипа")
    @Tag("png")
    @Tag("image")
    public void testDownloadWebDriverManagerLogo() throws Exception {
        executeDownloadTestStep("webdrivermanager.png", "WebDriverManager Logo");
    }

    @Test
    @Story("Загрузка документации WebDriverManager")
    @Description("Тест проверяет загрузку PDF файла с документацией WebDriverManager")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("UI: Загрузка PDF документации")
    @Tag("pdf")
    @Tag("document")
    public void testDownloadWebDriverManagerDoc() throws Exception {
        executeDownloadTestStep("webdrivermanager.pdf", "WebDriverManager Documentation");
    }

    @Test
    @Story("Загрузка логотипа Selenium-Jupiter")
    @Description("Тест проверяет загрузку PNG файла с логотипом Selenium-Jupiter")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("UI: Загрузка Selenium-Jupiter логотипа")
    @Tag("png")
    @Tag("image")
    public void testDownloadSeleniumJupiterLogo() throws Exception {
        executeDownloadTestStep("selenium-jupiter.png", "Selenium-Jupiter Logo");
    }

    @Test
    @Story("Загрузка документации Selenium-Jupiter")
    @Description("Тест проверяет загрузку PDF файла с документацией Selenium-Jupiter")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("UI: Загрузка Selenium-Jupiter документации")
    @Tag("pdf")
    @Tag("document")
    public void testDownloadSeleniumJupiterDoc() throws Exception {
        executeDownloadTestStep("selenium-jupiter.pdf", "Selenium-Jupiter Documentation");
    }

    @Step("Создание директории для загрузок")
    private String createDownloadDirectoryStep() throws IOException {
        String dir = System.getProperty("user.dir") + "/target/downloads/" + System.currentTimeMillis();
        Files.createDirectories(Paths.get(dir));
        return dir;
    }

    @Step("Создание ChromeOptions для загрузки")
    private ChromeOptions createChromeOptionsStep(String downloadPath) {
        ChromeOptions options = new ChromeOptions();
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", downloadPath);
        prefs.put("download.prompt_for_download", false);
        prefs.put("download.directory_upgrade", true);
        prefs.put("safebrowsing.enabled", true);
        options.setExperimentalOption("prefs", prefs);

        Allure.addAttachment("Chrome настройки", "text/plain",
                "Директория загрузки: " + downloadPath + "\nПодтверждение загрузки: отключено");

        return options;
    }

    @Step("Создание WebDriver с настройками")
    private WebDriver createWebDriverStep(ChromeOptions options) {
        return DriverFactory.createDriverWithOptions(options);
    }

    @Step("Выполнение теста загрузки файла: {fileName}")
    private void executeDownloadTestStep(String fileName, String fileDescription) throws Exception {
        Allure.parameter("Имя файла", fileName);
        Allure.parameter("Описание файла", fileDescription);

        Path expectedFile = Paths.get(downloadDir, fileName);

        openDownloadPageStep();
        performDownloadStep(fileName, fileDescription);
        waitForFileDownloadStep(expectedFile, 10000);
        validateDownloadedFileStep(expectedFile, fileName);
        attachFileToReportStep(expectedFile, fileDescription);
    }

    @Step("Открытие страницы загрузки")
    private void openDownloadPageStep() {
        pageManager.openDownloadPage("https://bonigarcia.dev/selenium-webdriver-java/download.html");
        Allure.step("Страница загрузки открыта");
    }

    @Step("Загрузка файла: {fileDescription}")
    private void performDownloadStep(String fileName, String fileDescription) {
        switch (fileName) {
            case "webdrivermanager.png":
                pageManager.getDownloadPage().downloadWebDriverManagerLogo();
                break;
            case "webdrivermanager.pdf":
                pageManager.getDownloadPage().downloadWebDriverManagerDoc();
                break;
            case "selenium-jupiter.png":
                pageManager.getDownloadPage().downloadSeleniumJupiterLogo();
                break;
            case "selenium-jupiter.pdf":
                pageManager.getDownloadPage().downloadSeleniumJupiterDoc();
                break;
        }
        Allure.step("Загрузка " + fileDescription + " инициирована");
    }

    @Step("Ожидание загрузки файла (таймаут: {timeoutMillis}ms)")
    private void waitForFileDownloadStep(Path filePath, long timeoutMillis) throws InterruptedException, IOException {
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeoutMillis) {
            if (Files.exists(filePath)) {
                try {
                    long size1 = Files.size(filePath);
                    Thread.sleep(1000);
                    long size2 = Files.size(filePath);

                    if (size1 == size2 && size1 > 0) {
                        Allure.step("Файл полностью загружен, размер: " + size1 + " bytes");
                        return;
                    }
                } catch (IOException e) {
                    // Файл может быть заблокирован во время записи, продолжаем ждать
                    Allure.step("Файл заблокирован, продолжаем ожидание: " + e.getMessage());
                    Thread.sleep(500);
                }
            }
            Thread.sleep(500);
        }

        Allure.step("Таймаут загрузки файла", Status.FAILED);
        throw new RuntimeException("Файл не загрузился за " + timeoutMillis + " мс");
    }

    @Step("Валидация загруженного файла: {fileName}")
    private void validateDownloadedFileStep(Path expectedFile, String fileName) throws IOException {
        assertTrue(Files.exists(expectedFile), "Файл " + fileName + " должен быть загружен");
        assertTrue(Files.size(expectedFile) > 0, "Файл не должен быть пустым");

        Allure.addAttachment("Информация о файле", "text/plain",
                "Имя: " + fileName + "\n" +
                        "Путь: " + expectedFile.toString() + "\n" +
                        "Размер: " + Files.size(expectedFile) + " bytes\n" +
                        "Существует: " + Files.exists(expectedFile));

        Allure.step("Файл валидирован успешно");
    }

    @Step("Прикрепление файла к отчету: {fileDescription}")
    @Attachment(value = "{attachmentName}", type = "application/octet-stream")
    private byte[] attachFileToReportStep(Path filePath, String fileDescription) throws IOException {
        String attachmentName = "Скачанный файл: " + fileDescription;
        byte[] fileContent = Files.readAllBytes(filePath);

        Allure.addAttachment("Метаданные файла", "text/plain",
                "Имя вложения: " + attachmentName + "\n" +
                        "Размер: " + fileContent.length + " bytes\n" +
                        "Тип: " + getFileType(filePath.toString()));

        return fileContent;
    }

    @Step("Закрытие браузера")
    private void closeBrowserStep() {
        if (pageManager != null) {
            pageManager.quit();
        }
    }

    @Step("Копирование загруженных файлов в Allure отчет")
    private void copyDownloadsToAllureStep() throws IOException {
        Path allureResults = Paths.get(System.getProperty("user.dir"), "target", "allure-results");
        Files.createDirectories(allureResults);

        java.io.File downloadFolder = new java.io.File(downloadDir);
        if (downloadFolder.exists() && downloadFolder.isDirectory()) {
            java.io.File[] files = downloadFolder.listFiles();
            if (files != null) {
                for (java.io.File file : files) {
                    if (file.isFile()) {
                        Path destination = allureResults.resolve(file.getName());
                        Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
                        Allure.step("Файл скопирован в Allure: " + file.getName());
                    }
                }
            }
        }
    }

    private String getFileType(String fileName) {
        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".pdf")) return "application/pdf";
        return "application/octet-stream";
    }
}