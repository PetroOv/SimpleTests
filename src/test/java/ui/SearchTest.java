package ui;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 * Date: 28.10.13
 */

@RunWith(DataProviderRunner.class)
public class SearchTest {

    static String login = "gexibawer@one2mail.info";
    static String pass = "q";
    static Set<Cookie> cookieSet;
    static String url;
    private static String loginPage = "https://onthe.io/auth";
    WebDriver driver;

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
        cookieSet = new HashSet<Cookie>();
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

    @DataProvider(format = "%m [%p[0]]")
    public static Object[][] dataProviderMenuTabs() {
        return new Object[][]{
                {"articles"},
                {"home"},
                {"authors"},
        };
    }

    @DataProvider(format = "%m [filter: %p[0]]")
    public static Object[][] dataProviderTimesTabs() {
        return new Object[][]{
                {"realtime", "realtime"},
                {"now", "last 10 minutes"},
                {"hour", "1 hour"},
                {"today", "Today"},
                {"yesterday", "Yesterday"},
                {"week", "7 days"},
                {"month", "30 days"}
        };
    }

    @DataProvider(format = "%m [sort by: %p[0], order: %p[0]]")
    public static Object[][] dataProviderSortingTabs() {
        return new Object[][]{
                {"Pageviews", "pageviews", "Best"},
                {"Average time", "timeread", "Best"},
                {"Finished reading", "readability", "Best"},
                {"Recirculation", "today", "Best"},
                {"Pageviews", "pageviews", "Worst"},
                {"Average time", "timeread", "Worst"},
                {"Finished reading", "readability", "Worst"},
                {"Recirculation", "recirculation", "Worst"}
        };
    }

    public void waitForFinishingLoading() {
        WebDriverWait wait30sec = new WebDriverWait(driver, 30);
        wait30sec.until(invisibilityOfElementLocated(By.cssSelector(".loader_container")));
    }

    public void waitForStartingLoading() {
        WebDriverWait wait5sec = new WebDriverWait(driver, 5);
        try {
            wait5sec.until(visibilityOfElementLocated(By.cssSelector(".loader_container")));
        } catch (Exception ignored) {
        }
    }

    @Before
    public void setUp() throws Exception {
        driver = new ChromeDriver();
        driver.navigate().to(loginPage);
        for (Cookie cookie :
                cookieSet) {
            driver.manage().addCookie(cookie);
        }
        driver.get(url);
    }

    @Test
    @UseDataProvider("dataProviderMenuTabs")
    public void menuTabsTest(String page) {
        driver.findElement(By.cssSelector("div[qa-id='" + page + "']")).click();
        waitForFinishingLoading();
        assertThat("", driver.findElement(By.cssSelector(".data_" + page)).isDisplayed());
    }

    @Test
    @UseDataProvider("dataProviderTimesTabs")
    public void datesFilterOnHomePageTest(String periodName, String expectedFilter) {
        waitForFinishingLoading();
        driver.findElement(By.cssSelector("div[data-period='" + periodName + "']")).click();
        waitForStartingLoading();
        waitForFinishingLoading();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        assertThat("", js.executeScript("return data.get_period_name()"), equalTo(expectedFilter));
        for (WebElement element : driver.findElements(By.cssSelector("div.data_list.data_list_pubs div.row.item"))) {
            System.out.println(element.findElement(By.cssSelector("div.value")).getAttribute("data-tooltip"));
            assertThat("", element.findElement(By.cssSelector("div.value")).getAttribute("data-tooltip").contains(expectedFilter));
        }
        System.out.println("__________________________________________________________");
        System.out.println(js.executeScript("return data.get_period_name()"));
        //Todo check list
    }

    @Test
    @UseDataProvider("dataProviderSortingTabs")
    public void sortingTest() {
        driver.findElement(By.cssSelector("div[qa-id='articles']")).click();
        waitForFinishingLoading();
        System.out.println(driver.findElement(By.cssSelector(".ui__switch input")).isSelected());
        waitForFinishingLoading();
        driver.findElement(By.cssSelector(".ui__switch")).click();
        System.out.println(driver.findElement(By.cssSelector(".ui__switch input")).isSelected());
    }

    @Test
    public void authorTest() {
        //click on 30d
        //if for authors checks publications

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
        driver.quit();
    }
}

