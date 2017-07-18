// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.jni;

public class Input {

  public static final String NATIVE_LIBRARY_NAME = "input_jni";

  public static native int capture(int fd, boolean is_captured);

  public static native short read_key_code(int fd);

}
