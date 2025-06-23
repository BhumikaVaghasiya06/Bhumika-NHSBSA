package steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.apache.log4j.Logger;
import org.junit.Assert;
import utils.DriverFactory;
import utils.LoggerHelper;

import java.time.Duration;
import java.util.List;

public class NhsJobSearchSteps {

    private WebDriver driver;
    private WebDriverWait wait;
    private Logger logger = LoggerHelper.getLogger(NhsJobSearchSteps.class);
    private String lastSelectedSortOption = "Best Match";

    @Before
    public void setup() {
        logger.info("Initializing WebDriver and WebDriverWait");
        driver = DriverFactory.initializeDriver("chrome"); // change browser if needed
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Given("I am on the NHS jobs search page")
    public void i_am_on_the_nhs_jobs_search_page() {
        logger.info("Navigating to NHS jobs search page");
        driver.get("https://www.jobs.nhs.uk/candidate/search");
        wait.until(ExpectedConditions.elementToBeClickable(By.id("keyword")));
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

        try {
            By suggestionLocator = By.cssSelector("ul#location__listbox li.autocomplete__option");
            wait.until(ExpectedConditions.visibilityOfElementLocated(suggestionLocator));
            for (WebElement suggestion : driver.findElements(suggestionLocator)) {
                if (suggestion.getText().equalsIgnoreCase(location)) {
                    suggestion.click();
                    break;
                }
            }
        } catch (TimeoutException e) {
            logger.warn("No location suggestion appeared for: " + location);
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
        logger.info("Waiting for results to load");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sort")));
    }

    @Then("I wait for sort by dropdown to show default Best Match")
    public void waitForSortByAndVerifyDefault() {
        logger.info("Verifying default sort option is 'Best Match'");
        WebElement sortDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sort")));
        Select select = new Select(sortDropdown);
        String defaultOption = select.getFirstSelectedOption().getText();
        lastSelectedSortOption = defaultOption;
        Assert.assertEquals("Best Match", defaultOption);
    }

    @When("I select Date Posted newest from sort by dropdown")
    public void selectDatePostedNewestFromSortBy() {
        logger.info("Selecting 'Date Posted (newest)'");
        WebElement sortDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("sort")));
        Select select = new Select(sortDropdown);
        select.selectByVisibleText("Date Posted (newest)");
        lastSelectedSortOption = "Date Posted (newest)";
    }

    @Then("the sort by dropdown should have Date Posted newest selected")
    public void verifySortByIsDatePostedNewest() {
        logger.info("Verifying sort option is 'Date Posted (newest)'");
        WebElement sortDropdown = driver.findElement(By.id("sort"));
        Select select = new Select(sortDropdown);
        String selectedOption = select.getFirstSelectedOption().getText();
        Assert.assertEquals("Date Posted (newest)", selectedOption);
    }

    // New step for verifying exact error message based on input
    @Then("I should see error message {string}")
    public void i_should_see_error_message(String expectedMessage) {
        logger.info("Checking for expected error message: " + expectedMessage);

        List<By> messageLocators = List.of(
                By.xpath("//h3[contains(text(),'No result found')]"),
                By.xpath("//h2[contains(text(),'Location not found')]")
        );

        boolean found = false;
        String actualMessage = "";

        for (By locator : messageLocators) {
            try {
                WebElement elem = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
                actualMessage = elem.getText().trim();
                if (actualMessage.equalsIgnoreCase(expectedMessage.trim())) {
                    found = true;
                    break;
                }
            } catch (TimeoutException ignored) {
                // Try next locator
            }
        }

        if (!found) {
            logger.error("Expected error message not found. Last found message: " + actualMessage);
        }
        Assert.assertTrue("Expected error message '" + expectedMessage + "' not found on page.", found);
    }

    @Then("I should see validation errors or results page with no filters")
    public void i_should_see_validation_errors_or_results_with_no_filters() {
        logger.info("Handling empty input case");
        try {
            List<WebElement> validationErrors = driver.findElements(By.className("error-message"));
            if (!validationErrors.isEmpty()) {
                logger.info("Validation errors present");
                Assert.assertTrue(true);
                return;
            }

            WebElement filtersSection = driver.findElement(By.cssSelector(".filters"));
            Assert.assertTrue("Expected no filters applied", filtersSection.getText().isEmpty());
        } catch (Exception e) {
            logger.warn("Fallback to checking no results");
            List<WebElement> results = driver.findElements(By.cssSelector(".search-results__item"));
            Assert.assertTrue("Expected no job results", results.isEmpty());
        }
    }

    @When("I try to select an invalid option from sort by dropdown")
    public void i_try_to_select_invalid_sort_option() {
        logger.info("Trying to select invalid sort option");
        WebElement sortDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("sort")));
        Select select = new Select(sortDropdown);
        lastSelectedSortOption = select.getFirstSelectedOption().getText();

        try {
            select.selectByVisibleText("Invalid Option");
            Assert.fail("Expected NoSuchElementException for invalid sort");
        } catch (NoSuchElementException e) {
            logger.info("Invalid option correctly not selectable");
        }
    }

    @Then("the dropdown should remain unchanged")
    public void dropdown_should_remain_unchanged() {
        logger.info("Checking if sort dropdown remains unchanged");
        WebElement sortDropdown = driver.findElement(By.id("sort"));
        Select select = new Select(sortDropdown);
        String currentOption = select.getFirstSelectedOption().getText();
        Assert.assertEquals("Sort dropdown changed unexpectedly", lastSelectedSortOption, currentOption);
    }

    @After
    public void tearDown() {
        logger.info("Tearing down WebDriver");
        DriverFactory.quitDriver();
    }
}
