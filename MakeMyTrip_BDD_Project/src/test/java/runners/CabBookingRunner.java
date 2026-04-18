package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * CabBookingRunner - TestNG runner for CabBooking.feature
 *
 * Parallel execution is enabled at the scenario level via @DataProvider(parallel=true).
 * The testng.xml file also runs this class in a dedicated thread for full isolation.
 */
@CucumberOptions(
    features = "src/test/resources/features/CabBooking.feature",
    glue     = { "stepDefinitions", "hooks" },
    tags     = "@CabBooking",
    plugin   = {
        "pretty",
        "html:target/cucumber-reports/CabBooking_Report.html",
        "json:target/cucumber-reports/CabBooking_Report.json",
        "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
    },
    monochrome  = true,
    dryRun      = false
)
public class CabBookingRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
