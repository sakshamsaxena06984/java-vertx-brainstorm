package com.vert.youtube_vertx;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.*;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.UUID;

/**
 * Handler : It is use for start the vert.x program via config setting| information....
 */

public class tut6 extends AbstractVerticle {

  String verticleId= UUID.randomUUID().toString();

  @Override
  public void start(Promise<Void> start){
    DeploymentOptions options=new DeploymentOptions()
      .setWorker(true)
        .setInstances(8);
//    vertx.deployVerticle(new HelloVerticle(), options);
    vertx.deployVerticle("com.vert.youtube_vertx.HelloVerticle", options);
    Router router=Router.router(vertx);
    router.get("/api/v1/Hello").handler(ctx->{
      ctx.request().response().end("Hello Vertx World!");
    });
    router.get("/api/v1/Hello/:name").handler(ctx->{
      String args=ctx.pathParam("name");
      ctx.request().response().end(String.format("Hello %s, from %s!", args,verticleId));
    });

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
    v.deployVerticle(new tut6());
  }
}
