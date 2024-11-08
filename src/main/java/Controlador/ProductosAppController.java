package Controlador;

import Model.ProductoModel;
import Dao.DaoProducto;

import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.hugo.dein_examen.dein_examen.ProductosApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class ProductosAppController implements Initializable {
    private Blob imagenProducto;

    @FXML
    private Button btt_Actualizar;

    @FXML
    private Button btt_Crear;

    @FXML
    private CheckBox cbDisponible;

    @FXML
    private TableColumn<ProductoModel, String> colCodigo;

    @FXML
    private TableColumn<ProductoModel, Boolean> colDisponible;

    @FXML
    private TableColumn<ProductoModel, String> colNombre;

    @FXML
    private TableColumn<ProductoModel, Float> colPrecio;

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

    /**
     * Método de inicialización que configura la tabla, las columnas y el listener de selección.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.imagenProducto = null;
        // Configuración de las columnas de la tabla
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colDisponible.setCellValueFactory(cellData -> {
            ProductoModel p = cellData.getValue();
            boolean v = p.isDisponible();
            return new ReadOnlyBooleanWrapper(v);
        });
        colDisponible.setCellFactory(CheckBoxTableCell.<ProductoModel>forTableColumn(colDisponible));

        // Configuración del ContextMenu en la tabla
        ContextMenu contextMenu = new ContextMenu();
        MenuItem verImagenItem = new MenuItem("Ver imagen");
        MenuItem borrarItem = new MenuItem("Eliminar");
        contextMenu.getItems().addAll(verImagenItem,borrarItem);
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

        // Añadir listener para cuando se selecciona un item de la tabla
        tabla.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ProductoModel>() {
            @Override
            public void changed(ObservableValue<? extends ProductoModel> observableValue, ProductoModel oldValue, ProductoModel newValue) {
                if (newValue != null) {
                    // Actualizar los campos con los datos del producto seleccionado
                    txt_Codigo.setText(newValue.getCodigo());
                    txt_Codigo.setDisable(true);
                    txt_Nombre.setText(newValue.getNombre());
                    txt_Precio.setText(newValue.getPrecio() + "");
                    cbDisponible.setSelected(newValue.isDisponible());
                    imagenProducto = newValue.getImagen();
                    if (newValue.getImagen() != null) {
                        try {
                            InputStream image = newValue.getImagen().getBinaryStream();
                            imagen.setImage(new Image(image));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        imagen.setImage(null);
                    }
                    btt_Crear.setDisable(true);
                    btt_Actualizar.setDisable(false);
                }
            }
        });

        // Cargar los productos al iniciar la vista
        cargarTabla();
    }

    /**
     * Carga los productos en la tabla.
     */
    public void cargarTabla() {
        tabla.getItems().clear();
        limpiar(null);
        tabla.setItems(DaoProducto.cargarListado());
    }

    /**
     * Muestra la imagen del producto seleccionado en una ventana modal.
     */
    private void verImagen(ActionEvent actionEvent) {
        ProductoModel producto = tabla.getSelectionModel().getSelectedItem();
        if (producto == null) {
            alerta("No hay ningún producto seleccionado");
        } else {
            if (producto.getImagen() == null) {
                alerta("Ese producto no tiene imagen");
            } else {
                try {
                    Window ventana = tabla.getScene().getWindow();
                    InputStream image = producto.getImagen().getBinaryStream();
                    VBox root = new VBox();
                    ImageView imagen = new ImageView(new Image(image));
                    imagen.setFitWidth(300);
                    imagen.setFitHeight(300);
                    root.getChildren().add(imagen);
                    Scene scene = new Scene(root, 300, 300);
                    Stage stage = new Stage();
                    stage.setResizable(false);
                    stage.setScene(scene);
                    stage.setTitle("Imagen");
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initOwner(ventana);
                    stage.getIcons().add(new Image(ProductosApp.class.getResourceAsStream("/Imagenes/carrito.png")));
                    stage.showAndWait();
                } catch (SQLException e) {
                    alerta("No se ha podido cargar la imagen");
                }
            }
        }
    }

    /**
     * Elimina el producto seleccionado de la base de datos.
     */
    private void eliminar(ActionEvent actionEvent) {
        ProductoModel producto = tabla.getSelectionModel().getSelectedItem();
        if (producto == null) {
            alerta("No hay ningún producto seleccionado");
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initOwner(txt_Nombre.getScene().getWindow());
            alert.setHeaderText(null);
            alert.setTitle("Eliminar producto");
            alert.setContentText("¿Estás seguro que quieres eliminar ese producto?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                if (DaoProducto.eliminar(producto)) {
                    confirmacion("Producto eliminado correctamente");
                    cargarTabla();
                } else {
                    alerta("No se ha podido eliminar ese producto de la base de datos");
                }
            }
        }
    }

    /**
     * Actualiza un producto en la base de datos con los nuevos datos.
     */
    @FXML
    void actualizar(ActionEvent event) {
        String error = validar();
        if (!error.isEmpty()) {
            alerta(error);
        } else {
            ProductoModel producto = DaoProducto.getProducto(txt_Codigo.getText());
            producto.setNombre(txt_Nombre.getText());
            producto.setPrecio(Float.parseFloat(txt_Precio.getText()));
            producto.setDisponible(cbDisponible.isSelected());
            producto.setImagen(imagenProducto);
            if (DaoProducto.modificar(producto)) {
                cargarTabla();
            } else {
                alerta("No se ha podido actualizar ese producto en la base de datos");
            }
        }
    }

    /**
     * Crea un nuevo producto en la base de datos.
     */
    @FXML
    void crear(ActionEvent event) {
        if (txt_Codigo.getText().isEmpty()) {
            alerta("El codigo no puede estar vacío");
        } else if (txt_Codigo.getText().length() != 5) {
            alerta("El codigo debe tener 5 caracteres");
        } else {
            ProductoModel p = DaoProducto.getProducto(txt_Codigo.getText());
            if (p != null) {
                alerta("Ya existe un producto con ese codigo");
            } else {
                String error = validar();
                if (!error.isEmpty()) {
                    alerta(error);
                } else {
                    ProductoModel producto = new ProductoModel();
                    producto.setCodigo(txt_Codigo.getText());
                    producto.setNombre(txt_Nombre.getText());
                    producto.setPrecio(Float.parseFloat(txt_Precio.getText()));
                    producto.setDisponible(cbDisponible.isSelected());
                    producto.setImagen(imagenProducto);
                    if (DaoProducto.insertar(producto)) {
                        confirmacion("Producto creado correctamente");
                        cargarTabla();
                    } else {
                        alerta("No se ha podido crear ese producto en la base de datos");
                    }
                }
            }
        }
    }

    /**
     * Valida los campos de texto para crear o actualizar un producto.
     */
    public String validar() {
        String error = "";
        if (txt_Nombre.getText().isEmpty()) {
            error += "El nombre no puede estar vacio\n";
        }
        if (txt_Precio.getText().isEmpty()) {
            error += "El precio no puede estar vacio\n";
        } else {
            try {
                Float.parseFloat(txt_Precio.getText());
            } catch (NumberFormatException e) {
                error += "El precio debe ser un numero decimal\n";
            }
        }
        return error;
    }

    /**
     * Muestra información sobre la aplicación.
     */
    @FXML
    void acercaDe(ActionEvent event) {
        alertInfo("Proyecto Examen", "Examen DEIN. Hugo. V1.0");
    }

    /**
     * Limpia los campos de texto.
     */
    public void limpiar(ActionEvent actionEvent) {
        txt_Codigo.clear();
        txt_Nombre.clear();
        txt_Precio.clear();
        cbDisponible.setSelected(false);
        imagen.setImage(null);
        btt_Actualizar.setDisable(true);
        btt_Crear.setDisable(false);
        txt_Codigo.setDisable(false);
        imagenProducto = null;
    }

    /**
     * Muestra una alerta con el mensaje proporcionado.
     */
    public void alerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra una alerta de confirmación con el mensaje proporcionado.
     */
    public void confirmacion(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra una alerta de información con el mensaje proporcionado.
     */
    public void alertInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
