package cucumberOptions;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * TestRunnerTest – Runs TravelBooking.feature (combined all-in-one flow).
 *
 * ONE browser:
 *   Tab 1 → Cabs (Delhi → Manali, June 10, 10:30 AM, SUV, lowest price)
 *   Tab 2 → Gift Cards (Wedding card, invalid email, capture error)
 *   Tab 1 → Hotels (extract adult numbers into List)
 *
 * No parallel – sequential execution.
 */
@CucumberOptions(
    features   = "src/test/resources/features/TravelBooking.feature",
    glue       = { "stepDefinitions", "hooks" },
    plugin     = {
        "pretty",
        "html:target/cucumber-reports/TravelBooking_Report.html",
        "json:target/cucumber-reports/TravelBooking_Report.json",
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
