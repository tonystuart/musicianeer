// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.frankenmusic.db;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class Database {

  private static final String IS = "is";
  private static final String GET = "get";
  private static final Object GETID = "getId";
  private static final Object GETCLASS = "getClass";
  private static final String SET = "set";

  private Connection connection;

  public Database(Connection connection) {
    this.connection = connection;
  }

  public Database(String url) {
    try {
      Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
      connection = DriverManager.getConnection(url);
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void commit() {
    try {
      connection.commit();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public <T> void executeSelect(Consumer<T> consumer, Class<T> objectClass, String sql) {
    try {
      Method[] methods = objectClass.getMethods();
      try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
        statement.execute();
        ResultSet resultSet = statement.getResultSet();
        while (resultSet.next()) {
          T object = objectClass.newInstance();
          for (Method method : methods) {
            String name = method.getName();
            if (method.getParameterCount() == 1) {
              if (name.startsWith(SET)) {
                String fieldName = name.substring(SET.length()).toUpperCase();
                Object value = resultSet.getObject(fieldName);
                method.invoke(object, value);
              }
            }
          }
          consumer.accept(object);
        }
      }
    } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SQLException | InstantiationException e) {
      throw new RuntimeException(e);
    }
  }

  public Connection getConnection() {
    return connection;
  }

  public void initializeParameters(PreparedStatement statement, Iterable<Object> values) {
    int column = 1;
    for (Object value : values) {
      try {
        if (value == null) {
          int parameterType = statement.getParameterMetaData().getParameterType(column);
          statement.setNull(column, parameterType);
        } else if (value instanceof InputStream) {
          statement.setBlob(column, (InputStream) value);
        } else {
          statement.setObject(column, value);
        }
        column++;
      } catch (SQLException e) {
        throw new RuntimeException(e.toString() + "\n" + "column=" + column + ", value=" + value);
      }
    }
  }

  public void insert(Object object) {
    insert(object, quote(object.getClass().getSimpleName().toUpperCase()));
  }

  public void insert(Object object, String tableName) {
    try {
      boolean hasId = false;
      StringBuilder names = new StringBuilder();
      StringBuilder markers = new StringBuilder();
      List<Object> values = new LinkedList<>();
      Method[] methods = object.getClass().getMethods();
      for (Method method : methods) {
        String columnName = null;
        String name = method.getName();
        if (method.getParameterCount() == 0 && !name.equals(GETCLASS)) {
          if (name.startsWith(GET)) {
            if (name.equals(GETID)) {
              hasId = true;
            }
            columnName = name.substring(GET.length()).toUpperCase();
          } else if (name.startsWith(IS)) {
            columnName = name.substring(IS.length()).toUpperCase();
          }
          if (columnName != null) {
            if (names.length() > 0) {
              names.append(", ");
              markers.append(", ");
            }
            names.append(quote(columnName));
            markers.append("?");
            values.add(method.invoke(object));
          }
        }
      }
      StringBuilder s = new StringBuilder();
      s.append("insert into " + tableName + "\n");
      s.append("(\n");
      s.append("  " + names + "\n");
      s.append(")\n");
      s.append("values\n");
      s.append("(\n");
      s.append("  " + markers + "\n");
      s.append(")\n");
      try (PreparedStatement statement = connection.prepareStatement(s.toString(), hasId ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS)) {
        initializeParameters(statement, values);
        statement.execute();
        Method setId;
        try {
          setId = object.getClass().getMethod("setId", Integer.class);
          ResultSet generatedKeys = statement.getGeneratedKeys();
          if (generatedKeys.next()) {
            setId.invoke(object, generatedKeys.getInt(1));
          }
        } catch (NoSuchMethodException e) {
          // Object has no setId method, do not set the generated identifier
        }
      }
    } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void rollback() {
    try {
      connection.rollback();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public <T extends Object> void selectAll(Consumer<T> consumer, Class<T> objectClass) {
    selectAllByTableAndClause(consumer, objectClass, null, null);
  }

  public <T extends Object> void selectAllByClause(Consumer<T> consumer, Class<T> objectClass, String clause) {
    selectAllByTableAndClause(consumer, objectClass, null, clause);
  }

  public <T extends Object> void selectAllByTable(Consumer<T> consumer, Class<T> objectClass, String tableName) {
    selectAllByTableAndClause(consumer, objectClass, tableName, null);
  }

  public <T extends Object> void selectAllByTableAndClause(Consumer<T> consumer, Class<T> objectClass, String tableName, String clause) {
    if (tableName == null) {
      tableName = quote(objectClass.getSimpleName().toUpperCase());
    }
    try {
      StringBuilder names = new StringBuilder();
      Method[] methods = objectClass.getMethods();
      for (Method method : methods) {
        String name = method.getName();
        if (method.getParameterCount() == 1) {
          if (name.startsWith(SET)) {
            String fieldName = name.substring(SET.length()).toUpperCase();
            String columnName = quote(fieldName);
            if (names.length() > 0) {
              names.append(", ");
            }
            names.append(columnName);
          }
        }
      }
      StringBuilder s = new StringBuilder();
      s.append("select\n");
      s.append("  " + names + "\n");
      s.append("from " + tableName + "\n");
      if (clause != null) {
        s.append(clause + "\n");
      }
      executeSelect(consumer, objectClass, s.toString());
    } catch (SecurityException | IllegalArgumentException e) {
      throw new RuntimeException(e);
    }
  }

  public void setAutoCommit(boolean isAutoCommit) {
    try {
      connection.setAutoCommit(isAutoCommit);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void update(String sql) {
    Statement statement;
    try {
      statement = connection.createStatement();
      int count = statement.executeUpdate(sql);
      System.out.println(sql + " returned " + count);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private String quote(String name) {
    return "\"" + name + "\"";
  }

}
