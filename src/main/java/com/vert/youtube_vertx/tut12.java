package com.vert.youtube_vertx;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;

import java.util.List;
import java.util.UUID;

import static io.vertx.core.Future.future;

/**
 * Asynchronous Coordination:
 * We have two way:
 * ->1 : Sequential Composition :: Do A, then B, then C .... handle errors...
 * ->2 : Concurrent Composition :: Do A, B, C and once all/any complete - Do Something else...
 * ->3 : doConfig()
 *       compose(this::doDataBaseMigration)
 *       compose(this::configuration)
 *       compose(this::startHttpServer)
 *       compose(this::deployOtherVertices)
 *       compose(start::handle)
 */

public class tut12 extends AbstractVerticle {
  final JsonObject loadConfig=new JsonObject();

  String verticleId= UUID.randomUUID().toString();

  @Override
  public void start(Promise<Void> start){
    // will check, why it is not working???
    dbConfig()
      .compose(this::storeConfig)
      .compose(this::dbDatabaseMigrations)
      .compose(this::configurationRouter);
      .compose(this::startHttpServer)
      .compose(this::deployOtherVerticles)
      .compose(start::handle);




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
  Future<JsonObject> dbConfig(){
    ConfigStoreOptions defaultConfig=new ConfigStoreOptions()
      .setType("file")
      .setFormat("json")
      .setConfig(new JsonObject().put("path","config.json"));

    ConfigRetrieverOptions opts=new ConfigRetrieverOptions()
      .addStore(defaultConfig);
    ConfigRetriever cfgRetriever=ConfigRetriever.create(vertx, opts);

     return future(promise -> cfgRetriever.getConfig(promise));
  }

  Future<Void> storeConfig(JsonObject config){
    loadConfig.mergeIn(config);
    return Promise.<Void>promise().future();

  }

  void handleMigrationResult(Promise<Void> start, AsyncResult<Void> result){
    if(result.failed()){
      start.fail(result.cause());
    }
  }

  Future<Void> dbDatabaseMigrations(Void unused){
//    JsonObject dbConfig = loadedC
      Flyway flyway=Flyway.configure().dataSource("jdbc:postgresql://localhost:5433/todo","postgres","introduction").load();
      try {
        flyway.migrate();
        return Promise.<Void>promise().future();
      }catch (FlywayException fe){
       return (Future<Void>) Promise.<Void>promise();
      }

  }
  Future<HttpServer> startHttpServer(Router router){
    JsonObject http=loadConfig.getJsonObject("http");
    int httpPort = http.getInteger("port");
    HttpServer server= vertx.createHttpServer().requestHandler(router);
//     future(promise -> server.listen(httpPort,promise));
    return Future.future(promise -> server.listen(httpPort, promise.toString()));
  }

  Future<Object> configurationRouter(Void unused){
    Router router=Router.router(vertx);
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

    return Promise.promise().future();
  }

  Future<Void> deployOtherVerticles(HttpServer server){
    Future<String> helloGroovy = future(promise -> vertx.deployVerticle("Hello.groovy",promise));
    return CompositeFuture.all((List<Future>) helloGroovy).mapEmpty();
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
    v.deployVerticle(new tut12());
  }
}
