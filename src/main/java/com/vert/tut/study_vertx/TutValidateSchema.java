package com.vert.tut.study_vertx;
import io.vertx.core.json.JsonObject;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
public class TutValidateSchema {
  public static void main(String[] args) {
    String schemaString = "{\n" +
      "  \"$schema\": \"http://json-schema.org/draft-07/schema#\",\n" +
      "  \"type\": \"object\",\n" +
      "  \"properties\": {\n" +
      "    \"name\": {\"type\": \"string\"},\n" +
      "    \"age\": {\"type\": \"integer\"}\n" +
      "  },\n" +
      "  \"required\": [\"name\", \"age\"]\n" +
      "}";

    JSONObject schemajson=new JSONObject(new JSONTokener(schemaString));
    Schema schema=SchemaLoader.load(schemajson);
    String jsonString = "{\n" +
      "  \"name\": \"John Doe\",\n" +
      "  \"age\": 30\n" +
      "}";

    JSONObject jsonObject = new JSONObject(new JSONTokener(jsonString));

    try {
      System.out.println(schema.getId());
      schema.validate(jsonObject);
      System.out.println("Json schema has been Validated!");
    }catch (Exception e){
      System.out.println("JSON is invalid : "+e.getMessage());
    }



  }

}
