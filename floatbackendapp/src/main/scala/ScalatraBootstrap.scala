import com.example.app._
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    // Optional because * is the default
    context.initParameters("org.scalatra.cors.allowedOrigins") = "*"
    context.initParameters("org.scalatra.cors.allowedMethods") = "GET"
    // Disables cookies, but required because browsers will not allow passing credentials to wildcard domains
    context.initParameters("org.scalatra.cors.allowCredentials") = "false"
    // If CorsSupport needs to be disabled, set to false. Default: CorsSupport is enabled.
    context.initParameters("org.scalatra.cors.enable") = "false"
    context.mount(new MyScalatraServlet, "/*")
  }
}
