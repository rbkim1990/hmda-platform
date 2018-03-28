package hmda.api.http.public

import akka.event.{LoggingAdapter, NoLogging}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.Timeout
import hmda.api.http.model.public.LarValidateRequest
import org.scalatest.{MustMatchers, WordSpec}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import hmda.model.filing.lar.LarGenerators._
import hmda.model.filing.lar.LoanApplicationRegister
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import hmda.api.http.codec.LarCodec._

class LarValidationHttpApiSpec
    extends WordSpec
    with MustMatchers
    with ScalatestRouteTest
    with LarValidationHttpApi {
  override val log: LoggingAdapter = NoLogging
  val ec: ExecutionContext = system.dispatcher
  override implicit val timeout: Timeout = Timeout(5.seconds)

  val lar = larGen.sample.get
  val larCsv = lar.toCSV

  "LAR HTTP Service" must {
    "parse a valid pipe delimited LAR and return JSON representation" in {
      val larValidateRequest = LarValidateRequest(larCsv)
      Post("/lar/parse", larValidateRequest) ~> larRoutes ~> check {
        response.status mustBe StatusCodes.OK
        responseAs[LoanApplicationRegister] mustBe lar
      }
    }

    "fail to parse an invalid pipe delimited LAR and return a list of errors" in {
      val csv = larGen.sample.get.toCSV
      val values = csv.split('|').map(_.trim)
      val badValues = values.head.replace("2", "A") ++ values.tail
      val badCsv = badValues.mkString("|")
      Post("/lar/parse", LarValidateRequest(badCsv)) ~> larRoutes ~> check {
        status mustBe StatusCodes.BadRequest
        responseAs[List[String]] mustBe List("id is not numeric")
      }
    }

    "fail to parse a valid pipe delimited LAR with too many fields and return an error" in {
      Post("/lar/parse", LarValidateRequest(larCsv + "|too|many|fields")) ~> larRoutes ~> check {
        status mustBe StatusCodes.BadRequest
        responseAs[List[String]] mustBe List(
          "An incorrect number of data fields were reported: 113 data fields were found, when 110 data fields were expected.")
      }
    }

  }

}
