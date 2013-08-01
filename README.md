twitter-typeahead
=================

<a href="https://github.com/twitter/typeahead.js/">Twitter Typeahead (.js)</a> module for Lift 

## Setup and Configuration 

Get the code from this repo. I'll hopefully have some releases on Sonatype soon.

### Integrating into your project

soon ...

## Using Twitter Typeahead

It's pretty easy. There has support for two modes: local and remote. The interface
for each is the same. Here is an examle of each.


```scala
import net.liftmodules.typeahead.{TwitterTypeahead}

class TypeAhead {

  def render = {
    val local = List("red", "blue", "green", "orange", "purple", "white", "grey")
    val remote = List("foo", "bar", "baz")

    // stores candidates on the client's browser; sent on page load
    "@local *" #> TwitterTypeahead.local("colors", local, Empty, s =>  {}) &
    
    // tries to prefetch and store locally, if that fails it makes remote ajax requests on input
    "@remote *" #> TwitterTypeahead.remote("foo", remote, Empty, s =>  {})
  }
}

 /**
* Builds a Twitter Typeahead text input 
*
* @param name the name used on the input field
* @param candidates the candidates that are used for typeahead suggestions
* @param deflt the default value in the input field
* @param f the function that is called with the value of the input field on form post
*
* @return a text input field that hooked up to twitter typeahead
*/
TwitterTypeadhead.local(name: String, candidates: List[String], deflt: Box[String], 
  f: String => Any, attr: ElemAttr*)

```
### Future
Support custom styling



