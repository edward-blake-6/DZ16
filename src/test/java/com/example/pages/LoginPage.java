package com.example.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginPage extends BasePage {

    @FindBy(id = "username")
    private WebElement usernameInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(css = ".btn.btn-outline-primary.mt-2")
    private WebElement submitButton;

    @FindBy(id = "invalid")
    private WebElement errorMessage;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageLoaded() {
        return usernameInput.isDisplayed() && passwordInput.isDisplayed();
    }

    public void enterUsername(String username) {
        usernameInput.clear();
        usernameInput.sendKeys(username);
    }

    public void enterPassword(String password) {
        passwordInput.clear();
        passwordInput.sendKeys(password);
    }

    public void submitForm() {
        submitButton.click();
    }

    public String getErrorMessage() {
        return errorMessage.getText();
    }

    public boolean isErrorDisplayed() {
        return errorMessage.isDisplayed();
    }

    public void loginWithCredentials(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        submitForm();
    }

    public void verifyErrorMessage(String expectedError) {
        assertTrue(getErrorMessage().contains(expectedError),
                "Ожидалась ошибка: " + expectedError + ", но получено: " + getErrorMessage());
    }
}