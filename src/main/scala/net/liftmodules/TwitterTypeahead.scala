package net.liftmodules

import net.liftmodules.typeahead.{TypeaheadRest}
import _root_.net.liftweb.http._

object TwitterTypeahead {

  def init() {
    LiftRules.addToPackages("net.liftmodules.typeahead")
    LiftRules.statelessDispatch.append(TypeaheadRest) 
  }
}
