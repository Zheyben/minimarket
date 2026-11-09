package zheyben.minimarketaj.minimarket.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Modelo: representa una venta registrada (tabla Venta)
public class Venta {

    private int idVenta;
    private LocalDateTime fechaHora;
    private BigDecimal total;

    public Venta() {
    }

    public Venta(int idVenta, LocalDateTime fechaHora, BigDecimal total) {
        this.idVenta = idVenta;
        this.fechaHora = fechaHora;
        this.total = total;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
