package com.example.app.servlet;

import com.example.app.model.UserMapper;
import com.google.cloud.bigquery.*;
import com.google.cloud.datastore.*;

import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

public class MigrateServlet extends HttpServlet {
  private Datastore datastore;
  private BigQuery bigquery;
  private static final String KIND = "User";
  // Replace dataset/table with your values or set via env/config
  private static final String BQ_DATASET = "userdataset";
  private static final String BQ_TABLE = "User";

  @Override
  public void init() {
    datastore = DatastoreOptions.getDefaultInstance().getService();
    bigquery = BigQueryOptions.getDefaultInstance().getService();
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/plain");
    Query<Entity> query = Query.newEntityQueryBuilder().setKind(KIND).build();
    QueryResults<Entity> results = datastore.run(query);
    List<InsertAllRequest.RowToInsert> rows = new ArrayList<>();

    int count = 0;
    while (results.hasNext() && count < 100) {
      Entity e = results.next();
      Map<String, Object> row = UserMapper.toBigQueryRow(e);
      rows.add(InsertAllRequest.RowToInsert.of(UUID.randomUUID().toString(), row));
      count++;
    }

    TableId tableId = TableId.of(BQ_DATASET, BQ_TABLE);
    InsertAllRequest insertRequest = InsertAllRequest.newBuilder(tableId).setRows(rows).build();
    InsertAllResponse insertResponse = bigquery.insertAll(insertRequest);

    if (insertResponse.hasErrors()) {
      StringBuilder sb = new StringBuilder();
      insertResponse.getInsertErrors().forEach((k,v)-> sb.append(k).append(":").append(v).append("\n"));
      resp.getWriter().write("ERROR: " + sb.toString());
    } else {
      resp.getWriter().write("MIGRATED " + rows.size() + " rows");
    }
  }
}
