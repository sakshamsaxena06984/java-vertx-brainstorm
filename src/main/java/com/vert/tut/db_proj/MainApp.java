package com.vert.tut.db_proj;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.jdbcclient.JDBCPool;

public class MainApp {
  public static void main(String[] args) {
    // Create a Vertx instance
    Vertx vertx = Vertx.vertx();

    // Deploy the UserVerticle
    vertx.deployVerticle(new UserVerticleV1(), res -> {
      if (res.succeeded()) {
        System.out.println("Deployment id is: " + res.result());
      } else {
        System.out.println("Deployment failed: " + res.cause());
      }
    });

  }
}
