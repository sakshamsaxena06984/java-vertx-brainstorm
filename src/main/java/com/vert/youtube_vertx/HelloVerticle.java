package com.vert.youtube_vertx;

import io.vertx.core.AbstractVerticle;

public class HelloVerticle extends AbstractVerticle {
  @Override
  public void start(){
    // it is just an entry point...
    vertx.eventBus().consumer("Hello.vertx.addr", msg->{
      System.out.println("it is the consumer of eventBust : first");
      msg.reply("Hello Vert.x World");
    });
    vertx.eventBus().consumer("Hello.named.addr", msg->{
      System.out.println("it is the consumer of eventButs : second");
      String name = (String) msg.body();
      msg.reply(String.format("Hello %s",name));
    });
  }

}
