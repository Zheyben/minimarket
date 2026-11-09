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
import zheyben.minimarketaj.minimarket.servicio.ProductoServicio;

import java.math.BigDecimal;
import java.util.List;

// Controlador: CUS02 Consultar catálogo de productos y CUS03 Actualizar precios
@Controller
public class ProductoControlador {

    private static final String VISTA_CATALOGO = "catalogo";
    private static final String VISTA_EDITAR_PRECIO = "editar-precio";
    private static final String REDIRECCION_CATALOGO = "redirect:/catalogo";

    private final ProductoServicio productoServicio;

    public ProductoControlador(ProductoServicio productoServicio) {
        this.productoServicio = productoServicio;
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
