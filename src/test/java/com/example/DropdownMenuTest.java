package com.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DropdownMenuTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private Actions actions;
    private final String TEST_PAGE_URL = "https://bonigarcia.dev/selenium-webdriver-java/dropdown-menu.html";

    @BeforeEach
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        actions = new Actions(driver);
        driver.get(TEST_PAGE_URL);
    }

    @AfterEach
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void clickContextMenuItem(String contextMenuId, String itemText) {
        WebElement contextMenu = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id(contextMenuId))
        );

        WebElement menuItem = contextMenu.findElement(
                By.xpath(".//a[contains(@class, 'dropdown-item') and text()='" + itemText + "']")
        );
        wait.until(ExpectedConditions.elementToBeClickable(menuItem)).click();
    }


    @Test
    @Order(1)
    public void testLeftClickMenu() {
        WebElement leftClickButton = driver.findElement(By.id("my-dropdown-1"));
        leftClickButton.click();
        clickContextMenuItem("context-menu-1", "Action");
    }

    @Test
    @Order(2)
    public void testRightClickMenu() {
        WebElement rightClickButton = driver.findElement(By.id("my-dropdown-2"));
        actions.contextClick(rightClickButton).perform();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        clickContextMenuItem("context-menu-2", "Another action");
    }

    @Test
    @Order(3)
    public void testDoubleClickMenu() {
        WebElement doubleClickButton = driver.findElement(By.id("my-dropdown-3"));
        actions.doubleClick(doubleClickButton).perform();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        clickContextMenuItem("context-menu-3", "Something else here");
    }
}