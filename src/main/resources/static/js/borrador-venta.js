/*
 * Borrador de la venta en curso (CUS04 Registrar venta).
 * Guarda en el navegador (sessionStorage: solo en esta pestaña) los productos y cantidades
 * que el actor va eligiendo, para que no se pierdan si va a otra pantalla y luego vuelve.
 * Se borra al confirmar o cancelar la venta, y también al cerrar la pestaña.
 * Cada usuario tiene su propio borrador, porque varios trabajadores pueden usar el mismo celular.
 *
 * Uso: Minimarket.BorradorVenta.guardar(usuario, lineas) / leer(usuario) / borrar(usuario)
 */
window.Minimarket = window.Minimarket || {};

window.Minimarket.BorradorVenta = (function () {
    'use strict';

    const PREFIJO = 'minimarket.borradorVenta.';

    function clave(usuario) {
        return PREFIJO + (usuario || '');
    }

    function tieneDatos(linea) {
        return Boolean(linea.idProducto) || Boolean(linea.cantidad);
    }

    // lineas: [{idProducto: '1', cantidad: '2'}, ...]
    function guardar(usuario, lineas) {
        if (!lineas.some(tieneDatos)) {
            borrar(usuario);
            return;
        }
        try {
            sessionStorage.setItem(clave(usuario), JSON.stringify(lineas));
        } catch (error) {
            // El navegador no permite guardar (modo privado o sin espacio): la venta sigue funcionando sin borrador
        }
    }

    function leer(usuario) {
        try {
            const texto = sessionStorage.getItem(clave(usuario));
            const lineas = texto ? JSON.parse(texto) : [];
            return Array.isArray(lineas) ? lineas.filter(tieneDatos) : [];
        } catch (error) {
            return [];
        }
    }

    function borrar(usuario) {
        try {
            sessionStorage.removeItem(clave(usuario));
        } catch (error) {
            // Nada que borrar si el navegador no permite guardar
        }
    }

    // Los formularios que terminan la venta (confirmar o cancelar) borran el borrador al enviarse
    function enlazarFormulariosQueTerminanLaVenta() {
        document.querySelectorAll('form[data-borrar-borrador]').forEach(function (formulario) {
            formulario.addEventListener('submit', function () {
                borrar(formulario.dataset.usuario);
            });
        });
    }

    enlazarFormulariosQueTerminanLaVenta();

    return {
        guardar: guardar,
        leer: leer,
        borrar: borrar
    };
})();
