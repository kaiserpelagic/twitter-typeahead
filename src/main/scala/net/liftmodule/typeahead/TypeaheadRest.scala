package net.liftmodules
package typeahead

import net.liftweb._
import common._
import http._
import rest._
import json._
import JsonDSL._


object TypeaheadRest extends RestHelper {

  serve {
    case "twitter" :: "typeahead" :: "prefetch" :: key :: Nil JsonGet _ => {
      val suggestions = TypeaheadSuggestions.getOrElseNil(key)
      JArray(suggestions map { JString } )
    }
    case "twitter" :: "typeahead" :: "remote" :: key :: query :: Nil JsonGet _ => {
      val suggestions = TypeaheadSuggestions.getOrElseNil(key) filter { _.toLowerCase.startsWith(query) }
      JArray(suggestions map { JString } )
    }
  }
}
