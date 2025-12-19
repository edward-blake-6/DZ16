package com.example.manager;

import com.example.pages.*;
import org.openqa.selenium.WebDriver;

public class PageManager {

    private WebDriver driver;
    private MainPage mainPage;
    private LoginPage loginPage;
    private WebFormPage webFormPage;

    public PageManager(WebDriver driver) {
        this.driver = driver;
    }

    public MainPage getMainPage() {
        if (mainPage == null) {
            mainPage = new MainPage(driver);
        }
        return mainPage;
    }

    public LoginPage getLoginPage() {
        if (loginPage == null) {
            loginPage = new LoginPage(driver);
        }
        return loginPage;
    }

    public WebFormPage getWebFormPage() {
        if (webFormPage == null) {
            webFormPage = new WebFormPage(driver);
        }
        return webFormPage;
    }

    public void openMainPage(String url) {
        driver.get(url);
        getMainPage();
    }

    public void openLoginPage(String url) {
        driver.get(url);
        getLoginPage();
    }

    public void openWebFormPage(String url) {
        driver.get(url);
        getWebFormPage();
    }

    public void quit() {
        if (driver != null) {
            driver.quit();
        }
    }
}