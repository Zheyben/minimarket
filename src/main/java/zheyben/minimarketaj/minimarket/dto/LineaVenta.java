package zheyben.minimarketaj.minimarket.dto;

// Producto y cantidad que el actor selecciona al registrar una venta
public class LineaVenta {

    private Integer idProducto;
    private Integer cantidad;

    public LineaVenta() {
    }

    public LineaVenta(Integer idProducto, Integer cantidad) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
