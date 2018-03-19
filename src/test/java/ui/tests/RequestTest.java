package ui.tests;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.qameta.allure.Attachment;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarEntry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ui.TestBase;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(DataProviderRunner.class)
public class RequestTest {
    private WebDriver driver;
    private BrowserMobProxy browserMobProxy;

    @DataProvider(format = "%m [Post: %p[0]]")
    public static Object[][] dataProviderAdd() {
        return new Object[][]{
                {1},
                {2},
                {3},
        };
    }



    @Before
    public void setUp() {
        ChromeDriverManager.getInstance().setup();
        browserMobProxy = new BrowserMobProxyServer();
        browserMobProxy.start();
        Proxy seleniumProxyConfiguration = ClientUtil.createSeleniumProxy(browserMobProxy);
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability(CapabilityType.PROXY, seleniumProxyConfiguration);
        driver = new ChromeDriver(desiredCapabilities);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get("https://ruhighload.com/");
        assertThat("HighLoad isn`t download", driver.findElement(By.id("head")).isDisplayed());
    }


    @Test
    @DisplayName("Data tracking test")
    @UseDataProvider("dataProviderAdd")
    public void secondTest(int postNumber) throws InterruptedException {
        browserMobProxy.newHar();
        openPost(postNumber);
        waitForPageLoaded();
        Thread.sleep(3000);
        assertThat("Request not found", searchRequest(browserMobProxy.getHar(), "https://tt.onthe.io/?k[]=28:pageviews"));
    }

    @Step
    @Description("Click on {postNumber} post")
    public void openPost(int postNumber) {
        driver.findElements(By.cssSelector("#posts a")).get(postNumber - 1).click();
    }

    @Step
    public boolean searchRequest(Har archive, String requestURL) {
        attachRequests(archive);
        for (HarEntry entry : archive.getLog().getEntries()) {
            if (entry.getRequest().getUrl().startsWith(requestURL)) {
                return true;
            }
        }
        return false;
    }

    @After
    public void tearDown() {
        try {
            saveScreenshot();
        } catch (Exception ignored) {
        } finally {
            if (driver != null) {
                driver.quit();
                driver = null;
            }
        }
    }

    @Attachment(value = "har archive", type = "text/plain")
    public String attachRequests(Har archive) {
        StringBuilder result = new StringBuilder();
        for (HarEntry entry : archive.getLog().getEntries()) {
            result.append(entry.getTimings().getSend()).append(": ").append(entry.getRequest().getUrl()).append("\n");
        }
        return result.toString();
    }

    @Attachment(value = "Page screenshot", type = "image/png")
    public byte[] saveScreenshot() {
        return (((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
    }

    @Step
    public void waitForPageLoaded() {
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until((ExpectedCondition<Boolean>) wdriver -> ((JavascriptExecutor) driver).executeScript(
                "return document.readyState"
        ).equals("complete"));
    }
}
