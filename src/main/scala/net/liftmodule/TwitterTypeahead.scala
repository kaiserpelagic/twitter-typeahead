package net.liftmodules

import _root_.net.liftweb.http._
import net.liftmodules.typeahead.{TypeaheadRest}

object TwitterTypeahead {

  def init() {
    LiftRules.addToPackages("net.liftmodules.typeahead")
    LiftRules.statelessDispatch.append(TypeaheadRest)
  }
}
