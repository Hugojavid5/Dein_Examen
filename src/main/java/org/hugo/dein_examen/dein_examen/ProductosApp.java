package org.hugo.dein_examen.dein_examen;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ProductosApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/inicio.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("Productos");
        stage.setMinWidth(700);
        stage.setMinHeight(600);
        stage.setResizable(false);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/Imagenes/carrito.png")));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
