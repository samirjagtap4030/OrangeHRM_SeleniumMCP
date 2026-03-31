package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class DashboardPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators
    private final By dashboardHeading = By.cssSelector(".oxd-topbar-header-breadcrumb h6");
    private final By userDropdown     = By.cssSelector(".oxd-userdropdown-tab");
    private final By logoutMenuItem   = By.xpath("//a[text()='Logout']");

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public boolean isDashboardLoaded() {
        String heading = wait.until(
            ExpectedConditions.visibilityOfElementLocated(dashboardHeading)
        ).getText();
        return heading.equalsIgnoreCase("Dashboard");
    }

    public String getDashboardTitle() {
        return wait.until(
            ExpectedConditions.visibilityOfElementLocated(dashboardHeading)
        ).getText();
    }

    public LoginPage logout() {
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(userDropdown));
        dropdown.click();
        WebElement logoutLink = wait.until(ExpectedConditions.elementToBeClickable(logoutMenuItem));
        logoutLink.click();
        return new LoginPage(driver);
    }
}
