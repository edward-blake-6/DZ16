package com.example.tests;

import com.example.driver.DriverFactory;
import com.example.manager.PageManager;
import com.example.pages.LoginPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LoginPageTest {

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
    public void testWrongUsername() throws InterruptedException {
        pageManager.openLoginPage("https://bonigarcia.dev/selenium-webdriver-java/login-form.html");

        LoginPage loginPage = pageManager.getLoginPage();
        loginPage.enterUsername("qwe");
        loginPage.submitForm();

        Thread.sleep(500);
        loginPage.verifyErrorMessage("Invalid credentials");
    }

    @Test
    public void testWrongPassword() throws InterruptedException {
        pageManager.openLoginPage("https://bonigarcia.dev/selenium-webdriver-java/login-form.html");

        LoginPage loginPage = pageManager.getLoginPage();
        loginPage.enterPassword("ewq");
        loginPage.submitForm();

        Thread.sleep(500);
        loginPage.verifyErrorMessage("Invalid credentials");
    }

    @Test
    public void testAdminCredentialsShowError() throws InterruptedException {
        pageManager.openLoginPage("https://bonigarcia.dev/selenium-webdriver-java/login-form.html");

        LoginPage loginPage = pageManager.getLoginPage();
        loginPage.loginWithCredentials("admin", "password");

        Thread.sleep(500);
        loginPage.verifyErrorMessage("Invalid credentials");
    }
}