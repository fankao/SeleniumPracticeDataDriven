package com.selenium;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.net.MalformedURLException;
import java.net.URL;
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
                webDriver.set(new FirefoxDriver(caps));
                break;
            case "chrome":
                caps = DesiredCapabilities.chrome();
                webDriver.set(new ChromeDriver(caps));
                break;
            case "internet explorer":
                caps = DesiredCapabilities.internetExplorer();
                webDriver.set(new
                        InternetExplorerDriver(caps));
                break;
            case "safari":
                caps = DesiredCapabilities.safari();
                webDriver.set(new SafariDriver(caps));
                break;
            case "microsoftedge":
                caps = DesiredCapabilities.edge();
                webDriver.set(new EdgeDriver(caps));
                break;
            case "iphone":
            case "ipad":
                if (browser.equalsIgnoreCase("ipad")) {
                    caps = DesiredCapabilities.ipad();
                } else {
                    caps = DesiredCapabilities.iphone();
                }
                mobileDriver.set(new IOSDriver<MobileElement>(
                        new URL(localHub), caps));
                break;
            case "android":
                caps = DesiredCapabilities.android();
                mobileDriver.set(new
                        AndroidDriver<MobileElement>(
                        new URL(localHub), caps));
                break;
        }
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
