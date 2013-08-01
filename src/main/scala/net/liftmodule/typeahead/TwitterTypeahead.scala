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


object TwitterTypeahead {

  implicit val formats = net.liftweb.json.DefaultFormats

  def local(name: String, candidates: List[String], deflt: Box[String],
    f: String => Any, attrs: ElemAttr*) = {
    
    val options = (
      ("name" -> name) ~ 
      ("local" -> JArray(candidates map { JString(_) } ))
    )

    typeahead(name, candidates, deflt, f, options, attrs: _*)
  }

  def remote(name: String, candidates: List[String], deflt: Box[String],
    f: String => Any, attrs: ElemAttr*) = {
    
    def suggest(value: JValue) = {

      val matches = for {
        q <- value.extractOpt[String].map(_.toLowerCase).toList
        m <- candidates.filter(_.toLowerCase startsWith q)
      } yield JString(m) 

      JArray(matches)
    }
  
    // adapeted from The Lift Cookbook page 63
    val callbackContext  = new JsonContext(Full("callback"), Empty)
    val runSuggestions = SHtml.jsonCall(JsVar("query"), callbackContext, suggest _)
    S.appendJs(Function("askServer", "query" :: "callback" :: Nil, Run(runSuggestions.toJsCmd)))

    val options = (
      ("name" -> name) ~ 
      ("source" -> JString("askServer"))
    )

    typeahead(name, candidates, deflt, f, options, attrs: _*)
  }
  
  private def script(id: String, options: JValue) = { 
     JsRaw("""
      (function($) {
        $('#%s').typeahead( %s );
      })(jQuery);
     """.format(id, compact(render(options)))
    )
  }

  private def typeahead(name: String, candidates: List[String], deflt: Box[String],
    f: String => Any, options: JValue, attrs: ElemAttr*) = {
 
    val id = discoverId(attrs: _*)
    val atts = addIdIfNeeded(id, attrs: _*)
    val attributes = placeholder(name, atts: _*)
    val typescript = script(id, options) 
    
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
