package com.sugarcrm.voodoo.automation;

import java.awt.Toolkit;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;

import org.openqa.selenium.JavascriptExecutor;

import com.google.common.base.Function;
import com.sugarcrm.voodoo.IAutomation.Strategy;
import com.sugarcrm.voodoo.Utils;
import com.sugarcrm.voodoo.Voodoo;


public class Selenium implements IFramework {
	
	private final Voodoo voodoo;
	private final Properties props;
	private final WebDriver browser;
	private HashMap<Integer, String> windowHandles = new HashMap<Integer, String>();
	private int windowIndex = 0;
	
	
	/**
	 * @param voodoo
	 * @param props
	 * @param browserType
	 * @throws Exception
	 */
	public Selenium(Voodoo voodoo, Properties props, Voodoo.InterfaceType browserType) throws Exception {
		this.voodoo = voodoo;
		this.props = props;
		this.browser = this.getBrowser(browserType);
	}
	
	/* (non-Javadoc)
	 * @see com.sugarcrm.voodoo.automation.IFramework#start(java.lang.String)
	 */
	@Override
	public void start() throws Exception {
	}

	/* (non-Javadoc)
	 * @see com.sugarcrm.voodoo.automation.IFramework#stop()
	 */
	@Override
	public void stop() throws Exception {
		browser.quit();
	}
	
	/* (non-Javadoc)
	 * @see com.sugarcrm.voodoo.automation.IFramework#closeWindow()
	 */
	public void closeWindow() throws Exception {
		this.browser.close();
	}
	
	/* (non-Javadoc)
	 * @see com.sugarcrm.voodoo.automation.IFramework#start(java.lang.String)
	 */
	@Override
	public void go(String url) throws Exception {
		browser.get(url);
		browser.switchTo().window(browser.getWindowHandle());
	}

	/* (non-Javadoc)
	 * @see com.sugarcrm.voodoo.automation.IFramework#acceptDialog()
	 */
	@Override
	public void acceptDialog() throws Exception {
		Alert alert = browser.switchTo().alert();
		alert.accept();
	}
	
	/* (non-Javadoc)
	 * @see com.sugarcrm.voodoo.automation.IFramework#focusByIndex(int)
	 */
	@Override
	public void focusByIndex(int index) throws Exception {
		Set<String> Handles = browser.getWindowHandles();
		while (Handles.iterator().hasNext()){
			String windowHandle = Handles.iterator().next();
			if (!windowHandles.containsValue(windowHandle)){
				windowHandles.put(windowIndex, windowHandle);
				windowIndex++;
			}
			Handles.remove(windowHandle);
		}
		browser.switchTo().window(windowHandles.get(index));
	}
	
	/* (non-Javadoc)
	 * @see com.sugarcrm.voodoo.automation.IFramework#focusByTitle(java.lang.String)
	 */
	@Override
	public void focusByTitle(String title) throws Exception {
		Set<String> Handles = browser.getWindowHandles();
		while (Handles.iterator().hasNext()){
			String windowHandle = Handles.iterator().next();
			WebDriver window = browser.switchTo().window(windowHandle);
			if (window.getTitle().equals(title)){
				break;
			}
			Handles.remove(windowHandle);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.sugarcrm.voodoo.automation.IFramework#focusByUrl(java.lang.String)
	 */
	@Override
	public void focusByUrl(String url) throws Exception {
		Set<String> Handles = browser.getWindowHandles();
		while (Handles.iterator().hasNext()){
			String windowHandle = Handles.iterator().next();
			WebDriver window = browser.switchTo().window(windowHandle);
			if (window.getCurrentUrl().equals(url)){
				break;
			}
			Handles.remove(windowHandle);
		}
	}
	
	/**
	 * @param browser
	 */
	public static void maximizeBrowserWindow(WebDriver browser) {
		java.awt.Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		browser.manage().window().setSize(new Dimension(screenSize.width, screenSize.height));
	}
	
	/**
	 * @param element
	 * @return
	 */
	public static String webElementToString(WebElement element) {
		List<WebElement> childElements = element.findElements(By.xpath("*"));
		String s = element.getTagName() + ":" + element.getText() + " ";
		for(WebElement we : childElements) {
			s += we.getTagName() + ":" + we.getText() + " ";
		}
		return s;
	}
	
	/**
	 * @param nativeOptions
	 * @param queryOptionNames
	 * @return
	 */
	public static boolean optionValuesEqual(List<WebElement> nativeOptions, Set<String> queryOptionNames) {
		Set<String> nativeOptionNames = new HashSet<String>();
		for (WebElement option : nativeOptions) {
			nativeOptionNames.add(option.getText());
		}
		if (nativeOptionNames.containsAll(queryOptionNames) && queryOptionNames.containsAll(nativeOptionNames)) return true;
		else return false;
	}
	
	/* (non-Javadoc)
	 * @see com.sugarcrm.voodoo.automation.IFramework#getSelected(com.sugarcrm.voodoo.automation.VControl)
	 */
	@Override
	public String getSelected(VControl control) throws Exception {
		try {
			Select dropDownList = new Select(((SeleniumVControl) control).webElement);
			WebElement selectedOption  = dropDownList.getFirstSelectedOption();
			return selectedOption.getText();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.sugarcrm.voodoo.automation.IFramework#getSelected(com.sugarcrm.voodoo.IAutomation.Strategy, java.lang.String)
	 */
	@Override
	public String getSelected(Strategy strategy, String hook) throws Exception {
		return this.getSelected(this.getControl(strategy, hook));
	}
	
	/* (non-Javadoc)
	 * @see com.sugarcrm.voodoo.automation.IFramework#select(com.sugarcrm.voodoo.automation.VControl, java.lang.String)
	 */
	@Override
	public void select(VControl control, String value) throws Exception {
		try {
			Select dropDownList = new Select(((SeleniumVControl) control).webElement);
			dropDownList.selectByVisibleText(value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.sugarcrm.voodoo.automation.IFramework#select(com.sugarcrm.voodoo.IAutomation.Strategy, java.lang.String, java.lang.String)
	 */
	@Override
	public void select(Strategy strategy, String hook, String value) throws Exception {
		this.select(this.getControl(strategy, hook), value);
	}
	
	/**
	 * @param selectElement
	 * @param actionElement
	 */
	public static void allOptionsAction(Select selectElement, WebElement actionElement) {
		List<WebElement> options = selectElement.getOptions();
		for (WebElement option : options) {
			selectElement.selectByVisibleText(option.getText());
			actionElement.click();
		}
	}
	
	/**
	 * @param selectElement
	 * @param actionOptionValues
	 * @param actionElement
	 * @throws Exception
	 */
	public static void optionAction(Select selectElement, Set<String> actionOptionValues, WebElement actionElement) throws Exception {
		List<WebElement> allOptions = selectElement.getOptions();
		HashSet<String> optionValues = new HashSet<String>();
		for(WebElement option : allOptions) {
			optionValues.add(option.getText());
//			System.out.println("Adding to options set:" + option.getText());
		}
		if(optionValues.containsAll(actionOptionValues)) {
			for(String option : actionOptionValues) {
				selectElement.selectByVisibleText(option);
				actionElement.click();
			}
		} else throw new Exception("Specified select option unavailable...");
	}
	
	/**
	 * @param tableElement
	 * @param rowRelativeXPathTextKey
	 * @param value
	 * @return
	 */
	public static boolean tableContainsValue(WebElement tableElement, String rowRelativeXPathTextKey, String value) {
		List<WebElement> rows = tableElement.findElements(By.tagName("tr"));
		for (WebElement row : rows) {
			if (row.findElement(By.xpath(rowRelativeXPathTextKey)).getText().equalsIgnoreCase(value)) return true;
		}
		return false;
	}
	
	/**
	 * @param table
	 * @param rowRelativeXPathTextKey
	 * @param rowRelativeXPathElementValue
	 * @return
	 * @throws Exception
	 */
	public static Map<String, WebElement> loadMapFromTable(WebElement table, String rowRelativeXPathTextKey, String rowRelativeXPathElementValue) throws Exception {
		Map<String, WebElement>	rowMap = new HashMap<String, WebElement>();
		List<WebElement> rows = table.findElements(By.tagName("tr"));
//		System.out.println("table # rows:" + rows.size());
		for (WebElement row : rows) {
//			List<WebElement> childTDs = row.findElements(By.tagName("td"));
//			for (WebElement childTD : childTDs) System.out.println("td text:" + childTD.getText());
			String k = row.findElement(By.xpath(rowRelativeXPathTextKey)).getText();
			WebElement v = row.findElement(By.xpath(rowRelativeXPathElementValue));
//			System.out.println("key text:" + k + ", value we:" + v.getTagName() + "/" + v.getText());
			rowMap.put(k, v);
		}
		return rowMap;
	}	
	
	/* (non-Javadoc)
	 * @see com.sugarcrm.voodoo.automation.IFramework#getControl(com.sugarcrm.voodoo.IAutomation.Strategy, java.lang.String)
	 */
	@Override
	public VControl getControl(Strategy strategy, String hook) throws Exception {
		WebElement webElement = null;
		switch (strategy) {
		case CSS:
			webElement = this.browser.findElement(By.cssSelector(hook));
			break;
		case XPATH:
			webElement = this.browser.findElement(By.xpath(hook));
			break;
		case ID:
			webElement = this.browser.findElement(By.id(hook));
			break;
		case NAME:
			webElement = this.browser.findElement(By.name(hook));
			break;
		case LINK:
			webElement = this.browser.findElement(By.linkText(hook));
			break;
		case PLINK:
			webElement = this.browser.findElement(By.partialLinkText(hook));
			break;
		}
		return new SeleniumVControl(this, webElement);
	}

	/* (non-Javadoc)
	 * @see com.sugarcrm.voodoo.automation.IFramework#getText(com.sugarcrm.voodoo.automation.VControl)
	 */
	@Override
	public String getText(VControl control) throws Exception {
		if (control instanceof SeleniumVControl) {
			WebElement we = ((SeleniumVControl) control).webElement;
			return we.getText();
		}
		else throw new Exception("Selenium: VControl not selenium-based.");
	}
	
	/* (non-Javadoc)
	 * @see com.sugarcrm.voodoo.automation.IFramework#getText(com.sugarcrm.voodoo.IAutomation.Strategy, java.lang.String)
	 */
	@Override
	public String getText(Strategy strategy, String hook) throws Exception {
		WebElement we = ((SeleniumVControl) this.getControl(strategy, hook)).webElement;
		return we.getText();
	}
	
	/* (non-Javadoc)
	 * @see com.sugarcrm.voodoo.automation.IFramework#click(com.sugarcrm.voodoo.automation.VControl)
	 */
	@Override
	public void click(VControl control) throws Exception {
		if (control instanceof SeleniumVControl) {
			((SeleniumVControl) control).webElement.click();
		}
		else throw new Exception("Selenium: VControl not selenium-based.");
	}

	/* (non-Javadoc)
	 * @see com.sugarcrm.voodoo.automation.IFramework#click(com.sugarcrm.voodoo.IAutomation.Strategy, java.lang.String)
	 */
	@Override
	public void click(Strategy strategy, String hook) throws Exception {
		this.click(this.getControl(strategy, hook));
	}
	
	/* (non-Javadoc)
	 * @see com.sugarcrm.voodoo.automation.IFramework#hover(com.sugarcrm.voodoo.automation.VControl)
	 */
	@Override
	public void hover(VControl control) throws Exception {
		if (control instanceof SeleniumVControl) {
			WebElement we = ((SeleniumVControl) control).webElement;
			Actions action = new Actions(browser);
			action.moveToElement(we).perform();
		}
		else throw new Exception("Selenium: VControl not selenium-based.");
	}
	
	/* (non-Javadoc)
	 * @see com.sugarcrm.voodoo.automation.IFramework#hover(com.sugarcrm.voodoo.IAutomation.Strategy, java.lang.String)
	 */
	@Override
	public void hover(Strategy strategy, String hook) throws Exception {
		WebElement we = ((SeleniumVControl) this.getControl(strategy, hook)).webElement;
		Actions action = new Actions(browser);
		action.moveToElement(we).perform();
	}
	
	/* (non-Javadoc)
	 * @see com.sugarcrm.voodoo.automation.IFramework#input(com.sugarcrm.voodoo.automation.VControl, java.lang.String)
	 */
	@Override
	public void input(VControl control, String input) throws Exception {
		if (control instanceof SeleniumVControl) {
			WebElement we = ((SeleniumVControl) control).webElement;
			we.clear();
			we.sendKeys(input);
		}
		else throw new Exception("Selenium: VControl not selenium-based.");
	}
	
	/* (non-Javadoc)
	 * @see com.sugarcrm.voodoo.automation.IFramework#input(com.sugarcrm.voodoo.IAutomation.Strategy, java.lang.String, java.lang.String)
	 */
	@Override
	public void input(Strategy strategy, String hook, String input) throws Exception {
		this.input(this.getControl(strategy, hook), input);
	}
	
	/* (non-Javadoc)
	 * @see com.sugarcrm.voodoo.automation.IFramework#rightClick(com.sugarcrm.voodoo.automation.VControl)
	 */
	@Override
	public void rightClick(VControl control) throws Exception {
		if (control instanceof SeleniumVControl) {
			WebElement we = ((SeleniumVControl) control).webElement;
			Actions action = new Actions(browser);
			action.contextClick(we).perform();
		}
		else throw new Exception("Selenium: VControl not selenium-based.");
	}
	
	/* (non-Javadoc)
	 * @see com.sugarcrm.voodoo.automation.IFramework#rightClick(com.sugarcrm.voodoo.IAutomation.Strategy, java.lang.String)
	 */
	@Override
	public void rightClick(Strategy strategy, String hook) throws Exception {
		WebElement we = ((SeleniumVControl) this.getControl(strategy, hook)).webElement;
		Actions action = new Actions(browser);
		action.contextClick(we).perform();
	}
	
	/* (non-Javadoc)
	 * @see com.sugarcrm.voodoo.automation.IFramework#rightClick(com.sugarcrm.voodoo.automation.VControl)
	 */
	@Override
	public void scroll(VControl control) throws Exception {
		if (control instanceof SeleniumVControl) {
			WebElement we = ((SeleniumVControl) control).webElement;
			int y = we.getLocation().y;
			((JavascriptExecutor) browser).executeScript("window.scrollBy(0," + y +");");
		}
		else throw new Exception("Selenium: VControl not selenium-based.");
	}
	
	/* (non-Javadoc)
	 * @see com.sugarcrm.voodoo.automation.IFramework#rightClick(com.sugarcrm.voodoo.IAutomation.Strategy, java.lang.String)
	 */
	@Override
	public void scroll(Strategy strategy, String hook) throws Exception {
		WebElement we = ((SeleniumVControl) this.getControl(strategy, hook)).webElement;
		int y = we.getLocation().y;
		((JavascriptExecutor) browser).executeScript("window.scrollBy(0," + y +");");
	}
	
    /* (non-Javadoc)
     * @see com.sugarcrm.voodoo.automation.IFramework#dragAndDrop(com.sugarcrm.voodoo.automation.VControl, com.sugarcrm.voodoo.automation.VControl)
     */
    @Override
    public void dragNDrop(VControl control1, VControl control2) throws Exception {
            if (control1 instanceof SeleniumVControl && control2 instanceof SeleniumVControl) {
                    WebElement draggable = ((SeleniumVControl) control1).webElement;
                    WebElement target = ((SeleniumVControl) control2).webElement;
                    Actions action = new Actions(browser);
                    action.dragAndDrop(draggable, target).build().perform();
            }
            else throw new Exception("Selenium: VControl not selenium-based.");
    }

    /* (non-Javadoc)
     * @see com.sugarcrm.voodoo.automation.IFramework#dragAndDrop(com.sugarcrm.voodoo.IAutomation.Strategy, java.lang.String, java.lang.String)
     */
    @Override
    public void dragNDrop(Strategy strategy, String hook1, String hook2) throws Exception {
            VControl vc1 =  this.getControl(strategy, hook1);
            VControl vc2 =  this.getControl(strategy, hook2);
            this.dragNDrop(vc1, vc2);
    }
    
	/* (non-Javadoc)
	 * @see com.sugarcrm.voodoo.automation.IFramework#wait(com.sugarcrm.voodoo.automation.VControl)
	 */
	@Override	
	public void waitFor(VControl control) throws Exception{
		long explicitWait = Long.parseLong(props.getProperty("perf.explicit_wait"));
		if (control instanceof SeleniumVControl) {
			final WebElement we = ((SeleniumVControl) control).webElement;
			WebDriverWait wait = new WebDriverWait(this.browser, explicitWait);
			wait.until(new Function<WebDriver, Boolean>() {
				public Boolean apply(WebDriver driver) {
					return we.isDisplayed();
				}
			});
		} else throw new Exception("Selenium: VControl not selenium-based.");
	}
	
	/* (non-Javadoc)
	 * @see com.sugarcrm.voodoo.automation.IFramework#wait(com.sugarcrm.voodoo.IAutomation.Strategy, java.lang.String)
	 */
	@Override
	public void waitFor(Strategy strategy, String hook) throws Exception{
		this.waitFor(this.getControl(strategy, hook));
	}
	
	/* (non-Javadoc)
	 * @see com.sugarcrm.voodoo.automation.IFramework#wait(com.sugarcrm.voodoo.automation.VControl, java.lang.String, java.lang.String)
	 */
	@Override
	public void waitFor(VControl control, String attribute, String value) throws Exception {
		final String vAttribute = attribute;
		final String vValue = value;
		long explicitWait = Long.parseLong(props.getProperty("perf.explicit_wait"));
		if (control instanceof SeleniumVControl) {
			final WebElement we = ((SeleniumVControl) control).webElement;
			WebDriverWait wait = new WebDriverWait(this.browser, explicitWait);
			   wait.until(new Function<WebDriver, Boolean>() {
			        public Boolean apply(WebDriver driver) {
			            return we.getAttribute(vAttribute).contains(vValue);
			        }
			    });
		} else throw new Exception("Selenium: VControl not selenium-based.");
	}
	
	/* (non-Javadoc)
	 * @see com.sugarcrm.voodoo.automation.IFramework#wait(com.sugarcrm.voodoo.IAutomation.Strategy, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void waitFor(Strategy strategy, String hook, String attribute, String value) throws Exception {
		this.waitFor(this.getControl(strategy, hook), attribute, value);
	}
	
    /* (non-Javadoc)
     * @see com.sugarcrm.voodoo.automation.IFramework#select(com.sugarcrm.voodoo.automation.VControl, boolean)
     */
    @Override
    public void select(VControl control, boolean isSelected) throws Exception {
            if (control instanceof SeleniumVControl) {
                    WebElement we =  ((SeleniumVControl) control).webElement;
                    if (!we.getAttribute("type").equals("checkbox")) {
                            //throw new Exception("Selenium: webElement is not a checkbox.");
                            throw new Exception("Selenium: this web element is not a checkbox.");
                    }

                    if (we.isSelected() != isSelected) {
                    we.click();    
                    }
            }
            else throw new Exception("Selenium: VControl not selenium-based.");
    }

    /* (non-Javadoc)
     * @see com.sugarcrm.voodoo.automation.IFramework#select(com.sugarcrm.voodoo.IAutomation.Strategy, java.lang.String, boolean)
     */
    @Override
    public void select(Strategy strategy, String hook, boolean isSelected) throws Exception {
            this.select(this.getControl(strategy, hook), isSelected);
    }
    
    /* (non-Javadoc)
     * @see com.sugarcrm.voodoo.automation.IFramework#getAttributeValue(com.sugarcrm.voodoo.automation.VControl, java.lang.String)
     */
    @Override
    public String getAttributeValue(VControl control, String attribute) throws Exception {
            if (control instanceof SeleniumVControl) {
                    WebElement we =  ((SeleniumVControl) control).webElement;
                    String value = we.getAttribute(attribute);
                    if (value == null) {
                        throw new Exception("Selenium: attribute does not exist.");
                    }
                    else {
                            return value;
                    }
            }
            else throw new Exception("Selenium: VControl not selenium-based.");
    }

    /* (non-Javadoc)
     * @see com.sugarcrm.voodoo.automation.IFramework#getAttributeValue(com.sugarcrm.voodoo.IAutomation.Strategy, java.lang.String, java.lang.String)
     */
    @Override
    public String getAttributeValue(Strategy strategy, String hook, String attribute) throws Exception {
            return this.getAttributeValue(this.getControl(strategy, hook), attribute);
    }


	/**
	 * @param browserType
	 * @return
	 * @throws Exception
	 */
	private WebDriver getBrowser(Voodoo.InterfaceType browserType) throws Exception {
		WebDriver webDriver = null;
		switch (browserType) {
		case FIREFOX:		
			String profileName = Utils.getCascadingPropertyValue(this.props, "default", "browser.firefox_profile");
			String ffBinaryPath = Utils.getCascadingPropertyValue(this.props, "//home//conrad//Applications//firefox-10//firefox", "browser.firefox_binary");
			FirefoxProfile ffProfile = (new ProfilesIni()).getProfile(profileName);
			FirefoxBinary ffBinary = new FirefoxBinary(new File(ffBinaryPath));
//			if (System.getProperty("headless") != null) {
//				FirefoxBinary ffBinary = new FirefoxBinary();//new File("//home//conrad//Applications//firefox-10//firefox"));
//				ffBinary.setEnvironmentProperty("DISPLAY", ":1"); 
//				webDriver = new FirefoxDriver(ffBinary, ffProfile);
//			}
			voodoo.log.info("Instantiating Firefox with profile name: " + profileName + " and binary path: " + ffBinaryPath);
			webDriver = new FirefoxDriver(ffBinary, ffProfile);
			break;
		case CHROME:
			String workingDir = System.getProperty("user.dir");
			ChromeOptions chromeOptions = new ChromeOptions();
			String chromeDriverLogPath = Utils.getCascadingPropertyValue(props, workingDir + "/log/chromedriver.log", "browser.chrome_driver_log_path");
			chromeOptions.addArguments("--log-path=" + chromeDriverLogPath);
			String chromeDriverPath = Utils.getCascadingPropertyValue(props, workingDir + "/etc/chromedriver-mac", "browser.chrome_driver_path");
//			chromeOptions.setBinary(new File(chromeDriverPath));
			System.setProperty("webdriver.chrome.driver", chromeDriverPath);
			voodoo.log.info("Instantiating Chrome with:\n    log path:" + chromeDriverLogPath + "\n    driver path: " + chromeDriverPath);
			webDriver = new ChromeDriver(chromeOptions);
			break;
		case IE:
			throw new Exception("Selenium: ie browser not yet supported.");
		case SAFARI:
			throw new Exception("Selenium: safari browser not yet supported.");
		default:
			throw new Exception("Selenium: browser type not recognized.");
		}
		long implicitWait = Long.parseLong(props.getProperty("perf.implicit_wait"));
		if (System.getProperty("headless") == null) {
			java.awt.Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			webDriver.manage().window().setSize(new Dimension(screenSize.width, screenSize.height));
		}
		webDriver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
		return webDriver;
	}
	
	public class SeleniumVControl extends VControl {
		public final WebElement webElement;
		public SeleniumVControl(IFramework vAutomation, WebElement webElement) {
			super(vAutomation);
			this.webElement = webElement;
		}
	}
}
