package org.hugo.dein_examen.dein_examen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Aplicación principal de la ventana de productos utilizando JavaFX.
 * Configura la escena inicial y muestra la ventana principal de la aplicación.
 */
public class ProductosApp extends Application {

    /**
     * Método que se ejecuta al iniciar la aplicación. Configura la escena principal y la ventana.
     *
     * @param stage El escenario principal de la aplicación.
     * @throws Exception Si ocurre un error al cargar el archivo FXML o al configurar la ventana.
     */
    @Override
    public void start(Stage stage) throws Exception {
        // Cargar el archivo FXML para la escena
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/inicio.fxml"));
        Scene scene = new Scene(loader.load());

        // Establecer la escena en el escenario principal
        stage.setScene(scene);

        // Configurar el título y las propiedades de la ventana
        stage.setTitle("Productos");
        stage.setMinWidth(700);
        stage.setMinHeight(600);
        stage.setResizable(false);

        // Establecer el ícono de la ventana
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/Imagenes/carrito.png")));

        // Mostrar la ventana
        stage.show();
    }

    /**
     * Método principal que lanza la aplicación JavaFX.
     *
     * @param args Argumentos de línea de comandos (no utilizados en este caso).
     */
    public static void main(String[] args) {
        launch(args);
    }
}
