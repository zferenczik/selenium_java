package com.mozilla.example;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

// based on https://github.com/mdn/headless-examples/blob/master/headlessfirefox-gradle/src/main/java/com/mozilla/example/HeadlessFirefoxSeleniumExample.java
// https://blog.mozilla.org/firefox/profiles-shmofiles-whats-browser-profile-anyway/
public class HeadlessFirefoxSeleniumExample {
	static Map<String, String> browserDrivers = new HashMap<>();
	static {
		browserDrivers.put("chrome", "chromeDriverPath");
		browserDrivers.put("firefox", "geckoDriverPath");
		browserDrivers.put("edge", "edgeDriverPath");
		browserDrivers.put("safari", null);
	}
	// application configuration file
	private static String propertiesFileName = "application.properties";
	private static Map<String, String> propertiesMap = new HashMap<>();

	public static void main(String[] args) {
		propertiesMap = PropertiesParser
				.getProperties(String.format("%s/src/main/resources/%s",
						System.getProperty("user.dir"), propertiesFileName));

		// https://www.programcreek.com/java-api-examples/?api=org.openqa.selenium.firefox.FirefoxBinary
		FirefoxProfile firefoxProfile = new FirefoxProfile();
		FirefoxBinary firefoxBinary = new FirefoxBinary();
		/*
		 * FirefoxBrowserProfile firefoxBrowserProfile = new
		 * FirefoxBrowserProfile(); String sProfile =
		 * firefoxBrowserProfile.getDefaultProfile(); try { firefoxBinary = new
		 * FirefoxBinary(new
		 * File(firefoxBrowserProfile.getFirefoxBinInstallPath())); } catch
		 * (Exception e) { }
		 */
		// TODO: convert to cmdline parameter
		firefoxBinary.addCommandLineOptions("--headless");
		String browserDriver = (propertiesMap
				.get(browserDrivers.get("browser")) != null)
						? propertiesMap.get(browserDrivers.get("browser"))
						: "/home/sergueik/Downloads/geckodriver";
		System.setProperty("webdriver.gecko.driver", browserDriver);
		FirefoxOptions firefoxOptions = new FirefoxOptions();
		firefoxOptions.setBinary(firefoxBinary);
		FirefoxDriver driver = new FirefoxDriver(firefoxOptions);
		try {
			driver.get("http://www.google.com");
			driver.manage().timeouts().implicitlyWait(4, TimeUnit.SECONDS);
			WebElement queryBox = driver.findElement(By.name("q"));
			queryBox.sendKeys("headless firefox");
			WebElement searchBtn = driver.findElement(By.name("btnK"));
			try {
				searchBtn.click();
				WebElement iresDiv = driver.findElement(By.id("ires"));
				iresDiv.findElements(By.tagName("a")).get(0).click();
				System.out.println("Response: " + driver.getPageSource());
			} catch (WebDriverException e) {
				System.err.println("Excepion (ignored) " + e.toString());
				// sporadic exceptions, in headless mode when there is avisible Firefox browser opened on the same desktop and has got focus whle test is run
				// Element <input name="btnK" type="submit"> is not clickable at
				// point (607,411) because another element <b> obscures it
				// ExceptionHandler::GenerateDump cloned child 4157
				// ExceptionHandler::SendContinueSignalToChild sent continue signal to child
				// ExceptionHandler::WaitForContinueSignal waiting for continue signal...
// take screenshot in catch block.
				try {
					System.err.println("Taking a screenshot");
					// take a screenshot
					File scrFile = ((TakesScreenshot) driver)
							.getScreenshotAs(OutputType.FILE);
					String currentDir = System.getProperty("user.dir");
					// save the screenshot in png format on the disk.
					FileUtils.copyFile(scrFile,
							new File(FilenameUtils.concat(currentDir, "screenshot.png")));
				} catch (IOException ex) {
				System.err.println("Excepion when taking the screenshot (ignored) " + ex.toString());
					// ignore
				}
			}
		} finally {
			if (driver != null) {
				driver.close();
				try {
					driver.quit();
				} catch (SessionNotCreatedException e) {
					// Tried to run command without establishing a connection
				}
			}
		}
	}
}
