package net.liftmodules.typeahead

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


object TwitterCandidates {
  val suggestions = new LRUMap[String, List[String]](250000)

  def update(key: String, value: List[String]) = suggestions.update(key, value)

  def getOrElseNil(key: String) = suggestions.get(key).openOr(Nil)
}

object TwitterTypeahead {

  def local(name: String, candidates: List[String], deflt: Box[String],
    f: String => Any, attrs: ElemAttr*) = {
    
    typeahead(name, candidates, deflt, f, ("local", localJsonArray), attrs: _*)
  }

  def prefetch(name: String, candidates: List[String], deflt: Box[String],
    f: String => Any, attrs: ElemAttr*) = {
    
    typeahead(name, candidates, deflt, f, ("prefetch", prefetchUrl), attrs: _*)
  }
  
  def remote(name: String, candidates: List[String], deflt: Box[String],
    f: String => Any, attrs: ElemAttr*) = {

    typeahead(name, candidates, deflt, f, ("remote", remoteUrl), attrs: _*)
  }

  private def typeahead(name: String, candidates: List[String], deflt: Box[String],
    f: String => Any, config: (String, String => JValue), attrs: ElemAttr*) = {
 
    val id = discoverId(attrs: _*)
    val atts = addIdIfNeeded(id, attrs: _*)
    val attributes = placeholder(name, atts: _*)
    
    TwitterCandidates.update(id, candidates)

    val options = (
      ("name" -> name) ~ 
      (config._1 -> config._2(id))
    )
    
    val typescript = script(id, options) 
   
    <head_merge>
      { Script(OnLoad(typescript)) }
    </head_merge> ++
    SHtml.text(deflt openOr "", f, attributes: _*)
  }

  private val _url = "/twitter/typeahead/%s/%s%s"
 
  private def makeUrl(part: String, query: Box[String])(id: String) = JString(_url.format(part, id, query openOr ""))

  private val prefetchUrl = makeUrl("prefetch", Empty) _

  private val remoteUrl = makeUrl("remote", Full("/%QUERY")) _ 

  private def localJsonArray(id: String) = JArray(TwitterCandidates.getOrElseNil(id) map { JString(_) })

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
