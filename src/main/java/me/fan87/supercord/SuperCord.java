package me.fan87.supercord;

import me.fan87.MySQLConfig;
import me.fan87.MySQLDatabase;
import me.fan87.Schema;
import me.fan87.Table;

import java.sql.SQLException;

public final class SuperCord {

    public static String username;
    public static String password;
    public static String schemaName;
    public static String ip;
    public static String customParameters = "";
    public static int port;
    public static MySQLDatabase database;
    public static Schema schema;
    public static Table mojangAPITable;

    public static final String VERSION = "1.3";
    // TEST


    public static void setupSuperCord() throws SQLException, ClassNotFoundException {

        System.out.println("Loaded SuperCord (Version: " + VERSION + " )!");

        database = new MySQLDatabase(password, username, schemaName, ip, port, customParameters, new MySQLConfig(), false);
        for (Schema s : database.getSchemasQuery()) {
            if (s.getName().equalsIgnoreCase(database.getSchema())) {
                schema = s;
            }
        }
        mojangAPITable = schema.createTableQuery("cached-players", new Table.Column("name", "text", false, null),
                new Table.Column("uuid", "text", false, null));

    }
}
