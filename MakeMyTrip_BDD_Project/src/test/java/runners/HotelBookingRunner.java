package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * HotelBookingRunner - TestNG runner for HotelBooking.feature
 *
 * Parallel execution is enabled at the scenario level via @DataProvider(parallel=true).
 */
@CucumberOptions(
    features = "src/test/resources/features/HotelBooking.feature",
    glue     = { "stepDefinitions", "hooks" },
    tags     = "@HotelBooking",
    plugin   = {
        "pretty",
        "html:target/cucumber-reports/HotelBooking_Report.html",
        "json:target/cucumber-reports/HotelBooking_Report.json",
        "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
    },
    monochrome  = true,
    dryRun      = false
)
public class HotelBookingRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
