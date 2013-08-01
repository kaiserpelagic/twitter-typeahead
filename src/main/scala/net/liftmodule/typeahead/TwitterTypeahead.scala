package net.liftmodules
package typeahead

import net.liftweb.json.Serialization.{read, write}
import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import net.liftweb.common._
import net.liftweb.http._
import SHtml._
import net.liftweb.http.js.{JsCmd, JsObj, JsCmds, JE} 
import net.liftweb.http.SHtml.ElemAttr
import net.liftweb.util.Helpers._
import JsCmds._ 
import JE._

import scala.xml._


case class TypeaheadOptions(
  name: String, 
  local: List[String], 
  prefetch: Box[String => String], 
  remote: Box[String => String]
) 
{
  def toJson(id: String) = {
    val json =
     ( ("name" -> name) ~ 
      ("local" -> JArray(local map { JString(_) }))  ~
      ("prefetch" -> JString(prefetch map { _(id) } openOr "" )) ~
      ("remote" -> JString(remote map { _(id) } openOr "" )) )
 
    // Filter out any options that weren't set. This probably isn't
    // the best way to handle this but it works
    json transform {
      case JField("local", JArray(a)) if a.isEmpty => JNothing 
      case JField("prefetch", JString(s)) if s.isEmpty => JNothing 
      case JField("remote", JString(s)) if s.isEmpty => JNothing 
    }
  }
}

object TwitterTypeahead {

  def local(name: String, candidates: List[String], deflt: Box[String],
    f: String => JsCmd, attrs: ElemAttr*) = {
    
    val options = TypeaheadOptions(name, candidates, Empty, Empty)
    typeahead(name, candidates, deflt, f, options, attrs: _*)
  }

  def prefetch(name: String, candidates: List[String], deflt: Box[String],
    f: String => JsCmd, attrs: ElemAttr*) = {
    
    val options = TypeaheadOptions(name, Nil, Full(prefetchUrl), Empty)
    typeahead(name, candidates, deflt, f, options, attrs: _*)
  }
  
  def remote(name: String, candidates: List[String], deflt: Box[String],
    f: String => JsCmd, attrs: ElemAttr*) = {

    val options = TypeaheadOptions(name, Nil, Empty, Full(remoteUrl))
    typeahead(name, candidates, deflt, f, options, attrs: _*)
  }
  
  def remoteWithPrefetch(name: String, candidates: List[String], deflt: Box[String],
    f: String => JsCmd, attrs: ElemAttr*) = {
    
    val options = TypeaheadOptions(name, Nil, Full(prefetchUrl), Full(remoteUrl))
    typeahead(name, candidates, deflt, f, options, attrs: _*)
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

  private def typeahead(name: String, candidates: List[String], deflt: Box[String],
    f: String => JsCmd, options: TypeaheadOptions, attrs: ElemAttr*) = {
 
    val id = discoverId(attrs: _*)
    val atts = addIdIfNeeded(id, attrs: _*)
    val attributes = placeholder(name, atts: _*)
    val typescript = script(id, options.toJson(id)) 
    
    TypeaheadSuggestions.register(id, candidates)
    
    <head_merge>
      { Script(OnLoad(typescript)) }
    </head_merge> ++
    SHtml.text(deflt openOr "", f, attributes: _*)
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
