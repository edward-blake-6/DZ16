package com.example.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class WebFormPage extends BasePage {

    @FindBy(id = "my-text-id")
    private WebElement textInput;

    @FindBy(name = "my-password")
    private WebElement passwordInput;

    @FindBy(name = "my-textarea")
    private WebElement textarea;

    @FindBy(name = "my-disabled")
    private WebElement disabledInput;

    @FindBy(name = "my-readonly")
    private WebElement readonlyInput;

    @FindBy(name = "my-select")
    private WebElement selectElement;

    @FindBy(name = "my-datalist")
    private WebElement datalistInput;

    @FindBy(name = "my-file")
    private WebElement fileInput;

    @FindBy(id = "my-check-2")
    private WebElement checkbox;

    @FindBy(id = "my-radio-2")
    private WebElement radioButton;

    @FindBy(xpath = "//button[text()='Submit']")
    private WebElement submitButton;

    public WebFormPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageLoaded() {
        return textInput.isDisplayed() && submitButton.isDisplayed();
    }

    public void setTextInput(String text) {
        textInput.clear();
        textInput.sendKeys(text);
    }

    public String getTextInputValue() {
        return textInput.getAttribute("value");
    }

    public void setPassword(String password) {
        passwordInput.clear();
        passwordInput.sendKeys(password);
    }

    public String getPasswordType() {
        return passwordInput.getAttribute("type");
    }

    public void setTextarea(String text) {
        textarea.clear();
        textarea.sendKeys(text);
    }

    public String getTextareaValue() {
        return textarea.getAttribute("value");
    }

    public boolean isDisabledInputEnabled() {
        return disabledInput.isEnabled();
    }

    public String getDisabledInputPlaceholder() {
        return disabledInput.getAttribute("placeholder");
    }

    public boolean isReadonlyInputEditable() {
        return readonlyInput.getAttribute("readonly") == null;
    }

    public String getReadonlyInputValue() {
        return readonlyInput.getAttribute("value");
    }

    public void selectDropdownOptionByValue(String value) {
        Select dropdown = new Select(selectElement);
        dropdown.selectByValue(value);
    }

    public String getSelectedDropdownOption() {
        Select dropdown = new Select(selectElement);
        return dropdown.getFirstSelectedOption().getText();
    }

    public void setDatalistValue(String value) {
        datalistInput.clear();
        datalistInput.sendKeys(value);
    }

    public String getDatalistValue() {
        return datalistInput.getAttribute("value");
    }

    public void uploadFile(String filePath) {
        fileInput.sendKeys(filePath);
    }

    public boolean isFileSelected() {
        return !fileInput.getAttribute("value").isEmpty();
    }

    public void toggleCheckbox() {
        checkbox.click();
    }

    public boolean isCheckboxSelected() {
        return checkbox.isSelected();
    }

    public void selectRadioButton() {
        radioButton.click();
    }

    public boolean isRadioButtonSelected() {
        return radioButton.isSelected();
    }

    public void submitForm() {
        submitButton.click();
    }

    public void verifyTextInput(String expectedText) {
        assertEquals(expectedText, getTextInputValue(),
                "Текст в поле не соответствует ожидаемому");
    }

    public void verifyPasswordField() {
        assertEquals("password", getPasswordType(),
                "Поле должно быть типа password");
    }

    public void verifyTextarea(String expectedText) {
        assertEquals(expectedText, getTextareaValue(),
                "Текст в textarea не соответствует ожидаемому");
    }

    public void verifyDisabledInput() {
        assertFalse(isDisabledInputEnabled(),
                "Поле должно быть disabled");
        assertEquals("Disabled input", getDisabledInputPlaceholder(),
                "Placeholder не соответствует ожидаемому");
    }

    public void verifyReadonlyInput() {
        assertFalse(isReadonlyInputEditable(),
                "Поле должно быть readonly");
        assertEquals("Readonly input", getReadonlyInputValue(),
                "Значение поля не соответствует ожидаемому");
    }

    public void verifyDropdownSelection(String expectedOption) {
        assertEquals(expectedOption, getSelectedDropdownOption(),
                "Выбранная опция в dropdown не соответствует ожидаемой");
    }

    public void verifyDatalistValue(String expectedValue) {
        assertEquals(expectedValue, getDatalistValue(),
                "Значение в datalist не соответствует ожидаемому");
    }

    public void verifyFileUpload() {
        assertTrue(isFileSelected(),
                "Файл не был выбран");
    }

    public void verifyCheckboxToggled(boolean initialState) {
        assertNotEquals(initialState, isCheckboxSelected(),
                "Состояние чекбокса не изменилось после клика");
    }

    public void verifyRadioButtonSelected() {
        assertTrue(isRadioButtonSelected(),
                "Radio button должен быть выбран");
    }
}