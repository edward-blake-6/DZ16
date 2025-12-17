package com.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Chapter4 {

    @Test
    public void testInfiniteScrollBasic() throws InterruptedException {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();

        try {
            driver.get("https://bonigarcia.dev/selenium-webdriver-java/infinite-scroll.html");

            List<WebElement> paragraphs = driver.findElements(By.tagName("p"));
            int initialCount = paragraphs.size();
            for (int i = 0; i < 3; i++) {
                ((JavascriptExecutor) driver)
                        .executeScript("window.scrollTo(0, document.body.scrollHeight);");
                Thread.sleep(1500);
            }

            paragraphs = driver.findElements(By.tagName("p"));
            int finalCount = paragraphs.size();
            System.out.println("Конечное количество параграфов: " + finalCount);
            assertTrue(finalCount > initialCount,
                    "Бесконечный скролл не работает. Начало: " + initialCount +
                            ", конец: " + finalCount);

            System.out.println("Тест пройден! Добавилось " +
                    (finalCount - initialCount) + " новых параграфов");

        } finally {
            driver.quit();
        }
    }

    @Test
    public void testShadowDomContent() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();

        try {
            driver.get("https://bonigarcia.dev/selenium-webdriver-java/shadow-dom.html");
            WebElement hostElement = driver.findElement(By.id("content"));
            JavascriptExecutor js = (JavascriptExecutor) driver;
            WebElement shadowHeading = (WebElement) js.executeScript(
                    "return arguments[0].shadowRoot.querySelector('h1')",
                    hostElement
            );
            if (shadowHeading == null) {
                shadowHeading = (WebElement) js.executeScript(
                        "const shadow = arguments[0].shadowRoot; " +
                                "const elements = shadow.querySelectorAll('*'); " +
                                "for (let el of elements) { " +
                                "  if (el.textContent.includes('Shadow DOM')) return el; " +
                                "} " +
                                "return null;",
                        hostElement
                );
            }
            assertNotNull(shadowHeading, "Не найден элемент внутри Shadow DOM");
            String actualText = shadowHeading.getText();
            System.out.println("Найденный текст: " + actualText);
            if (actualText.contains("Shadow DOM")) {
                System.out.println("Текст содержит 'Shadow DOM' - проверка пройдена");
            } else {
                assertEquals("Hello Shadow DOM", actualText,
                        "Текст не соответствует ожидаемому");
            }
            System.out.println("Тег элемента: " + shadowHeading.getTagName());
            System.out.println("Видим ли элемент: " + shadowHeading.isDisplayed());

        } finally {
            driver.quit();
        }
    }

    @Test
    public void testDisplayCookiesButton() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            driver.get("https://bonigarcia.dev/selenium-webdriver-java/cookies.html");
            System.out.println("Исходные cookies:");
            driver.manage().getCookies().forEach(cookie ->
                    System.out.println(cookie.getName() + " = " + cookie.getValue())
            );

            WebElement displayButton = driver.findElement(By.id("refresh-cookies"));
            displayButton.click();

            wait.until(d -> {
                String pageText = d.findElement(By.tagName("body")).getText();
                return pageText.contains("username=") && pageText.contains("date=");
            });
            String pageText = driver.findElement(By.tagName("body")).getText();
            System.out.println("\nТекст после нажатия кнопки:\n" + pageText);

            boolean hasUsername = pageText.contains("username=John Doe");
            boolean hasDate = pageText.contains("date=10/07/2018");

            if (hasUsername && hasDate) {
                System.out.println("Все проверки пройдены!");
            } else {
                System.out.println("\nДетальная проверка:");

                if (pageText.contains("username=")) {
                    String[] lines = pageText.split("\n");
                    for (String line : lines) {
                        if (line.contains("username=")) {
                            System.out.println("Найдено: " + line.trim());
                            assertTrue(line.contains("John"),
                                    "Username должен содержать 'John'");
                        }
                        if (line.contains("date=")) {
                            System.out.println("Найдено: " + line.trim());
                            assertTrue(line.contains("2018"),
                                    "Date должен содержать '2018'");
                        }
                    }
                } else {
                    throw new AssertionError("Текст 'username=' не найден на странице");
                }
            }

            assertTrue(pageText.contains("username="),
                    "На странице должен отображаться username");
            assertTrue(pageText.contains("date="),
                    "На странице должен отображаться date");

        } finally {
            driver.quit();
        }
    }

    @Test
    public void testScrollIframeAndCheckLastSentence() throws InterruptedException {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        try {
            driver.get("https://bonigarcia.dev/selenium-webdriver-java/iframes.html");
            WebElement iframe = driver.findElement(By.id("my-iframe"));
            driver.switchTo().frame(iframe);
            WebElement body = driver.findElement(By.tagName("body"));
            JavascriptExecutor js = (JavascriptExecutor) driver;

            String initialText = body.getText();
            System.out.println("Длина текста до скролла: " + initialText.length() + " символов");

            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            Thread.sleep(1500);
            String textAfterScroll = body.getText();
            String expectedText = "Non consequat fringilla mauris mus tortor commodo cum, " +
                    "quis ultrices lobortis curabitur ad pulvinar massa imperdiet, " +
                    "primis quisque nisi ultricies purus lacus.";

            boolean containsFullSentence = textAfterScroll.contains(expectedText);
            boolean containsPart1 = textAfterScroll.contains("Non consequat fringilla");
            boolean containsPart2 = textAfterScroll.contains("primis quisque nisi");
            boolean containsPart3 = textAfterScroll.contains("lacus");

            System.out.println("Результаты проверки:");
            System.out.println("- Полное предложение: " + containsFullSentence);
            System.out.println("- Часть 'Non consequat fringilla': " + containsPart1);
            System.out.println("- Часть 'primis quisque nisi': " + containsPart2);
            System.out.println("- Часть 'lacus': " + containsPart3);

            if (!containsFullSentence) {
                int length = textAfterScroll.length();
                String lastPart = textAfterScroll.substring(Math.max(0, length - 200), length);
                System.out.println("\nПоследние 200 символов текста:");
                System.out.println("..." + lastPart);
            }
            assertTrue(
                    containsFullSentence ||
                            (containsPart1 && containsPart2 && containsPart3),
                    "После скролла iframe должно отображаться последнее предложение"
            );

            System.out.println("Тест пройден - последнее предложение найдено!");

            driver.switchTo().defaultContent();

        } finally {
            driver.quit();
        }
    }

    @Test
    void test1_Alert() {
        WebDriver driver = new ChromeDriver();
        driver.get("https://bonigarcia.dev/selenium-webdriver-java/dialog-boxes.html");
        driver.findElement(By.id("my-alert")).click();
        driver.switchTo().alert().accept();
        driver.quit();
    }

    @Test
    void test2_Confirm() {
        WebDriver driver = new ChromeDriver();
        driver.get("https://bonigarcia.dev/selenium-webdriver-java/dialog-boxes.html");
        driver.findElement(By.id("my-confirm")).click();
        driver.switchTo().alert().accept();
        assertTrue(driver.getPageSource().contains("You chose: true"));
        driver.quit();
    }

    @Test
    void test3_Prompt() {
        WebDriver driver = new ChromeDriver();
        driver.get("https://bonigarcia.dev/selenium-webdriver-java/dialog-boxes.html");
        driver.findElement(By.id("my-prompt")).click();
        Alert prompt = driver.switchTo().alert();
        prompt.sendKeys("1");
        prompt.accept();
        assertTrue(driver.getPageSource().contains("You typed: 1"));
        driver.quit();
    }

    @Test
    void test4_Modal() throws InterruptedException {
        WebDriver driver = new ChromeDriver();
        driver.get("https://bonigarcia.dev/selenium-webdriver-java/dialog-boxes.html");
        driver.findElement(By.id("my-modal")).click();
        Thread.sleep(500);
        driver.findElement(By.xpath("//button[text()='Close']")).click();
        Thread.sleep(500);
        assertTrue(driver.getPageSource().contains("You chose: Close"));
        driver.quit();
    }

    @Test
    void test_local() throws Exception {
        WebDriver driver = new ChromeDriver();
        driver.get("https://bonigarcia.dev/selenium-webdriver-java/web-storage.html");
        driver.findElement(By.id("display-local")).click();
        Thread.sleep(300);
        assert driver.findElement(By.tagName("body")).getText().contains("{}");
        driver.quit();
    }

    @Test
    void test_session() throws Exception {
        WebDriver driver = new ChromeDriver();
        driver.get("https://bonigarcia.dev/selenium-webdriver-java/web-storage.html");
        driver.findElement(By.id("display-session")).click();
        Thread.sleep(300);
        String text = driver.findElement(By.tagName("body")).getText();
        assert text.contains("Doe") && text.contains("John");
        driver.quit();
    }
}