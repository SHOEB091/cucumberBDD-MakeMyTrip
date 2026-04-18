package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static Properties properties;
    private static final String CONFIG_PATH = "src/test/resources/config.properties";

    static {
        try {
            FileInputStream fis = new FileInputStream(CONFIG_PATH);
            properties = new Properties();
            properties.load(fis);
            fis.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties from: " + CONFIG_PATH, e);
        }
    }

    public static String get(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Property '" + key + "' not found in config.properties");
        }
        return value.trim();
    }

    public static String getBaseUrl()       { return get("base.url"); }
    public static String getBrowser()       { return get("browser"); }
    public static int getImplicitWait()     { return Integer.parseInt(get("implicit.wait")); }
    public static int getExplicitWait()     { return Integer.parseInt(get("explicit.wait")); }
    public static boolean isHeadless()      { return Boolean.parseBoolean(get("headless")); }
    public static boolean screenshotOnFail(){ return Boolean.parseBoolean(get("screenshot.on.failure")); }
    public static boolean screenshotOnPass(){ return Boolean.parseBoolean(get("screenshot.on.pass")); }
}
