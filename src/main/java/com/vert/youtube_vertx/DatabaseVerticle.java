package com.vert.youtube_vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;

public class DatabaseVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> start) throws Exception{
//    dbDatabaseMigrations()
//      .setHandler

  }
  Future<Void> dbDatabaseMigrations(){
//    JsonObject dbConfig = loadedC
    Flyway flyway=Flyway.configure().dataSource("jdbc:postgresql://localhost:5433/todo","postgres","introduction").load();
    try {
      flyway.migrate();
      return Promise.<Void>promise().future();
    }catch (FlywayException fe){
      return (Future<Void>) Promise.<Void>promise();
    }

  }
}
