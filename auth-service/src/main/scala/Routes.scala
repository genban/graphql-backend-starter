import com.timzaak.di.DIWithPlay
import play.api.routing.{Router, SimpleRouter}
import play.api._
import play.api.mvc._
import play.api.routing.sird._

trait Routes extends DIWithPlay {

  import Results._

  def router: Router = SimpleRouter(
    {
      case GET(p"/hello") => Action {
        Ok("world")
      }
    }: Router.Routes)

}
