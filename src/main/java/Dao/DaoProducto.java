package Dao;

import BBDD.ConexionBBDD;
import Model.ProductoModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;

/**
 * Clase que gestiona las operaciones de acceso a la base de datos relacionadas con los productos.
 * Incluye métodos para obtener, insertar, actualizar y eliminar productos en la base de datos.
 */
public class DaoProducto {

    /**
     * Obtiene un producto de la base de datos según su código.
     *
     * @param codigo El código del producto a buscar.
     * @return El producto encontrado o null si no se encuentra.
     */
    public static ProductoModel getProducto(String codigo) {
        ConexionBBDD connection;
        ProductoModel producto = null;
        try {
            connection = new ConexionBBDD();
            String consulta = "SELECT codigo,nombre,precio,disponible,imagen FROM productos WHERE codigo = ?";
            PreparedStatement pstmt = connection.getConnection().prepareStatement(consulta);
            pstmt.setString(1, codigo);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String codigo_db = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                float precio = rs.getFloat("precio");
                boolean disponible = rs.getBoolean("disponible");
                Blob imagen = rs.getBlob("imagen");
                producto = new ProductoModel(codigo_db, nombre, precio, disponible, imagen);
            }
            rs.close();
            connection.closeConnection();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return producto;
    }

    /**
     * Convierte un archivo en un Blob para ser almacenado en la base de datos.
     *
     * @param file El archivo que se desea convertir a Blob.
     * @return El Blob creado a partir del archivo.
     * @throws SQLException Si ocurre un error al crear el Blob.
     * @throws IOException  Si ocurre un error al leer el archivo.
     */
    public static Blob convertFileToBlob(File file) throws SQLException, IOException {
        ConexionBBDD connection = new ConexionBBDD();
        try (Connection conn = connection.getConnection();
             FileInputStream inputStream = new FileInputStream(file)) {

            Blob blob = conn.createBlob();
            byte[] buffer = new byte[1024];
            int bytesRead;
            try (var outputStream = blob.setBinaryStream(1)) {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            return blob;
        }
    }

    /**
     * Carga todos los productos desde la base de datos y los devuelve en una lista observable.
     *
     * @return Una lista observable con los productos.
     */
    public static ObservableList<ProductoModel> cargarListado() {
        ConexionBBDD connection;
        ObservableList<ProductoModel> productos = FXCollections.observableArrayList();
        try {
            connection = new ConexionBBDD();
            String consulta = "SELECT codigo,nombre,precio,disponible,imagen FROM productos";
            PreparedStatement pstmt = connection.getConnection().prepareStatement(consulta);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                float precio = rs.getFloat("precio");
                boolean disponible = rs.getBoolean("disponible");
                Blob imagen = rs.getBlob("imagen");
                ProductoModel producto = new ProductoModel(codigo, nombre, precio, disponible, imagen);
                productos.add(producto);
            }
            rs.close();
            connection.closeConnection();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return productos;
    }

    /**
     * Modifica un producto en la base de datos.
     *
     * @param producto El producto con los nuevos valores a actualizar.
     * @return True si la operación fue exitosa, false si no se actualizó ningún producto.
     */
    public static boolean modificar(ProductoModel producto) {
        ConexionBBDD connection;
        PreparedStatement pstmt;
        try {
            connection = new ConexionBBDD();
            String consulta = "UPDATE productos SET nombre = ?,precio = ?,disponible = ?,imagen = ? WHERE codigo = ?";
            pstmt = connection.getConnection().prepareStatement(consulta);
            pstmt.setString(1, producto.getNombre());
            pstmt.setFloat(2, producto.getPrecio());
            pstmt.setBoolean(3, producto.isDisponible());
            pstmt.setBlob(4, producto.getImagen());
            pstmt.setString(5, producto.getCodigo());
            int filasAfectadas = pstmt.executeUpdate();
            pstmt.close();
            connection.closeConnection();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * Inserta un nuevo producto en la base de datos.
     *
     * @param producto El producto a insertar.
     * @return True si el producto fue insertado exitosamente, false en caso contrario.
     */
    public static boolean insertar(ProductoModel producto) {
        ConexionBBDD connection;
        PreparedStatement pstmt;
        try {
            connection = new ConexionBBDD();
            String consulta = "INSERT INTO productos (codigo,nombre,precio,disponible,imagen) VALUES (?,?,?,?,?) ";
            pstmt = connection.getConnection().prepareStatement(consulta);
            pstmt.setString(1, producto.getCodigo());
            pstmt.setString(2, producto.getNombre());
            pstmt.setFloat(3, producto.getPrecio());
            pstmt.setBoolean(4, producto.isDisponible());
            pstmt.setBlob(5, producto.getImagen());
            int filasAfectadas = pstmt.executeUpdate();
            pstmt.close();
            connection.closeConnection();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un producto de la base de datos según su código.
     *
     * @param producto El producto a eliminar.
     * @return True si el producto fue eliminado exitosamente, false en caso contrario.
     */
    public static boolean eliminar(ProductoModel producto) {
        ConexionBBDD connection;
        PreparedStatement pstmt;
        try {
            connection = new ConexionBBDD();
            String consulta = "DELETE FROM productos WHERE codigo = ?";
            pstmt = connection.getConnection().prepareStatement(consulta);
            pstmt.setString(1, producto.getCodigo());
            int filasAfectadas = pstmt.executeUpdate();
            pstmt.close();
            connection.closeConnection();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
}
