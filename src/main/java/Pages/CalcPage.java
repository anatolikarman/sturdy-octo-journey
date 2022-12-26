package Pages;

import Enums.CountriesEnum;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.conditions.webdriver.Url;

import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import static Enums.CountriesEnum.LITHUANIA;
import static Utils.Helpers.getProperty;
import static Utils.Helpers.openPage;
import static com.codeborne.selenide.Selenide.*;
import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;

public class CalcPage {

    private final SelenideElement buyInput = $x(".//label[text()='Buy']/following-sibling::input"),
            sellInput = $x(".//label[text()='Sell']/following-sibling::input"),
            localButton = $(".js-localization-popover"),
            countryButton = $("#countries-dropdown"),
            currency = sellInput.$x("parent::div//span"),
            officialUSDRate = $x("(.//td[text()[contains(., 'USD')]]/following-sibling::td)[1]"),
            psrUSDRate = $x("(.//td[text()[contains(., 'USD')]]/following-sibling::td)[2]"),
            psrUSDAmount = $x("(.//td[text()[contains(., 'USD')]]/following-sibling::td)[3]"),
            bank_1USDAmount = $x("(.//td[text()[contains(., 'USD')]]/following-sibling::td)[4]" +
                                                "//span[contains(@data-ng-class, 'commercial-rate')]/span[1]");

    private final CountriesEnum defaultCountry = LITHUANIA;

    private  String fullUrlTemplate,
                    defaultSellValue = "100",
                    countryFromList = ".//a[text()[contains(., '%s')]]";

    public void openCalcPage() {
        openPage(format(fullUrlTemplate, defaultCountry.getDomain(), defaultCountry.getUrl_path()));
        waitPageToLoad();
    }

    public CalcPage() throws IOException {
        String forbiddenWord = getProperty(CalcPage.class, "forbiddenWord");
        fullUrlTemplate = format("https://www.%s", forbiddenWord) + ".%s/v2/%s/fees/currency-conversion-calculator#/";
    }

    public void openCalcPage(CountriesEnum country) {
        openPage(format(fullUrlTemplate, country.getDomain(), country.getUrl_path()));
        waitPageToLoad();
    }

    public CalcPage fillBuyInput(int s) {
        buyInput.append(Integer.toString(s));
        return this;
    }

    public CalcPage fillSellInput(int s) {
        sellInput.append(Integer.toString(s));
        return this;
    }

    public void assertBuyInputEmpty() {
        assertThat(buyInput.getValue(), isEmptyOrNullString());
    }

    public void assertSellInputEmpty() {
        assertThat(sellInput.getValue(), isEmptyOrNullString());
    }

    public void switchToCountryViaMenu(CountriesEnum country) {
        localButton.click();
        countryButton.click();
        $x(format(countryFromList, country.getName())).click();
        waitPageToLoad();
    }

    public CalcPage assertURLForCountry(CountriesEnum country){
        String expectedUrl = format(fullUrlTemplate, country.getDomain(), country.getUrl_path());
        webdriver().shouldHave(new Url(expectedUrl));
        return this;
    }

    public CalcPage assertCurrency(CountriesEnum country) {
        assertThat("Actual currency name doesn't match the expected",
                            currency.getText(), equalTo(country.getCurrency()));
        return this;
    }

    public List<String> getCurrencyRates() {
        return List.of(officialUSDRate.text().trim(), psrUSDRate.getText().trim());
    }

    public void assertCurrencyUpdates(List<String> defaultRates, List<String> updatedRates) {
        SoftAssert softAssert = new SoftAssert();
        for (int i = 0; i < defaultRates.size() - 1; i++) {
            softAssert.assertNotEquals(defaultRates.get(i), updatedRates.get(i));
        }
        softAssert.assertAll("Currency rates haven't been updated");
    }

    public boolean isPsrUSDAmountHigher(List<Float> amountValues){
        Float psrUSDAmountValue = amountValues.get(0);
        Float bank1AmountValue = amountValues.get(1);
        return psrUSDAmountValue.compareTo(bank1AmountValue) > 0;
    }

    public List<Float> getAmountValues(){
        Float psrUSDAmountValue = Float.parseFloat(psrUSDAmount.text().trim());
        Float bank_1AmountValue = Float.parseFloat(bank_1USDAmount.text().trim());
        return List.of(psrUSDAmountValue, bank_1AmountValue);
    }

    public void assertAmountLossValue(List<Float> amountValues){
        Float psrUSDAmountValue = amountValues.get(0);
        Float bank_1AmountValue = amountValues.get(1);
        Float expectedAmountLossUnrounded =  bank_1AmountValue - psrUSDAmountValue;
        Float expectedAmountLossRounded = Float.parseFloat(format("%.2f", expectedAmountLossUnrounded));
        String actualAmountLossString = bank_1USDAmount.$x(".//following-sibling::span")
                                                       .text()
                                                       .replaceAll("[()]","");
        Float actualAmountLossValue = Float.parseFloat(actualAmountLossString);
        assertThat("Actual amount of loss doesn't match the expected one",
                            actualAmountLossValue, equalTo(expectedAmountLossRounded));
    }

    private void waitPageToLoad() {
        sellInput.shouldBe(Condition.value(defaultSellValue), Duration.ofSeconds(7));
    }
}
