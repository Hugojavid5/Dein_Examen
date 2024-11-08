package BBDD;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase para gestionar la conexión a la base de datos.
 * Proporciona métodos para obtener una conexión y cerrarla.
 */
public class ConexionBBDD {
    private static Connection connection;

    /**
     * Constructor de la clase. Establece una conexión a la base de datos si no existe o si está cerrada.
     *
     * @throws SQLException Si ocurre un error al establecer la conexión.
     */
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

    /**
     * Obtiene la conexión a la base de datos. Si no existe o está cerrada, se establece una nueva conexión.
     *
     * @return La conexión a la base de datos.
     * @throws SQLException Si ocurre un error al obtener la conexión.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            new ConexionBBDD();
        }
        return connection;
    }

    /**
     * Cierra la conexión a la base de datos si está abierta.
     *
     * @throws SQLException Si ocurre un error al cerrar la conexión.
     */
    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    /**
     * Carga la configuración de la base de datos desde el archivo "configuration.properties".
     *
     * @return Las propiedades de configuración leídas del archivo.
     */
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
