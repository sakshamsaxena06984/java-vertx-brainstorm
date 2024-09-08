package com.vert.youtube_vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;

public class tut1 extends AbstractVerticle {

  @Override
  public void start(){
    vertx.createHttpServer().requestHandler(req->{
      req.response().end("HelloWorld");
    }).listen(8080);

  }

  public static void main(String[] args) {
    Vertx v=Vertx.vertx();
    v.deployVerticle(new tut1());
  }
}
