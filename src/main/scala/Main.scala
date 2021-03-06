import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.{Calendar, Date, Properties}
import java.util.concurrent.TimeUnit

import org.openqa.selenium.{By, WebDriver, WebElement}
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.Select

import collection.JavaConverters._
import Protocols.{TimeRange, TimeRangeWithOption}

import scala.util.Try

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
  val project = props.getProperty("project")
  val task = props.getProperty("task")

  // Start chrome
  implicit val driver: WebDriver = new ChromeDriver()
  driver.manage().timeouts().implicitlyWait(500, TimeUnit.SECONDS)
  driver.get(url)

  implicit val timeToWait = 1000

  // Put user and password
  waitTillDisplayedToSendKeys(By.xpath("//input[@id='M__Id']"), user)
  waitTillDisplayedToSendKeys(By.xpath("//input[@id='M__Ida']"), password)
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
      .filter(_.getText != "More Periods...")
      .map(toRange)
      .filter(range => range.timeRange.startDate.before(now) && !range.option.getText.contains("~"))
      .sorted
      .headOption

  timeRangeOption match {
    case Some(TimeRangeWithOption(timeRange, option)) =>
      periodSelect.selectByVisibleText(option.getText)
      waitTillDisplayedToSendKeys(By.id("A241N1display"), project)
      waitTillDisplayedToSendKeys(By.id("A251N1display"), task)
      waitTillDisplayedToSendKeys(By.id("A261N1display"), "Labor")

      waitTillDisplayedToSendKeys(By.id("B22_1_1"), "8")
      waitTillDisplayedToSendKeys(By.id("B22_1_2"), "8")
      waitTillDisplayedToSendKeys(By.id("B22_1_3"), "8")
      waitTillDisplayedToSendKeys(By.id("B22_1_4"), "8")

      // Happy Fridays!!!!
      if (isWeekOnSummerFriday(timeRange.endDate)) waitTillDisplayedToSendKeys(By.id("B22_1_5"), "4")
      else waitTillDisplayedToSendKeys(By.id("B22_1_5"), "8")

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

  // Wait till the element is displayed.
  def waitTillDisplayedToSendKeys(
    by: By,
    value: String)(implicit
    driver: WebDriver,
    timeToWait: Int): Unit = {

    def runTill(no: Int): Boolean =
      if (no > 0) {
        try { driver.findElement(by).click(); true }
        catch {
          case _: Throwable =>
            runTill(no - 1)
        }
      }
      else false

    if (runTill(10)) driver.findElement(by).sendKeys(value)
    else println(s"Failed to find $by")
  }

  // Check for summer fridays
  def isWeekOnSummerFriday(endDayOfWeek: Date): Boolean = {
    val calendar = Calendar.getInstance()
    calendar.setTime(endDayOfWeek)

    // Change endDayOfWeek from Saturday to Friday!
    calendar.add(Calendar.DAY_OF_MONTH, -1)
    val thisYear = calendar.get(Calendar.YEAR)
    val dayBefore = new Date(calendar.getTime.getTime)

    val memorialDay = simpleDateFormat.parse(s"$thisYear/05/29")
    val laborDay = simpleDateFormat.parse(s"$thisYear/09/04")

    if (isBetweenOrEqual(memorialDay, laborDay, dayBefore)) true
    else false
  }

  def isBetweenOrEqual(to: Date, from: Date, theDate: Date): Boolean = theDate.equals(to) || theDate.after(to) || theDate.equals(from) || theDate.before(from)
}
