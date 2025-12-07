package com.example.app.servlet;

import com.example.app.model.UserMapper;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.InputStream;
import java.util.Iterator;
import java.util.UUID;

public class UploadServlet extends HttpServlet {
  private static final String KIND = "User";
  private Datastore datastore;

  @Override
  public void init() {
    datastore = DatastoreOptions.getDefaultInstance().getService();
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException {
    resp.setContentType("text/plain");
    try {
      if (!ServletFileUpload.isMultipartContent(req)) {
        resp.getWriter().write("Form must be multipart/form-data");
        return;
      }
      ServletFileUpload upload = new ServletFileUpload();
      FileItemIterator iter = upload.getItemIterator(req);
      while (iter.hasNext()) {
        FileItemStream item = iter.next();
        if (!item.isFormField() && item.getName() != null && !item.getName().isEmpty()) {
          try (InputStream in = item.openStream();
               Workbook wb = new XSSFWorkbook(in)) {
            Sheet sheet = wb.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            if (rows.hasNext()) rows.next(); // skip header

            while (rows.hasNext()) {
              Row r = rows.next();
              String name = getCellString(r,0);
              String dob = getCellString(r,1);
              String email = getCellString(r,2);
              String password = getCellString(r,3);
              String phone = getCellString(r,4);
              String gender = getCellString(r,5);
              String address = getCellString(r,6);

              String id = (email != null && !email.isEmpty()) ? email : UUID.randomUUID().toString();
              Entity e = UserMapper.buildUserEntity(datastore, KIND, id, name, dob, email, password, phone, gender, address);
              datastore.put(e);
            }
          }
        }
      }
      resp.getWriter().write("Upload complete");
    } catch (Exception ex) {
      throw new ServletException(ex);
    }
  }

  private String getCellString(Row r, int idx) {
    Cell c = r.getCell(idx);
    if (c == null) return "";
    if (c.getCellType() == CellType.STRING) return c.getStringCellValue();
    if (c.getCellType() == CellType.NUMERIC) {
      if (DateUtil.isCellDateFormatted(c)) {
        return c.getLocalDateTimeCellValue().toLocalDate().toString();
      } else {
        double v = c.getNumericCellValue();
        long lv = (long) v;
        if (lv == v) return Long.toString(lv);
        return Double.toString(v);
      }
    }
    return c.toString();
  }
}
