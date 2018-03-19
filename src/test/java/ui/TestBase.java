package ui;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashSet;
import java.util.Set;

import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class TestBase {
    private static String login = "gexibawer@one2mail.info";
    private static String pass = "q";
    private static Set<Cookie> cookieSet;
    private static String url;
    private static String loginPage = "https://onthe.io/auth";
    protected WebDriver driver;
    protected SomeObjects pageObject;

    @BeforeClass
    public static void init() {
        ChromeDriverManager.getInstance().setup();
        WebDriver webDriver = new ChromeDriver();
        webDriver.get(loginPage);
        webDriver.findElement(By.name("email")).sendKeys(login);
        webDriver.findElement(By.name("pwd")).sendKeys(pass);
        webDriver.findElement(By.cssSelector("form#auth button")).click();
        waitForProjectsPage(webDriver);
        webDriver.findElement(By.xpath("//*[contains(text(), 'RuHighload (prod)')]")).click();
        waitForRunWidget(webDriver);
        webDriver.switchTo().window(String.valueOf(webDriver.getWindowHandles().toArray()[1]));
        url = webDriver.getCurrentUrl();
        cookieSet = new HashSet<>();
        cookieSet.add(webDriver.manage().getCookieNamed("u"));
        cookieSet.add(webDriver.manage().getCookieNamed("_io_widget_introduced"));
        webDriver.quit();
    }

    private static void waitForProjectsPage(WebDriver driver) {
        WebDriverWait wait30sec = new WebDriverWait(driver, 30);
        wait30sec.until(visibilityOfElementLocated(By.cssSelector(".projects_page")));
    }

    private static void waitForRunWidget(WebDriver driver) {
        WebDriverWait wait30sec = new WebDriverWait(driver, 30);
        wait30sec.until(visibilityOfElementLocated(By.cssSelector(".widget-overlay__popup")));
        driver.findElement(By.cssSelector(".run-widget")).click();
    }

    @Before
    public void setUp() throws Exception {
        driver = new ChromeDriver();
        pageObject = new SomeObjects(driver);
        driver.get(loginPage);
        for (Cookie cookie :
                cookieSet) {
            driver.manage().addCookie(cookie);
        }
        driver.get(url);
    }

    protected void waitForElementVisible(By by) {
        WebDriverWait wait10sec = new WebDriverWait(driver, 10);
        try {
            wait10sec.until(ExpectedConditions.visibilityOfElementLocated(by));
        } catch (Exception ignored) {
        }
    }

    protected void waitForFinishingLoading() {
        WebDriverWait wait30sec = new WebDriverWait(driver, 30);
        wait30sec.until(invisibilityOfElementLocated(By.cssSelector(".loader_container")));
    }

    private void waitForStartingLoading() {
        WebDriverWait wait5sec = new WebDriverWait(driver, 5);
        try {
            wait5sec.until(visibilityOfElementLocated(By.cssSelector(".loader_container")));
        } catch (Exception ignored) {
        }
    }

    protected void loaderPause(){
        waitForStartingLoading();
        waitForFinishingLoading();
    }

    public void waitForPageLoaded() {
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver wdriver) {
                return ((JavascriptExecutor) driver).executeScript(
                        "return document.readyState"
                ).equals("complete");
            }
        });
    }

    @After
    public void tearDown() throws Exception {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
