import com.example.app._
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    // Optional because * is the default
    context.initParameters("org.scalatra.cors.allowedOrigins") = "*"
    context.mount(new MyScalatraServlet, "/*")
  }
}
