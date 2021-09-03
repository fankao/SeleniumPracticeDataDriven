package com.selenium;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class CreateDriver {

    private static CreateDriver instance = null;
    private String browserHandle = null;
    private static final int IMPLICIT_TIMEOUT = 0;

    private ThreadLocal<WebDriver> webDriver =
            new ThreadLocal<WebDriver>();
    private ThreadLocal<AppiumDriver<MobileElement>> mobileDriver =
            new ThreadLocal<AppiumDriver<MobileElement>>();
    private ThreadLocal<String> sessionId =
            new ThreadLocal<String>();
    private ThreadLocal<String> sessionBrowser =
            new ThreadLocal<String>();
    private ThreadLocal<String> sessionPlatform =
            new ThreadLocal<String>();
    private ThreadLocal<String> sessionVersion =
            new ThreadLocal<String>();
    private String getEnv = null;

    private CreateDriver() {

    }

    public static CreateDriver getInstance() {
        if (instance == null) {
            instance = new CreateDriver();
        }
        return instance;
    }

    /**
     * Allow users to create a new instance of the driver for testing browser or mobile devices.
     * The method will take parameters for browser, platform, environment, and
     * optional preferences. Based on these preferences, the
     * WebDriver/AppiumDriver of choice will be created
     *
     * @param browser        Chrome, Firefox, Internet Explorer, Microsoft Edge, Opera, Safari
     *                       (iPhone/iPad, or Android for mobile)
     * @param platform       Linux, Windows, Mac, Sierra, Win10 (iPhone/iPad, or Android for
     *                       mobile)
     * @param environment    Local, remote, and Sauce Labs
     * @param optPreferences Map of driver preferences (this will be covered later in detail)
     */
    @SafeVarargs
    public final void setDriver(String browser,
                                String platform,
                                String environment,
                                Map<String, Object>... optPreferences) throws MalformedURLException {

        DesiredCapabilities caps = null;
        String localHub = "http://127.0.0.1:4723/wd/hub";
        String getPlatform = null;

        switch (browser) {
            case "firefox":
                caps = DesiredCapabilities.firefox();
                FirefoxOptions ffOpts = new FirefoxOptions();
                FirefoxProfile ffProfile = new FirefoxProfile();
                ffProfile.setPreference("browser.autofocus", true);
                caps.setCapability(FirefoxDriver.PROFILE, ffProfile);
                caps.setCapability("marionette", true);
                System.setProperty("webdriver.gecko.driver",
                        "gecko_driver_windows_path/geckodriver.exe");
                if ( optPreferences.length > 0 ) {
                    processFFProfile(ffProfile, optPreferences);
                }
                webDriver.set(new FirefoxDriver(ffOpts.merge(caps)));
                break;
            case "chrome":
                caps = DesiredCapabilities.chrome();
                ChromeOptions chOptions = new ChromeOptions();
                Map<String, Object> chromePrefs =
                        new HashMap<String, Object>();
                chromePrefs.put("credentials_enable_service",
                        false);
                chOptions.setExperimentalOption("prefs",
                        chromePrefs);
                chOptions.addArguments("--disable-plugins",
                        "--disable-extensions",
                        "--disable-popup-blocking");
                caps.setCapability(ChromeOptions.CAPABILITY,
                        chOptions);
                caps.setCapability("applicationCacheEnabled",
                        false);
                System.setProperty("webdriver.chrome.driver",
                        "chrome_driver_windows_path/chromedriver.exe");
                if ( optPreferences.length > 0 ) {
                    processCHOptions(chOptions, optPreferences);
                }
                webDriver.set(new ChromeDriver(chOptions.merge(caps)));
                break;
            case "internet explorer":
                caps = DesiredCapabilities.internetExplorer();
                InternetExplorerOptions ieOpts =
                        new InternetExplorerOptions();
                ieOpts.requireWindowFocus();
                ieOpts.merge(caps);
                caps.setCapability("requireWindowFocus",
                        true);
                System.setProperty("webdriver.ie.driver",
                        "ie_driver_windows_path/IEDriverServer.exe");
                if ( optPreferences.length > 0 ) {
                    processDesiredCaps(caps, optPreferences);
                }
                webDriver.set(new InternetExplorerDriver(
                        ieOpts.merge(caps)));
                break;
            case "safari":
                caps = DesiredCapabilities.safari();
                SafariOptions safariOpts = new SafariOptions();
                safariOpts.setUseCleanSession(true);
                caps.setCapability(SafariOptions.CAPABILITY,
                        safariOpts);
                caps.setCapability("autoAcceptAlerts",
                        true);
                webDriver.set(new SafariDriver(safariOpts.merge(caps)));
                break;
            case "microsoftedge":
                caps = DesiredCapabilities.edge();
                EdgeOptions edgeOpts = new EdgeOptions();
                edgeOpts.setPageLoadStrategy("normal");
                caps.setCapability(EdgeOptions.CAPABILITY,
                        edgeOpts);
                caps.setCapability("requireWindowFocus",
                        true);
                webDriver.set(new EdgeDriver(edgeOpts.merge(caps)));
                break;
            case "iphone":
            case "ipad":
                if (browser.equalsIgnoreCase("ipad")) {
                    caps = DesiredCapabilities.ipad();
                } else {
                    caps = DesiredCapabilities.iphone();
                }
                caps.setCapability("appName",
                        "https://myapp.com/myApp.zip");
                caps.setCapability("udid",
                        "12345678"); // physical device
                caps.setCapability("device",
                        "iPhone"); // or iPad
                mobileDriver.set(new IOSDriver<MobileElement>
                        (new URL("http://127.0.0.1:4723/wd/hub"),
                                caps));
                break;
            case "android":
                caps = DesiredCapabilities.android();
                caps.setCapability("appName",
                        "https://myapp.com/myApp.apk");
                caps.setCapability("udid",
                        "12345678"); // physical device
                caps.setCapability("device",
                        "Android");
                mobileDriver.set(new AndroidDriver<MobileElement>
                        (new URL("http://127.0.0.1:4723/wd/hub"),
                                caps));
                break;
        }

        getEnv = "local";
        getPlatform = platform;
        if (browser.equalsIgnoreCase("iphone") ||
                browser.equalsIgnoreCase("android")) {
            sessionId.set(((IOSDriver<MobileElement>)
                    mobileDriver.get()).getSessionId().toString());
            sessionId.set(((AndroidDriver<MobileElement>)
                    mobileDriver.get()).getSessionId().toString());
            sessionBrowser.set(browser);
            sessionVersion.set(caps.getCapability("deviceName").toString());
            sessionPlatform.set(getPlatform);
        } else {
            sessionId.set(((RemoteWebDriver) webDriver.get())
                    .getSessionId().toString());
            sessionBrowser.set(caps.getBrowserName());
            sessionVersion.set(caps.getVersion());
            sessionPlatform.set(getPlatform);
        }
    }

    private void processDesiredCaps(DesiredCapabilities caps, Map<String, Object>[] optPreferences) {
    }

    private void processCHOptions(ChromeOptions chOptions, Map<String, Object>[] optPreferences) {
    }

    private void processFFProfile(FirefoxProfile ffProfile, Map<String, Object>[] optPreferences) {
    }

    /**
     * overloaded setDriver method to switch driver to specific WebDriver
     * if running concurrent drivers
     * *
     *
     * @param driver WebDriver instance to switch to
     */
    public void setDriver(WebDriver driver) {
        webDriver.set(driver);
        sessionId.set(((RemoteWebDriver) webDriver.get())
                .getSessionId().toString());
        sessionBrowser.set(((RemoteWebDriver) webDriver.get())
                .getCapabilities().getBrowserName());
        sessionPlatform.set(((RemoteWebDriver) webDriver.get())
                .getCapabilities().getPlatform().toString());
    }

    /**
     * overloaded setDriver method to switch driver to specific AppiumDriver
     * if running concurrent drivers
     *
     * @param driver AppiumDriver instance to switch to
     */
    public void setDriver(AppiumDriver<MobileElement> driver) {
        mobileDriver.set(driver);
        sessionId.set(mobileDriver.get()
                .getSessionId().toString());
        sessionBrowser.set(mobileDriver.get()
                .getCapabilities().getBrowserName());
        sessionPlatform.set(mobileDriver.get()
                .getCapabilities().getPlatform().toString());
    }

    /**
     * getDriver method will retrieve the active WebDriver
     * <p>
     * *@return WebDriver
     */
    public WebDriver getDriver() {
        return webDriver.get();
    }

    /**
     * getDriver method will retrieve the active AppiumDriver
     *
     * @param mobile boolean parameter
     * @return AppiumDriver
     */
    public AppiumDriver<MobileElement> getDriver(boolean mobile) {
        return mobileDriver.get();
    }

    /**
     * getCurrentDriver method will retrieve the active WebDriver
     * or AppiumDriver
     *
     * @return webDriver
     */
    public WebDriver getCurrentDriver() {
        if (getInstance().getSessionBrowser().contains("iphone") ||
                getInstance().getSessionBrowser().contains("ipad") ||
                getInstance().getSessionBrowser().contains("android")) {
            return getInstance().getDriver(true);
        }
        return getInstance().getDriver();
    }

    /**
     * driverWait method pauses the driver in seconds
     * *
     *
     * @param seconds to pause
     */
    public void driverWait(long seconds) {
        try {
            Thread.sleep(seconds);
        } catch (InterruptedException e) {
            //TODO: todo something
            // e.printStackTrace();
        }

    }

    /**
     * driverRefresh method reloads the current browser page
     */
    public void driverRefresh() {
        getCurrentDriver().navigate().refresh();
    }

    /**
     * closeDriver method quits the current active driver
     */
    public void closeDriver() {
        try {
            getCurrentDriver().quit();
        } catch (Exception e) {
            //do something
        }

    }

    public String getSessionId() {
        return sessionId.get();
    }

    public String getSessionBrowser() {
        return sessionBrowser.get();
    }

    public String getSessionPlatform() {
        return sessionPlatform.get();
    }

    public String getSessionVersion() {
        return sessionVersion.get();
    }
}
