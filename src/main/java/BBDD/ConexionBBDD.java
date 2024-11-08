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
 * Clase que gestiona la conexión a la base de datos.
 * Utiliza un archivo de configuración para obtener los parámetros de conexión.
 */
public class ConexionBBDD {
    private static Connection connection;

    /**
     * Constructor de la clase, que establece la conexión con la base de datos si no está ya establecida.
     * @throws SQLException Si ocurre un error al intentar conectar con la base de datos.
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
     * Obtiene la conexión a la base de datos. Si la conexión no está establecida o está cerrada,
     * se crea una nueva.
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
     * @throws SQLException Si ocurre un error al intentar cerrar la conexión.
     */
    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    /**
     * Lee el archivo de configuración y obtiene los parámetros necesarios para la conexión
     * a la base de datos.
     * @return Un objeto Properties con los parámetros de configuración.
     * @throws RuntimeException Si ocurre un error al leer el archivo de configuración.
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
