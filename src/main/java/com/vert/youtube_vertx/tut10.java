package com.vert.youtube_vertx;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.*;
import io.vertx.ext.web.sstore.ClusteredSessionStore;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;

import java.net.CookieHandler;
import java.util.UUID;

/**
 * vertx launching :
 */

public class tut10 extends AbstractVerticle {

  String verticleId= UUID.randomUUID().toString();

  @Override
  public void start(Promise<Void> start){
    vertx.deployVerticle("Hello.groovy");
//    vertx.deployVerticle("Hello.js");
    Router router=Router.router(vertx);
    Handler<AsyncResult<Void>> dbMigrationResultHandler = result -> this.handleMigrationResult(start,result);

    vertx.executeBlocking(this::dbDatabaseMigrations,dbMigrationResultHandler);


    SessionStore store=LocalSessionStore.create(vertx);

    // using for storing session
    router.route().handler(SessionHandler.create(store));
    router.route().handler(LoggerHandler.create());

    router.route().handler(CorsHandler.create("localhost"));
//    router.route().handler(CSRFHandler.create("samdklansdkjasnd"));
    router.get("api/v1/hello").handler(this::helloName);
    router.get("/api/v1/Hello").handler(this::helloVertx);
    router.get("/api/v1/Hello/:name").handler(this::helloName);

    /***
     * If any of router request are not match then web/index.html page will be called...
     */
    router.route().handler(StaticHandler.create("web").setIndexPage("index.html"));

    dbConfig(start,router);



//    int httpPort;
//    try {
//      httpPort=Integer.parseInt(System.getProperty("http.port","8080"));
//    }catch (NumberFormatException nfe){
//      httpPort=8080;
//    }

    ConfigStoreOptions defaultConfig=new ConfigStoreOptions()
      .setType("file")
        .setFormat("json")
          .setConfig(new JsonObject().put("path","config.json"));

    ConfigStoreOptions cliConfig=new ConfigStoreOptions()
      .setType("json")
      .setConfig(config());

    ConfigRetrieverOptions opts=new ConfigRetrieverOptions()
      .addStore(defaultConfig)
      .addStore(cliConfig);


    ConfigRetriever configRetriever=ConfigRetriever.create(vertx, opts);
    Handler<AsyncResult<JsonObject>> handler = asyncResult -> this.handleConfigResult(start,router,asyncResult);

    configRetriever.getConfig(handler);
  }
  void handleConfigResult(Promise<Void> start,Router router ,AsyncResult<JsonObject> asyncResult){
    if (asyncResult.succeeded()){
      JsonObject config=asyncResult.result();
      JsonObject http=config.getJsonObject("http");
      int httpPort=http.getInteger("port");
      vertx.createHttpServer().requestHandler(router).listen(8080);
      start.complete();
    }else{
      // figure out, what we can do ??
      start.fail("Unable to load Configuration!");
    }


  }
  private void dbConfig(Promise<Void> start, Router router){
    ConfigStoreOptions defaultConfig=new ConfigStoreOptions()
      .setType("file")
      .setFormat("json")
      .setConfig(new JsonObject().put("path","config.json"));

    ConfigRetrieverOptions opts=new ConfigRetrieverOptions()
      .addStore(defaultConfig);
    ConfigRetriever cfgRetriever=ConfigRetriever.create(vertx, opts);

    Handler<AsyncResult<JsonObject>> handler=asyncResult -> this.handleConfigResult(start,router,asyncResult);
    cfgRetriever.getConfig(handler);
  }

  void handleMigrationResult(Promise<Void> start, AsyncResult<Void> result){
    if(result.failed()){
      start.fail(result.cause());
    }
  }

  void dbDatabaseMigrations(Promise<Void> promise){
      Flyway flyway=Flyway.configure().dataSource("jdbc:postgresql://localhost:5433/todo","postgres","introduction").load();
      try {
        flyway.migrate();
        promise.complete();
      }catch (FlywayException fe){
        promise.fail(fe);
      }

  }


  void helloVertx(RoutingContext ctx){
    vertx.eventBus().request("Hello.vertx.addr", "", reply->{
      System.out.println("It is the request of helloVertx");
      ctx.request().response().end((String) reply.result().body());
    });

  }

  void helloName(RoutingContext ctx){
    String name= ctx.pathParam("name");
    vertx.eventBus().request("Hello.named.addr", name, reply ->{
      System.out.println("It is the Request of helloName");
      ctx.request().response().end((String)reply.result().body());
    });
  }

  public static void main(String[] args) {
    Vertx v=Vertx.vertx();
    v.deployVerticle(new tut10());
  }
}
