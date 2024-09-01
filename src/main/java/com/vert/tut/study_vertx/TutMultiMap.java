package com.vert.tut.study_vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;

public class TutMultiMap extends AbstractVerticle {

  @Override
  public void start(){
    HttpServer server = vertx.createHttpServer();
    server.requestHandler(req->{
      MultiMap headers=req.headers();
      System.out.println("size of headers MultiMap : "+headers.size());
      headers.forEach(ele->{
        System.out.println(ele.getKey() +"  ::  "+ele.getValue());
      });

      String contentType = headers.get("Content-Type");
      System.out.println("Content-Type : "+contentType);
      req.response()
        .putHeader("Customer-Header","MyCustomValue")
        .end("Hello vertx MultiMap");
    });

    server.listen(8808,result->{
      if(result.succeeded()){
        System.out.println("Server is now listening the port : 8808");
      }else{
        System.out.println("Failed to bind server "+result.cause().getMessage());
      }
    });



  }
  public static void main(String[] args){
    Vertx vertx=Vertx.vertx();
    vertx.deployVerticle(new TutMultiMap());
  }
}
