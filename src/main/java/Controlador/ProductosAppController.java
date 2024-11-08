package Controlador;

import Dao.DaoProducto;
import Model.ProductoModel;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class ProductosAppController implements Initializable {

    @FXML
    private Button btt_actualizar;

    @FXML
    private Button btt_crear;

    @FXML
    private CheckBox cb_Disponible;

    @FXML
    private TableColumn<String, ProductoModel> colCodigo;

    @FXML
    private TableColumn<Boolean, ProductoModel> colDisponible;

    @FXML
    private TableColumn<String, ProductoModel> colNombre;

    @FXML
    private TableColumn<Float, ProductoModel> colPrecio;

    @FXML
    private ImageView imagen;

    @FXML
    private TableView<ProductoModel> tabla;

    @FXML
    private TextField txt_Codigo;

    @FXML
    private TextField txt_Nombre;

    @FXML
    private TextField txt_Precio;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configuración de las columnas de la tabla
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colDisponible.setCellValueFactory(cellData -> cellData.getValue().isDisponibleProperty());

        // Columna de disponible como CheckBox
        colDisponible.setCellFactory(CheckBoxTableCell.forTableColumn(colDisponible));

        // ContextMenu
        ContextMenu contextMenu = new ContextMenu();
        MenuItem verImagenItem = new MenuItem("Ver imagen");
        MenuItem borrarItem = new MenuItem("Eliminar");
        contextMenu.getItems().addAll(verImagenItem, borrarItem);
        verImagenItem.setOnAction(this::verImagen);
        borrarItem.setOnAction(this::eliminar);
        tabla.setRowFactory(tv -> {
            TableRow<ProductoModel> row = new TableRow<>();
            row.setOnContextMenuRequested(event -> {
                if (!row.isEmpty()) {
                    tabla.getSelectionModel().select(row.getItem());
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });
            return row;
        });

        // Cargar productos
        cargarTabla();
    }


    public void cargarTabla() {
        tabla.setItems(DaoProducto.cargarListado());
    }
    @FXML
    void actualizar(ActionEvent event) {

    }

    @FXML
    void crear(ActionEvent event) {

    }

    @FXML
    void limpiar(ActionEvent event) {

    }

    @FXML
    void seleccionarImagen(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccione una imagen");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files","*.jpg", "*.jpeg","*.png"));
        fileChooser.setInitialDirectory(new File("."));
        File file = fileChooser.showOpenDialog(null);
        try {
            double kbs = (double) file.length() / 1024;
            if (kbs > 64) {
                alerta("");
            } else {
                InputStream image = new FileInputStream(file);
                Blob blob = DaoProducto.convertFileToBlob(file);
                imagen.setImage(new Image(image));
                imagen.setDisable(false);
            }
        } catch (IOException | NullPointerException e) {
            //e.printStackTrace();
            System.out.println("Imagen no seleccionada");
        } catch (SQLException e) {
            e.printStackTrace();
            alerta("No se ha podido ");
        }
    }

    /**
     * Función que muestra un mensaje de alerta al usuario
     *
     * @param texto contenido de la alerta
     */
    public void alerta(String texto) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setHeaderText(null);
        alerta.setTitle("Error");
        alerta.setContentText(texto);
        alerta.showAndWait();
    }

    /**
     * Función que muestra un mensaje de confirmación al usuario
     *
     * @param texto contenido del mensaje
     */
    public void confirmacion(String texto) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setHeaderText(null);
        alerta.setTitle("Info");
        alerta.setContentText(texto);
        alerta.showAndWait();
    }
}