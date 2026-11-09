package zheyben.minimarketaj.minimarket.dto;

import java.math.BigDecimal;
import java.util.List;

// Resultado del cálculo de una venta: subtotales de cada producto y monto total
public record ResumenVenta(List<DetalleCalculado> detalles, BigDecimal total) {
}
