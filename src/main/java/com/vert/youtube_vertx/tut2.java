package com.vert.youtube_vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public class tut2 extends AbstractVerticle {

  @Override
  public void start(){
    Router router=Router.router(vertx);
    router.get("/api/v1/Hello").handler(ctx->{
      ctx.request().response().end("Hello Vertx World!");
    });
    router.get("/api/v1/Hello/:name").handler(ctx->{
      String args=ctx.pathParam("name");
      ctx.request().response().end(String.format("Hello %s", args));
    });

    vertx.createHttpServer().requestHandler(router).listen(8080);


  }

  public static void main(String[] args) {
    Vertx v=Vertx.vertx();
    v.deployVerticle(new tut2());
  }
}
