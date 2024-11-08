package Controlador;

import BBDD.ConexionBBDD;
import Model.ProductoModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ProductoController {
    @FXML
    private TableView<Producto> productosTable;
    @FXML
    private TextField codigoField, nombreField, precioField;
    @FXML
    private Button crearButton, actualizarButton, limpiarButton;
    @FXML
    private ImageView imagenView;

    private ObservableList<Producto> productosList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        cargarProductos();
        actualizarButton.setDisable(true);
        productosTable.setItems(productosList);
    }

    private void cargarProductos() {
        productosList.clear();
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM productos")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Producto producto = new Producto(rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getString("imagen"));
                productosList.add(producto);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar productos", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void crearProducto() {
        String codigo = codigoField.getText();
        String nombre = nombreField.getText();
        double precio = Double.parseDouble(precioField.getText());

        if (!validarCampos(codigo, nombre, precio)) return;

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO productos (codigo, nombre, precio) VALUES (?, ?, ?)")) {
            stmt.setString(1, codigo);
            stmt.setString(2, nombre);
            stmt.setDouble(3, precio);
            stmt.executeUpdate();
            mostrarAlerta("Éxito", "Producto creado con éxito", Alert.AlertType.INFORMATION);
            cargarProductos();
            limpiarCampos();
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al guardar producto", Alert.AlertType.ERROR);
        }
    }

    private boolean validarCampos(String codigo, String nombre, double precio) {
        if (codigo.length() != 5 || nombre.isEmpty() || precio <= 0) {
            mostrarAlerta("Validación", "Campos inválidos", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    @FXML
    public void seleccionarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.jpg", "*.png"));
        var archivo = fileChooser.showOpenDialog(null);
        if (archivo != null) {
            imagenView.setImage(new Image(archivo.toURI().toString()));
        }
    }

    @FXML
    public void limpiarCampos() {
        codigoField.clear();
        nombreField.clear();
        precioField.clear();
        imagenView.setImage(null);
    }
}
