package zheyben.minimarketaj.minimarket.modelo;

import java.math.BigDecimal;

// Modelo: representa un producto del catálogo (tabla Producto)
public class Producto {

    private int idProducto;
    private String nombre;
    private BigDecimal precio;
    private int cantidadDisponible;

    public Producto() {
    }

    public Producto(int idProducto, String nombre, BigDecimal precio, int cantidadDisponible) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidadDisponible = cantidadDisponible;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public int getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(int cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }
}
