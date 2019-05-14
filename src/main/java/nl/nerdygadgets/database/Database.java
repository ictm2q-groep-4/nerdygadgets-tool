package nl.nerdygadgets.database;


import java.sql.*;

/**
 * Connection management for the database & querying.
 * See: https://nl.wikipedia.org/wiki/Singleton_(ontwerppatroon) for more information about Singletons.
 *
 * @author Lucas Ouwens
 * @author Djabir Omar Mohamed
 * @author Joris Vos
 */
public class Database {
     private Connection connection = null;

    /**
     * A private database constructor to block anything outside from making a new instance of this class.
     */
    private Database() {
        String DB_URL = "jdbc:mysql://127.0.0.1:3306/cursus";
        String DB_USER = "root";
        String DB_PASS = "";

        try {
             connection = DriverManager.getConnection
                    (DB_URL,DB_USER,DB_PASS);
            Statement stmt = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void getUpTime() {
        String sql = "SELECT id,name_first FROM user";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            System.out.println(stmt.execute());
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * A variable which holds our *only* instance of this class.
     */
    private static Database databaseInstance = null;

    /**
     * Get access to the Singleton Database class.
     *
     * @return Database
     */
    public static Database getDatabaseInstance() {
        if (databaseInstance == null) {
            databaseInstance = new Database();
        }
        return databaseInstance;
    }
}
