package Model;

import java.sql.Blob;
import java.util.Objects;

/**
 * Modelo que representa un producto en el sistema.
 * Contiene información sobre el código, nombre, precio, disponibilidad e imagen del producto.
 */
public class ProductoModel {
    private String codigo;
    private String nombre;
    private float precio;
    private boolean disponible;
    private Blob imagen;

    /**
     * Constructor para crear un producto con los parámetros proporcionados.
     *
     * @param codigo    El código único del producto.
     * @param nombre    El nombre del producto.
     * @param precio    El precio del producto.
     * @param disponible Indica si el producto está disponible.
     * @param imagen    La imagen del producto en formato Blob.
     */
    public ProductoModel(String codigo, String nombre, float precio, boolean disponible, Blob imagen) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.disponible = disponible;
        this.imagen = imagen;
    }

    /**
     * Constructor por defecto.
     */
    public ProductoModel() {}

    /**
     * Obtiene el código del producto.
     *
     * @return El código del producto.
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Establece el código del producto.
     *
     * @param codigo El código del producto.
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * Obtiene el nombre del producto.
     *
     * @return El nombre del producto.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del producto.
     *
     * @param nombre El nombre del producto.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el precio del producto.
     *
     * @return El precio del producto.
     */
    public float getPrecio() {
        return precio;
    }

    /**
     * Establece el precio del producto.
     *
     * @param precio El precio del producto.
     */
    public void setPrecio(float precio) {
        this.precio = precio;
    }

    /**
     * Verifica si el producto está disponible.
     *
     * @return True si el producto está disponible, false en caso contrario.
     */
    public boolean isDisponible() {
        return disponible;
    }

    /**
     * Establece la disponibilidad del producto.
     *
     * @param disponible Indica si el producto está disponible.
     */
    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    /**
     * Obtiene la imagen del producto.
     *
     * @return La imagen del producto en formato Blob.
     */
    public Blob getImagen() {
        return imagen;
    }

    /**
     * Establece la imagen del producto.
     *
     * @param imagen La imagen del producto en formato Blob.
     */
    public void setImagen(Blob imagen) {
        this.imagen = imagen;
    }

    /**
     * Compara dos objetos ProductoModel para verificar si son iguales.
     * Dos productos se consideran iguales si tienen el mismo código.
     *
     * @param o El objeto con el que se va a comparar.
     * @return True si los productos son iguales, false en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductoModel producto = (ProductoModel) o;
        return Objects.equals(codigo, producto.codigo);
    }

    /**
     * Genera el valor hash para el objeto ProductoModel basado en su código.
     *
     * @return El valor hash del objeto.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(codigo);
    }
}
