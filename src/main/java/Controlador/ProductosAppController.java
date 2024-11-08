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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.imagenProducto = null;
        // Columnas de la tabla
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colDisponible.setCellValueFactory(cellData -> {
            ProductoModel p = cellData.getValue();
            boolean v = p.isDisponible();
            return new ReadOnlyBooleanWrapper(v);
        });
        colDisponible.setCellFactory(CheckBoxTableCell.<ProductoModel>forTableColumn(colDisponible));
        // ContextMenu
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
        // Cargar productos
        cargarTabla();
    }

    public void cargarTabla() {
        tabla.getItems().clear();
        limpiar(null);
        tabla.setItems(DaoProducto.cargarListado());
    }

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
                    //e.printStackTrace();
                    alerta("No se ha podido cargar la imagen");
                }
            }
        }
    }
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

    @FXML
    void acercaDe(ActionEvent event) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setHeaderText(null);
        alerta.setTitle("info");
        String info = "Gestion de productos 1.0\n";
        info += "Autor:Hugo Javid";
        alerta.setContentText(info);
        Stage alertaStage = (Stage) alerta.getDialogPane().getScene().getWindow();
        alertaStage.getIcons().add(new Image(ProductosApp.class.getResourceAsStream("/Imagenes/carrito.png")));
        alerta.showAndWait();
    }

    @FXML
    void limpiar(ActionEvent event) {
        imagenProducto = null;
        txt_Codigo.setText("");
        txt_Codigo.setDisable(false);
        txt_Nombre.setText("");
        txt_Precio.setText("");
        cbDisponible.setSelected(false);
        imagen.setImage(null);
        btt_Crear.setDisable(false);
        btt_Actualizar.setDisable(true);
        tabla.getSelectionModel().clearSelection();
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
                imagenProducto = blob;
                imagen.setImage(new Image(image));
                imagen.setDisable(false);
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            //e.printStackTrace();
            alerta("No se ha podido convertir la imagen al formato Blob");
        }
    }

    public void alerta(String texto) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setHeaderText(null);
        alerta.setTitle("Error");
        alerta.setContentText(texto);
        alerta.showAndWait();
    }

    public void confirmacion(String texto) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setHeaderText(null);
        alerta.setTitle("Info");
        alerta.setContentText(texto);
        alerta.showAndWait();
    }
}