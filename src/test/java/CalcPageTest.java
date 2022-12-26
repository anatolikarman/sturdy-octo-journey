import DataProviders.CountriesDP;
import Enums.CountriesEnum;
import Pages.CalcPage;
import jdk.jfr.Description;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static Utils.Helpers.generateRandNum;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CalcPageTest {

    private CalcPage calcPage;

    @BeforeMethod
    void init() throws IOException {
        calcPage = new CalcPage();
    }

    @Description("Entering data in Buy field should empty Sell field")
    @Test
    void buyEmptied() {
        calcPage.openCalcPage();
        calcPage.fillBuyInput(generateRandNum())
                .assertSellInputEmpty();
    }

    @Description("Entering data in Sell field should empty Buy field")
    @Test
    void sellEmptied() {
        calcPage.openCalcPage();
        calcPage.fillSellInput(generateRandNum())
                .assertBuyInputEmpty();
    }

    @Description("Switching to a different country via menu " +
                "should update url, currency name and currency rates")
    @Test(dataProviderClass = CountriesDP.class, dataProvider = "countries")
    void countrySwitchViaMenuUpdates(CountriesEnum country) {
        calcPage.openCalcPage();
        List<String> defaultCurrencyRates = calcPage.getCurrencyRates();
        calcPage.switchToCountryViaMenu(country);
        List<String> updatedCurrencyRates = calcPage.getCurrencyRates();

        calcPage.assertURLForCountry(country)
                .assertCurrency(country)
                .assertCurrencyUpdates(defaultCurrencyRates, updatedCurrencyRates);
    }

    @Description("Assert that the USD amount loss for a country's 1st commercial bank" +
                "is calcualted correctly," +
                "if there is no expected loss for this particular country-bank, " +
                "than we switch to a different country and check." +
                "Proceed until we find a loss. " +
                "In case there wasn't a single loss encountered (which is highly unlikely) - " +
                "test will fail, so we won't receive potentially false negative result.")
    @Test
    void amountLossUSDCalculation() {
        boolean lossHasBeenEncountered = false;
        for (CountriesEnum country : CountriesEnum.values()
        ) {
            calcPage.openCalcPage(country);
            List<Float> amountValues = calcPage.getAmountValues();
            lossHasBeenEncountered = calcPage.isPsrUSDAmountHigher(amountValues);
            if (lossHasBeenEncountered) {
                calcPage.assertAmountLossValue(amountValues);
                break;
            }
        }
        assertThat("Amount loss hasn't been encountered for all countries",
                lossHasBeenEncountered, is(true));
    }

}
