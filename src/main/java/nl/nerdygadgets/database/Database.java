package nl.nerdygadgets.database;


import java.sql.*;

/**
 * Connection management for the database & querying.
 * See: https://nl.wikipedia.org/wiki/Singleton_(ontwerppatroon) for more information about Singletons.
 *
 * This class is not used!
 *
 * @author Lucas Ouwens
 * @author Djabir Omar Mohamed
 * @author Joris Vos
 */
@Deprecated
public class Database {
    /**
     * This variable stores the connection
     */
     private Connection connection = null;

    /**
     * A private database constructor to block anything outside from making a new instance of this class.
     */
    private Database() {
        String DB_URL = "jdbc:mysql://<host>:<port>/<database>";
        String DB_USER = "<username>";
        String DB_PASS = "<password>";

        try {
             connection = DriverManager.getConnection
                    (DB_URL,DB_USER,DB_PASS);
            Statement stmt = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Executes query
     */
    public void executeQuery(String query) {
        try {
            PreparedStatement stmt = connection.prepareStatement(query);
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
