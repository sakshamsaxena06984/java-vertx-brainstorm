package com.vert.tut.study_vertx;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
class SenderVerticle extends AbstractVerticle {

  @Override
  public void start() {
    vertx.setPeriodic(2000, id -> {
      vertx.eventBus().send("news-feed", "Hello from SenderVerticle!");
      System.out.println("Message sent to the event bus.");
    });
  }
}

class ReceiverVerticle extends AbstractVerticle {

  @Override
  public void start() {
    vertx.eventBus().consumer("news-feed", message -> {
      System.out.println("Received message: " + message.body());
    });
  }
}

public class TutEventBusUses {
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();

    // Deploy the sender and receiver verticles
    vertx.deployVerticle(new SenderVerticle());
    vertx.deployVerticle(new ReceiverVerticle());
  }
}
