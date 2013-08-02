package net.liftmodules
package typeahead

import net.liftweb.json.Serialization.{read, write}
import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import net.liftweb.common._
import net.liftweb.http._
import SHtml._
import net.liftweb.http.js.{JsObj, JsCmds, JE} 
import net.liftweb.http.SHtml.ElemAttr
import net.liftweb.util.Helpers._
import JsCmds._ 
import JE._

import scala.xml._


object TwitterCandidates extends RequestVar(scala.collection.mutable.Map.empty[String, List[String]])

object TwitterTypeahead {


  def local(name: String, candidates: List[String], deflt: Box[String],
    f: String => Any, attrs: ElemAttr*) = {
    
    val options = (
      ("name" -> name) ~ 
      ("local" -> JArray(candidates map { JString(_) }))
    )

    typeahead(name, candidates, deflt, f, options, attrs: _*)
  }

  def prefetch(name: String, candidates: List[String], deflt: Box[String],
    f: String => Any, attrs: ElemAttr*) = {
    
    val options = (
      ("name" -> name) ~ 
      ("prefetch" -> JString(prefetchUrl("id")))
    )
    typeahead(name, candidates, deflt, f, options, attrs: _*)
  }
  
  def remote(name: String, candidates: List[String], deflt: Box[String],
    f: String => Any, attrs: ElemAttr*) = {

    val options = (
      ("name" -> name) ~ 
      ("prefetch" -> JString(remoteUrl("id")))
    )

    typeahead(name, candidates, deflt, f, options, attrs: _*)
  }

  private def typeahead(name: String, candidates: List[String], deflt: Box[String],
    f: String => Any, options: JValue, attrs: ElemAttr*) = {
 
    val id = discoverId(attrs: _*)
    val atts = addIdIfNeeded(id, attrs: _*)
    val attributes = placeholder(name, atts: _*)
    val typescript = script(id, options) 
   
    TwitterCandidates.is += ("id" -> candidates)
     
    <head_merge>
      { Script(OnLoad(typescript)) }
    </head_merge> ++
    SHtml.text(deflt openOr "", f, attributes: _*)
  }

  private val _url = "/twitter/typeahead/%s/%s%s"
 
  private def makeUrl(part: String, query: Box[String])(id: String) = _url.format(part, id, query openOr "")

  private val prefetchUrl = makeUrl("prefetch", Empty) _

  private val remoteUrl = makeUrl("remote", Full("/%QUERY")) _ 

  private def script(id: String, options: JValue) = { 
     JsRaw("""
      (function($) {
        $('#%s').typeahead( %s );
      })(jQuery);
     """.format(id, compact(render(options)))
    )
  }

  private def discoverId(attrs: ElemAttr*): String = {
    attrs collectFirst { 
      case BasicElemAttr(name, value) if name == "id" => value
    } getOrElse nextFuncName
  }

  private def addIdIfNeeded(id: String, attrs: ElemAttr*) = {
    val idElem = BasicElemAttr("id", id)
    if (attrs.contains(idElem)) attrs 
    else idElem +: attrs
  }
  
  private def placeholder(name: String, attrs: ElemAttr*) = {
    BasicElemAttr("placeholder", name) +: attrs
  }

}
