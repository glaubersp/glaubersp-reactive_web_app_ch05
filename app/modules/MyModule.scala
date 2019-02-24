package modules

import play.api.{ Configuration, Environment }
import play.api.inject.{ Binding, Module }

class MyModule extends Module {
  def bindings(
    environment: Environment,
    configuration: Configuration): Seq[Binding[_]] = {
    Seq( //bind[MyComponent].to[MyComponentImpl]
    )
  }
}
