package zheyben.minimarketaj.minimarket.dto;

import java.math.BigDecimal;

// Producto vendido dentro de una venta del historial
public record DetalleHistorial(int idVenta,
                               String nombreProducto,
                               int cantidad,
                               BigDecimal subtotal) {
}
