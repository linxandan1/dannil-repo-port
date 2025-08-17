package org.yeehy.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AddWidgetPage extends BasePage {

    // Step 1: choose widget
    private By launchStatisticsChart = By.xpath("//div[contains(@class,'widgetTypeItem__widget-type-item-name') and text()='Launch statistics chart']");
    private By nextStepButton1 = By.xpath("//span[text()='Next step']");

    // Step 2: select filter
    private By demoFilterOption = By.xpath("//span[contains(@class,'filterName__name') and text()='DEMO_FILTER']");
    private By nextStepButton2 = By.xpath("//span[text()='Next step']");

    // Step 3: save widget
    private By widgetNameField = By.cssSelector("input[placeholder*='name']");
    private By addButton = By.xpath("//button[contains(@class,'bigButton__color-booger') and text()='Add']");

    public AddWidgetPage(WebDriver driver) {
        super(driver);
    }

    public DashboardPage createWidget(String widgetName) {
        log.info("=== Step: CREATING WIDGET '{}' ===", widgetName);

        log.info("Step 1: Selecting 'Launch statistics chart'");
        jsClick(launchStatisticsChart);
        jsClick(nextStepButton1);

        log.info("Step 2: Selecting filter 'DEMO_FILTER'");
        jsClick(demoFilterOption);
        jsClick(nextStepButton2);

        log.info("Step 3: Entering name and clicking Add");
        jsType(widgetNameField, widgetName);
        jsClick(addButton);

        log.info("Widget '{}' created, returning to DashboardPage", widgetName);
        return new DashboardPage(driver, false);
    }
}