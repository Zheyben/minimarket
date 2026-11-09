package zheyben.minimarketaj.minimarket.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Modelo: representa una venta registrada (tabla Venta).
// Si la venta se anula (CUS08), se guardan la fecha y el motivo de la anulación.
public class Venta {

    private int idVenta;
    private LocalDateTime fechaHora;
    private BigDecimal total;
    private EstadoVenta estado = EstadoVenta.REGISTRADA;
    private LocalDateTime fechaAnulacion;
    private String motivoAnulacion;

    public Venta() {
    }

    public Venta(int idVenta, LocalDateTime fechaHora, BigDecimal total, EstadoVenta estado,
                 LocalDateTime fechaAnulacion, String motivoAnulacion) {
        this.idVenta = idVenta;
        this.fechaHora = fechaHora;
        this.total = total;
        this.estado = estado;
        this.fechaAnulacion = fechaAnulacion;
        this.motivoAnulacion = motivoAnulacion;
    }

    public boolean estaAnulada() {
        return estado == EstadoVenta.ANULADA;
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

    public EstadoVenta getEstado() {
        return estado;
    }

    public void setEstado(EstadoVenta estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaAnulacion() {
        return fechaAnulacion;
    }

    public void setFechaAnulacion(LocalDateTime fechaAnulacion) {
        this.fechaAnulacion = fechaAnulacion;
    }

    public String getMotivoAnulacion() {
        return motivoAnulacion;
    }

    public void setMotivoAnulacion(String motivoAnulacion) {
        this.motivoAnulacion = motivoAnulacion;
    }
}
