package com.example.tests;

import com.example.driver.DriverFactory;
import com.example.listeners.TestListener;
import com.example.manager.PageManager;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(TestListener.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PageObjectTestsWithScreenshots {

    private PageManager pageManager;

    @BeforeEach
    public void setup() {
        pageManager = new PageManager(DriverFactory.createDefaultDriver());
    }

    @AfterEach
    public void teardown() {
        pageManager.quit();
    }

    @Test
    @Story("Логин с неверными данными")
    @Description("Тест проверяет отображение ошибки при неверном логине")
    @Severity(SeverityLevel.CRITICAL)
    public void testLoginPage_WrongUsername() throws InterruptedException {
        pageManager.openLoginPage("https://bonigarcia.dev/selenium-webdriver-java/login-form.html");

        pageManager.getLoginPage().enterUsername("wronguser");
        pageManager.getLoginPage().submitForm();
        Thread.sleep(500);

        assertTrue(pageManager.getLoginPage().getErrorMessage().contains("Invalid credentials!"),
                "Ожидалась другая ошибка");
    }

    @Test
    @Story("Валидация веб-формы")
    @Description("Тест проверяет обязательные поля формы")
    @Severity(SeverityLevel.NORMAL)
    public void testWebForm_Validation() {
        pageManager.openWebFormPage("https://bonigarcia.dev/selenium-webdriver-java/web-form.html");

        pageManager.getWebFormPage().setTextInput("Test Data");
        assertTrue(pageManager.getWebFormPage().getTextInputValue().equals("Test Data"));
    }
}