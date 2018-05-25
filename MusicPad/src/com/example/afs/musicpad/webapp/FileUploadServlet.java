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

import com.example.afs.musicpad.webapp.musicianeer.MidiLibraryManager;

public class FileUploadServlet extends HttpServlet {

  private static final Pattern pattern = Pattern.compile("^.*filename[^;=\\n]*=(?:(\\\\?['\"])(.*?)\\1|(?:[^\\s]+'.*?')?([^;\\n]*)).*$");

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String midiLibraryPath = MidiLibraryManager.getMidiLibraryPath();
    MultipartConfigElement multipartConfigElement = new MultipartConfigElement(midiLibraryPath);
    request.setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);
    for (Part part : request.getParts()) {
      String fileName = getFileName(part);
      part.write(fileName);
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