package com.vert.tut.db_proj;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.spi.impl.HikariCPDataSourceProvider;

import java.util.ArrayList;
import java.util.List;

public class UserVerticleV1 extends AbstractVerticle {
  private JDBCPool client;
//  Vertx vertx = Vertx.vertx();

  @Override
  public void start(Promise<Void> startPromise) {
    JsonObject dbConfig = new JsonObject()
      .put("url", "jdbc:mysql://localhost:3306/test_db")
      .put("driver_class", "com.mysql.cj.jdbc.Driver")
      .put("user", "root")
      .put("password", "saksham123")
      .put("max_pool_size", 10);

    client = JDBCPool.pool(vertx, dbConfig);
    Router router = Router.router(vertx);

    // POST - Create a new user
    router.post("/user").handler(BodyHandler.create()).handler(ctx -> {
      User user = Json.decodeValue(ctx.getBodyAsString(), User.class);
      client.getConnection().compose(conn -> insertUser(conn, user)).onComplete(ar -> {
        if (ar.succeeded()) {
          ctx.response().setStatusCode(201).end("User created");
        } else {
          ctx.response().setStatusCode(500).end(ar.cause().getMessage());
        }
      });
    });


    // GET - Retrieve users
//    router.get("/user").handler(ctx -> {
//      client.query("SELECT * FROM users").execute(ar -> {
//        if (ar.succeeded()) {
//          ctx.response()
//            .putHeader("content-type", "application/json")
//            .end(Json.encodePrettily(ar.result().value()));
//        } else {
//          ctx.response().setStatusCode(500).end(ar.cause().getMessage());
//        }
//      });
//    });
    // ---  it is the main GET call

//    router.get("/user").handler(ctx -> {
//      client.query("SELECT * FROM users").execute(ar -> {
//        if (ar.succeeded()) {
//          List<User> users = new ArrayList<>();
//          ar.result().forEach(row -> {
//            User user = new User();
//            user.setName(row.getString("name"));
//            user.setAge(row.getInteger("age"));
//            user.setCountry(row.getString("country"));
//            users.add(user);
//          });
//          ctx.response()
//            .putHeader("content-type", "application/json")
//            .end(Json.encodePrettily(users));
//        } else {
//          ctx.response().setStatusCode(500).end(ar.cause().getMessage());
//        }
//      });
//    });
    vertx.setPeriodic(100, id ->{
      router.get("/user").handler(ctx -> {
        client.query("SELECT * FROM users").execute(ar -> {
          if (ar.succeeded()) {
            List<User> users = new ArrayList<>();
            ar.result().forEach(row -> {
              User user = new User();
              user.setName(row.getString("name"));
              user.setAge(row.getInteger("age"));
              user.setCountry(row.getString("country"));
              users.add(user);
              System.out.println(user.toString());
            });
            ctx.response()
              .putHeader("content-type", "application/json")
              .end(Json.encodePrettily(users));
          } else {
            ctx.response().setStatusCode(500).end(ar.cause().getMessage());
          }
        });
      });
    });


    vertx.createHttpServer().requestHandler(router).listen(8080, http -> {
      if (http.succeeded()) {
        startPromise.complete();
      } else {
        startPromise.fail(http.cause());
      }
    });

  }

  private Future<Void> insertUser(SqlConnection conn, User user) {
    return conn.preparedQuery("INSERT INTO users (name, age, country) VALUES (?, ?, ?)")
      .execute(Tuple.of(user.getName(), user.getAge(), user.getCountry()))
      .mapEmpty();
  }

  @Override
  public void stop() throws Exception {
    client.close();
  }

}
