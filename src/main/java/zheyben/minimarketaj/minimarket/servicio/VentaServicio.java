package zheyben.minimarketaj.minimarket.servicio;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zheyben.minimarketaj.minimarket.dao.DetalleVentaDAO;
import zheyben.minimarketaj.minimarket.dao.VentaDAO;
import zheyben.minimarketaj.minimarket.dto.DetalleCalculado;
import zheyben.minimarketaj.minimarket.dto.DetalleHistorial;
import zheyben.minimarketaj.minimarket.dto.LineaVenta;
import zheyben.minimarketaj.minimarket.dto.ResumenVenta;
import zheyben.minimarketaj.minimarket.dto.VentaHistorial;
import zheyben.minimarketaj.minimarket.excepcion.DatosInvalidosException;
import zheyben.minimarketaj.minimarket.excepcion.StockInsuficienteException;
import zheyben.minimarketaj.minimarket.modelo.DetalleVenta;
import zheyben.minimarketaj.minimarket.modelo.Producto;
import zheyben.minimarketaj.minimarket.modelo.Venta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Capa de lógica de negocio: CUS04 Registrar venta y CUS06 Consultar historial de ventas
@Service
public class VentaServicio {

    private final ProductoServicio productoServicio;
    private final InventarioServicio inventarioServicio;
    private final VentaDAO ventaDAO;
    private final DetalleVentaDAO detalleVentaDAO;

    public VentaServicio(ProductoServicio productoServicio,
                         InventarioServicio inventarioServicio,
                         VentaDAO ventaDAO,
                         DetalleVentaDAO detalleVentaDAO) {
        this.productoServicio = productoServicio;
        this.inventarioServicio = inventarioServicio;
        this.ventaDAO = ventaDAO;
        this.detalleVentaDAO = detalleVentaDAO;
    }

    // CUS04, pasos 3 y 4: comprueba las cantidades disponibles y calcula subtotales y total
    public ResumenVenta calcularVenta(List<LineaVenta> lineas) {
        Map<Integer, Integer> cantidadesPorProducto = agruparCantidades(lineas);
        List<DetalleCalculado> detalles = cantidadesPorProducto.entrySet().stream()
                .map(entrada -> calcularDetalle(entrada.getKey(), entrada.getValue()))
                .toList();
        return new ResumenVenta(detalles, calcularTotal(detalles));
    }

    // CUS04, pasos 7 a 9: se llama cuando el actor ya confirmó el pago.
    // Descontar el inventario (CUS05) y guardar la venta ocurren juntos: si algo falla, no se guarda nada.
    @Transactional
    public int registrarVenta(List<LineaVenta> lineas) {
        ResumenVenta resumen = calcularVenta(lineas);
        inventarioServicio.descontarInventario(resumen.detalles());
        int idVenta = ventaDAO.insertar(crearVenta(resumen));
        detalleVentaDAO.insertarTodos(crearDetalles(idVenta, resumen.detalles()));
        return idVenta;
    }

    // CUS06: ventas en orden cronológico con su fecha, monto y productos vendidos
    public List<VentaHistorial> listarHistorial() {
        Map<Integer, List<DetalleHistorial>> detallesPorVenta = agruparDetallesPorVenta();
        return ventaDAO.listarTodas().stream()
                .map(venta -> crearVentaHistorial(venta, detallesPorVenta))
                .toList();
    }

    // Une las líneas repetidas de un mismo producto y valida las cantidades
    private Map<Integer, Integer> agruparCantidades(List<LineaVenta> lineas) {
        List<LineaVenta> seleccionadas = filtrarProductosSeleccionados(lineas);
        if (seleccionadas.isEmpty()) {
            throw new DatosInvalidosException("Seleccione al menos un producto.");
        }
        Map<Integer, Integer> cantidadesPorProducto = new LinkedHashMap<>();
        for (LineaVenta linea : seleccionadas) {
            validarCantidad(linea.getCantidad());
            cantidadesPorProducto.merge(linea.getIdProducto(), linea.getCantidad(), Integer::sum);
        }
        return cantidadesPorProducto;
    }

    private List<LineaVenta> filtrarProductosSeleccionados(List<LineaVenta> lineas) {
        if (lineas == null) {
            return List.of();
        }
        return lineas.stream()
                .filter(linea -> linea != null && linea.getIdProducto() != null)
                .toList();
    }

    private void validarCantidad(Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new DatosInvalidosException("La cantidad de cada producto debe ser mayor que cero.");
        }
    }

    // Usa el precio vigente guardado en la base de datos, no un precio enviado desde la pantalla
    private DetalleCalculado calcularDetalle(int idProducto, int cantidad) {
        Producto producto = productoServicio.obtenerProducto(idProducto);
        verificarDisponibilidad(producto, cantidad);
        BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(cantidad));
        return new DetalleCalculado(
                producto.getIdProducto(),
                producto.getNombre(),
                producto.getPrecio(),
                cantidad,
                subtotal
        );
    }

    private void verificarDisponibilidad(Producto producto, int cantidadSolicitada) {
        if (cantidadSolicitada > producto.getCantidadDisponible()) {
            throw new StockInsuficienteException(producto.getNombre(), producto.getCantidadDisponible());
        }
    }

    private BigDecimal calcularTotal(List<DetalleCalculado> detalles) {
        return detalles.stream()
                .map(DetalleCalculado::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Venta crearVenta(ResumenVenta resumen) {
        Venta venta = new Venta();
        venta.setFechaHora(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        venta.setTotal(resumen.total());
        return venta;
    }

    private List<DetalleVenta> crearDetalles(int idVenta, List<DetalleCalculado> detalles) {
        return detalles.stream()
                .map(detalle -> crearDetalle(idVenta, detalle))
                .toList();
    }

    private DetalleVenta crearDetalle(int idVenta, DetalleCalculado detalle) {
        DetalleVenta detalleVenta = new DetalleVenta();
        detalleVenta.setIdVenta(idVenta);
        detalleVenta.setIdProducto(detalle.idProducto());
        detalleVenta.setCantidad(detalle.cantidad());
        detalleVenta.setSubtotal(detalle.subtotal());
        return detalleVenta;
    }

    private Map<Integer, List<DetalleHistorial>> agruparDetallesPorVenta() {
        return detalleVentaDAO.listarConNombreProducto().stream()
                .collect(Collectors.groupingBy(DetalleHistorial::idVenta));
    }

    private VentaHistorial crearVentaHistorial(Venta venta, Map<Integer, List<DetalleHistorial>> detallesPorVenta) {
        return new VentaHistorial(
                venta.getIdVenta(),
                venta.getFechaHora(),
                venta.getTotal(),
                detallesPorVenta.getOrDefault(venta.getIdVenta(), List.of())
        );
    }
}
