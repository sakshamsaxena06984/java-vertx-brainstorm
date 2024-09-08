package com.vert.youtube_vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.UUID;

/**
 * EventBus:- one verticle deployed in multiple verticles..
 * note: smallest unit of working in vertx is known as Verticle. And it is single thread.
 */

public class tut5 extends AbstractVerticle {

  String verticleId= UUID.randomUUID().toString();

  @Override
  public void start(){
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

    int httpPort;
    try {
      httpPort=Integer.parseInt(System.getProperty("http.port","8080"));
    }catch (NumberFormatException nfe){
      httpPort=8080;
    }
    vertx.createHttpServer().requestHandler(router).listen(8080);
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
    v.deployVerticle(new tut5());
  }
}
