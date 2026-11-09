package zheyben.minimarketaj.minimarket.dto;

import java.util.ArrayList;
import java.util.List;

// Datos que llegan desde el formulario de registro de venta
public class VentaFormulario {

    private List<LineaVenta> lineas = new ArrayList<>();

    public List<LineaVenta> getLineas() {
        return lineas;
    }

    public void setLineas(List<LineaVenta> lineas) {
        this.lineas = lineas;
    }
}
