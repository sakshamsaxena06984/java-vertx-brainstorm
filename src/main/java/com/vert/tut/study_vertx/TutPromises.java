package com.vert.tut.study_vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.Future;

//class PromiseExampleVerticle extends AbstractVerticle{
//  @Override
//  public void start(){
//    performAsyncOperation().onComplete(result -> {
//      if (result.succeeded()) {
//        // Operation completed successfully
//        System.out.println("Operation succeeded with result: " + result.result());
//      } else {
//        // Operation failed
//        System.out.println("Operation failed with cause: " + result.cause().getMessage());
//      }
//    });
//  }
//
//
//
//  private Promise<String> performAsyncOperation(){
//    Promise<String> promise=Promise.promise();
//    vertx.setTimer(1000,id ->{
//      boolean success=true;
//      if (success){
//        promise.complete("Success!");
//      }else {
//        promise.fail("Someting went wrong");
//      }
//    });
//
//    return promise;
//
//
//  }
//}



public class TutPromises extends AbstractVerticle {

  @Override
  public void start() {
    // Simulate an asynchronous operation
    performAsyncOperation().onComplete(result -> {
      if (result.succeeded()) {
        // Operation completed successfully
        System.out.println("Operation succeeded with result: " + result.result());
      } else {
        // Operation failed
        System.out.println("Operation failed with cause: " + result.cause().getMessage());
      }
    });
  }

  // Method simulating an asynchronous operation
  private Future<String> performAsyncOperation() {
    // Create a promise to manage the result of the async operation
    Promise<String> promise = Promise.promise();

    // Simulating asynchronous work (e.g., database call, HTTP request)
    vertx.setTimer(10, id -> {
      boolean success = true; // Simulate success or failure

      if (success) {
        promise.complete("Success!"); // Complete the promise successfully
      } else {
        promise.fail("Something went wrong!"); // Fail the promise
      }
    });

    // Return the future associated with the promise
    return promise.future();
  }


  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new TutPromises());
  }
}
