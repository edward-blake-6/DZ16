package com.example.components;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class Header {
    private WebDriver driver;

    @FindBy(xpath = "//nav[@class='navbar']")
    private WebElement navbar;

    @FindBy(xpath = "//a[contains(text(), 'Home')]")
    private WebElement homeLink;

    public Header(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isDisplayed() {
        return navbar.isDisplayed();
    }

    public void clickHome() {
        homeLink.click();
    }
}
