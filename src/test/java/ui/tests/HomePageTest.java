package ui.tests;

import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import io.qameta.allure.Description;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ui.DataProviders;
import ui.TestBase;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(DataProviderRunner.class)
public class HomePageTest extends TestBase {
    @Test
    @Description("Переключение между отчетами (Home / Articles / Authors")
    @UseDataProvider(value = "dataProviderMenuTabs", location = DataProviders.class)
    public void menuTabsTest(String page) {
        pageObject.setMenuTabLocator(page);
        pageObject.clickOnMenuTab();
        waitForFinishingLoading();
        assertThat("Menu tab doesn't work", driver.findElement(By.cssSelector(".data_" + page)).isDisplayed());
    }

    @Test
    @Description("Открытие TV Дашборда.")
    public void dashBoardTest(){
        driver.findElement(By.cssSelector(".fullscreen_icon")).click();
        WebDriverWait wait30sec = new WebDriverWait(driver,30);
        wait30sec.until(ExpectedConditions.numberOfWindowsToBe(2));
        driver.switchTo().window((String) driver.getWindowHandles().toArray()[1]);
        waitForElementVisible(By.cssSelector(".title.show"));
        assertThat("Erorr with dashboard", driver.findElement(By.cssSelector(".title.show")).getText(), equalTo("WELCOME TO NEWSROOM TV"));
    }

    @Test
    @Description("Переключение между таймфреймами (RT, 10m, 1H, 1D, YD, 7D, 30D)")
    @UseDataProvider(value = "dataProviderTimesTabs", location = DataProviders.class)
    public void datesFilterOnHomePageTest(String periodName, String expectedFilter) {
        waitForFinishingLoading();
        pageObject.setPeriodFilterLocator(periodName);
        pageObject.selectDateFilter();
        loaderPause();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        assertThat("", js.executeScript("return data.get_period_name()"), equalTo(expectedFilter));
        for (WebElement element : pageObject.getPublications()) {
            assertThat("Publication with wrong date filter", pageObject.publicationViews(element).getAttribute("data-tooltip").contains(expectedFilter));
        }
    }

    @Test
    @Description("Фильтрация по авторам -> Выбрать период 30 дней, Нажать фильтр по " +
            "каждому автору отдельно -> должен быть список публикаций и выбранный автор " +
            "присутствует в каждой публикации")
    public void authorTest() {
        waitForFinishingLoading();
        pageObject.setPeriodFilterLocator("month");
        pageObject.selectDateFilter();
        loaderPause();
        int authorsCount = pageObject.getAuthors().size();
        for (int i = 0; i < authorsCount; i++) {
            String authorName = pageObject.getAuthorName(pageObject.getAuthors().get(i));
            pageObject.selectAuthor(pageObject.getAuthors().get(i));
            loaderPause();
            for (WebElement element : pageObject.getPublications()) {
                assertThat("", pageObject.getPublicationAuthor(element).getText(), equalTo(authorName));
            }
            pageObject.selectAuthor();
            loaderPause();
        }
    }
}

