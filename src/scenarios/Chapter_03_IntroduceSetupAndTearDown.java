package scenarios;

import com.google.common.base.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class Chapter_03_IntroduceSetupAndTearDown {
    WebDriver driver;


    @BeforeMethod
    public void setup(){
        // initiate the driver and launch the application under test
        driver = new FirefoxDriver();
        driver.get("http://www.cleartrip.com/");
    }

    @Test
    public void testThatResultsAppearForAOneWayJourney(){

        chooseToHaveAOneWayJourney();

        enterOriginAs("Bangalore");
        selectTheFirstAvailableAutoCompleteOption();

        enterDestinationAs("Delhi");
        selectTheFirstAvailableAutoCompleteOption();

        enterDepartureDate();

        //all fields filled in. Now click on search
        searchForTheJourney();
        waitForSearchResultsToAppear();

        //verify that result appears for the provided journey search
        Assert.assertTrue(isElementPresent(By.id("outbound")));

    }

    @Test
    public void testThatResultsAppearForAReturnJourney(){

        chooseToHaveAReturnJourney();

        enterOriginAs("Bangalore");
        selectTheFirstAvailableAutoCompleteOption();

        enterDestinationAs("Delhi");
        selectTheFirstAvailableAutoCompleteOption();

        enterDepartureDate();
        enterReturnDate();

        //all fields filled in. Now click on search
        searchForTheJourney();
        waitForSearchResultsToAppear();

        //verify that result appears for the provided journey search
        Assert.assertTrue(isElementPresent(By.id("outbound")));
        Assert.assertTrue(isElementPresent(By.id("return")));

    }

    @AfterMethod
    public void teardown(){
        //close the browser
        driver.close();

    }

    private void searchForTheJourney() {
        driver.findElement(By.id("button_flight_search")).click();
    }


    private void enterDepartureDate() {
        driver.findElement(By.id("dpt_date")).clear();
        driver.findElement(By.id("dpt_date")).sendKeys(tomorrow());
    }

    private void enterReturnDate() {
        driver.findElement(By.id("rtn_date")).clear();
        driver.findElement(By.id("rtn_date")).sendKeys(dayAfterTomorrow());
    }


    private void enterDestinationAs(String destination) {
        driver.findElement(By.id("destination_autocomplete")).clear();
        driver.findElement(By.id("destination_autocomplete")).sendKeys(destination);
    }


    private void enterOriginAs(String origin) {
        driver.findElement(By.id("origin_autocomplete")).clear();
        driver.findElement(By.id("origin_autocomplete")).sendKeys(origin);
    }


    private void chooseToHaveAOneWayJourney() {
        driver.findElement(By.id("one_way")).click();
    }

    private void chooseToHaveAReturnJourney() {
        driver.findElement(By.id("rnd_trip")).click();
    }


    private void selectTheFirstAvailableAutoCompleteOption() {
        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(30, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS)
                .ignoring(NoSuchElementException.class);

        WebElement optionListElement = wait.until(new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver driver) {
                return driver.findElement(By.id("autocompleteOptionsContainer"));
            }
        });

        //select the first item from the auto complete list
        WebElement originOptionsElement = optionListElement;
        List<WebElement> originOptions = originOptionsElement.findElements(By.tagName("li"));
        originOptions.get(0).click();
    }


    private boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public String tomorrow(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 1);
        return new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
    }

    public String dayAfterTomorrow(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 2);
        return new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
    }


    public void waitForSearchResultsToAppear() {
        Wait<WebDriver> wait = new WebDriverWait(driver, 30);
        wait.until(visibilityOfElementLocated(By.id("mod_link")));
    }

    public ExpectedCondition<WebElement> visibilityOfElementLocated(final By locator) {
        return new ExpectedCondition<WebElement>() {
            public WebElement apply(WebDriver driver) {
                WebElement toReturn = driver.findElement(locator);
                if (toReturn.isDisplayed()) {
                    return toReturn;
                }
                return null;
            }
        };
    }


}
