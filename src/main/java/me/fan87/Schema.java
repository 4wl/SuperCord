package me.fan87;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Schema {

    private String name;
    private MySQLDatabase database;

    public Schema(String name, MySQLDatabase database) {
        this.name = name;
        this.database = database;
    }

    public String getName() {
        return name;
    }

    public void dropQuery() throws SQLException{
        database.getConnection().createStatement().execute("drop schema `" + name + "`;");
    }

    public Table createTableQuery(String name, Table.Column... columns) throws SQLException {
        String column = "";
        int i = 0;
        for (Table.Column column1 : columns) {
            i++;

            column += "`" + column1.getName() + "` " + column1.getType() + " " + (column1.getDefaultValue()==null?"":"default " + (column1.getDefaultValue() instanceof String?"'":"") + column1.getDefaultValue().toString() + (column1.getDefaultValue() instanceof String?"'":"") + " ") + (column1.isNullable()?"null":"not null") + (i==columns.length?"":", ");
        }
        execute("CREATE TABLE IF NOT EXISTS `" + name + "` ( " + column + " );");
        Table table = new Table(name, this, database);
        return table;
    }

    public List<Table> getTablesQuery() throws SQLException {
        ResultSet resultSet = executeQuery("SHOW TABLES");
        List<Table> tables = new ArrayList<>();
        while (resultSet.next()) {
            String name = resultSet.getString(1);
            Table table = new Table(name, this, database);
            tables.add(table);
        }

        return tables;
    }

    public void execute(String sql) throws SQLException {
        database.getConnection().createStatement().execute("USE `" + getName() + "`;");
        database.getConnection().createStatement().execute(sql);
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        database.getConnection().createStatement().execute("USE `" + getName() + "`;");
        return database.getConnection().createStatement().executeQuery(sql);
    }

}
