package nl.nerdygadgets.database;

/**
 * Connection management for the database & querying.
 * See: https://nl.wikipedia.org/wiki/Singleton_(ontwerppatroon) for more information about Singletons.
 *
 * @author Lucas Ouwens
 * @author <your name>
 */
public class Database {

    /**
     * A private database constructor to block anything outside from making a new instance of this class.
     */
    private Database() {
        // ...
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
