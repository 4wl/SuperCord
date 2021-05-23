package me.fan87;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {

    public static class TooManyResultException extends Exception {

    }

    public static class Column {
        private String name;
        private String type;
        private Object defaultValue;
        private boolean nullable;

        public Column(String name, String type, boolean nullable, Object defaultValue) {
            this.name = name;
            this.type = type;
            this.defaultValue = defaultValue;
            this.nullable = nullable;
        }

        public boolean isNullable() {
            return nullable;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }
    }

    public static class Row {
        private Map<String, Object> values = new HashMap<>();

        public Row(Map<String, Object> values) {
            this.values = values;
        }


        public Map<String, Object> getValues() {
            return values;
        }
    }

    private String name;
    private Schema schema;
    private MySQLDatabase database;

    public Table(String name, Schema schema, MySQLDatabase database) {
        this.name = name;
        this.database = database;
        this.schema = schema;
    }

    public void dropQuery() throws SQLException{
        execute("drop table `" + name + "`;");
    }

    public String getName() {
        return name;
    }

    public MySQLDatabase getDatabase() {
        return database;
    }

    public Schema getSchema() {
        return schema;
    }

    public void createColumnQuery(Column column1) throws SQLException {
        execute("ALTER TABLE `" + name + "` ADD " + "`" + column1.getName() + "` " + column1.getType() + " " + (column1.getDefaultValue()==null?"":"default " + (column1.getDefaultValue() instanceof String?"'":"") + column1.getDefaultValue().toString() + (column1.getDefaultValue() instanceof String?"'":"") + " ") + (column1.isNullable()?"null":"not null"));
    }

    public void removeColumnQuery(String columnName) throws SQLException {
        execute("ALTER TABLE `" + name + "` DROP `" + columnName + "`;");
    }

    public void execute(String sql) throws SQLException {
        schema.execute(sql);
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        return schema.executeQuery(sql);
    }

    public List<Column> getColumnsQuery() throws SQLException {
        List<Column> columns = new ArrayList<>();

        ResultSet resultSet = executeQuery("SHOW COLUMNS from `" +  name + "`");
        while (resultSet.next()) {
            columns.add(new Column(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3).equalsIgnoreCase("YES"), resultSet.getObject(5)));
        }
        return columns;
    }

    public void setQuery(Map<String, Object> value) throws TooManyResultException, SQLException {
        String conditions = "";
        int i = 0;
        for (String key : value.keySet()) {
            i++;
            conditions += "`" + key + "`=" + ((value.get(key) instanceof String)?"'" + value.get(key) + "'":value.get(key)) + "";
            if (i != value.size()) {
                conditions += " AND ";
            }
        }
        if (getQuery(conditions).size() == 0) {
            insertQuery(value);
        } else if (getQuery(conditions).size() == 1) {
            updateQuery(conditions, value);
        } else if (getQuery(conditions).size() >= 2) {
            throw new TooManyResultException();
        }
    }

    public List<Row> getQuery(String conditions) throws SQLException{
        List<Row> rows = new ArrayList<>();
        List<Column> columns = getColumnsQuery();
        ResultSet resultSet = executeQuery("SELECT * FROM `" + name + "`" + (conditions.equals("")?"":" WHERE " + conditions) + ";");
        while (resultSet.next()) {
            HashMap<String, Object> map = new HashMap<>();
            int i = 0;
            for (Column c : columns) {
                map.put(columns.get(i).getName(), resultSet.getObject(i + 1));
                i++;
            }
            rows.add(new Row(map));
            i++;
        }
        return rows;
    }

    @Deprecated
    public void insertQuery(Map<String, Object> value) throws SQLException{
        String command = "INSERT INTO `" + name + "` (";
        int i = 0;
        for (String key : value.keySet()) {
            command += "`" + key + "`";
            i++;
            if (i != value.size()) command += ", ";
        }
        command += ") VALUES (";
        i = 0;
        for (Object v : value.values()) {
            if (v instanceof String) {
                command += "'";
            }
            command += v;
            if (v instanceof String) {
                command += "'";
            }
            i++;
            if (i != value.size()) command += ", ";
        }
        command += ")";
        execute(command);
    }

    @Deprecated
    public void updateQuery(String conditions, Map<String, Object> value) throws SQLException{
        String command = "UPDATE `" + name + "` SET ";
        int i = 0;
        for (String key : value.keySet()) {
            command += "`" + key + "`=" + ((value.get(key) instanceof String)?"'" + value.get(key) + "'":value.get(key));
            i++;
            if (i != value.size()) command += ", ";
        }
        command += conditions.equals("")?"":" WHERE " + conditions;
        execute(command);
    }

    public void deleteQuery(String condition) throws SQLException{
        String command = "DELETE FROM `" + name + (condition.equals("")?"":"` WHERE " + condition) + ";";
        execute(command);
    }

}
