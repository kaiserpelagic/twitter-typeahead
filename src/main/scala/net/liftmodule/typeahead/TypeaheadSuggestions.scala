package net.liftmodules
package typeahead

import scala.collection.mutable.Map

object TypeaheadSuggestions {
  
  private val _suggestions = Map[String, List[String]]()
  
  def getOrElseNil(key: String) = _suggestions.get(key) getOrElse Nil
  
  def register(key: String, values: List[String]) = _suggestions.put(key,values)
}
