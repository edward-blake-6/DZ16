package com.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PaginationTest {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static final String BASE_URL = "https://bonigarcia.dev/selenium-webdriver-java/navigation1.html";

    @BeforeAll
    public static void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void goToFirstPage() {
        driver.get(BASE_URL);
    }

    private void clickButtonByText(String buttonText) {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("a.page-link")));

        java.util.List<WebElement> links = driver.findElements(By.cssSelector("a.page-link"));
        for (WebElement link : links) {
            if (link.getText().equals(buttonText)) {
                // Проверяем, не disabled ли родительский элемент
                WebElement parent = link.findElement(By.xpath(".."));
                if (!parent.getAttribute("class").contains("disabled")) {
                    link.click();
                    waitForPage();
                    return;
                }
            }
        }
        throw new NoSuchElementException("Кнопка с текстом '" + buttonText + "' не найдена или disabled");
    }

    private void waitForPage() {
        wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete'"));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String getPageText() {
        return driver.findElement(By.tagName("body")).getText();
    }

    @Test
    public void test1_ClickPage3() {
        clickButtonByText("3");
        String pageText = getPageText();
        assertTrue(pageText.contains("Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."));
    }

    @Test
    public void test2_ClickNext() {
        clickButtonByText("Next");
        String pageText = getPageText();
        assertTrue(pageText.contains("Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur."));
    }

    @Test
    public void test3_ClickPage2ThenPrevious() {
        clickButtonByText("2");
        assertTrue(getPageText().contains("Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur."));

        clickButtonByText("Previous");
        assertTrue(getPageText().contains("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."));
    }

    @Test
    public void test4_ClickPage3ThenPage1() {
        clickButtonByText("3");
        assertTrue(getPageText().contains("Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."));

        clickButtonByText("1");
        assertTrue(getPageText().contains("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."));
    }
}