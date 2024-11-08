package BBDD;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

/**
 * Clase de conexión a la base de datos.
 * Esta clase establece y gestiona la conexión con la base de datos utilizando los parámetros definidos en un archivo de configuración.
 */
public class ConexionBBDD {
    private final Connection connection;

    /**
     * Constructor de la clase que establece la conexión a la base de datos.
     * Utiliza los parámetros de configuración leídos desde el archivo "configuracion.properties".
     *
     * @throws SQLException Si ocurre un error al intentar conectar a la base de datos.
     */
    public ConexionBBDD() throws SQLException {
        // Los parámetros de la conexión
        Properties configuracion = getConfiguracion();
        Properties connConfig = new Properties();
        connConfig.setProperty("user", configuracion.getProperty("user"));
        connConfig.setProperty("password", configuracion.getProperty("password"));

        // La conexión en sí
        connection = DriverManager.getConnection("jdbc:mariadb://" + configuracion.getProperty("address") + ":" + configuracion.getProperty("port") + "/" + configuracion.getProperty("database") + "?createDatabaseIfNotExist=true", connConfig);

        DatabaseMetaData databaseMetaData = connection.getMetaData();
        connection.setAutoCommit(true);

    }

    /**
     * Obtiene la conexión a la base de datos.
     *
     * @return La conexión establecida a la base de datos.
     *
     */
    public Connection getConnection() {
        return connection;
    }


    /**
     * Cierra la conexión a la base de datos.
     *
     * @return La conexión cerrada.
     * @throws SQLException Si ocurre un error al intentar cerrar la conexión.
     */
    public Connection closeConnection() throws SQLException {
        connection.close();
        return connection;
    }

    // Se inicializa la configuración
    Properties configuracion = getConfiguracion();

    /**
     * Lee y devuelve la configuración de la base de datos desde el archivo "configuracion.properties".
     * Este archivo debe contener los parámetros necesarios para la conexión.
     *
     * @return Las propiedades de configuración necesarias para establecer la conexión a la base de datos.
     * @throws RuntimeException Si no se encuentra el archivo de configuración.
     */
    public static Properties getConfiguracion() {
        HashMap<String, String> map = new HashMap<String, String>();
        File f = new File("configuration.properties");
        Properties properties;
        try {
            FileInputStream configFileReader = new FileInputStream(f);
            properties = new Properties();
            try {
                properties.load(configFileReader);
                configFileReader.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("configuracion.properties not found at config file path " + f.getPath());
        }
        return properties;
    }
}
