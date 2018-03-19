package ui;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class SomeObjects {
    private final WebDriver driver;
    private By menuTabLocator = By.cssSelector("div[qa-id='home']");
    private By articlesMenuTabLocator = By.cssSelector("div[qa-id='articles']");
    private By sortingCategoriesLocator = By.cssSelector("div.data_articles_sorting div[data-type='category']");
    private By sortingOrderLocator = By.cssSelector(".ui__switch");
    private By publicationsLocator = By.cssSelector("div.data_list.data_list_pubs div.row.item");
    private By sortByLocator = By.cssSelector("div[data-name='pageviews'");
    private By periodFilterLocator = By.cssSelector("div[data-period='today']");
    private By publicationPageViewsLocator = By.cssSelector("div.value");
    private By publicationsAuthorsLocator = By.cssSelector("div[data-type='author']");
    SomeObjects(WebDriver driver) {
        this.driver = driver;
    }

    @Step
    public void clickOnMenuTab() {
        driver.findElement(menuTabLocator).click();
    }
    @Step
    public void setMenuTabLocator(String tab) {
        this.menuTabLocator = By.cssSelector(String.format("div[qa-id='%s']", tab));
    }

    @Step
    public void goToArticlesTab() {
        driver.findElement(articlesMenuTabLocator).click();
    }

    public List<WebElement> getAuthors() {
        return driver.findElements(publicationsAuthorsLocator);
    }

    public WebElement getPublicationAuthor(WebElement publication) {
        return publication.findElement(By.cssSelector("div[data-tooltip='Author']"));
    }

    @Step
    public String getAuthorName(WebElement author) {
        return author.findElement(By.cssSelector("div span")).getText();
    }

    public void selectAuthor(WebElement author) {
        author.findElement(By.cssSelector("div span")).click();
    }

    @Step
    public void selectAuthor() {
        driver.findElement(By.cssSelector("div[data-type='author'] div span")).click();
    }

    @Step
    public WebElement publicationViews(WebElement publication) {
        return publication.findElement(publicationPageViewsLocator);
    }

    @Step
    public void setPeriodFilterLocator(String filterName) {
        this.periodFilterLocator = By.cssSelector(String.format("div[data-period='%s']", filterName));
    }

    @Step
    public void selectDateFilter() {
        driver.findElement(periodFilterLocator).click();
    }

    @Step
    public void openSortingCategories() {
        driver.findElement(sortingCategoriesLocator).click();
    }

    @Step
    public void changeSortingOrder() {
        driver.findElement(sortingOrderLocator).click();
    }

    @Step
    public boolean getSortingOrder() {
        return driver.findElement(sortingOrderLocator).findElement(By.cssSelector("input")).isSelected();
    }

    @Step
    public List<WebElement> getPublications() {
        return driver.findElements(publicationsLocator);
    }

    @Step
    public void selectSortBy() {
        driver.findElement(sortByLocator).click();
    }

    @Step
    public By getSortByLocator() {
        return sortByLocator;
    }

    @Step
    public void setSortByLocator(String sortBy) {
        sortByLocator = By.cssSelector(String.format("div[data-name='%s'", sortBy));

    }

    public boolean checkPublicationsStatus(String sortBy, WebElement first, WebElement second, boolean input) {
        boolean status;

        switch (sortBy) {
            case "pageviews":
                status = compareValues(getPageviews(first), getPageviews(second), input);
                break;
            case "timeread":
                status = compareValues(getTimeReads(first), getTimeReads(second), input);
                break;
            case "readability":
                status = compareValues(getReadability(first), getReadability(second), input);
                break;
            case "recirculation":
                status = compareValues(getRecirculation(first), getRecirculation(second), input);
                break;
            default:
                status = compareValues(getPageviews(first), getPageviews(second), input);
                break;
        }
        return status;
    }

    @Step
    private boolean compareValues(int first, int second, boolean how) {
        if (how)
            return second >= first;
        else
            return first < second;
    }

    private int getPageviews(WebElement webElement) {
        return Integer.valueOf(webElement.findElement(By.cssSelector("div.value")).getText());
    }

    private int getTimeReads(WebElement webElement) {
        String time = webElement.findElement(By.cssSelector("span.time_value")).getText();
        return Integer.valueOf(time.split(":")[0]) * 60 + Integer.valueOf(time.split(":")[1]);
    }

    private int getReadability(WebElement webElement) {
        return parsePercentages(webElement.findElements(By.cssSelector("div.persent_bar_value")).get(0).getText());
    }

    private int getRecirculation(WebElement webElement) {
        return parsePercentages(webElement.findElements(By.cssSelector("div.persent_bar_value")).get(1).getText());
    }

    private int parsePercentages(String line) {
        return Integer.parseInt(line.replace("%", ""));
    }
}
