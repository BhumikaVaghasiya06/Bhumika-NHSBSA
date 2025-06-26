package steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import utils.DriverFactory;
import utils.LoggerHelper;

import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class NhsJobSearchSteps {

    private WebDriver driver;
    private WebDriverWait wait;
    private Logger logger = LoggerHelper.getLogger(NhsJobSearchSteps.class);
    private List<WebElement> suggestions;


    @Before
    public void setup() {
        logger.info("Initializing WebDriver and WebDriverWait");
        driver = DriverFactory.initializeDriver("chrome");
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
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(By.id("keyword")));
        input.clear();
        input.sendKeys(jobTitle);
    }

    @When("I enter location {string}")
    public void i_enter_location(String location) {
        logger.info("Entering location: " + location);
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(By.id("location")));
        input.clear();
        input.sendKeys(location);
        try {
            By suggestions = By.cssSelector("ul#location__listbox li.autocomplete__option");
            wait.until(ExpectedConditions.visibilityOfElementLocated(suggestions));
            for (WebElement suggestion : driver.findElements(suggestions)) {
                if (suggestion.getText().equalsIgnoreCase(location)) {
                    suggestion.click();
                    break;
                }
            }
        } catch (TimeoutException ignored) {}
    }

    @When("I select {string} in the Distance dropdown")
    public void i_select_distance(String value) {
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("distance")));
        new Select(dropdown).selectByVisibleText(value);
    }

    @When("I click on More search options")
    @When("I click on the More search options link")
    public void i_click_on_more_search_options() {
        logger.info("Clicking on More search options");
        try {
            WebElement jobReferenceField = driver.findElement(By.id("jobReference"));
            if (!jobReferenceField.isDisplayed()) {
                WebElement moreOptionsBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("searchOptionsBtn")));
                moreOptionsBtn.click();
            }
        } catch (NoSuchElementException e) {
            WebElement moreOptionsBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("searchOptionsBtn")));
            moreOptionsBtn.click();
        }
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("payRange")));
    }


    @When("I enter job reference {string}")
    public void i_enter_job_reference(String ref) {
        if (!ref.isEmpty()) {
            WebElement input = wait.until(ExpectedConditions.elementToBeClickable(By.id("jobReference")));
            input.clear();
            input.sendKeys(ref);
        }
    }

    @When("I enter employer {string}")
    public void i_enter_employer(String employer) {
        if (!employer.isEmpty()) {
            WebElement input = wait.until(ExpectedConditions.elementToBeClickable(By.id("employer")));
            input.clear();
            input.sendKeys(employer);
        }
    }

    @When("I select {string} from the Pay Range dropdown")
    public void i_select_from_pay_range(String payRange) {
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("payRange")));
        new Select(dropdown).selectByVisibleText(payRange);
    }

    @When("I click on the Search button")
    @When("I click on search button")
    public void i_click_search() {
        wait.until(ExpectedConditions.elementToBeClickable(By.id("search"))).click();
    }

    @Then("I wait for search results to be displayed")
    public void wait_for_results() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sort")));
    }

    @Then("the sort by dropdown should have {string} selected by default")
    @Then("the sort by dropdown should have {string} selected")
    public void sort_by_selected(String expected) {
        WebElement dropdown = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("sort")));
        Select select = new Select(dropdown);
        Assert.assertEquals(expected, select.getFirstSelectedOption().getText());
    }

    @When("I select {string} from the sort by dropdown")
    public void i_select_from_sort_by(String option) {
        Select select = new Select(wait.until(ExpectedConditions.elementToBeClickable(By.id("sort"))));
        select.selectByVisibleText(option);
    }

    @Then("the Job Title field should contain {string}")
    public void job_title_contains(String expected) {
        Assert.assertEquals(expected, driver.findElement(By.id("keyword")).getAttribute("value"));
    }

    @Then("the Location field should contain {string}")
    public void location_field_contains(String expected) {
        Assert.assertEquals(expected, driver.findElement(By.id("location")).getAttribute("value"));
    }

    @Then("the Job Reference field should contain {string}")
    public void job_reference_contains(String expected) {
        Assert.assertEquals(expected, driver.findElement(By.id("jobReference")).getAttribute("value"));
    }

    @Then("the Employer field should contain {string}")
    public void employer_field_contains(String expected) {
        Assert.assertEquals(expected, driver.findElement(By.id("employer")).getAttribute("value"));
    }

    @Then("the What field should display hint text {string}")
    public void what_field_hint(String expected) {
        WebElement hint = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("keyword-hint")));
        Assert.assertEquals(expected, hint.getText().trim());
    }

    @Then("the Distance dropdown should be disabled with {string} selected by default")
    public void distance_disabled_with_default(String expected) {
        WebElement dropdown = driver.findElement(By.id("distance"));
        Assert.assertFalse(dropdown.isEnabled());
        Assert.assertEquals(expected, new Select(dropdown).getFirstSelectedOption().getText());
    }

    @Then("the Pay Range dropdown should have {string} option selected by default")
    public void pay_range_default_selected(String expected) {
        WebElement dropdown = driver.findElement(By.id("payRange"));
        Assert.assertEquals(expected, new Select(dropdown).getFirstSelectedOption().getText());
    }

    @Then("the Distance dropdown should contain the following options:")
    public void distance_options(io.cucumber.datatable.DataTable dataTable) {
        List<String> expected = dataTable.asList();
        List<String> actual = new Select(driver.findElement(By.id("distance"))).getOptions()
                .stream().map(WebElement::getText).collect(Collectors.toList());
        Assert.assertTrue(actual.containsAll(expected));
    }

    @Then("the Pay Range dropdown should contain the following options:")
    public void pay_range_options(io.cucumber.datatable.DataTable dataTable) {
        List<String> expectedOptions = dataTable.asList().stream().map(String::trim).collect(Collectors.toList());

        // Make sure the dropdown is visible
        i_click_on_more_search_options(); // Ensures dropdown is present
        WebElement payRangeDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("payRange")));

        List<String> actualOptions = new Select(payRangeDropdown)
                .getOptions()
                .stream()
                .map(WebElement::getText)
                .map(String::trim)
                .collect(Collectors.toList());

        logger.info("Expected Pay Range options: " + expectedOptions);
        logger.info("Actual Pay Range options: " + actualOptions);

        Assert.assertEquals("Mismatch in Pay Range dropdown options", expectedOptions, actualOptions);
    }

    @Then("the sort by dropdown should contain the following options:")
    public void sort_by_options(io.cucumber.datatable.DataTable dataTable) {
        List<String> expected = dataTable.asList();
        List<String> actual = new Select(driver.findElement(By.id("sort"))).getOptions()
                .stream().map(WebElement::getText).collect(Collectors.toList());
        Assert.assertTrue(actual.containsAll(expected));
    }

    @Then("I should see error message {string}")
    public void error_message(String expected) {
        List<By> locators = List.of(
                By.id("no-result-title"),                                      // For "No result found"
                By.cssSelector("h2[data-test='search-result-query']")          // For "Location not found"
        );

        boolean found = false;
        for (By locator : locators) {
            try {
                WebElement msg = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
                String actualMessage = msg.getText().trim();
                logger.info("Found error message: " + actualMessage);
                if (actualMessage.equalsIgnoreCase(expected)) {
                    found = true;
                    break;
                }
            } catch (TimeoutException ignored) {
                // Continue trying other locators
            }
        }
        Assert.assertTrue("Expected error message not found: " + expected, found);
    }


    @Then("I should see all available job results")
    public void see_all_jobs() {
        WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("search-results-heading")));
        Matcher matcher = Pattern.compile("(\\d+)\\s+jobs found").matcher(heading.getText());
        Assert.assertTrue("No job count found", matcher.find());
        int count = Integer.parseInt(matcher.group(1));
        Assert.assertTrue("Expected jobs but found none", count > 0);
    }

    @When("I click on the Clear filters button")
    public void click_clear_filters() {
        wait.until(ExpectedConditions.elementToBeClickable(By.id("clearFilters"))).click();
    }

    @Then("the Job Title field should be empty")
    public void job_title_empty() {
        Assert.assertTrue(driver.findElement(By.id("keyword")).getAttribute("value").isEmpty());
    }

    @Then("the Location field should be empty")
    public void location_empty() {
        Assert.assertTrue(driver.findElement(By.id("location")).getAttribute("value").isEmpty());
    }

    @Then("the Distance dropdown should be disabled and have {string} selected")
    public void distance_disabled_and_selected(String expected) {
        WebElement dropdown = driver.findElement(By.id("distance"));
        Assert.assertFalse(dropdown.isEnabled());
        Assert.assertEquals(expected, new Select(dropdown).getFirstSelectedOption().getText());
    }

    @Then("the Employer field should be empty")
    public void employer_empty() {
        Assert.assertTrue(driver.findElement(By.id("employer")).getAttribute("value").isEmpty());
    }

    @Given("the Distance field is disabled")
    public void distance_field_disabled() {
        Assert.assertFalse(driver.findElement(By.id("distance")).isEnabled());
    }

    @Then("the Distance field should be enabled")
    public void distance_field_enabled() {
        WebElement dropdown = driver.findElement(By.id("distance"));
        wait.until(ExpectedConditions.elementToBeClickable(dropdown));
        Assert.assertTrue(dropdown.isEnabled());
    }

    @When("I enter {string} into the what field")
    public void i_enter_into_the_what_field(String whatText) {
        WebElement whatField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("keyword")));
        whatField.clear();
        whatField.sendKeys(whatText);
        logger.info("Entered job title: " + whatText);
    }

    @When("I enter {string} into the location field")
    public void i_enter_into_the_location_field(String locationInput) {
        WebElement locationField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("location")));
        locationField.clear();
        locationField.sendKeys(locationInput);
        logger.info("Entered location: " + locationInput);

        // Wait for suggestion list to appear (or not in case of invalid input)
        try {
            wait.until(driver -> {
                List<WebElement> elements = driver.findElements(By.cssSelector("#location__listbox li"));
                return !elements.isEmpty() || locationInput.equals("XYZ123");
            });
        } catch (TimeoutException e) {
            logger.warn("No suggestions appeared within timeout");
        }
    }

    @Then("I should see suggestions containing {string}")
    public void i_should_see_suggestions_containing(String input) {
        suggestions = driver.findElements(By.cssSelector("#location__listbox li"));
        Assert.assertTrue("Expected suggestions, but none found", suggestions.size() > 0);

        List<String> actualTexts = new ArrayList<>();
        for (WebElement element : suggestions) {
        actualTexts.add(element.getText());
        }

        for (String text : actualTexts) {
        Assert.assertTrue("Suggestion '" + text + "' does not contain: " + input,
                text.toLowerCase().contains(input.toLowerCase()));
        }

        logger.info("Suggestions matching input '" + input + "': " + actualTexts);
    }

    @And("I select {string} from the suggestions")
    public void i_select_from_the_suggestions(String desiredText) {
        boolean selected = false;
        for (WebElement suggestion : suggestions) {
        if (suggestion.getText().equalsIgnoreCase(desiredText)) {
            suggestion.click();
            logger.info("Selected suggestion: " + desiredText);
            selected = true;
            break;
        }
        }
        Assert.assertTrue("Could not find desired suggestion: " + desiredText, selected);
    }

    @Then("I should see no suggestions")
    public void i_should_see_no_suggestions() {
        List<WebElement> emptySuggestions = driver.findElements(By.cssSelector("#location__listbox li"));
        Assert.assertEquals("Expected no suggestions, but some were found", 0, emptySuggestions.size());
        logger.info("No suggestions shown for invalid input");
    }

    @After
    public void tearDown() {
        WebDriver driver = DriverFactory.getDriver();
        if (driver != null) {
            driver.quit();
            DriverFactory.quitDriver();
        }
    }
}
