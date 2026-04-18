package utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtil {

    private static final String SCREENSHOT_DIR = System.getProperty("user.dir")
            + File.separator + "target"
            + File.separator + "Screenshots"
            + File.separator;

    /**
     * Captures a screenshot and saves it to target/Screenshots/ with a timestamp.
     *
     * @param driver   active WebDriver instance
     * @param fileName a descriptive name for the screenshot (no extension)
     * @return absolute path of the saved screenshot, or empty string on failure
     */
    public static String capture(WebDriver driver, String fileName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
        String threadId   = String.valueOf(Thread.currentThread().getId());
        String fullName   = fileName + "_Thread" + threadId + "_" + timestamp + ".png";
        String destPath   = SCREENSHOT_DIR + fullName;

        try {
            File srcFile  = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destFile = new File(destPath);
            FileUtils.forceMkdirParent(destFile);
            FileUtils.copyFile(srcFile, destFile);
            System.out.println("[ScreenshotUtil] Screenshot saved => " + destPath);
            return destPath;
        } catch (IOException e) {
            System.err.println("[ScreenshotUtil] Failed to save screenshot: " + e.getMessage());
            return "";
        }
    }

    /**
     * Captures a screenshot and returns it as a byte array (for embedding in Cucumber/Extent report).
     *
     * @param driver active WebDriver instance
     * @return screenshot bytes, or empty array on failure
     */
    public static byte[] captureAsBytes(WebDriver driver) {
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            System.err.println("[ScreenshotUtil] Failed to capture screenshot as bytes: " + e.getMessage());
            return new byte[0];
        }
    }
}
