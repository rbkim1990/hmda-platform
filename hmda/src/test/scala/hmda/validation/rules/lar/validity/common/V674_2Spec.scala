package hmda.validation.rules.lar.validity

import hmda.model.filing.lar.LarGenerators._
import hmda.model.filing.lar.LoanApplicationRegister
import hmda.model.filing.lar.enums.ReverseMortgage
import hmda.validation.rules.EditCheck
import hmda.validation.rules.lar.LarEditCheckSpec

class V674_2Spec extends LarEditCheckSpec {
  override def check: EditCheck[LoanApplicationRegister] = V674_2

  property("If loan is a reverse mortgage, origination charges must be NA") {
    forAll(larGen) { lar =>
      whenever(lar.reverseMortgage != ReverseMortgage) {
        lar.mustPass
      }

      val appLar = lar.copy(reverseMortgage = ReverseMortgage)
      appLar
        .copy(
          loanDisclosure =
            appLar.loanDisclosure.copy(originationCharges = "-10.0"))
        .mustFail
      appLar
        .copy(
          loanDisclosure =
            appLar.loanDisclosure.copy(originationCharges = "10.0"))
        .mustFail
      appLar
        .copy(loanDisclosure =
          appLar.loanDisclosure.copy(originationCharges = "NA"))
        .mustPass
    }
  }
}
