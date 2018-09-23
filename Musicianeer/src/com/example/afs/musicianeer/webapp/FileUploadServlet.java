// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.webapp;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.example.afs.musicianeer.html.Division;
import com.example.afs.musicianeer.html.TextElement;
import com.example.afs.musicianeer.main.ImportService;
import com.example.afs.musicianeer.main.MidiLibraryManager;
import com.example.afs.musicianeer.main.Services;
import com.example.afs.musicianeer.midi.SongInfoFactory.SongInfo;
import com.example.afs.musicianeer.task.MessageBroker;
import com.example.afs.musicianeer.task.ServiceTask;

public class FileUploadServlet extends HttpServlet {

  private class FileUploadServiceTask extends ServiceTask {
    protected FileUploadServiceTask(MessageBroker broker) {
      super(broker);
    }

    @Override
    public synchronized <T> T request(Service<T> service) {
      return super.request(service);
    }
  }

  private static final Pattern pattern = Pattern.compile("^.*filename[^;=\\n]*=(?:(\\\\?['\"])(.*?)\\1|(?:[^\\s]+'.*?')?([^;\\n]*)).*$");

  private FileUploadServiceTask serviceTask;

  public FileUploadServlet(MessageBroker broker) {
    serviceTask = new FileUploadServiceTask(broker);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    int importCount = 0;
    Division div = new Division();
    div.style("color: #B4B7BA;");
    try {
      // NB: relative pathnames will be rooted in /tmp
      String midiLibraryPath = MidiLibraryManager.getMidiLibraryPath();
      MultipartConfigElement multipartConfigElement = new MultipartConfigElement(midiLibraryPath);
      request.setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);
      for (Part part : request.getParts()) {
        try {
          String status;
          String filename = getFileName(part);
          part.write(filename);
          String pathname = midiLibraryPath + File.separator + filename;
          SongInfo songInfo = serviceTask.request(new ImportService(pathname));
          if (songInfo != null) {
            status = "Imported " + filename;
            importCount++;
          } else {
            status = "Cannot import " + filename;
          }
          div.add(new Division().add(new TextElement(status)));
        } catch (RuntimeException | IOException e) {
          div.add(new Division().add(new TextElement(e.toString())));
        }
      }
      if (importCount > 0) {
        serviceTask.request(Services.refreshMidiLibrary);
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
