import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

import org.openqa.selenium.{By, WebDriver}
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.Select

import collection.JavaConverters._

object First extends App {
  // Start chrome
  val driver: WebDriver = new ChromeDriver()
  driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS)
  driver.get("https://na.erp.weightwatchers.com/OA_HTML/AppsLocalLogin.jsp")

  // Put user and password
  driver.findElement(By.xpath("//input[@id='M__Id']")).sendKeys(System.getenv("USER_NAME_ORACLE"))
  driver.findElement(By.xpath("//input[@id='M__Ida']")).sendKeys(System.getenv("PASSWORD_ORACLE"))
  driver.findElement(By.xpath("//form[@id='myForm']")).submit()


  driver.findElements(By.id("AppsNavLink"))
    .asScala
    .zipWithIndex
    .foreach {
      case (elem, idx) if idx == 3 =>  elem.click()
      case _ =>
    }

  driver.findElement(By.xpath("//a[@id='N80']")).click()


  val simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd")
  val latestPeriodSelect = new Select(driver.findElement(By.id("N32"))).getFirstSelectedOption

  val latestPeriodSelectValue = latestPeriodSelect.getAttribute("value").split("\\|")
  val latestPeriodSelectText = latestPeriodSelect.getText
  val startPeriod = simpleDateFormat.parse(latestPeriodSelectValue(0))
  val endPeriod = simpleDateFormat.parse(latestPeriodSelectValue(1))
  val now = new Date()
  if (startPeriod.before(now) && latestPeriodSelectText.contains("~"))
  {
    driver.findElement(By.id("A241N1display")).sendKeys("WWC2017.007 - Core (2017)")
    driver.findElement(By.id("A251N1display")).sendKeys("30 - Build & QA")
    driver.findElement(By.id("A261N1display")).sendKeys("Labor")

    driver.findElement(By.id("B22_1_1")).sendKeys("8")
    driver.findElement(By.id("B22_1_2")).sendKeys("8")
    driver.findElement(By.id("B22_1_3")).sendKeys("8")
    driver.findElement(By.id("B22_1_4")).sendKeys("8")
    driver.findElement(By.id("B22_1_5")).sendKeys("8")

    driver.findElement(By.id("review_uixr")).click()

    // This will submit it.
//    driver.findElement(By.id("submit_uixr")).click()
  }

// This will close the driver.
//  driver.close()
}
