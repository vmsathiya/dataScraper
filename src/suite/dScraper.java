package suite;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterTest;

public class dScraper {
	WebDriver driver = null;
	String resortName = null;
	String browserName = null;
	String URL = "https://www.makemytrip.com/hotels/hotel-details/?checkin=09092020&checkout=09102020&locusId=CTXMN&locusType=city&city=CTXMN&country=IN&roomStayQualifier=2e0e&_uCurrency=INR&reference=hotel&hotelId=RESORTID&type=hotel";
	int reviewCount = 1;
	FileWriter fw = null;
	String fileName = null;
	
	@Parameters({ "resortName", "resortID","browserName" })
	@BeforeTest
	public void setupDriver(String rName,String rID,String bName) throws IOException {
		resortName = rName;
		browserName = bName;
		fileName=System.getProperty("user.dir") + "/output/" + resortName + ".txt";
		URL = URL.replaceAll("RESORTID", rID);
		fw =new FileWriter(fileName);
		DesiredCapabilities cap = null;
		if (browserName.equals("Chrome")) {
			cap = DesiredCapabilities.chrome();
		} else if (browserName.equals("Mozila")) {
			cap = DesiredCapabilities.firefox();
		}
	
		cap.setPlatform(Platform.WINDOWS);
		driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"),cap);
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		driver.get(URL);
	}
  
	@Test
	public void dataScrapper() throws IOException, InterruptedException {
		//click search button
		driver.findElement(By.xpath("//div[@class='whatGuestsSaid']/p/a")).click();
		Boolean flag = true;
		while (flag) {
			//get the list of reviews
			List rl = driver.findElements(By.xpath("//section[@id='UserReviews']/div[@class='_RatingReview']/div[@class='reviewBox']"));
	
			System.out.println("No of reviews in current page: " + rl.size());
			
			for (int i = 1; i <= rl.size() ; i++) {
				String xpath1 = "//section[@id='UserReviews']/div[@class='_RatingReview']/div[@class='reviewBox'][" +i + "]/div[@class='reviewRow']/div[@class='reviewBoxLeft']/p[@class='latoBold font18 lineHight22 greenText']";
				String xpath2 = "//section[@id='UserReviews']/div[@class='_RatingReview']/div[@class='reviewBox'][" +i + "]/div[@class='reviewRow']/div[@class='reviewBoxLeft']/p[@class='font14 lineHight22']";
							
				if (IsDisplayed(driver,xpath2)) {
					String titleStr = "";
					if (IsDisplayed(driver,xpath1)) {
						titleStr = driver.findElement(By.xpath(xpath1)).getText();
					}
					String commentStr = driver.findElement(By.xpath(xpath2)).getText();
					titleStr = titleStr.replaceAll("\n+", " ");
					commentStr = commentStr.replaceAll("\n+", " ");					
					System.out.println(resortName + " : " + (reviewCount++) + " : " + titleStr + " :" + commentStr);
					fw.write(resortName + " : " + (reviewCount) + " : " + titleStr + " : " + commentStr + "\n");
				} else {
					JavascriptExecutor js = (JavascriptExecutor) driver;
					js.executeScript("window.scrollBy(0,800)"); //Scroll vertically down by 800 pixels
					if (IsDisplayed(driver,xpath2)) {
						String titleStr = "";
						if (IsDisplayed(driver,xpath1)) {
							titleStr = driver.findElement(By.xpath(xpath1)).getText();
							}
						String commentStr = driver.findElement(By.xpath(xpath2)).getText();
						titleStr = titleStr.replaceAll("\n+", " ");
						commentStr = commentStr.replaceAll("\n+", " ");
						System.out.println(resortName + " - " + (reviewCount++) + " : " + titleStr + " :" + commentStr);
						fw.write(resortName + " : " + (reviewCount) + " : " + titleStr + " : " + commentStr + "\n");
					} else {
						System.out.println("Element is InVisible ......Exiting ..");
					}
				}
			}
			
		flag=gotoNextPage(driver);
		Thread.sleep(5000);
		}

	}
 
	@AfterTest
	public void afterTest() throws IOException {
		if (driver != null) {
			driver.quit();
		}
		
		if (fw != null ) {
		fw.close();
		}
	}
	public Boolean IsDisplayed(WebDriver driver,String xpath) {
		try {
			return driver.findElement(By.xpath(xpath)).isDisplayed();
		} catch (Exception e){
			return false;
		}
	}
	public Boolean gotoNextPage(WebDriver driver) {
		Boolean flag = false;
		WebElement pagination = driver.findElement(By.xpath("//div[@id='detpg_review_ratings_pagination']/ul"));
		List < WebElement > pagelist = pagination.findElements(By.tagName("li"));
		for (int i=1; i <= pagelist.size(); i++ ) {
			int j = i +1;
			String str = driver.findElement(By.xpath("//div[@id='detpg_review_ratings_pagination']/ul/li[" + i + "]")).getAttribute("class");
			if (str.equals("active")) {
				String strnew = driver.findElement(By.xpath("//div[@id='detpg_review_ratings_pagination']/ul/li[" + j + "]")).getAttribute("class");
				 if (strnew.equals("disabled")) {
					 return false;
				 } else {
					 System.out.println("Tag Value : " + driver.findElement(By.xpath("//div[@id='detpg_review_ratings_pagination']/ul/li[" + j + "]/a")).getText());
					 driver.findElement(By.xpath("//div[@id='detpg_review_ratings_pagination']/ul/li[" + j + "]/a")).click();
					 return true;
				 }
			 }
		 }
		  return flag;
	}

}
