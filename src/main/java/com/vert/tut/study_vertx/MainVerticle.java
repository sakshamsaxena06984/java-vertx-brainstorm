package com.vert.tut.study_vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
//    vertx.createHttpServer().requestHandler(req -> {
//      req.response()
//        .putHeader("content-type", "text/plain")
//        .end("Hello from Vert.x!");
//    }).listen(8888).onComplete(http -> {
//      if (http.succeeded()) {
//        startPromise.complete();
//        System.out.println("HTTP server started on port 8888");
//      } else {
//        startPromise.fail(http.cause());
//      }
//    });
    // ------- first method for creating the get api call
//    vertx.createHttpServer().requestHandler(req->{
//      req.response().end("Hello world");
//    }).listen(8080);

    // ------ second way
//    vertx.createHttpServer().requestHandler(req->{
//      if (req.path().startsWith("/api/v1/hello")){
//        req.response().end("Hello Vertx, if condition");
//      }
//    }).listen(8080);

    // ------ creating api via router, which is provided via vertx-web
//    Router router = Router.router(vertx);
//    router.get("/api/v1/hello").handler(ctx->{
//      ctx.request().response().end("Hello Vert.x World");
//    });
//    router.get("/api/v1/hello/:name").handler(ctx->{
//      String name=ctx.pathParam("name");
//      ctx.request().response().end(String.format("Hello %s",name));
//    });
//
//    vertx.createHttpServer().requestHandler(router).listen(8080);

    //------  using own create eventBus in the
//    DeploymentOptions opts=new DeploymentOptions()
//      .setWorker(true)
//        .setInstances(8);
//

    vertx.deployVerticle(new HelloVerticle());
    Router router = Router.router(vertx);
    System.out.println("----------- calling line number 51 -------");
    router.get("/api/v1/hello").handler(ctx->{
      ctx.request().response().end("Hello Vert.x World");
    });
    System.out.println("------------  calling line number 52");
    router.get("/api/v1/hello/:name").handler(ctx->{
      String name=ctx.pathParam("name");
      ctx.request().response().end(String.format("Hello %s",name));
    });

    vertx.createHttpServer().requestHandler(router).listen(8080);

  }
  void helloVertx(RoutingContext ctx){
    vertx.eventBus().request("hello.vertx.addr","",reply->{
      ctx.request().response().end((String) reply.result().body());
    });
  }

  void helloName(RoutingContext ctx){
    String name=ctx.pathParam("name");
    vertx.eventBus().request("hello.named.addr",name,reply ->{
      ctx.request().response().end((String) reply.result().body());
    });

  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }

}
