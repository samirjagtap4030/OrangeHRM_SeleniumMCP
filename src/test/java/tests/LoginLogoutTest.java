package tests;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import pages.DashboardPage;
import pages.LoginPage;
import utils.DriverManager;

public class LoginLogoutTest {

    private static final String BASE_URL  = "https://opensource-demo.orangehrmlive.com";
    private static final String USERNAME  = "Admin";
    private static final String PASSWORD  = "admin123";

    private WebDriver driver;
    private LoginPage loginPage;

    @BeforeMethod
    public void setUp() {
        driver    = DriverManager.getDriver();
        loginPage = new LoginPage(driver);
        loginPage.navigateTo(BASE_URL);
    }

    @Test(description = "Verify successful login navigates to Dashboard")
    public void testSuccessfulLogin() {
        DashboardPage dashboardPage = loginPage.login(USERNAME, PASSWORD);

        Assert.assertTrue(dashboardPage.isDashboardLoaded(),
            "Dashboard should be displayed after successful login");
        Assert.assertEquals(dashboardPage.getDashboardTitle(), "Dashboard",
            "Page title should be 'Dashboard'");
    }

    @Test(description = "Verify logout redirects back to Login page")
    public void testLogout() {
        DashboardPage dashboardPage = loginPage.login(USERNAME, PASSWORD);

        Assert.assertTrue(dashboardPage.isDashboardLoaded(),
            "Dashboard should be loaded before logout");

        LoginPage returnedLoginPage = dashboardPage.logout();

        Assert.assertTrue(returnedLoginPage.isLoginPageDisplayed(),
            "Login page should be displayed after logout");
    }

    @Test(description = "Verify full login and logout flow end-to-end")
    public void testLoginAndLogoutFlow() {
        // Step 1: Login
        DashboardPage dashboardPage = loginPage.login(USERNAME, PASSWORD);
        Assert.assertTrue(dashboardPage.isDashboardLoaded(),
            "Dashboard should be visible after login");

        // Step 2: Logout
        LoginPage returnedLoginPage = dashboardPage.logout();
        Assert.assertTrue(returnedLoginPage.isLoginPageDisplayed(),
            "Login page should be visible after logout");
    }

    @AfterMethod
    public void tearDown() {
        DriverManager.quitDriver();
    }
}
