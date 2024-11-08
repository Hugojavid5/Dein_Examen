package Dao;
import BBDD.ConexionBBDD;
import Model.ProductoModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;


public class DaoProducto {

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
                producto = new ProductoModel(codigo_db,nombre,precio,disponible,imagen);
            }
            rs.close();
            connection.closeConnection();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return producto;
    }

    public static Blob convertFileToBlob(File file) throws SQLException, IOException {
        ConexionBBDD connection = new ConexionBBDD();
        // Open a connection to the database
        try (Connection conn = connection.getConnection();
             FileInputStream inputStream = new FileInputStream(file)) {

            // Create Blob
            Blob blob = conn.createBlob();
            // Write the file's bytes to the Blob
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

    public static ObservableList<ProductoModel> cargarListado() {
        ConexionBBDD connection;
        ObservableList<ProductoModel> productos = FXCollections.observableArrayList();
        try{
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
                ProductoModel producto = new ProductoModel(codigo,nombre,precio,disponible,imagen);
                productos.add(producto);
            }
            rs.close();
            connection.closeConnection();
        }catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return productos;
    }

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
