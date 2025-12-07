package com.example.app.servlet;

import com.google.cloud.datastore.*;
import javax.servlet.http.*;
import java.io.IOException;

public class LoginServlet extends HttpServlet {
  private Datastore datastore;
  private static final String KIND = "User";

  @Override
  public void init() { datastore = DatastoreOptions.getDefaultInstance().getService(); }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/plain");
    String email = req.getParameter("email");
    String password = req.getParameter("password");
    if (email == null) {
      resp.getWriter().write("MISSING_EMAIL");
      return;
    }
    Query<Entity> query = Query.newEntityQueryBuilder()
        .setKind(KIND)
        .setFilter(StructuredQuery.PropertyFilter.eq("email", email))
        .build();
    QueryResults<Entity> results = datastore.run(query);
    if (results.hasNext()) {
      Entity e = results.next();
      String stored = e.contains("password") ? e.getString("password") : "";
      if (stored.equals(password)) resp.getWriter().write("OK");
      else resp.getWriter().write("INVALID_PASSWORD");
    } else {
      resp.getWriter().write("NOT_FOUND");
    }
  }
}
