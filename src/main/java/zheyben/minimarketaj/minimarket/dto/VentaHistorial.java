package zheyben.minimarketaj.minimarket.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// Venta registrada con su fecha, monto y productos vendidos (CUS06)
public record VentaHistorial(int idVenta,
                             LocalDateTime fechaHora,
                             BigDecimal total,
                             List<DetalleHistorial> productos) {
}
