package com.vert.tut.study_vertx;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

public class SomeMethodVertx {
  public static void main(String[] args) {
    Vertx vertx= Vertx.vertx(new VertxOptions().setWorkerPoolSize(40));
    vertx.setPeriodic(100, id -> {
      // This handler will get called every second
      System.out.println("timer fired!");
    });


  }
}
