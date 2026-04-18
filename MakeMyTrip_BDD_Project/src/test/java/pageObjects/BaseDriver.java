package pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * BaseDriver - Parent class for all Page Object classes.
 * Provides shared driver and wait references.
 */
public class BaseDriver {

    protected WebDriver driver;
    protected WebDriverWait wait;

    public BaseDriver(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait   = wait;
    }
}
