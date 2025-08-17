package org.yeehy.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

public abstract class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected final Logger log;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        this.log = LoggerFactory.getLogger(this.getClass());
    }

    protected void click(By locator) {
        log.info("CLICK: Waiting for element to be clickable: {}", locator);
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
        log.info("CLICK: Clicking on {}", locator);
        el.click();
    }

    protected void jsClick(By locator) {
        log.info("JS_CLICK: Waiting for element to be clickable and clicking via JS: {}", locator);
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
    }

    protected void type(By locator, String text) {
        log.info("TYPE: Waiting for element visibility {} and entering: '{}'", locator, getTextPreview(text));
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        element.sendKeys(text);
    }

    protected void jsType(By locator, String text) {
        log.info("JS_TYPE: Waiting for element visibility {} and entering via JS: '{}'", locator, getTextPreview(text));  WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

        String script = """
            arguments[0].focus();
            arguments[0].value = '';
            arguments[0].value = arguments[1];
            arguments[0].dispatchEvent(new Event('input', { bubbles: true }));
            arguments[0].dispatchEvent(new Event('change', { bubbles: true }));
            arguments[0].blur();
            arguments[0].dispatchEvent(new Event('blur', { bubbles: true }));
        """;
        ((JavascriptExecutor) driver).executeScript(script, el, text);

        String actualValue = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].value;", el);
        log.info("After JS input, field value: '{}'", actualValue);
    }

    private String getTextPreview(String text) {
        return text == null ? "null" : (text.length() > 120 ? text.substring(0, 120) + "…" : text);
    }

    protected void logPageState(String note) {
        try {
            String url = driver.getCurrentUrl();
            String title = driver.getTitle();
            String src = driver.getPageSource();
            int bytes = src == null ? 0 : src.getBytes(StandardCharsets.UTF_8).length;
            log.info("PAGE STATE ({}): url='{}', title='{}', pageSource≈{} KB", note, url, title, bytes / 1024);
        } catch (Exception e) {
            log.warn("Failed to retrieve page state: {}", e.toString());
        }
    }
}
