package hmda.api.http.institutions.submissions

import akka.actor.ActorRef
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.pattern.ask
import hmda.api.http.InstitutionHttpApiSpec
import hmda.api.model.{ EditResult, _ }
import hmda.model.fi._
import hmda.persistence.HmdaSupervisor.FindProcessingActor
import hmda.persistence.processing.HmdaFileValidator
import hmda.validation.engine._

import scala.concurrent.Future

class SubmissionEditPathsSpec extends InstitutionHttpApiSpec {

  val supervisor = system.actorSelection("/user/supervisor")

  override def beforeAll(): Unit = {
    super.beforeAll()
    loadValidationErrors()
  }

  "return summary of validation errors" in {
    val expectedSummary = SummaryEditResults(
      EditResults(
        List(
          EditResult("S020", "Agency code must = 1, 2, 3, 5, 7, 9. The agency that submits the data must be the same as the reported agency code.", ts = true, List(LarEditResult(LarId("loan1")))),
          EditResult("S010", "The first record identifier in the file must = 1 (TS). The second and all subsequent record identifiers must = 2 (LAR).", ts = false, List(LarEditResult(LarId("loan1"))))
        )
      ),
      EditResults(
        List(
          EditResult("V280", "MSA/MD must = a valid Metropolitan Statistical Area or Metropolitan Division (if appropriate) code for period being processed or NA.", ts = false, List(LarEditResult(LarId("loan1")))),
          EditResult("V285", "State must = a valid FIPS code or (NA where MSA/MD = NA).", ts = false, List(LarEditResult(LarId("loan2")), LarEditResult(LarId("loan3"))))
        )
      ),
      EditResults.empty,
      MacroResults(List(MacroResult("Q007", List())))
    )

    getWithCfpbHeaders(s"/institutions/0/filings/2017/submissions/1/edits") ~> institutionsRoutes ~> check {
      status mustBe StatusCodes.OK
      responseAs[SummaryEditResults] mustBe expectedSummary
    }
  }

  "return a list of validation errors for a single type" in {
    val expectedEdits =
      EditResults(
        List(
          EditResult("V280", "MSA/MD must = a valid Metropolitan Statistical Area or Metropolitan Division (if appropriate) code for period being processed or NA.", ts = false, List(LarEditResult(LarId("loan1")))),
          EditResult("V285", "State must = a valid FIPS code or (NA where MSA/MD = NA).", ts = false, List(LarEditResult(LarId("loan2")), LarEditResult(LarId("loan3"))))
        )
      )

    getWithCfpbHeaders(s"/institutions/0/filings/2017/submissions/1/edits/validity") ~> institutionsRoutes ~> check {
      status mustBe StatusCodes.OK
      responseAs[EditResults] mustBe expectedEdits
    }

    getWithCfpbHeaders(s"/institutions/0/filings/2017/submissions/1/edits/macro") ~> institutionsRoutes ~> check {
      status mustBe StatusCodes.OK
      responseAs[MacroResults] mustBe MacroResults(List(MacroResult("Q007", List())))
    }
  }

  "Edits endpoint: return 404 for nonexistent institution" in {
    getWithCfpbHeaders(s"/institutions/xxxxx/filings/2017/submissions/1/edits") ~> institutionsRoutes ~> check {
      status mustBe StatusCodes.NotFound
      responseAs[ErrorResponse].message mustBe "Institution xxxxx not found"
    }
  }
  "Edits endpoint: return 404 for nonexistent filing period" in {
    getWithCfpbHeaders(s"/institutions/0/filings/1980/submissions/1/edits") ~> institutionsRoutes ~> check {
      status mustBe StatusCodes.NotFound
      responseAs[ErrorResponse].message mustBe "1980 filing period not found for institution 0"
    }
  }
  "Edits endpoint: return 404 for nonexistent submission" in {
    getWithCfpbHeaders(s"/institutions/0/filings/2017/submissions/0/edits") ~> institutionsRoutes ~> check {
      status mustBe StatusCodes.NotFound
      responseAs[ErrorResponse].message mustBe "Submission 0 not found for 2017 filing period"
    }
  }

  "Edit Type endpoint: return 404 for nonexistent institution" in {
    getWithCfpbHeaders(s"/institutions/xxxxx/filings/2017/submissions/1/edits/validity") ~> institutionsRoutes ~> check {
      status mustBe StatusCodes.NotFound
      responseAs[ErrorResponse].message mustBe "Institution xxxxx not found"
    }
  }
  "Edit Type endpoint: return 404 for nonexistent filing period" in {
    getWithCfpbHeaders(s"/institutions/0/filings/1980/submissions/1/edits/quality") ~> institutionsRoutes ~> check {
      status mustBe StatusCodes.NotFound
      responseAs[ErrorResponse].message mustBe "1980 filing period not found for institution 0"
    }
  }
  "Edit Type endpoint: return 404 for nonexistent submission" in {
    getWithCfpbHeaders(s"/institutions/0/filings/2017/submissions/0/edits/syntactical") ~> institutionsRoutes ~> check {
      status mustBe StatusCodes.NotFound
      responseAs[ErrorResponse].message mustBe "Submission 0 not found for 2017 filing period"
    }
  }

  private def loadValidationErrors(): Unit = {
    val supervisor = system.actorSelection("/user/supervisor")
    val id = "0"
    val period = "2017"
    val seqNr = 1
    val submissionId = SubmissionId(id, period, seqNr)
    val fHmdaValidator = (supervisor ? FindProcessingActor(HmdaFileValidator.name, submissionId)).mapTo[ActorRef]

    val s1 = ValidationError("loan1", ValidationErrorMetaData("S010"), Syntactical)
    val s2 = ValidationError("loan1", ValidationErrorMetaData("S020"), Syntactical)
    val v1 = ValidationError("loan1", ValidationErrorMetaData("V280"), Validity)
    val v2 = ValidationError("loan2", ValidationErrorMetaData("V285"), Validity)
    val v3 = ValidationError("loan3", ValidationErrorMetaData("V285"), Validity)
    val m1 = ValidationError("", ValidationErrorMetaData("Q007"), Macro)
    val larValidationErrors = LarValidationErrors(Seq(s1, s2, v1, v2, v3, m1))

    val tsValidationErrors = TsValidationErrors(Seq(s2))

    val fValidate: Future[Unit] = for {
      h <- fHmdaValidator
    } yield {
      h ! larValidationErrors
      h ! tsValidationErrors
    }

  }
}
