/*
 * Buscador de productos (CUS04 Registrar venta).
 * Reemplaza la lista desplegable de productos por un campo donde se escribe el nombre:
 * aparece una lista solo con los productos que coinciden y, al elegir uno, se muestran
 * su precio y su disponibilidad.
 *
 * Sigue el patrón "combobox" de WAI-ARIA para que funcione con el teclado
 * (flechas, Enter y Escape) y con lectores de pantalla.
 * La lista desplegable original queda oculta y es la que se envía al servidor.
 *
 * Uso: Minimarket.BuscadorProducto.mejorar(fila)
 */
window.Minimarket = window.Minimarket || {};

window.Minimarket.BuscadorProducto = (function () {
    'use strict';

    const MAXIMO_RESULTADOS = 8;
    const EVENTO_CAMBIO = 'producto-cambiado';

    // ---------- Utilidades ----------

    // Quita tildes y mayúsculas para comparar: "Azúcar" y "azucar" coinciden
    function normalizar(texto) {
        return texto.normalize('NFD').replace(/[̀-ͯ]/g, '').toLowerCase();
    }

    function crearElemento(etiqueta, clase) {
        const elemento = document.createElement(etiqueta);
        if (clase) {
            elemento.className = clase;
        }
        return elemento;
    }

    // Ícono de lupa (Tabler Icons, licencia MIT); es decorativo
    function crearIconoBuscar() {
        const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
        svg.setAttribute('class', 'icono combo-icono');
        svg.setAttribute('viewBox', '0 0 24 24');
        svg.setAttribute('aria-hidden', 'true');
        svg.setAttribute('focusable', 'false');
        ['M3 10a7 7 0 1 0 14 0a7 7 0 1 0 -14 0', 'M21 21l-6 -6'].forEach(function (trazo) {
            const ruta = document.createElementNS('http://www.w3.org/2000/svg', 'path');
            ruta.setAttribute('d', trazo);
            svg.appendChild(ruta);
        });
        return svg;
    }

    // Lee los productos desde las opciones de la lista desplegable que generó el servidor
    function leerProductos(selector) {
        return Array.from(selector.options)
            .filter(function (opcion) {
                return opcion.value !== '';
            })
            .map(function (opcion) {
                const nombre = opcion.textContent.trim();
                return {
                    id: opcion.value,
                    nombre: nombre,
                    clave: normalizar(nombre),
                    precio: opcion.dataset.precio,
                    disponible: opcion.dataset.disponible
                };
            });
    }

    // Escribe el nombre subrayando la parte que coincide con lo escrito
    function escribirConCoincidencia(elemento, producto, busqueda) {
        const inicio = producto.clave.indexOf(busqueda);
        const mismaLongitud = producto.clave.length === producto.nombre.length;

        if (inicio < 0 || !mismaLongitud) {
            elemento.textContent = producto.nombre;
            return;
        }

        const fin = inicio + busqueda.length;
        const resaltado = crearElemento('mark');
        resaltado.textContent = producto.nombre.slice(inicio, fin);
        elemento.append(producto.nombre.slice(0, inicio), resaltado, producto.nombre.slice(fin));
    }

    // ---------- Componente ----------

    function mejorar(fila) {
        const selector = fila.querySelector('select[data-campo="idProducto"]');
        const etiqueta = fila.querySelector('label[data-para="idProducto"]');
        const panelInfo = fila.querySelector('[data-info]');
        const campoCantidad = fila.querySelector('[data-campo="cantidad"]');

        if (!selector || !etiqueta || !panelInfo) {
            return;
        }

        const productos = leerProductos(selector);
        const elementos = construirElementos(selector, etiqueta);
        let resultados = [];
        let indiceActivo = -1;

        function construirElementos(selectorOriginal, etiquetaCampo) {
            const contenedor = crearElemento('div', 'combo');
            const campo = crearElemento('input', 'combo-campo');
            const lista = crearElemento('ul', 'combo-lista');
            const sinResultados = crearElemento('p', 'combo-vacio');
            const estado = crearElemento('p', 'solo-lector');

            campo.type = 'text';
            campo.dataset.id = 'buscar';
            campo.placeholder = 'Escribe el nombre del producto';
            campo.autocomplete = 'off';
            campo.spellcheck = false;
            campo.setAttribute('role', 'combobox');
            campo.setAttribute('aria-autocomplete', 'list');
            campo.setAttribute('aria-expanded', 'false');

            lista.dataset.id = 'opciones';
            lista.setAttribute('role', 'listbox');
            lista.setAttribute('aria-label', 'Productos encontrados');
            lista.hidden = true;

            sinResultados.textContent = 'No se encontraron productos con ese nombre.';
            sinResultados.hidden = true;
            estado.setAttribute('aria-live', 'polite');

            contenedor.append(crearIconoBuscar(), campo, lista, sinResultados, estado);
            selectorOriginal.parentNode.insertBefore(contenedor, selectorOriginal);

            // La lista original se oculta, pero sigue dentro del formulario para enviarse al servidor
            selectorOriginal.hidden = true;
            selectorOriginal.required = false;
            selectorOriginal.tabIndex = -1;
            etiquetaCampo.dataset.para = 'buscar';

            return {campo: campo, lista: lista, sinResultados: sinResultados, estado: estado};
        }

        function buscar() {
            const busqueda = normalizar(elementos.campo.value.trim());
            if (busqueda === '') {
                cerrarLista();
                return;
            }
            resultados = productos
                .filter(function (producto) {
                    return producto.clave.includes(busqueda);
                })
                .slice(0, MAXIMO_RESULTADOS);
            mostrarResultados(busqueda);
        }

        function mostrarResultados(busqueda) {
            const lista = elementos.lista;
            lista.replaceChildren();
            indiceActivo = -1;
            elementos.campo.removeAttribute('aria-activedescendant');

            resultados.forEach(function (producto, posicion) {
                lista.appendChild(crearOpcion(producto, posicion, busqueda));
            });

            const hayResultados = resultados.length > 0;
            lista.hidden = !hayResultados;
            elementos.sinResultados.hidden = hayResultados;
            elementos.campo.setAttribute('aria-expanded', String(hayResultados));
            elementos.estado.textContent = hayResultados
                ? resultados.length + (resultados.length === 1 ? ' producto encontrado.' : ' productos encontrados.')
                : 'No se encontraron productos.';
        }

        function crearOpcion(producto, posicion, busqueda) {
            const opcion = crearElemento('li', 'combo-opcion');
            opcion.id = elementos.lista.id + '-' + posicion;
            opcion.setAttribute('role', 'option');
            opcion.setAttribute('aria-selected', 'false');
            escribirConCoincidencia(opcion, producto, busqueda);

            // mousedown evita que el campo pierda el foco antes de elegir con el dedo o el ratón
            opcion.addEventListener('mousedown', function (evento) {
                evento.preventDefault();
            });
            opcion.addEventListener('click', function () {
                elegir(producto, true);
            });
            return opcion;
        }

        function marcarActivo(nuevoIndice) {
            const opciones = elementos.lista.querySelectorAll('[role="option"]');
            if (opciones.length === 0) {
                return;
            }
            indiceActivo = (nuevoIndice + opciones.length) % opciones.length;
            opciones.forEach(function (opcion, posicion) {
                opcion.setAttribute('aria-selected', String(posicion === indiceActivo));
            });
            const activa = opciones[indiceActivo];
            elementos.campo.setAttribute('aria-activedescendant', activa.id);
            activa.scrollIntoView({block: 'nearest'});
        }

        function cerrarLista() {
            elementos.lista.hidden = true;
            elementos.sinResultados.hidden = true;
            elementos.campo.setAttribute('aria-expanded', 'false');
            elementos.campo.removeAttribute('aria-activedescendant');
            indiceActivo = -1;
        }

        // Guarda el producto en la lista original y muestra su precio y disponibilidad
        function elegir(producto, pasarACantidad) {
            elementos.campo.value = producto.nombre;
            selector.value = producto.id;
            mostrarInformacion(producto);
            cerrarLista();
            avisarCambio();
            if (pasarACantidad && campoCantidad) {
                campoCantidad.focus();
            }
        }

        function quitarEleccion() {
            if (selector.value === '') {
                return;
            }
            selector.value = '';
            panelInfo.hidden = true;
            avisarCambio();
        }

        function mostrarInformacion(producto) {
            panelInfo.querySelector('[data-valor="precio"]').textContent = 'S/ ' + producto.precio;
            panelInfo.querySelector('[data-valor="disponible"]').textContent = producto.disponible;
            panelInfo.hidden = false;
        }

        function avisarCambio() {
            fila.dispatchEvent(new CustomEvent(EVENTO_CAMBIO, {bubbles: true}));
        }

        // Si se escribió el nombre completo sin tocar la lista, se elige ese producto
        function elegirSiCoincideExacto() {
            if (selector.value !== '') {
                return;
            }
            const escrito = normalizar(elementos.campo.value.trim());
            const exacto = productos.find(function (producto) {
                return producto.clave === escrito;
            });
            if (exacto) {
                elegir(exacto, false);
            }
        }

        function alEscribir() {
            quitarEleccion();
            buscar();
        }

        function alPulsarTecla(evento) {
            const listaAbierta = !elementos.lista.hidden;

            switch (evento.key) {
                case 'ArrowDown':
                    evento.preventDefault();
                    if (!listaAbierta) {
                        buscar();
                    }
                    marcarActivo(indiceActivo + 1);
                    break;
                case 'ArrowUp':
                    evento.preventDefault();
                    marcarActivo(indiceActivo - 1);
                    break;
                case 'Enter':
                    // Enter elige el producto marcado (o el único resultado) en vez de enviar el formulario
                    if (listaAbierta && (indiceActivo >= 0 || resultados.length === 1)) {
                        evento.preventDefault();
                        elegir(resultados[Math.max(indiceActivo, 0)], true);
                    }
                    break;
                case 'Escape':
                    if (listaAbierta) {
                        evento.preventDefault();
                        cerrarLista();
                    }
                    break;
                default:
                    break;
            }
        }

        function alSalirDelCampo() {
            elegirSiCoincideExacto();
            cerrarLista();
        }

        // Si la venta volvió con un error, se muestra el producto que ya estaba elegido
        function mostrarEleccionInicial() {
            const elegido = productos.find(function (producto) {
                return producto.id === selector.value;
            });
            if (elegido) {
                elementos.campo.value = elegido.nombre;
                mostrarInformacion(elegido);
            }
        }

        elementos.campo.addEventListener('input', alEscribir);
        elementos.campo.addEventListener('keydown', alPulsarTecla);
        elementos.campo.addEventListener('blur', alSalirDelCampo);
        mostrarEleccionInicial();
    }

    return {
        mejorar: mejorar,
        EVENTO_CAMBIO: EVENTO_CAMBIO
    };
})();
