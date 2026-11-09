package zheyben.minimarketaj.minimarket.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import zheyben.minimarketaj.minimarket.dto.ResumenVenta;
import zheyben.minimarketaj.minimarket.dto.VentaFormulario;
import zheyben.minimarketaj.minimarket.excepcion.NegocioException;
import zheyben.minimarketaj.minimarket.servicio.ProductoServicio;
import zheyben.minimarketaj.minimarket.servicio.VentaServicio;

// Controlador: CUS04 Registrar venta (incluye CUS05 Descontar inventario) y CUS06 Consultar historial de ventas
@Controller
public class VentaControlador {

    private static final String VISTA_REGISTRAR_VENTA = "registrar-venta";
    private static final String VISTA_CONFIRMAR_VENTA = "confirmar-venta";
    private static final String VISTA_HISTORIAL = "historial-ventas";
    private static final String REDIRECCION_NUEVA_VENTA = "redirect:/ventas/nueva";

    private final VentaServicio ventaServicio;
    private final ProductoServicio productoServicio;

    public VentaControlador(VentaServicio ventaServicio, ProductoServicio productoServicio) {
        this.ventaServicio = ventaServicio;
        this.productoServicio = productoServicio;
    }

    // CUS04, paso 1: muestra los productos para seleccionarlos con su cantidad
    @GetMapping("/ventas/nueva")
    public String mostrarRegistroVenta(Model modelo) {
        return mostrarFormularioVenta(modelo, new VentaFormulario());
    }

    // CUS04, pasos 2 a 4: comprueba las cantidades y muestra subtotales y total para verificar el pago
    @PostMapping("/ventas/calcular")
    public String calcularVenta(@ModelAttribute("venta") VentaFormulario venta, Model modelo) {
        try {
            ResumenVenta resumen = ventaServicio.calcularVenta(venta.getLineas());
            modelo.addAttribute("resumen", resumen);
            return VISTA_CONFIRMAR_VENTA;
        } catch (NegocioException error) {
            return mostrarFormularioConError(modelo, venta, error);
        }
    }

    // CUS04, pasos 6 a 9: el actor confirmó el pago; se descuenta el inventario y se guarda la venta
    @PostMapping("/ventas/registrar")
    public String registrarVenta(@ModelAttribute("venta") VentaFormulario venta, Model modelo,
                                 RedirectAttributes atributos) {
        try {
            ventaServicio.registrarVenta(venta.getLineas());
            atributos.addFlashAttribute("mensaje", "La venta se registró correctamente.");
            return REDIRECCION_NUEVA_VENTA;
        } catch (NegocioException error) {
            return mostrarFormularioConError(modelo, venta, error);
        }
    }

    // CUS04, flujo alternativo "Venta no confirmada": no se guarda la venta ni se descuentan unidades
    @PostMapping("/ventas/cancelar")
    public String cancelarVenta(RedirectAttributes atributos) {
        atributos.addFlashAttribute("mensaje", "La venta no se registró.");
        return REDIRECCION_NUEVA_VENTA;
    }

    // CUS06: exclusivo del propietario (se controla en SeguridadConfig)
    @GetMapping("/ventas/historial")
    public String mostrarHistorial(Model modelo) {
        modelo.addAttribute("ventas", ventaServicio.listarHistorial());
        return VISTA_HISTORIAL;
    }

    private String mostrarFormularioVenta(Model modelo, VentaFormulario venta) {
        modelo.addAttribute("venta", venta);
        modelo.addAttribute("productos", productoServicio.listarCatalogo());
        return VISTA_REGISTRAR_VENTA;
    }

    // Flujos alternativos (cantidad insuficiente o datos no válidos): se conserva lo que el actor seleccionó
    private String mostrarFormularioConError(Model modelo, VentaFormulario venta, NegocioException error) {
        modelo.addAttribute("error", error.getMessage());
        return mostrarFormularioVenta(modelo, venta);
    }
}
