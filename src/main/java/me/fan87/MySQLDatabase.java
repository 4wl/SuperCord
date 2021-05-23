package me.fan87;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySQLDatabase {

    private String password;
    private String username;
    private String schema;
    private String ip;
    private String customParameters;
    private int port;
    private boolean oneTimeUse;

    private MySQLConfig config;

    private Connection connection;

    public void reconnect() throws SQLException, ClassNotFoundException {
        if (!oneTimeUse) customParameters += (customParameters.equals("")?"":"&") + "autoReconnect=true";
        connection = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + Integer.toString(port) + "/" + (schema == null?"": schema) + (customParameters.equals("")?"":"?" + customParameters), username, password);
    }

    public void disconnect() throws SQLException {
        connection.close();
    }

    public MySQLDatabase(String password, String username, String schema, String ip, int port, String customParameters, MySQLConfig config, boolean oneTimeUse) throws SQLException, ClassNotFoundException {
        this.password = password;
        this.username = username;
        this.schema = schema;
        this.customParameters = customParameters;
        this.ip = ip;
        this.port = port;
        this.config = config;
        this.oneTimeUse = oneTimeUse;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException exception) {
            Class.forName("com.mysql.jdbc.Driver");
        }
        reconnect();
    }


    public boolean isOneTimeUse() {
        return oneTimeUse;
    }

    public List<Schema> getSchemasQuery() throws SQLException {
        ResultSet resultSet = connection.getMetaData().getCatalogs();
        List<String> schemaNames = new ArrayList<>();
        List<Schema> schemas = new ArrayList<>();
        while (resultSet.next()) {
            schemaNames.add(resultSet.getString(1));
        }
        for (String schemaName : schemaNames) {
            Schema schema = new Schema(schemaName, this);
            schemas.add(schema);
        }
        return schemas;
    }

    public MySQLConfig getConfig() {
        return config;
    }

    public Schema createSchemaQuery(String name) throws SQLException {
        execute("CREATE SCHEMA IF NOT EXISTS `" + name + "`;");
        Schema schema = new Schema(name, this);
        return schema;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getSchema() {
        return schema;
    }

    public String getCustomParameters() {
        return customParameters;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }

    public String getUsername() {
        return username;
    }

    public void execute(String sql) throws SQLException {
        getConnection().createStatement().execute(sql);
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        return getConnection().createStatement().executeQuery(sql);
    }

    public void undoQuery() throws SQLException {
        execute("ROLLBACK;");
    }
}
