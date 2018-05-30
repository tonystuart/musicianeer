package com.example.afs.musicpad.webapp;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.html.TextElement;
import com.example.afs.musicpad.webapp.musicianeer.MidiLibraryManager;

public class FileUploadServlet extends HttpServlet {

  private static final Pattern pattern = Pattern.compile("^.*filename[^;=\\n]*=(?:(\\\\?['\"])(.*?)\\1|(?:[^\\s]+'.*?')?([^;\\n]*)).*$");

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    Division div = new Division();
    div.style("color: #B4B7BA;");
    try {
      String midiLibraryPath = MidiLibraryManager.getMidiLibraryPath();
      MultipartConfigElement multipartConfigElement = new MultipartConfigElement(midiLibraryPath);
      request.setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);
      for (Part part : request.getParts()) {
        try {
          String filename = getFileName(part);
          part.write(filename);
          div.add(new Division().add(new TextElement("Imported " + filename)));
        } catch (RuntimeException | IOException e) {
          div.add(new Division().add(new TextElement(e.toString())));
        }
      }
    } catch (RuntimeException | ServletException | IOException e) {
      div.add(new Division().add(new TextElement(e.toString())));
    } finally {
      response.getOutputStream().println(div.render());
    }
  }

  private String getFileName(Part part) {
    String contentDisposition = part.getHeader("content-disposition");
    Matcher matcher = pattern.matcher(contentDisposition);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Cannot parse filename in " + contentDisposition);
    }
    int groupCount = matcher.groupCount();
    return matcher.group(groupCount - 1);
  }
}