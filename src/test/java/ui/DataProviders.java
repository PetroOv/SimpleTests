package ui;

import com.tngtech.java.junit.dataprovider.DataProvider;

public class DataProviders {
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

    @DataProvider(format = "%m [sort by: %p[0], order: %p[2]]")
    public static Object[][] dataProviderSortingTabs() {
        return new Object[][]{
                {"Pageviews", "pageviews", "Best"},
                {"Average time", "timeread", "Best"},
                {"Finished reading", "readability", "Best"},
                {"Recirculation", "recirculation", "Best"},
                {"Pageviews", "pageviews", "Worst"},
                {"Average time", "timeread", "Worst"},
                {"Finished reading", "readability", "Worst"},
                {"Recirculation", "recirculation", "Worst"}
        };
    }
}
