package com.example.testcontainers.junit;

import com.example.testcontainers.framework.selenium.SeleniumEnvironment;
import org.junit.*;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.time.Duration;

public abstract class TestCase {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestCase.class);
    private static final String TEST_CASE_BASE_PATH = "com.example.testcontainers.tests.";
    private static final String SERVICE_MYAPPL = "myappl_1";
    private static final int SERVICE_PORT = 8080;

    @ClassRule
    public static final DockerComposeContainer environment = new DockerComposeContainer(
            new File("src/test/resources/compose-test.yml"))
            .withPull(true)
            .withEnv("CONF_PATH", System.getProperty("CONF_PATH"))
            .withEnv("CONF_FOLDER", System.getProperty("CONF_FOLDER"))
            .withEnv("DOCKER_TAG", System.getProperty("DOCKER_TAG")).withLocalCompose(true)
            .waitingFor(SERVICE_MYAPPL, Wait.forHealthcheck().withStartupTimeout(Duration.ofMinutes(60)))
            .withExposedService(SERVICE_MYAPPL, SERVICE_PORT,
                    Wait.forHealthcheck().withStartupTimeout(Duration.ofMinutes(60)));
    private static final String DOCKER_HOST_BRIDGE_IP = "172.17.0.1";
    protected static volatile SeleniumEnvironment seleniumEnvironment;
    @ClassRule
    public static BrowserWebDriverContainer browser = configureBrowserContainer();
    private static boolean notInitialized = true;

    @Rule
    public TestRule testNameLogger = new TestWatcher() {
        @Override
        protected void starting(final Description description) {
            LOGGER.info("----------------------------------------------------------------------------------");
            LOGGER.info("Starting test: {}::{}", getTestCaseName(description), description.getMethodName());
            LOGGER.info("----------------------------------------------------------------------------------");
        }

        @Override
        protected void failed(final Throwable e, final Description description) {
            LOGGER.info("----------------------------------------------------------------------------------");
            String testCaseName = getTestCaseName(description);
            LOGGER.info("TEST FAILED ({}::{}) ", testCaseName, description.getMethodName(), e);
            LOGGER.info("----------------------------------------------------------------------------------");
        }

        @Override
        protected void succeeded(final Description description) {
            LOGGER.info("----------------------------------------------------------------------------------");
            LOGGER.info("TEST SUCCEEDED ({}::{})", getTestCaseName(description), description.getMethodName());
            LOGGER.info("----------------------------------------------------------------------------------");
        }

        private String getTestCaseName(final Description description) {
            final String fqClassName = description.getClassName();
            if (fqClassName.startsWith(TEST_CASE_BASE_PATH)) {
                return fqClassName.substring(TEST_CASE_BASE_PATH.length());
            }
            return getSimpleClassName(fqClassName);
        }

        private String getSimpleClassName(final String fqClassName) {
            final int packageNameEnd = fqClassName.lastIndexOf('.');
            if (packageNameEnd >= fqClassName.length()) {
                return fqClassName;
            }
            return fqClassName.substring(packageNameEnd + 1);
        }
    };

    private static BrowserWebDriverContainer configureBrowserContainer() {
        if (seleniumEnvironment == null) {
            seleniumEnvironment = new SeleniumEnvironment();
        }
        return seleniumEnvironment.configureBrowserContainer();
    }


    @BeforeClass
    public static void setUpSeleniumEnvironment() {
        if (seleniumEnvironment == null) {
            seleniumEnvironment = new SeleniumEnvironment();
        }
        seleniumEnvironment.setMyApplServerUrl("http://" + DOCKER_HOST_BRIDGE_IP + ":" + environment
                .getServicePort(SERVICE_MYAPPL, SERVICE_PORT) + "/");
        if (notInitialized) {
            seleniumEnvironment.createSeleniumEnvironment();
            notInitialized = false;
        }
    }


}
