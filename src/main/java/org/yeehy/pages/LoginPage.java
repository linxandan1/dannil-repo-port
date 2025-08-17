package org.yeehy.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage extends BasePage {
    private By usernameField = By.cssSelector("input[name='login']");
    private By passwordField = By.cssSelector("input[name='password']");
    private By loginButton = By.cssSelector("button[type='submit']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public DashboardPage login(String username, String password) {
        log.info("=== Step: LOGIN ===");
        logPageState("before login");

        type(usernameField, username);
        type(passwordField, password);
        click(loginButton);

        // Wait until we leave the login page
        WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(20));
        log.info("Waiting for URL to change from '#login' to main UI");
        w.until(ExpectedConditions.not(ExpectedConditions.urlContains("#login")));

        // Additionally, wait for Dashboards button as confirmation of successful login
        By dashboardsButton = By.xpath("//a[contains(@class, 'sidebarButton__nav-link') and contains(@href, 'dashboard')]");
        w.until(ExpectedConditions.visibilityOfElementLocated(dashboardsButton));

        logPageState("after login");
        return new DashboardPage(driver, true);
    }
}