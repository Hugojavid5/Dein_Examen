package Model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.StringProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.Blob;
import java.util.Objects;

public class ProductoModel {

    private StringProperty codigo;
    private StringProperty nombre;
    private FloatProperty precio;
    private BooleanProperty disponible;
    private Blob imagen;

    public ProductoModel(String codigo, String nombre, float precio, boolean disponible, Blob imagen) {
        this.codigo = new SimpleStringProperty(codigo);
        this.nombre = new SimpleStringProperty(nombre);
        this.precio = new SimpleFloatProperty(precio);
        this.disponible = new SimpleBooleanProperty(disponible);
        this.imagen = imagen;
    }

    public ProductoModel() {
        this.codigo = new SimpleStringProperty();
        this.nombre = new SimpleStringProperty();
        this.precio = new SimpleFloatProperty();
        this.disponible = new SimpleBooleanProperty();
    }

    public StringProperty getCodigoProperty() {
        return codigo;
    }

    public StringProperty getNombreProperty() {
        return nombre;
    }

    public FloatProperty getPrecioProperty() {
        return precio;
    }

    public BooleanProperty isDisponibleProperty() {
        return disponible;
    }

    public Blob getImagen() {
        return imagen;
    }

    public void setImagen(Blob imagen) {
        this.imagen = imagen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductoModel producto = (ProductoModel) o;
        return Objects.equals(codigo.get(), producto.codigo.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo.get());
    }
}
