package com.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class WebFormTest {

    @Test
    public void TextInput() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        try {
            driver.get("https://bonigarcia.dev/selenium-webdriver-java/web-form.html");
            WebElement element = driver.findElement(By.id("my-text-id"));
            element.sendKeys("Test");
            assertEquals("Test", element.getAttribute("value"));
        } finally {
            driver.quit();
        }
    }

    @Test
    public void PasswordInput() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        try {
            driver.get("https://bonigarcia.dev/selenium-webdriver-java/web-form.html");
            WebElement element = driver.findElement(By.name("my-password"));
            element.sendKeys("secret");
            assertEquals("password", element.getAttribute("type"));
        } finally {
            driver.quit();
        }
    }

    @Test
    public void Textarea() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        try {
            driver.get("https://bonigarcia.dev/selenium-webdriver-java/web-form.html");
            WebElement element = driver.findElement(By.name("my-textarea"));
            element.sendKeys("Text");
            assertEquals("Text", element.getAttribute("value"));
        } finally {
            driver.quit();
        }
    }

    @Test
    public void DisabledInput() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        try {
            driver.get("https://bonigarcia.dev/selenium-webdriver-java/web-form.html");
            WebElement element = driver.findElement(By.name("my-disabled"));
            assertTrue(element.getAttribute("disabled") != null);
            assertEquals("Disabled input", element.getAttribute("placeholder"));
        } finally {
            driver.quit();
        }
    }

    @Test
    public void ReadonlyInput() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        try {
            driver.get("https://bonigarcia.dev/selenium-webdriver-java/web-form.html");
            WebElement element = driver.findElement(By.name("my-readonly"));
            assertTrue(element.getAttribute("readonly") != null);
            assertEquals("Readonly input", element.getAttribute("value"));
        } finally {
            driver.quit();
        }
    }

    @Test
    public void DropdownSelect() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        try {
            driver.get("https://bonigarcia.dev/selenium-webdriver-java/web-form.html");
            Select dropdown = new Select(driver.findElement(By.name("my-select")));
            dropdown.selectByValue("1");
            assertEquals("One", dropdown.getFirstSelectedOption().getText());
        } finally {
            driver.quit();
        }
    }

    @Test
    public void DatalistInput() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        try {
            driver.get("https://bonigarcia.dev/selenium-webdriver-java/web-form.html");
            WebElement element = driver.findElement(By.name("my-datalist"));
            element.sendKeys("San Francisco");
            assertEquals("San Francisco", element.getAttribute("value"));
        } finally {
            driver.quit();
        }
    }

    @Test
    public void test8_FileInput() throws IOException {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        Path testFile = Files.createTempFile("test", ".txt");
        try {
            Files.write(testFile, "test".getBytes());
            driver.get("https://bonigarcia.dev/selenium-webdriver-java/web-form.html");
            WebElement element = driver.findElement(By.name("my-file"));
            element.sendKeys(testFile.toAbsolutePath().toString());
            assertFalse(element.getAttribute("value").isEmpty());
        } finally {
            Files.deleteIfExists(testFile);
            driver.quit();
        }
    }

    @Test
    public void test9_Checkbox() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        try {
            driver.get("https://bonigarcia.dev/selenium-webdriver-java/web-form.html");
            WebElement element = driver.findElement(By.id("my-check-2"));
            boolean initial = element.isSelected();
            element.click();
            assertNotEquals(initial, element.isSelected());
        } finally {
            driver.quit();
        }
    }

    @Test
    public void RadioButton() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        try {
            driver.get("https://bonigarcia.dev/selenium-webdriver-java/web-form.html");
            WebElement element = driver.findElement(By.id("my-radio-2"));
            element.click();
            assertTrue(element.isSelected());
        } finally {
            driver.quit();
        }
    }

    @Test
    public void SubmitForm() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        try {
            driver.get("https://bonigarcia.dev/selenium-webdriver-java/web-form.html");
            driver.findElement(By.id("my-text-id")).sendKeys("Test");
            driver.findElement(By.xpath("//button[text()='Submit']")).click();

            Thread.sleep(2000);

            assertTrue(
                    driver.getPageSource().contains("Submitted") ||
                            driver.getCurrentUrl().contains("submitted")
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            driver.quit();
        }
    }
}