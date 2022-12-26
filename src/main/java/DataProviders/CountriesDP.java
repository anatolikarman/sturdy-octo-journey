package DataProviders;

import org.testng.annotations.DataProvider;

import static Enums.CountriesEnum.*;

public class CountriesDP {

    @DataProvider
    Object[][] countries() {
        return new Object[][]{
                {ALBANIA},
                {ROMANIA},
                {UK},
                {BULGARIA}
        };
    }
}

