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


public class ProductosAppController {
    @FXML
    private MenuBar Meu_item_acercaDe;
    @FXML
    private MenuItem menuItem_acercaDe;
    @FXML
    private TableView<ProductoModel> productosTable;
    @FXML
    private Label lbl_codProd;
    @FXML
    private Label lbl_nombre,lbl_precio,lbl_imagen;

    @FXML
    private TableView tabla;
    @FXML
    private TextField txt_codProd, txt_nombre, txt_precio;
    @FXML
    private Button btt_crear, btt_actualizar, btt_limpiar,btt_secIma;
    @FXML
    private ImageView imagenView;

    private ObservableList<ProductoModel> productosList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        cargarProductos();
        btt_actualizar.setDisable(true);
        productosTable.setItems(productosList);
    }

    private void cargarProductos() {
        productosList.clear();
        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM productos")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ProductoModel producto = new ProductoModel(
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getString("imagen")
                );
                productosList.add(producto);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar productos", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void crearProducto() {
        String codigo = txt_codProd.getText();
        String nombre = txt_nombre.getText();
        double precio = Double.parseDouble(txt_precio.getText());

        if (!validarCampos(codigo, nombre, precio)) return;

        try (Connection conn = ConexionBBDD.getConnection();
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
        txt_codProd.clear();
        txt_nombre.clear();
        txt_precio.clear();
        imagenView.setImage(null);
    }
}
