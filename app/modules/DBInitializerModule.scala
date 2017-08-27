package modules

import com.google.inject.AbstractModule
import play.api.Logger
import scalikejdbc.config.DBs

/**
 * Created by Fumiyasu on 2016/09/15.
 */
class DBInitializer {
  DBs.setupAll()
  Logger.debug(s"db setup...")
}

class DBInitializerModule extends AbstractModule {
  def configure() = {
    bind(classOf[DBInitializer]).asEagerSingleton()
  }
}
