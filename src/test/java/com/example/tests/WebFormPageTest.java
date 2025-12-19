package com.example.tests;

import com.example.driver.DriverFactory;
import com.example.manager.PageManager;
import com.example.pages.WebFormPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class WebFormPageTest {

    private PageManager pageManager;
    private Path testFile;

    @BeforeEach
    public void setup() throws IOException {
        pageManager = new PageManager(DriverFactory.createDefaultDriver());
        testFile = Files.createTempFile("test", ".txt");
        Files.write(testFile, "test content".getBytes());
    }

    @AfterEach
    public void teardown() throws IOException {
        Files.deleteIfExists(testFile);
        pageManager.quit();
    }

    @Test
    public void testTextInput() {
        pageManager.openWebFormPage("https://bonigarcia.dev/selenium-webdriver-java/web-form.html");

        WebFormPage webFormPage = pageManager.getWebFormPage();
        webFormPage.setTextInput("Test");
        webFormPage.verifyTextInput("Test");
    }

    @Test
    public void testPasswordInput() {
        pageManager.openWebFormPage("https://bonigarcia.dev/selenium-webdriver-java/web-form.html");

        WebFormPage webFormPage = pageManager.getWebFormPage();
        webFormPage.setPassword("secret");
        webFormPage.verifyPasswordField();
    }

    @Test
    public void testTextarea() {
        pageManager.openWebFormPage("https://bonigarcia.dev/selenium-webdriver-java/web-form.html");

        WebFormPage webFormPage = pageManager.getWebFormPage();
        webFormPage.setTextarea("Text");
        webFormPage.verifyTextarea("Text");
    }

    @Test
    public void testDisabledInput() {
        pageManager.openWebFormPage("https://bonigarcia.dev/selenium-webdriver-java/web-form.html");

        WebFormPage webFormPage = pageManager.getWebFormPage();
        webFormPage.verifyDisabledInput();
    }

    @Test
    public void testReadonlyInput() {
        pageManager.openWebFormPage("https://bonigarcia.dev/selenium-webdriver-java/web-form.html");

        WebFormPage webFormPage = pageManager.getWebFormPage();
        webFormPage.verifyReadonlyInput();
    }

    @Test
    public void testDropdownSelect() {
        pageManager.openWebFormPage("https://bonigarcia.dev/selenium-webdriver-java/web-form.html");

        WebFormPage webFormPage = pageManager.getWebFormPage();
        webFormPage.selectDropdownOptionByValue("1");
        webFormPage.verifyDropdownSelection("One");
    }

    @Test
    public void testDatalistInput() {
        pageManager.openWebFormPage("https://bonigarcia.dev/selenium-webdriver-java/web-form.html");

        WebFormPage webFormPage = pageManager.getWebFormPage();
        webFormPage.setDatalistValue("San Francisco");
        webFormPage.verifyDatalistValue("San Francisco");
    }

    @Test
    public void testFileInput() {
        pageManager.openWebFormPage("https://bonigarcia.dev/selenium-webdriver-java/web-form.html");

        WebFormPage webFormPage = pageManager.getWebFormPage();
        webFormPage.uploadFile(testFile.toAbsolutePath().toString());
        webFormPage.verifyFileUpload();
    }

    @Test
    public void testCheckbox() {
        pageManager.openWebFormPage("https://bonigarcia.dev/selenium-webdriver-java/web-form.html");

        WebFormPage webFormPage = pageManager.getWebFormPage();
        boolean initialState = webFormPage.isCheckboxSelected();
        webFormPage.toggleCheckbox();
        webFormPage.verifyCheckboxToggled(initialState);
    }

    @Test
    public void testRadioButton() {
        pageManager.openWebFormPage("https://bonigarcia.dev/selenium-webdriver-java/web-form.html");

        WebFormPage webFormPage = pageManager.getWebFormPage();
        webFormPage.selectRadioButton();
        webFormPage.verifyRadioButtonSelected();
    }

    @Test
    public void testSubmitForm() throws InterruptedException {
        pageManager.openWebFormPage("https://bonigarcia.dev/selenium-webdriver-java/web-form.html");

        WebFormPage webFormPage = pageManager.getWebFormPage();
        webFormPage.setTextInput("Test");
        webFormPage.submitForm();

        Thread.sleep(2000);

        String pageSource = webFormPage.driver.getPageSource();
        assertTrue(pageSource.contains("Submitted") ||
                        webFormPage.driver.getCurrentUrl().contains("submitted"),
                "Форма не была успешно отправлена");
    }
}