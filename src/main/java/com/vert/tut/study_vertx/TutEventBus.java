package com.vert.tut.study_vertx;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;

public class TutEventBus {
  public static void main(String[] args){
    Vertx vertx=Vertx.vertx();
    // will create the one consumer
    // will create the one producer
    vertx.deployVerticle(new ConsumerVerticle());
    vertx.deployVerticle(new ProducerVerticle());

  }

}

class ConsumerVerticle extends AbstractVerticle{
  @Override
  public void start(){
    EventBus eventBus= vertx.eventBus();
    eventBus.consumer("example.address",this::handleMessage);
  }

  private void handleMessage(Message<String> message){
    System.out.println("Received messages : "+message.body());
    message.reply("Message has been received and processed!");
  }
}

class ProducerVerticle extends AbstractVerticle{
  @Override
  public void start(){
    EventBus eventBus=vertx.eventBus();
    eventBus.request("example.address","Hello EventBus!", reply->{
      if(reply.succeeded()){
        System.out.println("Received the reply : "+reply.result().body());
      }else {
        System.out.println("No reply");
      }
    });
  }
}
