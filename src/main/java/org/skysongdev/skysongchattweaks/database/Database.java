package org.skysongdev.skysongchattweaks.database;

import org.bukkit.Bukkit;

import java.sql.*;
import java.util.ArrayList;

import static org.skysongdev.skysongchattweaks.SkysongChatTweaks.getPlugin;

public class Database {
    private final String HOST;
    private final String PORT;
    private final String USER;
    private final String PASSWORD;
    private final String DATABASE;

    public Database(String host, String port, String user, String password, String database) {
        this.HOST = host;
        this.PORT = port;
        this.USER = user;
        this.PASSWORD = password;
        this.DATABASE = database;
    }

    private Connection sqlConnection;

    //Initialization
    public Connection getConnection() throws SQLException {
        if(sqlConnection != null){
            return sqlConnection;
        }
        String url = "jdbc:mysql://" + this.HOST + ":" + this.PORT + "/" + this.DATABASE;
        sqlConnection = DriverManager.getConnection(url, this.USER, this.PASSWORD);
        Bukkit.getLogger().info("Connected to the database");
        return sqlConnection;
    }

    public void initializeDatabase() throws SQLException{
        Statement buildStatement = getConnection().createStatement();
        String sql = "CREATE TABLE IF NOT EXISTS skysongchat_profiles(id INT AUTO_INCREMENT PRIMARY KEY, uuid VARCHAR(36), profile VARCHAR(20), permissions TEXT)";
        buildStatement.execute(sql);
    }

    //Data Handling
    public void addProfile(Profile profile) throws SQLException{
        PreparedStatement statement = getConnection().prepareStatement("INSERT INTO skysongchat_profiles(uuid, profile, permissions) VALUES(?, ?, ?)");
        statement.setString(1, profile.getUUID());
        statement.setString(2, profile.getProfile());
        String permissions = "";
        for(String perm : profile.getPermissions()){
            permissions += perm + ";";
        }
        if(!permissions.isEmpty())
            statement.setString(3, permissions.substring(0, permissions.length() - 1));
        else
            statement.setString(3, permissions);
        statement.execute();
        statement.close();
    }

    public void updateProfile(Profile profile) throws SQLException{
        PreparedStatement statement = getConnection().prepareStatement("UPDATE skysongchat_profiles SET permissions = ? WHERE uuid = ? AND profile = ?");

        String permissions = "";
        for(String perm : profile.getPermissions()){
            permissions += perm + ";";
        }
        statement.setString(1, permissions.substring(0, permissions.length() - 1));
        statement.setString(2, profile.getUUID());
        statement.setString(3, profile.getProfile());
        statement.execute();
        statement.close();
    }

    public void dumpDatabaseData() throws SQLException{
        Statement statement = getConnection().createStatement();
        ResultSet set = statement.executeQuery("SELECT * FROM skysongchat_profiles");
        while(set.next()){
            String uuid = set.getString("uuid");
            String profile = set.getString("profile");
            String permissions = set.getString("permissions");
            String[] perms = permissions.split(";");
            ArrayList<String> permsList = new ArrayList<>();
            for(String perm : perms){
                permsList.add(perm);
            }
            Profile newProfile = new Profile(uuid, profile, permsList);
            getPlugin().profiles.add(newProfile);
        }
        statement.close();
    }

    public void Ping() throws SQLException{
        Statement statement = getConnection().createStatement();
        String sql = "SELECT * from skysongchat_profiles";
        ResultSet results = statement.executeQuery(sql);
        statement.close();
    }

}
