package com.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginForm {

    @Test
    void test1_WrongUsername() throws Exception {
        WebDriver driver = new ChromeDriver();
        driver.get("https://bonigarcia.dev/selenium-webdriver-java/login-form.html");
        driver.findElement(By.id("username")).sendKeys("qwe");
        driver.findElement(By.cssSelector(".btn.btn-outline-primary.mt-2")).click();
        Thread.sleep(500);
        assertTrue(driver.findElement(By.id("invalid")).getText().contains("Invalid credentials"));
        driver.quit();
    }

    @Test
    void test2_WrongPassword() throws Exception {
        WebDriver driver = new ChromeDriver();
        driver.get("https://bonigarcia.dev/selenium-webdriver-java/login-form.html");
        driver.findElement(By.id("password")).sendKeys("ewq");
        driver.findElement(By.cssSelector(".btn.btn-outline-primary.mt-2")).click();
        Thread.sleep(500);
        assertTrue(driver.findElement(By.id("invalid")).getText().contains("Invalid credentials"));
        driver.quit();
    }

    @Test
    void test3_AdminPasswordError() throws Exception {
        WebDriver driver = new ChromeDriver();
        driver.get("https://bonigarcia.dev/selenium-webdriver-java/login-form.html");
        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("password")).sendKeys("password");
        driver.findElement(By.cssSelector(".btn.btn-outline-primary.mt-2")).click();
        Thread.sleep(500);
        assertTrue(driver.findElement(By.id("invalid")).getText().contains("Invalid credentials"));
        driver.quit();
    }

    static {
        WebDriverManager.chromedriver().setup();
    }
}