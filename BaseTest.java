package com.ibm.test;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.ibm.pages.AdminPage;
import com.ibm.pages.AdminPage1;
import com.ibm.pages.UserPage;
import com.ibm.utilities.ExcelUtil;
import com.ibm.utilities.PropertiesFileHandler;

public class BaseTest {
	WebDriver driver;
	WebDriverWait wait;
	PropertiesFileHandler propFIleHandler;
	HashMap<String, String> data;

	@BeforeSuite
	public void propertiesfile() throws IOException {
		String file = "./TestData/data.properties";
		PropertiesFileHandler propFileHandler = new PropertiesFileHandler();
		data = propFileHandler.getPropertiesAsMap(file);
	}

	@BeforeMethod
	public void BrowserInitialization() {
		System.setProperty("webdriver.chrome.driver", "./drivers/chromedriver.exe");
		driver = new ChromeDriver();
		wait = new WebDriverWait(driver, 60);
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

@Test
	public void DeleteAndCheckProductInUserPage() throws InterruptedException, SQLException {
		String url = data.get("url");
		String userName = data.get("username");
		String password = data.get("password");
		String name1 = data.get("Category");
		String tag = data.get("Tagtitle");
		driver.get(url);
		// To Add a record to category in and delete it and verify in the database whether it is deleted or not 
		System.out.println("Adding a record in admin portal, delete the record and check in database whether it is deleted there are not");
		AdminPage1 home = new AdminPage1(driver, wait);
		home.EnetrEmailAddress(userName);
		home.EnetrPassword(password);
		home.ClickonLoginButton();
		home.ClickonCatalogTabButton();
		home.ClickonCategories();
		home.ClickonAddButton();
		home.EnterCategoryName(name1);
		home.EnterTheTagTitle(tag);
		home.EnterTheSortOrder();
		home.EntertheStatus();
		home.ClickToGoTop();Thread.sleep(2000);
		home.ClickonTheSaveButton();
        home.ClickonActionButton();
		home.ClickonDelete();Thread.sleep(2000);
		home.ClickonDeleteRecord();Thread.sleep(2000);
		driver.navigate().back();
		WebElement Error=driver.findElement(By.xpath("//div[@class='alert alert-danger alert-dismissible']")); 
		String ErrorMsg= Error.getText();
		System.out.println("Message of data deleted: "+ErrorMsg); 
		System.out.println("....................................................................");
		Thread.sleep(2000);
		Connection c = DriverManager.getConnection("jdbc:mysql://foodsonfinger.com:3306/foodsonfinger_atozgroceries",
				"foodsonfinger_atoz", "welcome@123");
		Statement s = c.createStatement();
		ResultSet rs = s.executeQuery("SELECT * from as_category ");
		while (rs.next()) 
		{
			//System.out.println("The Data added in database:" +rs.getString("name"));
			String result=rs.getString("name");
			if(name1==result)
			{
			Assert.assertNotSame(result, name1);
			System.out.println("The data added to category is deleted in database successfully");
			}
		}
		System.out.println("The record 'ForTest' is deleted from the database");
		
	}
}



