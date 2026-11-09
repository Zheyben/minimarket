package zheyben.minimarketaj.minimarket.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import zheyben.minimarketaj.minimarket.excepcion.DatosInvalidosException;
import zheyben.minimarketaj.minimarket.excepcion.ProductoNoEncontradoException;
import zheyben.minimarketaj.minimarket.modelo.Producto;
import zheyben.minimarketaj.minimarket.servicio.InventarioServicio;
import zheyben.minimarketaj.minimarket.servicio.ProductoServicio;

import java.math.BigDecimal;
import java.util.List;

// Controlador: CUS02 Consultar catálogo de productos, CUS03 Actualizar precios y CUS07 Actualizar cantidades
@Controller
public class ProductoControlador {

    private static final String VISTA_CATALOGO = "catalogo";
    private static final String VISTA_EDITAR_PRECIO = "editar-precio";
    private static final String VISTA_EDITAR_CANTIDAD = "editar-cantidad";
    private static final String REDIRECCION_CATALOGO = "redirect:/catalogo";

    private final ProductoServicio productoServicio;
    private final InventarioServicio inventarioServicio;

    public ProductoControlador(ProductoServicio productoServicio, InventarioServicio inventarioServicio) {
        this.productoServicio = productoServicio;
        this.inventarioServicio = inventarioServicio;
    }

    @GetMapping("/")
    public String inicio() {
        return REDIRECCION_CATALOGO;
    }

    // CUS02: muestra el catálogo completo o los productos que coinciden con la búsqueda
    @GetMapping("/catalogo")
    public String mostrarCatalogo(@RequestParam(required = false) String busqueda, Model modelo) {
        List<Producto> productos = productoServicio.buscarProductos(busqueda);
        modelo.addAttribute("productos", productos);
        modelo.addAttribute("busqueda", busqueda);
        modelo.addAttribute("sinResultados", productos.isEmpty());
        return VISTA_CATALOGO;
    }

    // CUS03, pasos 2 y 3: habilita la edición del precio del producto seleccionado
    @GetMapping("/productos/{idProducto}/precio")
    public String mostrarEdicionPrecio(@PathVariable int idProducto, Model modelo,
                                       RedirectAttributes atributos) {
        try {
            modelo.addAttribute("producto", productoServicio.obtenerProducto(idProducto));
            return VISTA_EDITAR_PRECIO;
        } catch (ProductoNoEncontradoException error) {
            return volverAlCatalogoConError(atributos, error);
        }
    }

    // CUS03, pasos 4 y 5: guarda el nuevo precio y muestra la confirmación
    @PostMapping("/productos/{idProducto}/precio")
    public String actualizarPrecio(@PathVariable int idProducto,
                                   @RequestParam(required = false) BigDecimal nuevoPrecio,
                                   Model modelo,
                                   RedirectAttributes atributos) {
        try {
            productoServicio.actualizarPrecio(idProducto, nuevoPrecio);
            atributos.addFlashAttribute("mensaje", "El precio se actualizó correctamente.");
            return REDIRECCION_CATALOGO;
        } catch (DatosInvalidosException error) {
            return mostrarEdicionConError(idProducto, modelo, error);
        } catch (ProductoNoEncontradoException error) {
            return volverAlCatalogoConError(atributos, error);
        }
    }

    // CUS07, pasos 2 y 3: muestra la cantidad disponible y las dos formas de actualizarla
    @GetMapping("/productos/{idProducto}/cantidad")
    public String mostrarEdicionCantidad(@PathVariable int idProducto, Model modelo,
                                         RedirectAttributes atributos) {
        try {
            modelo.addAttribute("producto", productoServicio.obtenerProducto(idProducto));
            return VISTA_EDITAR_CANTIDAD;
        } catch (ProductoNoEncontradoException error) {
            return volverAlCatalogoConError(atributos, error);
        }
    }

    // CUS07, pasos 4 y 5: suma las unidades de la mercadería que llegó
    @PostMapping("/productos/{idProducto}/cantidad/ingreso")
    public String registrarIngreso(@PathVariable int idProducto,
                                   @RequestParam(required = false) Integer unidades,
                                   Model modelo,
                                   RedirectAttributes atributos) {
        try {
            Producto producto = inventarioServicio.registrarIngreso(idProducto, unidades);
            atributos.addFlashAttribute("mensaje", producto.getNombre() + ": se registró el ingreso de "
                    + textoUnidades(unidades) + ". Disponible: " + producto.getCantidadDisponible() + ".");
            return REDIRECCION_CATALOGO;
        } catch (DatosInvalidosException error) {
            return mostrarCantidadConError(idProducto, modelo, error, "unidades");
        } catch (ProductoNoEncontradoException error) {
            return volverAlCatalogoConError(atributos, error);
        }
    }

    // CUS07, flujo alternativo "Ajuste por conteo": reemplaza la cantidad por la que se contó en la tienda
    @PostMapping("/productos/{idProducto}/cantidad/ajuste")
    public String ajustarCantidad(@PathVariable int idProducto,
                                  @RequestParam(required = false) Integer cantidad,
                                  Model modelo,
                                  RedirectAttributes atributos) {
        try {
            Producto producto = inventarioServicio.ajustarCantidad(idProducto, cantidad);
            atributos.addFlashAttribute("mensaje", producto.getNombre()
                    + ": la cantidad disponible se ajustó a " + producto.getCantidadDisponible() + ".");
            return REDIRECCION_CATALOGO;
        } catch (DatosInvalidosException error) {
            return mostrarCantidadConError(idProducto, modelo, error, "cantidad");
        } catch (ProductoNoEncontradoException error) {
            return volverAlCatalogoConError(atributos, error);
        }
    }

    private String textoUnidades(int unidades) {
        return unidades + (unidades == 1 ? " unidad" : " unidades");
    }

    // El dato ingresado no es válido: se vuelve a mostrar el formulario y se marca el campo con error
    private String mostrarCantidadConError(int idProducto, Model modelo, DatosInvalidosException error,
                                           String campoConError) {
        modelo.addAttribute("producto", productoServicio.obtenerProducto(idProducto));
        modelo.addAttribute("error", error.getMessage());
        modelo.addAttribute("campoConError", campoConError);
        return VISTA_EDITAR_CANTIDAD;
    }

    // El precio ingresado no es válido: se vuelve a mostrar el formulario con el precio vigente
    private String mostrarEdicionConError(int idProducto, Model modelo, DatosInvalidosException error) {
        modelo.addAttribute("producto", productoServicio.obtenerProducto(idProducto));
        modelo.addAttribute("error", error.getMessage());
        return VISTA_EDITAR_PRECIO;
    }

    private String volverAlCatalogoConError(RedirectAttributes atributos, ProductoNoEncontradoException error) {
        atributos.addFlashAttribute("error", error.getMessage());
        return REDIRECCION_CATALOGO;
    }
}
