package com.example.testcontainers.framework.selenium;

import org.apache.commons.configuration2.SystemConfiguration;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.PullPolicy;

import java.io.File;
import java.time.Duration;
import java.util.HashMap;

public class SeleniumEnvironment {
    private static final Logger LOG = LoggerFactory.getLogger(SeleniumEnvironment.class);
    private RemoteWebDriver browserDriver;
    private BrowserWebDriverContainer browserContainer;
    private String browserToUse;
    private String serverUrl;

    public SeleniumEnvironment() {
        browserToUse = new SystemConfiguration().getString("BROWSER_TO_USE", "chrome");
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void createSeleniumEnvironment() {
        LOG.debug("Creating selenium environment...");
        switch (browserToUse) {
            case "firefox":
            case "chrome":
                browserDriver = browserContainer.getWebDriver();
                break;
            default:
                throw new IllegalArgumentException("Unsupported Browser type: " + browserToUse);
        }
        if (browserContainer != null) {
            LOG.info("###########################################################################");
            LOG.info("### To watch the tests you could connect via VNC: {}  ##", browserContainer.getVncAddress());
            LOG.info("###########################################################################");
        }

        browserDriver.manage().window().maximize();
        browserDriver.get(serverUrl);
        LOG.info("Surfed to the server at " + serverUrl);
        //possible SocketException is in IE OK (http://code.google.com/p/selenium/issues/detail?id=2568)
        LOG.info("#######################################");
        LOG.info("### Started Environment successfully ##");
        LOG.info("#######################################");
    }

    public WebDriver getBrowserDriver() {
        return browserDriver;
    }

    public BrowserWebDriverContainer configureBrowserContainer() {
        if (browserContainer == null) {
            browserContainer = new BrowserWebDriverContainer<>()
                    .withImagePullPolicy(PullPolicy.alwaysPull())
                    .withCapabilities(getBrowserCapabilities())
                    .withNetwork(Network.SHARED)
                    .withNetworkAliases("vnchost")
                    .withEnv("SCREEN_WIDTH", "1920")
                    .withEnv("SCREEN_HEIGHT", "1080")
                    .waitingFor(Wait.defaultWaitStrategy().withStartupTimeout(Duration.ofMinutes(20)))
                    .withClasspathResourceMapping("test_data/", "/test_data", BindMode.READ_ONLY)
                    .withRecordingMode(BrowserWebDriverContainer.VncRecordingMode.RECORD_FAILING, new File("results"));
        }
        return browserContainer;
    }

    private Capabilities getBrowserCapabilities() {
        switch (browserToUse) {
            case "firefox":
                return new FirefoxOptions();
            case "chrome":
                final String downloadFilePath = System
                        .getProperty("user.dir") + File.separator + "results" + File.separator + "temp";
                final HashMap<String, Object> chromePreferences = new HashMap<>();
                chromePreferences.put("profile.default_content_settings.popups", 0);
                chromePreferences.put("download.default_directory", downloadFilePath);
                final ChromeOptions options = new ChromeOptions();
                options.setExperimentalOption("prefs", chromePreferences);
                options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
                return options;
            default:
                throw new IllegalArgumentException("Unsupported Browser type: " + browserToUse);
        }
    }

    public void setMyApplServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
}
