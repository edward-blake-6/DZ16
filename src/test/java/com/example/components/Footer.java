package com.example.components;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class Footer {
    private WebDriver driver;

    @FindBy(className = "footer")
    private WebElement footerElement;

    @FindBy(xpath = "//footer//p")
    private WebElement copyrightText;

    public Footer(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isDisplayed() {
        return footerElement.isDisplayed();
    }

    public String getCopyrightText() {
        return copyrightText.getText();
    }
}