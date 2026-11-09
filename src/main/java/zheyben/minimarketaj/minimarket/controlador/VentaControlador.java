package zheyben.minimarketaj.minimarket.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import zheyben.minimarketaj.minimarket.dto.ResumenVenta;
import zheyben.minimarketaj.minimarket.dto.VentaFormulario;
import zheyben.minimarketaj.minimarket.excepcion.DatosInvalidosException;
import zheyben.minimarketaj.minimarket.excepcion.NegocioException;
import zheyben.minimarketaj.minimarket.excepcion.VentaNoAnulableException;
import zheyben.minimarketaj.minimarket.servicio.ProductoServicio;
import zheyben.minimarketaj.minimarket.servicio.VentaServicio;

// Controlador: CUS04 Registrar venta (incluye CUS05 Descontar inventario), CUS06 Consultar historial de ventas
// y CUS08 Anular la última venta
@Controller
public class VentaControlador {

    private static final String VISTA_REGISTRAR_VENTA = "registrar-venta";
    private static final String VISTA_CONFIRMAR_VENTA = "confirmar-venta";
    private static final String VISTA_HISTORIAL = "historial-ventas";
    private static final String VISTA_ANULAR_VENTA = "anular-venta";
    private static final String REDIRECCION_NUEVA_VENTA = "redirect:/ventas/nueva";
    private static final String REDIRECCION_HISTORIAL = "redirect:/ventas/historial";

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

    // CUS08, pasos 2 y 3: muestra la última venta y pide el motivo de la anulación (exclusivo del propietario)
    @GetMapping("/ventas/{idVenta}/anular")
    public String mostrarAnulacion(@PathVariable int idVenta, Model modelo, RedirectAttributes atributos) {
        try {
            modelo.addAttribute("venta", ventaServicio.obtenerVentaParaAnular(idVenta));
            return VISTA_ANULAR_VENTA;
        } catch (VentaNoAnulableException error) {
            return volverAlHistorialConError(atributos, error);
        }
    }

    // CUS08, pasos 4 a 6: anula la venta y devuelve sus unidades al inventario
    @PostMapping("/ventas/{idVenta}/anular")
    public String anularVenta(@PathVariable int idVenta,
                              @RequestParam(required = false) String motivo,
                              Model modelo,
                              RedirectAttributes atributos) {
        try {
            ventaServicio.anularVenta(idVenta, motivo);
            atributos.addFlashAttribute("mensaje", "La venta N.° " + idVenta
                    + " se anuló y sus unidades volvieron al inventario.");
            return REDIRECCION_HISTORIAL;
        } catch (DatosInvalidosException error) {
            return mostrarAnulacionConError(idVenta, motivo, modelo, atributos, error);
        } catch (VentaNoAnulableException error) {
            return volverAlHistorialConError(atributos, error);
        }
    }

    // CUS08, flujo alternativo "Motivo vacío": se vuelve a pedir el motivo conservando lo escrito
    private String mostrarAnulacionConError(int idVenta, String motivo, Model modelo,
                                            RedirectAttributes atributos, DatosInvalidosException error) {
        try {
            modelo.addAttribute("venta", ventaServicio.obtenerVentaParaAnular(idVenta));
        } catch (VentaNoAnulableException noAnulable) {
            return volverAlHistorialConError(atributos, noAnulable);
        }
        modelo.addAttribute("motivo", motivo);
        modelo.addAttribute("error", error.getMessage());
        return VISTA_ANULAR_VENTA;
    }

    private String volverAlHistorialConError(RedirectAttributes atributos, VentaNoAnulableException error) {
        atributos.addFlashAttribute("error", error.getMessage());
        return REDIRECCION_HISTORIAL;
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
