package com.cryo;

import com.cryo.entities.MySQLRead;
import com.google.common.base.CaseFormat;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import org.apache.commons.pool.ObjectPool;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.cryo.utils.ConnectionUtils.*;

@RequiredArgsConstructor
@SuppressWarnings("unused")
public class DBConnection {

    private final String schema;
    private final ObjectPool pool;

    public void set(String database, String update, String clause, Object... params) {
        if(params == null || params.length == 0) {
            String query = "UPDATE " + database + " SET " + update + " WHERE " + clause + ";";
            execute(query);
            return;
        }
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("UPDATE ").append(database).append(" SET ")
                    .append(update).append(" WHERE ").append(clause+";");
            Connection connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(builder.toString());
            if(params != null)
                setParams(stmt, params);
            stmt.execute();
            returnConnection(connection);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void setParams(PreparedStatement stmt, Object[] params) {
        try {
            int index = 0;
            for (int i = 0; i < params.length; i++) {
                Object obj = params[i];
                index++;
                if (obj instanceof String) {
                    String string = (String) obj;
                    if (string.equals("DEFAULT")) {
                        index--;
                        continue;
                    }
                    stmt.setString(index, (String) obj);
                } else if (obj instanceof Integer)
                    stmt.setInt(index, (int) obj);
                else if(obj instanceof Double)
                    stmt.setDouble(index, (double) obj);
                else if(obj instanceof Long)
                    stmt.setTimestamp(index, new Timestamp((long) obj));
                else if(obj instanceof Timestamp)
                    stmt.setTimestamp(index, (Timestamp) obj);
                else if(obj instanceof Date)
                    stmt.setDate(index, new java.sql.Date(((Date) obj).getTime()));
                else if(obj instanceof java.sql.Date)
                    stmt.setDate(index, (java.sql.Date) obj);
                else if (obj instanceof Boolean)
                    stmt.setBoolean(index, (Boolean) obj);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public <T> ArrayList<T> selectList(String table, String condition, Class<T> c, Object... values) {
        return selectList(table, condition, null, c, values);
    }

    public <T> ArrayList<T> selectList(String table, Class<T> c, Object... values) {
        return selectList(table, null, null, c, values);
    }

    public <T> ArrayList<T> selectList(String table, String condition, String order, Class<T> c, Object... values) {
        try {
            Connection connection = getConnection();
            StringBuilder builder = new StringBuilder();
            builder.append("SELECT * FROM ").append(table);
            if (condition != null && !condition.equals("")) builder.append(" WHERE ").append(condition);
            if (order != null && !order.equals("")) builder.append(" " + order);
            @Cleanup PreparedStatement stmt = connection.prepareStatement(builder.toString());
            if(values != null)
                setParams(stmt, values);
            @Cleanup ResultSet set = stmt.executeQuery();
            ArrayList<T> list = new ArrayList<>();
            returnConnection(connection);
            if (wasNull(set)) return list;
            while (next(set))
                list.add(loadClass(set, c));
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public <T> T selectClass(String table, String condition, Class<T> c, Object... values) {
        return selectClass(table, condition, null, c, values);
    }

    public <T> T selectClass(String table, Class<T> c, Object... values) {
        return selectClass(table, null, null, c, values);
    }

    public <T> T selectClass(String table, String condition, String order, Class<T> c, Object... values) {
        try {
            Connection connection = getConnection();
            StringBuilder builder = new StringBuilder();
            builder.append("SELECT * FROM ").append(table);
            if (condition != null && !condition.equals("")) builder.append(" WHERE ").append(condition);
            if (order != null && !order.equals("")) builder.append(" ").append(order);
            @Cleanup PreparedStatement stmt = connection.prepareStatement(builder.toString());
            if(values != null)
                setParams(stmt, values);
            @Cleanup ResultSet set = stmt.executeQuery();
            returnConnection(connection);
            if (empty(set)) return null;
            return loadClass(set, c);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> T loadClass(ResultSet set, Class<T> c) {
        try {
            List<Class<?>> types = new ArrayList<>();
            List<Object> cValues = new ArrayList<>();
            for (Field field : c.getDeclaredFields()) {
                if (!Modifier.isFinal(field.getModifiers()) && !field.isAnnotationPresent(MySQLRead.class)) continue;
                types.add(field.getType());
                String name = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName());
                if (field.isAnnotationPresent(MySQLRead.class)) {
                    String value = field.getAnnotation(MySQLRead.class).value();
                    if (!value.equals("null")) name = value;
                }
                switch (field.getType().getSimpleName().toLowerCase()) {
                    case "int":
                        cValues.add(getInt(set, name));
                        break;
                    case "string":
                        cValues.add(getString(set, name));
                        break;
                    case "boolean":
                        cValues.add(getBoolean(set, name));
                        break;
                    case "timestamp":
                        cValues.add(getTimestamp(set, name));
                        break;
                    case "time":
                        cValues.add(getTime(set, name));
                        break;
                    case "double":
                        cValues.add(getDouble(set, name));
                        break;
                    case "long":
                        cValues.add(getLongInt(set, name));
                        break;
                    case "date":
                        cValues.add(getDate(set, name));
                        break;
                    default:
                        System.out.println("Missing type: " + field.getType().getName().toLowerCase());
                        break;
                }
            }
            Constructor<T> constructor = c.getConstructor(types.toArray(new Class<?>[types.size()]));
            T obj = constructor.newInstance(cValues.toArray());
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int selectCount(String database, String condition, Object...values) {
        try {
            Connection connection = getConnection();
            StringBuilder builder = new StringBuilder();
            builder.append("SELECT COUNT(*) FROM "+database);
            if(condition != null && !condition.equals(""))
                builder.append(" WHERE ").append(condition);
            @Cleanup PreparedStatement stmt = connection.prepareStatement(builder.toString());
            if(values != null)
                setParams(stmt, values);
            @Cleanup ResultSet set = stmt.executeQuery();
            returnConnection(connection);
            if(!set.next()) return 0;
            int count = set.getInt(1);
            return count;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int insert(String database, Object... values) {
        try {
            Connection connection = getConnection();
            int inserts = values.length;
            Object[] objs = values;
            StringBuilder insert = new StringBuilder();
            for (int i = 0; i < inserts; i++) {
                Object obj = objs[i];
                if(obj == null) {
                    insert.append("NULL");
                    if (i != inserts - 1)
                        insert.append(", ");
                    continue;
                } else if (obj instanceof String) {
                    String string = (String) obj;
                    if (string.equals("DEFAULT") || string.equals("NULL")) {
                        insert.append(string);
                        if (i != inserts - 1)
                            insert.append(", ");
                        continue;
                    }
                }
                insert.append("?");
                if (i != inserts - 1)
                    insert.append(", ");
            }
            String query = "INSERT INTO `" + database + "` VALUES(" + insert.toString() + ")";
            @Cleanup PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            if(values != null)
                setParams(stmt, values);
//			System.out.println(stmt);
            stmt.execute();
            @Cleanup ResultSet set = stmt.getGeneratedKeys();
            returnConnection(connection);
            if(set.next())
                return set.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public ResultSet selectAll(String database, String condition) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT * FROM ").append(database).append(" "+condition);
        return executeQuery(builder.toString());
    }

    public void delete(String database, String condition, Object...values) {
        StringBuilder builder = new StringBuilder();
        builder.append("DELETE FROM ").append(database).append(" WHERE ").append(condition);
        try {
            Connection connection = getConnection();
            @Cleanup PreparedStatement stmt = connection.prepareStatement(builder.toString());
            if(values != null)
                setParams(stmt, values);
            stmt.execute();
            returnConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void delete(String database, String condition) {
        StringBuilder builder = new StringBuilder();
        builder.append("DELETE FROM ").append(database).append(condition != null ? " WHERE " : "")
                .append(condition != null ? condition : "");
        execute(builder.toString());
    }

    public void update(String database, String clause, Object clazz, String[] replacementsS, Object... values) {
        try {
            StringBuilder builder = new StringBuilder();
            ArrayList<Object> vals = new ArrayList<>();
            List<String> replacements = Arrays.asList(replacementsS);
            for(Field field : clazz.getClass().getDeclaredFields()) {
                if(!replacements.contains(field.getName())) continue;
                field.setAccessible(true);
                String name = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName());
                if (field.isAnnotationPresent(MySQLRead.class)) {
                    String value = field.getAnnotation(MySQLRead.class).value();
                    if (!value.equals("null"))
                        name = value;
                }
                builder.append(name+"=?,");
                vals.add(field.get(clazz));
            }
            vals.addAll(Arrays.asList(values));
            set(database, builder.substring(0, builder.length()-1), clause, vals.toArray());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void execute(String query) {
        try {
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.execute();
            statement.close();
            returnConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void execute(String query, Object... values) {
        try {
            Connection connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
            if(values != null)
                setParams(stmt, values);
            stmt.execute();
            stmt.close();
            returnConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet executeQuery(String query) {
        try {
            Connection connection = getConnection();
            @Cleanup PreparedStatement statement = connection.prepareStatement(query);
            @Cleanup ResultSet set = statement.executeQuery();
            returnConnection(connection);
            if (set != null)
                return set;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet executeQuery(String query, Object...values) {
        try {
            Connection connection = getConnection();
            @Cleanup PreparedStatement statement = connection.prepareStatement(query);
            if(values != null)
                setParams(statement, values);
            @Cleanup ResultSet set = statement.executeQuery();
            returnConnection(connection);
            if (set != null)
                return set;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Connection getConnection() {
        try {
            return (Connection) pool.borrowObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void returnConnection(Object connection) {
        try {
            pool.returnObject(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
