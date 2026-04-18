package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

/**
 * ExtentReportManager - Singleton manager for Extent Reports.
 *
 * NOTE: The primary Extent reporting in this project is handled automatically
 * by the extentreports-cucumber7-adapter via extent.properties.
 *
 * This class provides an additional standalone ExtentReports instance that can
 * be used for custom logging outside of the Cucumber lifecycle if needed.
 */
public class ExtentReportManager {

    private static ExtentReports extentReports;
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    private static final String REPORT_PATH =
            System.getProperty("user.dir") + "/target/ExtentReport/CustomReport.html";

    public static synchronized ExtentReports getInstance() {
        if (extentReports == null) {
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(REPORT_PATH);
            sparkReporter.config().setDocumentTitle("MakeMyTrip BDD Automation Report");
            sparkReporter.config().setReportName("End-to-End Test Execution Results");
            sparkReporter.config().setTheme(Theme.DARK);
            sparkReporter.config().setTimeStampFormat("dd-MMM-yyyy HH:mm:ss");
            sparkReporter.config().setEncoding("UTF-8");

            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            extentReports.setSystemInfo("Project",     "MakeMyTrip BDD Automation");
            extentReports.setSystemInfo("Framework",   "Cucumber 7 + TestNG + Selenium");
            extentReports.setSystemInfo("Browser",     ConfigReader.getBrowser());
            extentReports.setSystemInfo("Environment", "QA");
            extentReports.setSystemInfo("Author",      "Automation Team");
        }
        return extentReports;
    }

    public static ExtentTest getTest()                     { return extentTest.get(); }
    public static void setTest(ExtentTest test)            { extentTest.set(test); }
    public static void removeTest()                        { extentTest.remove(); }

    public static synchronized void flushReports() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }
}
