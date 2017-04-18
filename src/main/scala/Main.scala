import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

import org.openqa.selenium.{By, WebDriver, WebElement}
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.Select

import collection.JavaConverters._
import java.util.Properties

import Protocols.{TimeRange, TimeRangeWithOption}

object Protocols {
  case class TimeRange(startDate: Date, endDate: Date)
  case class TimeRangeWithOption(timeRange: TimeRange, option: WebElement) extends Ordered[TimeRangeWithOption] {
    override def compare(that: TimeRangeWithOption): Int =
      timeRange.startDate.compareTo(that.timeRange.startDate)
  }
}

object Main extends App {

  // Properties
  val props = new Properties()
  val input = new FileInputStream("config.properties")
  props.load(input)

  // Values
  val url = props.getProperty("url")
  val user = props.getProperty("user")
  val password = props.getProperty("password")

  // Start chrome
  val driver: WebDriver = new ChromeDriver()
  driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS)
  driver.get(url)

  // Put user and password
  driver.findElement(By.xpath("//input[@id='M__Id']")).sendKeys(user)
  driver.findElement(By.xpath("//input[@id='M__Ida']")).sendKeys(password)
  driver.findElement(By.xpath("//form[@id='myForm']")).submit()


  driver.findElements(By.id("AppsNavLink"))
    .asScala
    .zipWithIndex
    .foreach {
      case (elem, idx) if idx == 3 => elem.click()
      case _ =>
    }

  driver.findElement(By.xpath("//a[@id='N80']")).click()


  val simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd")
  val periodSelect = new Select(driver.findElement(By.id("N32")))
  val now = new Date()
  val timeRangeOption =
    periodSelect
      .getOptions
      .asScala
      .map(toRange)
      .filter(range => range.timeRange.startDate.before(now) && !range.option.getText.contains("~"))
      .sorted
      .map(range => range.option)
      .headOption

  timeRangeOption match {
    case Some(option) =>
      driver.findElement(By.id("N32")).sendKeys(option.getText)
      driver.findElement(By.id("A241N1display")).sendKeys("WWC2017.007 - Core (2017)")
      driver.findElement(By.id("A251N1display")).sendKeys("30 - Build & QA")
      driver.findElement(By.id("A261N1display")).sendKeys("Labor")

      driver.findElement(By.id("B22_1_1")).sendKeys("8")
      driver.findElement(By.id("B22_1_2")).sendKeys("8")
      driver.findElement(By.id("B22_1_3")).sendKeys("8")
      driver.findElement(By.id("B22_1_4")).sendKeys("8")
      driver.findElement(By.id("B22_1_5")).sendKeys("8")

      driver.findElement(By.id("review_uixr")).click()

    // This will submit it, uncomment after it works.
//      driver.findElement(By.id("submit_uixr")).click()

    case None =>
      println("Nothing to submit!")
  }

// This will close the driver.
//  driver.close()

  def toRange(option: WebElement): TimeRangeWithOption = {
    val latestPeriodSelectValue = option.getAttribute("value")
    val latestPeriodSelectValueRange = latestPeriodSelectValue.split("\\|")
    val timeRange = TimeRange(simpleDateFormat.parse(latestPeriodSelectValueRange(0)), simpleDateFormat.parse(latestPeriodSelectValueRange(1)))

    TimeRangeWithOption(timeRange, option)
  }
}
