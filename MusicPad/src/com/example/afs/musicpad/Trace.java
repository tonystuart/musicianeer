// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad;

public class Trace {

  public enum TraceOption {
    COMMAND, CONFIGURATION, PLAY
  }

  private static boolean isTraceCommand = true;
  private static boolean isTraceConfiguration;
  private static boolean isTracePlay;

  public static boolean isTraceCommand() {
    return isTraceCommand;
  }

  public static boolean isTraceConfiguration() {
    return isTraceConfiguration;
  }

  public static boolean isTracePlay() {
    return isTracePlay;
  }

  public static void setTraceCommand(boolean isTraceCommand) {
    Trace.isTraceCommand = isTraceCommand;
  }

  public static void setTraceConfiguration(boolean isTraceConfiguration) {
    Trace.isTraceConfiguration = isTraceConfiguration;
  }

  public static void setTracePlay(boolean isTracePlay) {
    Trace.isTracePlay = isTracePlay;
  }

}
