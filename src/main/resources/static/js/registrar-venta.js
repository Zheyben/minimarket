/*
 * CUS04 Registrar venta: agrega y quita filas de productos sin recargar la página,
 * activa el buscador de productos en cada fila (buscador-producto.js), guarda un borrador
 * para no perder la venta al ir a otra pantalla (borrador-venta.js) y revisa, antes de
 * enviar, que cada fila tenga un producto elegido de la lista.
 * Si el navegador no ejecuta JavaScript, el formulario sigue funcionando con una sola fila.
 */
(function () {
    'use strict';

    const lista = document.getElementById('lineas-venta');
    const plantilla = document.getElementById('plantilla-linea');
    const botonAgregar = document.getElementById('agregar-linea');
    const anuncio = document.getElementById('anuncio-lineas');
    const avisoBorrador = document.getElementById('aviso-borrador');
    const buscador = window.Minimarket && window.Minimarket.BuscadorProducto;
    const borrador = window.Minimarket && window.Minimarket.BorradorVenta;

    if (!lista || !plantilla || !botonAgregar || !anuncio || !buscador || !borrador) {
        return;
    }

    const formulario = lista.closest('form');
    const usuario = formulario.dataset.usuario;

    function obtenerLineas() {
        return Array.from(lista.querySelectorAll('.linea-venta'));
    }

    // ---------- Numeración de filas ----------

    // Numera las filas en orden (lineas[0], lineas[1], ...) para que el servidor reciba la lista sin huecos
    function renumerarLineas() {
        const lineas = obtenerLineas();
        lineas.forEach(function (linea, indice) {
            actualizarLinea(linea, indice, lineas.length);
        });
    }

    function actualizarLinea(linea, indice, totalLineas) {
        const numero = indice + 1;

        linea.querySelectorAll('[data-campo]').forEach(function (campo) {
            campo.name = 'lineas[' + indice + '].' + campo.dataset.campo;
        });

        linea.querySelectorAll('[data-id]').forEach(function (elemento) {
            elemento.id = elemento.dataset.id + '-' + indice;
        });

        linea.querySelectorAll('label[data-para]').forEach(function (etiqueta) {
            etiqueta.htmlFor = etiqueta.dataset.para + '-' + indice;
        });

        linea.querySelectorAll('.linea-numero').forEach(function (texto) {
            texto.textContent = numero;
        });

        const campoBuscar = linea.querySelector('[role="combobox"]');
        const listaOpciones = linea.querySelector('[role="listbox"]');
        if (campoBuscar && listaOpciones) {
            campoBuscar.setAttribute('aria-controls', listaOpciones.id);
        }

        const botonQuitar = linea.querySelector('[data-accion="quitar"]');
        botonQuitar.hidden = totalLineas === 1;
        botonQuitar.setAttribute('aria-label', 'Quitar producto ' + numero);
    }

    // ---------- Agregar y quitar filas ----------

    function crearLineaVacia() {
        const nuevaLinea = plantilla.content.firstElementChild.cloneNode(true);
        lista.appendChild(nuevaLinea);
        return nuevaLinea;
    }

    function agregarLinea() {
        const nuevaLinea = crearLineaVacia();
        buscador.mejorar(nuevaLinea);
        renumerarLineas();
        nuevaLinea.querySelector('[role="combobox"]').focus();
        anunciar('Se agregó el producto ' + obtenerLineas().length + '.');
        guardarBorrador();
    }

    function quitarLinea(linea) {
        linea.remove();
        renumerarLineas();
        botonAgregar.focus();
        anunciar('Se quitó un producto. Quedan ' + obtenerLineas().length + '.');
        guardarBorrador();
    }

    // Texto que los lectores de pantalla leen en voz alta (zona aria-live)
    function anunciar(texto) {
        anuncio.textContent = texto;
    }

    // ---------- Borrador de la venta ----------

    function selectorDe(linea) {
        return linea.querySelector('select[data-campo="idProducto"]');
    }

    function cantidadDe(linea) {
        return linea.querySelector('[data-campo="cantidad"]');
    }

    function leerLineasDelFormulario() {
        return obtenerLineas().map(function (linea) {
            return {idProducto: selectorDe(linea).value, cantidad: cantidadDe(linea).value};
        });
    }

    function guardarBorrador() {
        borrador.guardar(usuario, leerLineasDelFormulario());
    }

    // Si el servidor devolvió la venta con un error, ya trae los productos elegidos: esos tienen prioridad
    function hayDatosDelServidor() {
        return obtenerLineas().some(function (linea) {
            return selectorDe(linea).value !== '' || cantidadDe(linea).value !== '';
        });
    }

    // Vuelve a llenar las filas con lo guardado (antes de activar el buscador en ellas)
    function restaurarBorrador() {
        const guardadas = borrador.leer(usuario);
        if (guardadas.length === 0) {
            return false;
        }
        while (obtenerLineas().length < guardadas.length) {
            crearLineaVacia();
        }
        obtenerLineas().forEach(function (linea, indice) {
            selectorDe(linea).value = guardadas[indice].idProducto || '';
            cantidadDe(linea).value = guardadas[indice].cantidad || '';
        });
        return true;
    }

    // ---------- Validación antes de enviar ----------

    function tieneProducto(linea) {
        return linea.querySelector('select[data-campo="idProducto"]').value !== '';
    }

    function mostrarError(linea) {
        const error = linea.querySelector('.error-campo');
        const campoBuscar = linea.querySelector('[role="combobox"]');
        error.hidden = false;
        campoBuscar.setAttribute('aria-invalid', 'true');
        campoBuscar.setAttribute('aria-describedby', error.id);
    }

    function ocultarError(linea) {
        const error = linea.querySelector('.error-campo');
        const campoBuscar = linea.querySelector('[role="combobox"]');
        error.hidden = true;
        campoBuscar.removeAttribute('aria-invalid');
        campoBuscar.removeAttribute('aria-describedby');
    }

    function alEnviar(evento) {
        const lineasSinProducto = obtenerLineas().filter(function (linea) {
            return !tieneProducto(linea);
        });
        if (lineasSinProducto.length === 0) {
            return;
        }
        evento.preventDefault();
        lineasSinProducto.forEach(mostrarError);
        lineasSinProducto[0].querySelector('[role="combobox"]').focus();
    }

    // Al elegir un producto válido, desaparece el mensaje de error de esa fila
    function alCambiarProducto(evento) {
        const linea = evento.target.closest('.linea-venta');
        if (linea && tieneProducto(linea)) {
            ocultarError(linea);
        }
        guardarBorrador();
    }

    function alHacerClicEnLista(evento) {
        const botonQuitar = evento.target.closest('[data-accion="quitar"]');
        if (botonQuitar) {
            quitarLinea(botonQuitar.closest('.linea-venta'));
        }
    }

    // ---------- Inicio ----------

    const seRestauro = !hayDatosDelServidor() && restaurarBorrador();
    obtenerLineas().forEach(buscador.mejorar);
    renumerarLineas();
    guardarBorrador();
    if (seRestauro && avisoBorrador) {
        avisoBorrador.hidden = false;
    }

    botonAgregar.hidden = false;
    botonAgregar.addEventListener('click', agregarLinea);
    lista.addEventListener('click', alHacerClicEnLista);
    lista.addEventListener('input', guardarBorrador);
    lista.addEventListener(buscador.EVENTO_CAMBIO, alCambiarProducto);
    formulario.addEventListener('submit', alEnviar);
})();
