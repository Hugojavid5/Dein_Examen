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
 * Clase que gestiona las operaciones relacionadas con los productos en la base de datos.
 * Incluye métodos para obtener, insertar, actualizar y eliminar productos.
 */
public class DaoProducto {

    /**
     * Obtiene un producto de la base de datos según su código.
     * @param codigo El código del producto a obtener.
     * @return El producto encontrado o null si no se encuentra.
     */
    public static ProductoModel getProducto(String codigo) {
        ConexionBBDD connection;
        ProductoModel producto = null;
        try {
            connection = new ConexionBBDD();
            String consulta = "SELECT codigo,nombre,precio,disponible,imagen FROM Producto WHERE codigo = ?";
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
     * Convierte un archivo en un objeto Blob para almacenar en la base de datos.
     * @param file El archivo a convertir.
     * @return El objeto Blob correspondiente al archivo.
     * @throws SQLException Si ocurre un error al crear el Blob.
     * @throws IOException Si ocurre un error al leer el archivo.
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
     * Carga todos los productos de la base de datos en una lista observable.
     * @return Una lista observable con todos los productos.
     */
    public static ObservableList<ProductoModel> cargarListado() {
        ConexionBBDD connection;
        ObservableList<ProductoModel> productos = FXCollections.observableArrayList();
        try {
            connection = new ConexionBBDD();
            String consulta = "SELECT codigo,nombre,precio,disponible,imagen FROM Producto";
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
     * Modifica los detalles de un producto en la base de datos.
     * @param producto El producto con los nuevos datos.
     * @return true si el producto se actualizó correctamente, false en caso contrario.
     */
    public static boolean modificar(ProductoModel producto) {
        ConexionBBDD connection;
        PreparedStatement pstmt;
        try {
            connection = new ConexionBBDD();
            String consulta = "UPDATE Producto SET nombre = ?, precio = ?, disponible = ?, imagen = ? WHERE codigo = ?";
            pstmt = connection.getConnection().prepareStatement(consulta);
            pstmt.setString(1, producto.getNombre());
            pstmt.setFloat(2, producto.getPrecio());
            pstmt.setBoolean(3, producto.isDisponible());
            pstmt.setBlob(4, producto.getImagen());
            pstmt.setString(5, producto.getCodigo());
            int filasAfectadas = pstmt.executeUpdate();
            System.out.println("Actualizado producto");
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
     * @param producto El producto a insertar.
     * @return El ID generado para el producto o -1 si no se pudo insertar.
     */
    public static int insertar(ProductoModel producto) {
        ConexionBBDD connection;
        PreparedStatement pstmt;
        try {
            connection = new ConexionBBDD();
            String consulta = "INSERT INTO Producto (codigo, nombre, precio, disponible, imagen) VALUES (?, ?, ?, ?, ?)";
            pstmt = connection.getConnection().prepareStatement(consulta, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, producto.getCodigo());
            pstmt.setString(2, producto.getNombre());
            pstmt.setFloat(3, producto.getPrecio());
            pstmt.setBoolean(4, producto.isDisponible());
            pstmt.setBlob(5, producto.getImagen());
            int filasAfectadas = pstmt.executeUpdate();
            System.out.println("Nueva entrada en producto");
            if (filasAfectadas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    pstmt.close();
                    connection.closeConnection();
                    return id;
                }
            }
            pstmt.close();
            connection.closeConnection();
            return -1;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return -1;
        }
    }

    /**
     * Elimina un producto de la base de datos según su código.
     * @param producto El producto a eliminar.
     * @return true si el producto se eliminó correctamente, false en caso contrario.
     */
    public static boolean eliminar(ProductoModel producto) {
        ConexionBBDD connection;
        PreparedStatement pstmt;
        try {
            connection = new ConexionBBDD();
            String consulta = "DELETE FROM Producto WHERE codigo = ?";
            pstmt = connection.getConnection().prepareStatement(consulta);
            pstmt.setString(1, producto.getCodigo());
            int filasAfectadas = pstmt.executeUpdate();
            pstmt.close();
            connection.closeConnection();
            System.out.println("Eliminado con éxito");
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
}
