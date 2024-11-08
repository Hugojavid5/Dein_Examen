package Dao;

import BBDD.ConexionBBDD;
import Model.ProductoModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DaoProducto {

    public List<ProductoModel> obtenerTodosLosProductos() {
        List<ProductoModel> productos = new ArrayList<>();
        String query = "SELECT * FROM productos";

        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ProductoModel producto = new ProductoModel(
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getString("imagen")
                );
                productos.add(producto);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener productos: " + e.getMessage());
        }
        return productos;
    }

    public boolean agregarProducto(ProductoModel producto) {
        String query = "INSERT INTO productos (codigo, nombre, precio, imagen) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, producto.getCodigo());
            stmt.setString(2, producto.getNombre());
            stmt.setDouble(3, producto.getPrecio());
            stmt.setString(4, producto.getImagen());

            int filasInsertadas = stmt.executeUpdate();
            return filasInsertadas > 0;
        } catch (SQLException e) {
            System.out.println("Error al agregar producto: " + e.getMessage());
            return false;
        }
    }
    public boolean actualizarProducto(ProductoModel producto) {
        String query = "UPDATE productos SET nombre = ?, precio = ?, imagen = ? WHERE codigo = ?";

        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, producto.getNombre());
            stmt.setDouble(2, producto.getPrecio());
            stmt.setString(3, producto.getImagen());
            stmt.setString(4, producto.getCodigo());

            int filasActualizadas = stmt.executeUpdate();
            return filasActualizadas > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarProducto(String codigo) {
        String query = "DELETE FROM productos WHERE codigo = ?";

        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, codigo);
            int filasEliminadas = stmt.executeUpdate();
            return filasEliminadas > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }
}
