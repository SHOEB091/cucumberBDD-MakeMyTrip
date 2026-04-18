package cucumberOptions;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * TestRunnerTest - Combined TestNG runner for ALL features.
 *
 * Use this runner when you want to run the entire test suite in a single
 * TestNG test class (without parallel separation).
 *
 * For parallel execution across feature files, use testng.xml which
 * references CabBookingRunner, GiftCardRunner, and HotelBookingRunner.
 */
@CucumberOptions(
    features = "src/test/resources/features",
    glue     = { "stepDefinitions", "hooks" },
    plugin   = {
        "pretty",
        "html:target/cucumber-reports/FullSuite_Report.html",
        "json:target/cucumber-reports/FullSuite_Report.json",
        "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
    },
    monochrome = true,
    dryRun     = false
)
public class TestRunnerTest extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
