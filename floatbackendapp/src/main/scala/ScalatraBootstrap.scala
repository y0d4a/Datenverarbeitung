import com.example.app._
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    // Optional because * is the default
    context.initParameters("org.scalatra.cors.allowedOrigins") = "*"
    // List what HTTP methods will be accepted.
    // Available options are GET, POST, PUT, DELETE, HEAD, OPTIONS, and PATCH.
    // All methods are accepted by default.
    context.initParameters("org.scalatra.cors.allowedMethods") = "GET"

    // Set a list of allowed HTTP headers, most headers are supported.
    context.initParameters("org.scalatra.cors.allowedHeaders") = "Content-Type"

    // Set the number of seconds that preflight requests can be cached by the client.
    // Default value is 0 seconds.
    context.initParameters("org.scalatra.cors.preflightMaxAge") = "1800"

    // By default, cookies are not included in CORS requests. Set this to `true` to allow cookies.
    context.initParameters("org.scalatra.cors.allowCredentials") = "false"

    // If CorsSupport needs to be disabled, set to false. Default: CorsSupport is enabled.
    context.initParameters("org.scalatra.cors.enable") = "false"
    context.mount(new MyScalatraServlet, "/*")
  }
}
