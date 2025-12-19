package com.example.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class MainPage extends BasePage {

    @FindBy(tagName = "h1")
    private WebElement title;

    @FindBy(linkText = "Practice site")
    private WebElement practiceSiteLink;

    public MainPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageLoaded() {
        return title.isDisplayed() && title.getText().contains("Hands-On Selenium");
    }

    public String getTitleText() {
        return title.getText();
    }

    public void navigateToPracticeSite() {
        practiceSiteLink.click();
    }
}