package api;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeehy.config.ConfigReader;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class DashboardApiTest {

    private static final Logger logger = LoggerFactory.getLogger(DashboardApiTest.class);
    private static final String API_KEY = ConfigReader.getProperty("apiKey");
    private static final String BASE_URL = ConfigReader.getProperty("baseUrl") + "/api/v1";
    private static final String PROJECT_NAME = ConfigReader.getProperty("projectName");
    private long createdDashboardId = -1;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = BASE_URL;
        logger.info("SLF4J working");
    }

    @Test
    @Description("Create new Dashboard via API")
    public void testCreateDashboard() {
        String dashboardName = "Test Dashboard " + System.currentTimeMillis();
        String body = "{\"name\": \"" + dashboardName + "\", \"description\": \"Test\"}";

        Response response = createDashboard(body);
        logger.info("Response Status: {}", response.getStatusCode());
        logger.info("Response Headers: {}", response.getHeaders());
        logger.info("Response Body: {}", response.getBody().asString());
        assertEquals(201, response.getStatusCode(), "Expected status code 201");
        createdDashboardId = response.jsonPath().getLong("id");
        assertTrue(createdDashboardId > 0, "Dashboard ID should be positive");
        assertTrue(checkDashboardExists(dashboardName, createdDashboardId), "Dashboard not found in list");
    }

    @Test
    @Description("Create Dashboard with missing parameters")
    public void testCreateDashboardWithMissingParams() {
        String body = "{\"description\": \"Test\"}";

        Response response = createDashboard(body);
        logger.info("Response Status: {}", response.getStatusCode());
        logger.info("Response Headers: {}", response.getHeaders());
        logger.info("Response Body: {}", response.getBody().asString());
        assertEquals(400, response.getStatusCode(), "Expected status code 400");
        assertFalse(checkDashboardExists("NonExistent", -1), "Dashboard should not exist");
    }

    @Step("Create Dashboard")
    private Response createDashboard(String body) {
        return given()
                .header("Authorization", "Bearer " + API_KEY)
                .contentType("application/json")
                .body(body)
                .log().all()
                .post("/" + PROJECT_NAME + "/dashboard");
    }

    @Step("Check Dashboard exists")
    private boolean checkDashboardExists(String name, long dashboardId) {
        Response response = given()
                .header("Authorization", "Bearer " + API_KEY)
                .queryParam("filter.eq.name", name)
                .log().all()
                .get("/" + PROJECT_NAME + "/dashboard");
        logger.info("Response Status: {}", response.getStatusCode());
        logger.info("Response Headers: {}", response.getHeaders());
        logger.info("Response Body: {}", response.getBody().asString());
        return response.jsonPath().getList("content", Map.class).stream()
                .anyMatch(d -> {
                    Number idObj = (Number) d.get("id");
                    long id = idObj != null ? idObj.longValue() : -1;
                    String dashboardName = (String) d.get("name");
                    return id == dashboardId && (dashboardName != null && dashboardName.equals(name));
                });
    }

    @AfterEach
    public void cleanup() {
        if (createdDashboardId > 0) {
            Response response = given()
                    .header("Authorization", "Bearer " + API_KEY)
                    .log().all()
                    .delete("/" + PROJECT_NAME + "/dashboard/" + createdDashboardId);
            logger.info("Cleanup: Deleted dashboard with ID: {}, Status: {}", createdDashboardId, response.getStatusCode());
            createdDashboardId = -1;
        }
    }
}