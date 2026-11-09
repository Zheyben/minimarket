package zheyben.minimarketaj.minimarket.dto;

import java.math.BigDecimal;

// Detalle de una venta en proceso: producto, precio vigente, cantidad y subtotal calculado
public record DetalleCalculado(int idProducto,
                               String nombreProducto,
                               BigDecimal precioUnitario,
                               int cantidad,
                               BigDecimal subtotal) {
}
