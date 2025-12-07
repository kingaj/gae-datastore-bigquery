package com.example.app.model;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.StringValue;

import java.util.HashMap;
import java.util.Map;

public class UserMapper {

  public static Entity buildUserEntity(Datastore datastore, String kind, String id,
                                      String name, String dob, String email,
                                      String password, String phone, String gender,
                                      String address) {
    Key key = datastore.newKeyFactory().setKind(kind).newKey(id);
    Entity.Builder builder = Entity.newBuilder(key);
    if (name != null) builder.set("name", StringValue.of(name));
    if (dob != null) builder.set("dob", StringValue.of(dob));
    if (email != null) builder.set("email", StringValue.of(email));
    if (password != null) builder.set("password", StringValue.of(password));
    if (phone != null) builder.set("phone", StringValue.of(phone));
    if (gender != null) builder.set("gender", StringValue.of(gender));
    if (address != null) builder.set("address", StringValue.of(address));
    return builder.build();
  }

  public static Map<String, Object> toBigQueryRow(Entity e) {
    Map<String, Object> row = new HashMap<>();
    String id = e.getKey().getName() != null ? e.getKey().getName() : Long.toString(e.getKey().getId());
    row.put("id", id);
    if (e.contains("name")) row.put("name", e.getString("name"));
    if (e.contains("dob")) row.put("dob", e.getString("dob"));
    if (e.contains("email")) row.put("email", e.getString("email"));
    if (e.contains("phone")) row.put("phone", e.getString("phone"));
    if (e.contains("gender")) row.put("gender", e.getString("gender"));
    if (e.contains("address")) row.put("address", e.getString("address"));
    return row;
  }
}
