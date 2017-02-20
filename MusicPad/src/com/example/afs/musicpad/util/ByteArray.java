// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.util;

import java.nio.ByteOrder;

public class ByteArray {
  public static byte[] fromBigEndianLong(long l) {
    byte[] result = new byte[Long.BYTES];
    for (int i = Long.BYTES - 1; i >= 0; i--) {
      result[i] = (byte) (l & 0xFF);
      l >>= 8;
    }
    return result;
  }

  public static byte[] fromBigEndianShort(short s) {
    byte[] result = new byte[Short.BYTES];
    for (int i = Short.BYTES - 1; i >= 0; i--) {
      result[i] = (byte) (s & 0xFF);
      s >>= 8;
    }
    return result;
  }

  public static byte[] fromLittleEndianLong(long l) {
    byte[] result = new byte[Long.BYTES];
    for (int i = 0; i < Long.BYTES; i++) {
      result[i] = (byte) (l & 0xFF);
      l >>= 8;
    }
    return result;
  }

  public static byte[] fromLittleEndianShort(short s) {
    byte[] result = new byte[Short.BYTES];
    for (int i = 0; i < Short.BYTES; i++) {
      result[i] = (byte) (s & 0xFF);
      s >>= 8;
    }
    return result;
  }

  public static byte[] fromNativeLong(long l) {
    byte[] result;
    if (ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN)) {
      result = fromBigEndianLong(l);
    } else {
      result = fromLittleEndianLong(l);
    }
    return result;
  }

  public static byte[] fromNativeShort(short s) {
    byte[] result;
    if (ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN)) {
      result = fromBigEndianShort(s);
    } else {
      result = fromLittleEndianShort(s);
    }
    return result;
  }

  public static int toBigEndianInteger(byte[] b, int offset) {
    int result = 0;
    for (int i = 0; i < Integer.BYTES; i++) {
      result <<= Byte.SIZE;
      result |= (b[offset + i] & 0xFF);
    }
    return result;
  }

  public static long toBigEndianLong(byte[] b, int offset) {
    long result = 0;
    for (int i = 0; i < Long.BYTES; i++) {
      result <<= Byte.SIZE;
      result |= (b[offset + i] & 0xFF);
    }
    return result;
  }

  public static short toBigEndianShort(byte[] b, int offset) {
    short result = 0;
    for (int i = 0; i < Short.BYTES; i++) {
      result <<= Byte.SIZE;
      result |= (b[offset + i] & 0xFF);
    }
    return result;
  }

  public static int toLittleEndianInteger(byte[] b, int offset) {
    int result = 0;
    for (int i = Integer.BYTES - 1; i >= 0; i--) {
      result <<= Byte.SIZE;
      result |= (b[offset + i] & 0xFF);
    }
    return result;
  }

  public static long toLittleEndianLong(byte[] b, int offset) {
    long result = 0;
    for (int i = Long.BYTES - 1; i >= 0; i--) {
      result <<= Byte.SIZE;
      result |= (b[offset + i] & 0xFF);
    }
    return result;
  }

  public static short toLittleEndianShort(byte[] b, int offset) {
    short result = 0;
    for (int i = Short.BYTES - 1; i >= 0; i--) {
      result <<= Byte.SIZE;
      result |= (b[offset + i] & 0xFF);
    }
    return result;
  }

  public static int toNativeInteger(byte[] b, int offset) {
    int result;
    if (ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN)) {
      result = toBigEndianInteger(b, offset);
    } else {
      result = toLittleEndianInteger(b, offset);
    }
    return result;
  }

  public static long toNativeLong(byte[] b, int offset) {
    long result;
    if (ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN)) {
      result = toBigEndianLong(b, offset);
    } else {
      result = toLittleEndianLong(b, offset);
    }
    return result;
  }

  public static short toNativeShort(byte[] b, int offset) {
    short result;
    if (ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN)) {
      result = toBigEndianShort(b, offset);
    } else {
      result = toLittleEndianShort(b, offset);
    }
    return result;
  }
}
