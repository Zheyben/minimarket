package zheyben.minimarketaj.minimarket.modelo;

import java.util.Locale;

// Modelo: estados que puede tener una venta (columna estado de la tabla Venta)
public enum EstadoVenta {

    REGISTRADA,
    ANULADA;

    // Convierte el texto guardado en la base de datos en un estado
    public static EstadoVenta desdeTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            return REGISTRADA;
        }
        return EstadoVenta.valueOf(texto.trim().toUpperCase(Locale.ROOT));
    }
}
