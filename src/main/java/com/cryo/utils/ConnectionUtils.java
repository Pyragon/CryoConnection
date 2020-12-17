package com.cryo.utils;

import java.sql.*;

public class ConnectionUtils {

    public static boolean containsRow(ResultSet set, String row) {
        try {
            ResultSetMetaData rsMetaData = set.getMetaData();
            int numberOfColumns = rsMetaData.getColumnCount();

            // get the column names; column indexes start from 1
            for (int i = 1; i < numberOfColumns + 1; i++) {
                String columnName = rsMetaData.getColumnName(i);
                // Get the name of the column's table name
                if (row.equals(columnName))
                    return true;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getFetchSize(ResultSet set) {
        try {
            return set.getFetchSize();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long getLongInt(ResultSet set, String string) {
        try {
            return set.getLong(string);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean getBoolean(ResultSet set, String string) {
        try {
            return set.getBoolean(string);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getInt(ResultSet set, String string) {
        try {
            return set.getInt(string);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int getInt(ResultSet set, int index) {
        try {
            return set.getInt(index);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static double getDouble(ResultSet set, String string) {
        try {
            return set.getDouble(string);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Date getDate(ResultSet set, String string) {
        try {
            return set.getDate(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Time getTime(ResultSet set, String string) {
        try {
            return set.getTime(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Timestamp getTimestamp(ResultSet set, String string) {
        try {
            return set.getTimestamp(string);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getString(ResultSet set, String string) {
        try {
            return set.getString(string);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getRow(ResultSet set) {
        try {
            return set.getRow();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean last(ResultSet set) {
        try {
            return set.last();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean next(ResultSet set) {
        try {
            return set.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean wasNull(ResultSet set) {
        if(set == null)
            return true;
        try {
            return set.wasNull();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean empty(ResultSet set) {
        return set == null || wasNull(set) || !next(set);
    }
}
