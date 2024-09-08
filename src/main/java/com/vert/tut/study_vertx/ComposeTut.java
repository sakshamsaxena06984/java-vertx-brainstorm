package com.vert.tut.study_vertx;


import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;

public class ComposeTut {
  public static void main(String[] args) {
//    if(requestBody.containsKey("Hello") && requestBody.containsKey ){
//
//    }
    FileSystem fs= Vertx.vertx().fileSystem();
    Future<Void> future=fs.createFile("/Users/sakshamsaxena/Downloads/study-vertx/foo")
      .compose(v->{
        return fs.writeFile("/Users/sakshamsaxena/Downloads/study-vertx/foo", Buffer.buffer());
      })
      .compose(v->{
        return fs.move("/Users/sakshamsaxena/Downloads/study-vertx/foo","/Users/sakshamsaxena/Downloads/study-vertx/src/main/resources/bar");
      });

  }
}
