package org.yeehy.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

public class DashboardPage extends BasePage {

    private By addWidgetButton = By.xpath("//button[translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'add new widget']");

    public DashboardPage(WebDriver driver, boolean navigateToList) {
        super(driver);
        if (navigateToList) {
            navigateToDashboardList();
        }
    }

    private void navigateToDashboardList() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        By dashboardsButton = By.xpath("//a[contains(@class, 'sidebarButton__nav-link') and contains(@href, 'dashboard')]");

        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(dashboardsButton));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        log.info("CLICK: Clicking on Dashboards");

        wait.until(ExpectedConditions.urlContains("dashboard"));
        log.info("URL changed, we are on the Dashboard page");
    }

    public void openDashboardByName(String name) {
        By dashboardLinksLocator = By.xpath("//a[contains(@class, 'dashboardTable__name')]");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        // Wait for all dashboards to be present
        var dashboards = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(dashboardLinksLocator));

        log.info("Found {} dashboard entries, searching for exact text match (case-insensitive)", dashboards.size());

        WebElement dashboard = dashboards.stream()
                .filter(dash -> dash.getText().trim().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Dashboard '" + name + "' not found."));

        // Click via JS (more reliable for SPA)
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dashboard);
        log.info("CLICK (JS): Clicking on dashboard entry: '{}'", name);

        // Wait for URL to update to dashboard/{id}
        wait.until(ExpectedConditions.urlContains("dashboard"));
        log.info("URL changed, we are inside dashboard '{}'", name);
    }

    public AddWidgetPage clickAddWidget(String dashboardName) {
        log.info("=== Step: ADDING WIDGET TO DASHBOARD '{}' ===", dashboardName);
        openDashboardByName(dashboardName);

        WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(20));
        log.info("Waiting for Add new widget button: {}", addWidgetButton);
        WebElement addBtn = w.until(ExpectedConditions.elementToBeClickable(addWidgetButton));

        log.info("Clicking on Add new widget via JS");
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addBtn);
        log.info("Clicking on Add new widget via Actions");
        new Actions(driver).moveToElement(addBtn).click().perform();

        return new AddWidgetPage(driver);
    }

    public boolean isWidgetPresent(String widgetName) {
        log.info("=== Step: CHECKING PRESENCE OF WIDGET '{}' ===", widgetName);
        By[] widgetLocators = {
                By.xpath("//div[contains(@class,'widgetHeader__widget-name-block') and contains(text(),'" + widgetName + "')]"),
                By.xpath("//div[contains(@class,'widget-name-block') and contains(text(),'" + widgetName + "')]"),
                By.xpath("//*[contains(text(),'" + widgetName + "')]")
        };
        WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(12));
        for (By locator : widgetLocators) {
            log.info("Searching for widget by locator: {}", locator);
            try {
                w.until(ExpectedConditions.visibilityOfElementLocated(locator));
                log.info("Widget found by locator: {}", locator);
                return true;
            } catch (TimeoutException ignored) {
                log.warn("Widget not found by locator: {}", locator);
            }
        }
        log.warn("Widget '{}' not found by any locator", widgetName);
        logPageState("widget not found");
        return false;
    }
}