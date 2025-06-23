package steps;

import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.en.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import org.apache.log4j.Logger;
import org.junit.Assert;
import utils.DriverFactory;
import utils.LoggerHelper;


public class NhsJobSearchSteps {

    private WebDriver driver;
    private WebDriverWait wait;
    private Logger logger = LoggerHelper.getLogger(NhsJobSearchSteps.class);

    @Before
    public void setup() {
        logger.info("Initializing WebDriver and WebDriverWait");
        driver = DriverFactory.initializeDriver("firefox");  // or "firefox" for multi-browser support
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Given("I am on the NHS jobs search page")
    public void i_am_on_the_nhs_jobs_search_page() {
        logger.info("Navigating to NHS jobs search page");
        driver.get("https://www.jobs.nhs.uk/candidate/search");
        wait.until(ExpectedConditions.elementToBeClickable(By.id("keyword"))); // Wait for input field
    }

    @When("I enter job title {string}")
    public void i_enter_job_title(String jobTitle) {
        logger.info("Entering job title: " + jobTitle);
        WebElement whatInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("keyword")));
        whatInput.clear();
        whatInput.sendKeys(jobTitle);
    }

    @When("I enter location {string}")
    public void i_enter_location(String location) {
        logger.info("Entering location: " + location);
        WebElement whereInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("location")));
        whereInput.clear();
        whereInput.sendKeys(location);

        By suggestionLocator = By.cssSelector("ul#location__listbox li.autocomplete__option");
        wait.until(ExpectedConditions.visibilityOfElementLocated(suggestionLocator));

        for (WebElement suggestion : driver.findElements(suggestionLocator)) {
            if (suggestion.getText().equalsIgnoreCase(location)) {
                suggestion.click();
                break;
            }
        }
    }

    @When("I click on search button")
    public void i_click_on_search_button() {
        logger.info("Clicking on search button");
        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("search")));
        searchButton.click();
    }

    @Then("I wait for search results to be displayed")
    public void i_wait_for_search_results_to_be_displayed() {
        logger.info("Waiting for search results (sort dropdown) to be visible");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sort")));
    }

    @Then("I wait for sort by dropdown to show default Best Match")
    public void waitForSortByAndVerifyDefault() {
        logger.info("Verifying 'Sort by' dropdown default selection is 'Best Match'");
        WebElement sortDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sort")));
        Select select = new Select(sortDropdown);
        String defaultOption = select.getFirstSelectedOption().getText();
        Assert.assertEquals("Best Match", defaultOption);
    }

    @When("I select Date Posted newest from sort by dropdown")
    public void selectDatePostedNewestFromSortBy() {
        logger.info("Selecting 'Date Posted (newest)' from sort dropdown");
        WebElement sortDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("sort")));
        Select select = new Select(sortDropdown);
        select.selectByVisibleText("Date Posted (newest)");
    }

    @Then("the sort by dropdown should have Date Posted newest selected")
    public void verifySortByIsDatePostedNewest() {
        logger.info("Verifying the selected option is 'Date Posted (newest)'");
        WebElement sortDropdown = driver.findElement(By.id("sort"));
        Select select = new Select(sortDropdown);
        String selectedOption = select.getFirstSelectedOption().getText();
        Assert.assertEquals("Date Posted (newest)", selectedOption);
    }

    @After
    public void tearDown() {
        logger.info("Quitting WebDriver");
        DriverFactory.quitDriver();
    }
}
