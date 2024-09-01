package com.vert.tut.study_vertx;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
public class TutJsonArray {
  public static void main(String[] args) {
    JsonArray jsonArray=new JsonArray();
    jsonArray.add("Hello Vert.x!");
    jsonArray.add(123);
    jsonArray.add(true);


    JsonObject jsonObject=new JsonObject()
      .put("name","vert.x")
      .put("type","toolkit");

    jsonArray.add(jsonObject);
    System.out.println("Printing the json array in the form of string "+jsonArray.encodePrettily());

    System.out.println("String value from json array : "+jsonArray.getString(0));
    System.out.println("Boolean value from json array : "+jsonArray.getBoolean(2));
//    System.out.println("Another String value from json array : "+jsonArray.getString(1));
    JsonObject jsonObject1=jsonArray.getJsonObject(3);

    System.out.println("Object values of jsonArray");
    System.out.println(jsonObject1.encodePrettily());
  }


}
