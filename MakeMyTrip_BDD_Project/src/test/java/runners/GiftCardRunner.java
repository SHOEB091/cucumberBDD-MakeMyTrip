package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * GiftCardRunner - TestNG runner for GiftCard.feature
 *
 * Parallel execution is enabled at the scenario level via @DataProvider(parallel=true).
 */
@CucumberOptions(
    features = "src/test/resources/features/GiftCard.feature",
    glue     = { "stepDefinitions", "hooks" },
    tags     = "@GiftCard",
    plugin   = {
        "pretty",
        "html:target/cucumber-reports/GiftCard_Report.html",
        "json:target/cucumber-reports/GiftCard_Report.json",
        "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
    },
    monochrome  = true,
    dryRun      = false
)
public class GiftCardRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
