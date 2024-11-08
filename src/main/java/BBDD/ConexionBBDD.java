package BBDD;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBBDD {
    private static Connection connection;

    public ConexionBBDD() throws SQLException {
        if (connection == null || connection.isClosed()) {
            Properties configuracion = getConfiguracion();
            Properties connConfig = new Properties();
            connConfig.setProperty("user", configuracion.getProperty("user"));
            connConfig.setProperty("password", configuracion.getProperty("password"));

            connection = DriverManager.getConnection(
                    "jdbc:mariadb://" + configuracion.getProperty("address") + ":" +
                            configuracion.getProperty("port") + "/" + configuracion.getProperty("database") +
                            "?createDatabaseIfNotExist=true", connConfig);

            connection.setAutoCommit(true);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            new ConexionBBDD();
        }
        return connection;
    }

    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private static Properties getConfiguracion() {
        Properties properties = new Properties();
        try {
            File f = new File("configuration.properties");
            FileInputStream configFileReader = new FileInputStream(f);
            properties.load(configFileReader);
            configFileReader.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Archivo configuration.properties no encontrado: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("Error al leer configuration.properties: " + e.getMessage());
        }
        return properties;
    }
}
