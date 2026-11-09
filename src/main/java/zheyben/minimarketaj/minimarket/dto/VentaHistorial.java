package zheyben.minimarketaj.minimarket.dto;

import zheyben.minimarketaj.minimarket.modelo.EstadoVenta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// Venta registrada con su fecha, monto y productos vendidos (CUS06).
// Si fue anulada (CUS08), incluye la fecha y el motivo; anulable indica si todavía se puede anular.
public record VentaHistorial(int idVenta,
                             LocalDateTime fechaHora,
                             BigDecimal total,
                             List<DetalleHistorial> productos,
                             EstadoVenta estado,
                             LocalDateTime fechaAnulacion,
                             String motivoAnulacion,
                             boolean anulable) {

    public boolean estaAnulada() {
        return estado == EstadoVenta.ANULADA;
    }
}
