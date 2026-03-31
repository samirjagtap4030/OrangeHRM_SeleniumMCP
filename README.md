# OrangeHRM_SeleniumMCP

## 1. Project Overview

| Field | Details |
|-------|---------|
| **Project Name** | ORANGEHRM_SELENIUMMCP |
| **Description** | Selenium WebDriver automation testing framework integrated with Claude Code MCP (Model Context Protocol) servers for AI-assisted test development and execution |
| **Application Under Test** | [OrangeHRM Live Demo](https://opensource-demo.orangehrmlive.com) — an open-source Human Resource Management web application |
| **Design Pattern** | Page Object Model (POM) |

---

## 2. Tech Stack

| Technology | Version |
|-----------|---------|
| Language | Java 21 |
| Selenium WebDriver | 4.18.1 |
| TestNG | 7.9.0 |
| WebDriverManager | 5.7.0 |
| Build Tool | Maven |
| Browser | Google Chrome (auto-managed) |

### Maven Dependencies (pom.xml)

```xml
<!-- Selenium WebDriver -->
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>4.18.1</version>
</dependency>

<!-- WebDriverManager (auto ChromeDriver setup) -->
<dependency>
    <groupId>io.github.bonigarcia</groupId>
    <artifactId>webdrivermanager</artifactId>
    <version>5.7.0</version>
</dependency>

<!-- TestNG -->
<dependency>
    <groupId>org.testng</groupId>
    <artifactId>testng</artifactId>
    <version>7.9.0</version>
    <scope>test</scope>
</dependency>
```

---

## 3. MCP Servers Used

MCP (Model Context Protocol) servers are configured in `.claude/settings.local.json` and allow Claude Code to interact with the browser and filesystem directly during AI-assisted development.

| MCP Server | Tools Enabled | Purpose |
|-----------|--------------|---------|
| **selenium** | `start_browser`, `navigate`, `take_screenshot`, `send_keys`, `interact`, `get_element_text` | AI-driven Selenium browser control — Claude can launch, navigate, and interact with a real browser |
| **playwright** | `browser_navigate`, `browser_snapshot`, `browser_fill_form`, `browser_click`, `browser_take_screenshot` | Alternative browser automation via Playwright for AI-assisted testing and exploration |
| **filesystem** | `directory_tree`, `read_text_file` | AI access to project files for context-aware code generation |

### MCP Configuration (`.claude/settings.local.json`)

```json
{
  "permissions": {
    "allow": [
      "mcp__selenium__start_browser",
      "mcp__selenium__navigate",
      "mcp__selenium__take_screenshot",
      "mcp__selenium__send_keys",
      "mcp__selenium__interact",
      "mcp__selenium__get_element_text",
      "mcp__playwright__browser_navigate",
      "mcp__playwright__browser_snapshot",
      "mcp__playwright__browser_fill_form",
      "mcp__playwright__browser_click",
      "mcp__playwright__browser_take_screenshot",
      "mcp__filesystem__directory_tree",
      "mcp__filesystem__read_text_file"
    ]
  }
}
```

---

## 4. Project Structure

```
OrangeHRM_SeleniumMCP/
├── .claude/
│   ├── settings.json            # Claude Code default permission mode
│   └── settings.local.json      # MCP server permissions (local only, git-ignored)
├── src/
│   └── test/
│       └── java/
│           ├── pages/
│           │   ├── LoginPage.java        # Page Object for Login screen
│           │   └── DashboardPage.java    # Page Object for Dashboard screen
│           ├── tests/
│           │   └── LoginLogoutTest.java  # TestNG test class
│           └── utils/
│               └── DriverManager.java   # WebDriver lifecycle manager
├── target/                      # Maven build output (git-ignored)
│   └── surefire-reports/        # TestNG XML/HTML reports
├── .gitignore
├── pom.xml                      # Maven project configuration
├── testng.xml                   # TestNG suite configuration
└── README.md
```

---

## 5. Page Object Model

### LoginPage.java
**Package:** `pages`  
**Responsibility:** Encapsulates all interactions with the OrangeHRM login screen.

| Method | Description |
|--------|-------------|
| `navigateTo(String url)` | Opens the application URL in the browser |
| `enterUsername(String username)` | Types into the username field (with explicit wait) |
| `enterPassword(String password)` | Types into the password field (with explicit wait) |
| `clickLogin()` | Clicks the Submit/Login button |
| `login(String username, String password)` | Convenience method — performs full login and returns `DashboardPage` |
| `isLoginPageDisplayed()` | Returns `true` if the login heading is visible (used for post-logout assertions) |

**Locators used:**
- Username: `By.name("username")`
- Password: `By.name("password")`
- Login button: `By.cssSelector("button[type='submit']")`
- Login heading: `By.cssSelector(".orangehrm-login-title")`

---

### DashboardPage.java
**Package:** `pages`  
**Responsibility:** Encapsulates interactions with the OrangeHRM Dashboard after login.

| Method | Description |
|--------|-------------|
| `isDashboardLoaded()` | Returns `true` if the Dashboard heading text equals "Dashboard" |
| `getDashboardTitle()` | Returns the text of the top breadcrumb heading |
| `logout()` | Clicks the user dropdown, then Logout link; returns `LoginPage` |

**Locators used:**
- Dashboard heading: `By.cssSelector(".oxd-topbar-header-breadcrumb h6")`
- User dropdown: `By.cssSelector(".oxd-userdropdown-tab")`
- Logout menu item: `By.xpath("//a[text()='Logout']")`

---

### DriverManager.java
**Package:** `utils`  
**Responsibility:** Manages `WebDriver` lifecycle using `ThreadLocal` for thread-safe parallel execution.

| Method | Description |
|--------|-------------|
| `getDriver()` | Returns the current thread's `WebDriver`; creates a new `ChromeDriver` if none exists |
| `quitDriver()` | Quits the browser and removes the driver from `ThreadLocal` |

**Features:**
- Uses `WebDriverManager` to automatically download and configure the correct ChromeDriver version
- Launches Chrome maximized with notifications disabled
- `ThreadLocal` storage ensures thread safety for parallel test execution

---

## 6. Test Cases Covered

### LoginLogoutTest.java
**Package:** `tests`  
**Base URL:** `https://opensource-demo.orangehrmlive.com`  
**Credentials:** `Admin` / `admin123`

| Test Method | Description | Assertions |
|------------|-------------|------------|
| `testSuccessfulLogin()` | Verifies that valid credentials navigate to the Dashboard | Dashboard is loaded; title equals "Dashboard" |
| `testLogout()` | Verifies that clicking Logout redirects back to the Login page | Dashboard loads first; Login page is displayed after logout |
| `testLoginAndLogoutFlow()` | End-to-end test covering the complete login → dashboard → logout flow | Dashboard visible after login; Login page visible after logout |

**TestNG Lifecycle:**
- `@BeforeMethod` — initializes `WebDriver`, creates `LoginPage`, navigates to base URL
- `@AfterMethod` — calls `DriverManager.quitDriver()` to close the browser after each test

---

## 7. How to Run Tests

### Prerequisites
Ensure the following are installed before running:
- Java 11+
- Apache Maven 3.6+
- Google Chrome (latest)
- Internet access (WebDriverManager auto-downloads ChromeDriver)

### Run All Tests

```bash
mvn clean test
```

### Run with Specific TestNG Suite

```bash
mvn clean test -DsuiteXmlFile=testng.xml
```

### Run a Single Test Method

```bash
mvn clean test -Dtest=LoginLogoutTest#testSuccessfulLogin
```

### Skip Tests (build only)

```bash
mvn clean package -DskipTests
```

---

## 8. Prerequisites

| Requirement | Version | Notes |
|------------|---------|-------|
| Java (JDK) | 21 or higher | `JAVA_HOME` must be set |
| Apache Maven | 3.6+ | `mvn` must be on `PATH` |
| Google Chrome | Latest stable | ChromeDriver managed automatically by WebDriverManager |
| Internet Connection | — | Required for WebDriverManager to download ChromeDriver and to reach the demo site |

### Verify Installation

```bash
java -version
mvn -version
```

---

## 9. Configuration

### testng.xml

```xml
<suite name="OrangeHRM Test Suite" verbose="1">
    <test name="Login and Logout Tests">
        <classes>
            <class name="tests.LoginLogoutTest"/>
        </classes>
    </test>
</suite>
```

The suite runs all three test methods in `LoginLogoutTest` in a single test group named "Login and Logout Tests".

### Maven Surefire Plugin

The `maven-surefire-plugin` (v3.2.5) is configured in `pom.xml` to pick up `testng.xml` automatically when you run `mvn test`:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.2.5</version>
    <configuration>
        <suiteXmlFiles>
            <suiteXmlFile>testng.xml</suiteXmlFile>
        </suiteXmlFiles>
    </configuration>
</plugin>
```

### Claude Code MCP Configuration

The project uses `.claude/settings.json` (auto-approve mode) and `.claude/settings.local.json` (MCP tool permissions) to enable Claude Code to autonomously interact with the browser and filesystem during AI-assisted development sessions.

---

## 10. Reports

After running `mvn test`, TestNG generates reports in:

```
target/surefire-reports/
├── testng-results.xml       # Machine-readable XML results
├── TEST-tests.LoginLogoutTest.xml
└── index.html               # Human-readable HTML report (open in browser)
```

Open the HTML report:
```bash
# Windows
start target\surefire-reports\index.html

# macOS/Linux
open target/surefire-reports/index.html
```

> **Note:** For richer HTML reports (with charts and logs), consider adding the **ExtentReports** or **Allure** reporting dependency to `pom.xml`.

---

## 11. Author

**Samir Vijay Jagtap**  
GitHub: [samirjagtap4030](https://github.com/samirjagtap4030)
