package com.vert.tut.study_vertx;

import io.vertx.core.AbstractVerticle;

public class HelloVerticle extends AbstractVerticle {

  @Override
  public void start(){
    vertx.eventBus().consumer("Hello.vertx.addr", msg->{
      System.out.println("consuming msg from the first consumer : "+msg.body());
      msg.reply("Hello --------eventBus in vertx");
    });

    vertx.eventBus().consumer("Hello.named.addr", msg ->{
      System.out.println("consuming msg from the second consumer : "+msg.body());
      String name=(String) msg.body();
      msg.reply(String.format("Hello------- %s" ,name));
    });
  }


}
