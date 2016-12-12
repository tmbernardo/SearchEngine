import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handles all database-related actions. Uses singleton design pattern.
 *
 * @see LoginServer
 */
public class LoginDatabaseHandler {

	/** A {@link org.apache.log4j.Logger log4j} logger for debugging. */
	private static Logger log = LogManager.getLogger();

	/** Makes sure only one database handler is instantiated. */
	private static LoginDatabaseHandler singleton = new LoginDatabaseHandler();

	/** Used to determine if necessary tables are provided. */
	private static final String TABLES_SQL = "SHOW TABLES LIKE 'login_users';";

	// /** Used to determine if necessary tables are provided. */
	// private static final String HIST_TABLE_SQL = "SHOW TABLES LIKE
	// '?_search_hist';";

	/** Used to create necessary tables for this example. */
	private static final String CREATE_SQL = "CREATE TABLE login_users ("
			+ "userid INTEGER AUTO_INCREMENT PRIMARY KEY, " + "username VARCHAR(32) NOT NULL UNIQUE, "
			+ "password CHAR(64) NOT NULL, " + "usersalt CHAR(32) NOT NULL);";

	/** Used to create a unique table to save user search history **/
	private static final String CREATE_HIST_SQL = "CREATE TABLE %s_search_hist (searchterm VARCHAR(2000));";

	/** Used to insert a new user into the database. */
	private static final String REGISTER_SQL = "INSERT INTO login_users (username, password, usersalt) "
			+ "VALUES (?, ?, ?);";

	/** Used to determine if a username already exists. */
	private static final String USER_SQL = "SELECT username FROM login_users WHERE username = ?";

	/** Used to retrieve the salt associated with a specific user. */
	private static final String SALT_SQL = "SELECT usersalt FROM login_users WHERE username = ?";

	/** Used to authenticate a user. */
	private static final String AUTH_SQL = "SELECT username FROM login_users " + "WHERE username = ? AND password = ?";

	/** Used to remove a user from the database. */
	private static final String DELETE_SQL = "DELETE FROM login_users WHERE username = ?";

	private static final String DELETE_HIST_SQL = "DROP TABLE %s_search_hist;";

	/** Used to configure connection to database. */
	private DatabaseConnector db;

	/** Used to generate password hash salt for user. */
	private Random random;

	/**
	 * Initializes a database handler for the Login example. Private constructor
	 * forces all other classes to use singleton.
	 */
	private LoginDatabaseHandler() {
		Status status = Status.OK;
		random = new Random(System.currentTimeMillis());

		try {

			db = new DatabaseConnector("database.properties");
			status = db.testConnection() ? setupTables() : Status.CONNECTION_FAILED;
		} catch (FileNotFoundException e) {
			status = Status.MISSING_CONFIG;
		} catch (IOException e) {
			status = Status.MISSING_VALUES;
		}

		if (status != Status.OK) {
			log.fatal(status.message());
		}
	}

	/**
	 * Gets the single instance of the database handler.
	 *
	 * @return instance of the database handler
	 */
	public static LoginDatabaseHandler getInstance() {
		return singleton;
	}

	/**
	 * Checks to see if a String is null or empty.
	 * 
	 * @param text
	 *            - String to check
	 * @return true if non-null and non-empty
	 */
	public static boolean isBlank(String text) {
		return (text == null) || text.trim().isEmpty();
	}

	/**
	 * Returns the hex encoding of a byte array.
	 *
	 * @param bytes
	 *            - byte array to encode
	 * @param length
	 *            - desired length of encoding
	 * @return hex encoded byte array
	 */
	public static String encodeHex(byte[] bytes, int length) {
		BigInteger bigint = new BigInteger(1, bytes);
		String hex = String.format("%0" + length + "X", bigint);

		assert hex.length() == length;
		return hex;
	}

	public Status setupSearchHist() {
		Status status = Status.ERROR;

		try (Connection connection = db.getConnection(); Statement statement = connection.createStatement();) {
			if (!statement.executeQuery(TABLES_SQL).next()) {
				// Table missing, must create
				log.debug("Creating tables...");
				statement.executeUpdate(CREATE_SQL);

				// Check if create was successful
				if (!statement.executeQuery(TABLES_SQL).next()) {
					status = Status.CREATE_FAILED;
				} else {
					status = Status.OK;
				}
			} else {
				log.debug("Tables found.");
				status = Status.OK;
			}
		} catch (Exception ex) {
			status = Status.CREATE_FAILED;
			log.debug(status, ex);
		}

		return status;
	}

	/**
	 * Calculates the hash of a password and salt using SHA-256.
	 *
	 * @param password
	 *            - password to hash
	 * @param salt
	 *            - salt associated with user
	 * @return hashed password
	 */
	public static String getHash(String password, String salt) {
		String salted = salt + password;
		String hashed = salted;

		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(salted.getBytes());
			hashed = encodeHex(md.digest(), 64);
		} catch (Exception ex) {
			log.debug("Unable to properly hash password.", ex);
		}

		return hashed;
	}

	/**
	 * Checks if necessary table exists in database, and if not tries to create
	 * it.
	 *
	 * @return {@link Status.OK} if table exists or create is successful
	 */
	private Status setupTables() {
		Status status = Status.ERROR;

		try (Connection connection = db.getConnection(); Statement statement = connection.createStatement();) {
			if (!statement.executeQuery(TABLES_SQL).next()) {
				// Table missing, must create
				log.debug("Creating tables...");
				statement.executeUpdate(CREATE_SQL);

				// Check if create was successful
				if (!statement.executeQuery(TABLES_SQL).next()) {
					status = Status.CREATE_FAILED;
				} else {
					status = Status.OK;
				}
			} else {
				log.debug("Tables found.");
				status = Status.OK;
			}
		} catch (Exception ex) {
			status = Status.CREATE_FAILED;
			log.debug(status, ex);
		}

		return status;
	}

	/**
	 * Tests if a user already exists in the database. Requires an active
	 * database connection.
	 *
	 * @param connection
	 *            - active database connection
	 * @param user
	 *            - username to check
	 * @return Status.OK if user does not exist in database
	 * @throws SQLException
	 */
	private Status duplicateUser(Connection connection, String user) {

		assert connection != null;
		assert user != null;

		Status status = Status.ERROR;

		try (PreparedStatement statement = connection.prepareStatement(USER_SQL);) {
			statement.setString(1, user);

			ResultSet results = statement.executeQuery();
			status = results.next() ? Status.DUPLICATE_USER : Status.OK;
		} catch (SQLException e) {
			log.debug(e.getMessage(), e);
			status = Status.SQL_EXCEPTION;
		}

		return status;
	}

	/**
	 * Tests if a user already exists in the database.
	 *
	 * @see #duplicateUser(Connection, String)
	 * @param user
	 *            - username to check
	 * @return Status.OK if user does not exist in database
	 */
	public Status duplicateUser(String user) {
		Status status = Status.ERROR;

		try (Connection connection = db.getConnection();) {
			status = duplicateUser(connection, user);
		} catch (SQLException e) {
			status = Status.CONNECTION_FAILED;
			log.debug(e.getMessage(), e);
		}

		return status;
	}

	/**
	 * Registers a new user, placing the username, password hash, and salt into
	 * the database if the username does not already exist.
	 *
	 * @param newuser
	 *            - username of new user
	 * @param newpass
	 *            - password of new user
	 * @return {@link Status.OK} if registration successful
	 */
	private Status registerUser(Connection connection, String newuser, String newpass) {

		Status status = Status.ERROR;

		byte[] saltBytes = new byte[16];
		random.nextBytes(saltBytes);

		String usersalt = encodeHex(saltBytes, 32);
		String passhash = getHash(newpass, usersalt);

		try (PreparedStatement create_user = connection.prepareStatement(REGISTER_SQL);
				PreparedStatement create_hist = connection
						.prepareStatement(String.format(CREATE_HIST_SQL, usersalt));) {
			create_user.setString(1, newuser);
			create_user.setString(2, passhash);
			create_user.setString(3, usersalt);
			create_user.executeUpdate();

			// creates searchterm history table
			create_hist.executeUpdate();

			status = Status.OK;
		} catch (SQLException ex) {
			status = Status.SQL_EXCEPTION;
			log.debug(ex.getMessage(), ex);
		}

		return status;
	}

	/**
	 * Registers a new user, placing the username, password hash, and salt into
	 * the database if the username does not already exist.
	 *
	 * @param newuser
	 *            - username of new user
	 * @param newpass
	 *            - password of new user
	 * @return {@link Status.OK} if registration successful
	 */
	public Status registerUser(String newuser, String newpass) {
		Status status = Status.ERROR;
		log.debug("Registering " + newuser + ".");

		// make sure we have non-null and non-emtpy values for login
		if (isBlank(newuser) || isBlank(newpass)) {
			status = Status.INVALID_LOGIN;
			log.debug(status);
			return status;
		}

		// try to connect to database and test for duplicate user
		try (Connection connection = db.getConnection();) {
			status = duplicateUser(connection, newuser);

			// if okay so far, try to insert new user
			if (status == Status.OK) {
				status = registerUser(connection, newuser, newpass);
			}
		} catch (SQLException ex) {
			status = Status.CONNECTION_FAILED;
			log.debug(status, ex);
		}

		return status;
	}

	/**
	 * Gets the salt for a specific user.
	 *
	 * @param connection
	 *            - active database connection
	 * @param user
	 *            - which user to retrieve salt for
	 * @return salt for the specified user or null if user does not exist
	 * @throws SQLException
	 *             if any issues with database connection
	 */
	private String getSalt(Connection connection, String user) throws SQLException {
		assert connection != null;
		assert user != null;

		String salt = null;

		try (PreparedStatement statement = connection.prepareStatement(SALT_SQL);) {
			statement.setString(1, user);

			ResultSet results = statement.executeQuery();

			if (results.next()) {
				salt = results.getString("usersalt");
			}
		}

		return salt;
	}

	/**
	 * Checks if the provided username and password match what is stored in the
	 * database. Requires an active database connection.
	 *
	 * @param username
	 *            - username to authenticate
	 * @param password
	 *            - password to authenticate
	 * @return {@link Status.OK} if authentication successful
	 * @throws SQLException
	 */
	private Status authenticateUser(Connection connection, String username, String password) throws SQLException {

		Status status = Status.ERROR;

		try (PreparedStatement statement = connection.prepareStatement(AUTH_SQL);) {
			String usersalt = getSalt(connection, username);
			String passhash = getHash(password, usersalt);

			statement.setString(1, username);
			statement.setString(2, passhash);

			ResultSet results = statement.executeQuery();
			status = results.next() ? status = Status.OK : Status.INVALID_LOGIN;
		} catch (SQLException e) {
			log.debug(e.getMessage(), e);
			status = Status.SQL_EXCEPTION;
		}

		return status;
	}

	/**
	 * Checks if the provided username and password match what is stored in the
	 * database. Must retrieve the salt and hash the password to do the
	 * comparison.
	 *
	 * @param username
	 *            - username to authenticate
	 * @param password
	 *            - password to authenticate
	 * @return {@link Status.OK} if authentication successful
	 */
	public Status authenticateUser(String username, String password) {
		Status status = Status.ERROR;

		log.debug("Authenticating user " + username + ".");

		try (Connection connection = db.getConnection();) {
			status = authenticateUser(connection, username, password);
		} catch (SQLException ex) {
			status = Status.CONNECTION_FAILED;
			log.debug(status, ex);
		}

		return status;
	}

	/**
	 * Removes a user from the database if the username and password are
	 * provided correctly.
	 *
	 * @param username
	 *            - username to remove
	 * @param password
	 *            - password of user
	 * @return {@link Status.OK} if removal successful
	 */
	private Status removeUser(Connection connection, String username, String password) {
		Status status = Status.ERROR;

		try (PreparedStatement del_user = connection.prepareStatement(DELETE_SQL);
				PreparedStatement del_user_hist = connection.prepareStatement(DELETE_HIST_SQL);) {
			del_user.setString(1, username);

			int count1 = del_user.executeUpdate();
			int count2 = del_user_hist.executeUpdate();
			status = (count1 == 1 && count2 == 1) ? Status.OK : Status.INVALID_USER;
		} catch (SQLException ex) {
			status = Status.SQL_EXCEPTION;
			log.debug(status, ex);
		}

		return status;
	}

	/**
	 * Removes a user from the database if the username and password are
	 * provided correctly.
	 *
	 * @param username
	 *            - username to remove
	 * @param password
	 *            - password of user
	 * @return {@link Status.OK} if removal successful
	 */
	public Status removeUser(String username, String password) {
		Status status = Status.ERROR;

		log.debug("Removing user " + username + ".");

		try (Connection connection = db.getConnection();) {
			status = authenticateUser(connection, username, password);

			if (status == Status.OK) {
				status = removeUser(connection, username, password);
			}
		} catch (Exception ex) {
			status = Status.CONNECTION_FAILED;
			log.debug(status, ex);
		}

		return status;
	}

	private Status removeHist(Connection connection, String username) throws SQLException {
		Status status = Status.ERROR;

		String usersalt = getSalt(connection, username);

		try (PreparedStatement del_user_hist = connection.prepareStatement(String.format(DELETE_HIST_SQL, usersalt));) {

			int count = del_user_hist.executeUpdate();
			status = (count == 1) ? Status.OK : Status.INVALID_USER;
		} catch (SQLException ex) {
			status = Status.SQL_EXCEPTION;
			log.debug(status, ex);
		}

		return status;
	}

	public Status removeHist(String username) {
		// TODO use TRUNCATE (table) instead
		Status status = Status.ERROR;

		log.debug("Removing user history for " + username + ".");

		try (Connection connection = db.getConnection();) {
			status = removeHist(connection, username);
		} catch (Exception ex) {
			status = Status.CONNECTION_FAILED;
			log.debug(status, ex);
		}

		// try (Connection connection = db.getConnection();) {
		// status = authenticateUser(connection, username, password);
		//
		// if (status == Status.OK) {
		// status = removeHist(connection, username);
		// }
		// } catch (Exception ex) {
		// status = Status.CONNECTION_FAILED;
		// log.debug(status, ex);
		// }

		return status;
	}
}
