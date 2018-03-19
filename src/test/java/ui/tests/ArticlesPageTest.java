package ui.tests;

import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import io.qameta.allure.Description;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebElement;
import ui.DataProviders;
import ui.TestBase;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
@RunWith(DataProviderRunner.class)
public class ArticlesPageTest extends TestBase {
    @Test
    @Description("Проверить вариации сортировок публикаций (Best/Worst) по всем\n" +
            "метрикам Pageviews, Finished reading, Recirculation, Average time.")
    @UseDataProvider(value = "dataProviderSortingTabs", location = DataProviders.class)
    public void sortingTest(String sorting, String sortBy, String order) {
        pageObject.goToArticlesTab();
        waitForFinishingLoading();
        boolean input = false;
        if (order.equals("Worst")) {
            input = true;
        }
        boolean orderStatus = pageObject.getSortingOrder();
        if (orderStatus != input) {
            pageObject.changeSortingOrder();
            loaderPause();
        }
        pageObject.openSortingCategories();
        pageObject.setSortByLocator(sortBy);
        waitForElementVisible(pageObject.getSortByLocator());
        pageObject.selectSortBy();
        loaderPause();
        List<WebElement> elements = pageObject.getPublications();
        for (int i = 1; i < elements.size(); i++) {
            boolean status = pageObject.checkPublicationsStatus(sortBy, elements.get(i - 1), elements.get(i), input);
            assertThat("", status, equalTo(input));
        }
    }

}
