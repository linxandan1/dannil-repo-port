package ui;

import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeehy.config.ConfigReader;
import org.yeehy.config.DriverFactory;
import org.yeehy.pages.DashboardPage;
import org.yeehy.pages.LoginPage;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AddWidgetTest {

    private static final Logger log = LoggerFactory.getLogger(AddWidgetTest.class);
    private WebDriver driver;

    @BeforeAll
    static void beforeAll() {
        log.info("=== START UI TESTS ===");
    }

    @BeforeEach
    public void setUp() {
        log.info("Initializing driver and opening baseUrl");
        driver = DriverFactory.getDriver();
        String baseUrl = ConfigReader.getProperty("baseUrl");
        log.info("Opening: {}", baseUrl);
        driver.get(baseUrl);
    }

    @Test
    @Order(1)
    public void testAddWidget() {
        String widgetName = "TaskProgress_" + System.currentTimeMillis();
        log.info("Starting testAddWidget; widgetName='{}'", widgetName);

        DashboardPage dashboard = new LoginPage(driver)
                .login(ConfigReader.getProperty("login"), ConfigReader.getProperty("password"))
                .clickAddWidget("DEMO DASHBOARD")
                .createWidget(widgetName);

        boolean present = dashboard.isWidgetPresent(widgetName);
        log.info("Result of checking widget '{}': {}", widgetName, present);
        Assertions.assertTrue(present, "Widget not found on Dashboard!");
    }

    @AfterEach
    public void tearDown() {
        log.info("Test completed, closing driver");
        DriverFactory.quitDriver();
    }

    @AfterAll
    static void afterAll() {
        log.info("=== END UI TESTS ===");
    }
}