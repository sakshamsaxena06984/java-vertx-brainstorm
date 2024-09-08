package com.vert.youtube_vertx;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.UUID;

/**
 * Handler : It is use for start the vert.x program via config setting| information....
 */

public class tut8 extends AbstractVerticle {

  String verticleId= UUID.randomUUID().toString();

  @Override
  public void start(Promise<Void> start){
//    vertx.deployVerticle(new HelloVerticle());
    vertx.deployVerticle("Hello.groovy");
//    vertx.deployVerticle("Hello.js");
    Router router=Router.router(vertx);
//    router.route().handler(ctx->{
//      String authToken=ctx.request().getHeader("AUTH_TOKEN");
//      if (authToken!=null && "mySuperSecreAuthToken".contentEquals(authToken)){
//        ctx.next();
//      }else{
//        ctx.response().setStatusCode(401).setStatusMessage("UNAUTHORIZED").end();
//      }
//    });
    router.get("/api/v1/Hello").handler(this::helloVertx);
    router.get("/api/v1/Hello/:name").handler(this::helloName);

    /***
     * If any of router request are not match then web/index.html page will be called...
     */
    router.route().handler(StaticHandler.create("web").setIndexPage("index.html"));



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

    ConfigRetrieverOptions opts=new ConfigRetrieverOptions()
      .addStore(defaultConfig);
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
    v.deployVerticle(new tut8());
  }
}
