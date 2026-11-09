# Proyecto: Sistema web de Minimarket A&J

Este documento explica qué es el sistema, para qué sirve, cómo está construido y qué se debe tener en cuenta para mejorarlo. Está pensado para que cualquier persona del equipo (o alguien nuevo) entienda el proyecto completo sin tener que leer todo el código.

> Para instalarlo y ejecutarlo, ver el [README.md](README.md). Para entender cómo se trabaja en la tienda con el sistema, ver [FLUJO_NEGOCIO.md](FLUJO_NEGOCIO.md).

---

## 1. ¿Qué problema resuelve?

**Minimarket A&J** es una bodega del distrito de Ate (Lima). La atienden el propietario y cuatro vendedores que se turnan. Antes del sistema tenía tres problemas:

1. **Los precios eran difíciles de consultar.** Dependían de la memoria del vendedor, de etiquetas borrosas o de preguntarle al propietario. Un vendedor nuevo tardaba en atender.
2. **Un mismo producto se cobraba distinto** según quién atendía, y eso generaba desconfianza en los clientes.
3. **No había registro de ventas ni de inventario.** El stock (cantidad de productos que hay en la tienda) se revisaba mirando los estantes, así que un producto podía acabarse sin que nadie lo notara.

## 2. ¿Para qué sirve el sistema?

Es una página web que se abre desde el **navegador del celular** (no hay que instalar ninguna aplicación). Sirve para:

- Tener **una sola lista de productos con su precio y su cantidad disponible**, que todos consultan.
- **Registrar cada venta**: el sistema calcula el total con el precio oficial, así nadie puede cobrar otro monto.
- **Descontar automáticamente** del stock lo que se vende.
- Que el **propietario** cambie precios, **sume la mercadería que llega**, corrija cantidades, revise el **historial de ventas** y **anule la última venta** si se registró por error.

## 3. ¿Quién lo usa?

El sistema distingue dos **roles** (tipos de usuario). Cada persona entra con su propio usuario y contraseña.

| Función | Vendedor | Propietario |
|---|:---:|:---:|
| Iniciar y cerrar sesión | ✔ | ✔ |
| Consultar el catálogo y buscar productos | ✔ | ✔ |
| Registrar ventas | ✔ | ✔ |
| Cambiar precios | — | ✔ |
| Sumar mercadería o corregir cantidades | — | ✔ |
| Ver el historial de ventas | — | ✔ |
| Anular la última venta | — | ✔ |

Usuarios de prueba (vienen en el script de la base de datos): **Sebastián** / `propietario123` (propietario) y **Joaquín** / `vendedor123` (vendedor).

## 4. Requerimientos

Un **requerimiento** es algo que el sistema *debe* hacer. Los **funcionales (RF)** dicen *qué* hace; los **no funcionales (RNF)** dicen *cómo* debe comportarse.

| Código | Qué pide | Cómo se cumple |
|---|---|---|
| RF01 | Lista central de productos y precios | Pantalla **Catálogo** |
| RF02 | Modificar precios | Botón **Cambiar precio** (propietario) |
| RF03 | Búsqueda rápida de productos | Buscador del catálogo (busca por parte del nombre) |
| RF04 | Registrar ventas y calcular el total | Pantalla **Vender** |
| RF05 | Descontar automáticamente lo vendido | Se descuenta al confirmar la venta |
| RF06 | Acceso diferenciado por rol | Inicio de sesión y menú según el rol |
| RF07 | Historial básico de ventas | Pantalla **Historial** (propietario) |
| RF08 | Sumar la mercadería que llega y ajustar la cantidad contada | Botón **Cambiar cantidad** (propietario) |
| RF09 | Anular la última venta y guardar el motivo | Botón **Anular venta** en el historial (propietario) |
| RNF01 | Usarse bien desde el celular | Diseño pensado primero para pantallas pequeñas |
| RNF02 | Solo el propietario puede usar sus funciones | El servidor revisa el rol en cada solicitud |

RF08 y RF09 se agregaron después, a pedido del propietario ("que sea sencillo actualizar precios y cantidades" y "poder corregir ventas registradas").

## 5. Casos de uso

Un **caso de uso** describe una tarea completa que un usuario hace con el sistema, de principio a fin. Están detallados en el informe (sección 3.2) y en [FLUJO_NEGOCIO.md](FLUJO_NEGOCIO.md).

| Código | Caso de uso | Quién | Pantalla (archivo en `templates`) | Controlador |
|---|---|---|---|---|
| CUS01 | Autenticarse en el sistema | Ambos | `login.html` | `AutenticacionControlador` |
| CUS02 | Consultar catálogo de productos | Ambos | `catalogo.html` | `ProductoControlador` |
| CUS03 | Actualizar precios | Propietario | `editar-precio.html` | `ProductoControlador` |
| CUS04 | Registrar venta | Ambos | `registrar-venta.html`, `confirmar-venta.html` | `VentaControlador` |
| CUS05 | Descontar inventario | Ambos (automático) | — (ocurre dentro de CUS04) | `VentaControlador` |
| CUS06 | Consultar historial de ventas | Propietario | `historial-ventas.html` | `VentaControlador` |
| CUS07 | Actualizar cantidades | Propietario | `editar-cantidad.html` | `ProductoControlador` |
| CUS08 | Anular la última venta | Propietario | `anular-venta.html` | `VentaControlador` |

CUS04 **incluye** a CUS05: cada vez que se registra una venta, siempre se descuenta el inventario.

## 6. Tecnologías

| Herramienta | Versión | Para qué se usa (en palabras simples) |
|---|---|---|
| Java | 21 | El lenguaje en el que está escrito el programa. |
| Spring Boot | 4.1.2 | Un "kit" que trae lo necesario para hacer una página web en Java: recibe lo que pide el navegador, conecta con la base de datos y arranca un servidor web (Tomcat) incluido. |
| Thymeleaf | (incluido en Spring Boot) | Permite escribir las pantallas en HTML y rellenarlas con datos reales. Por ejemplo, la lista de productos del catálogo. |
| MySQL | 8.0 | La **base de datos**: el lugar donde se guardan de forma permanente los usuarios, productos y ventas. |
| JDBC | (incluido) | La forma en que Java "habla" con MySQL: envía consultas SQL y recibe los resultados. |
| Maven (wrapper `mvnw`) | 3.10 | Descarga las librerías que el proyecto necesita y lo ejecuta. No hay que instalarlo aparte. |

## 7. Cómo está organizado el código

El proyecto usa tres **patrones de diseño**. Un patrón de diseño es una forma probada de ordenar el código para que sea fácil de entender y de cambiar.

### 7.1 Patrón por capas

El programa está dividido en "pisos" (capas). Cada capa tiene una sola tarea y solo habla con la de al lado, como en una cadena:

```
Celular (navegador)
   │
   ▼
Capa de presentación   → recibe lo que pide el usuario y muestra las pantallas
   │                      (paquetes: controlador, seguridad, templates, static)
   ▼
Capa de lógica de negocio → aplica las reglas de la tienda: calcula totales,
   │                         revisa stock, valida datos (paquetes: servicio, excepcion)
   ▼
Capa de acceso a datos → lee y guarda en la base de datos (paquete: dao)
   │
   ▼
Base de datos MySQL (minimarket_aj)
```

**Ventaja:** si mañana se cambia el diseño de una pantalla, no hay que tocar las reglas de negocio ni la base de datos.

### 7.2 Patrón MVC (Modelo – Vista – Controlador)

Separa tres responsabilidades. Una analogía con un restaurante:

- **Modelo** (paquete `modelo`): los "ingredientes", es decir, los datos. Las clases `Producto`, `Usuario`, `Venta` y `DetalleVenta`.
- **Vista** (carpeta `templates`): el "plato servido", es decir, lo que ve el usuario. Son las páginas HTML.
- **Controlador** (paquete `controlador`): el "mesero", que recibe el pedido del usuario, se lo pasa a la cocina (servicios) y le devuelve la vista correcta.

### 7.3 Patrón DAO (Data Access Object, "objeto de acceso a datos")

Toda la comunicación con la base de datos está concentrada en el paquete `dao`. Ninguna otra parte del programa escribe SQL. Cada tabla tiene:

- Una **interfaz** (una lista de lo que se puede hacer), por ejemplo `ProductoDAO`: listar, buscar, actualizar precio, descontar cantidad, etc.
- Una **implementación** que lo hace realmente con JDBC, por ejemplo `ProductoDAOImpl`.

**Ventaja:** si se cambiara MySQL por otra base de datos, solo habría que reescribir las implementaciones del DAO.

### 7.4 Mapa de carpetas

```
code/
├── scripts/minimarket_aj.sql        → crea la base de datos con datos de prueba
└── minimarket/
    ├── pom.xml                      → lista de librerías y versión de Java
    └── src/main/
        ├── java/zheyben/minimarketaj/minimarket/
        │   ├── MinimarketApplication.java  → punto de arranque del programa
        │   ├── controlador/   → reciben las solicitudes de cada pantalla
        │   ├── seguridad/     → revisan que haya sesión y el rol correcto
        │   ├── servicio/      → reglas de negocio (ventas, inventario, precios, acceso)
        │   ├── excepcion/     → errores esperados con mensajes para el usuario
        │   ├── dao/           → acceso a la base de datos (interfaces + JDBC)
        │   ├── modelo/        → clases que representan las tablas
        │   └── dto/           → "paquetes" de datos que viajan entre capas
        └── resources/
            ├── application.properties → conexión a MySQL y configuración
            ├── templates/   → pantallas HTML
            └── static/      → estilos (css) y comportamiento en el navegador (js)
```

Un **DTO** (Data Transfer Object) es solo un "sobre" para llevar datos de un lugar a otro. Por ejemplo, `ResumenVenta` lleva los subtotales y el total de una venta desde el servicio hasta la pantalla de confirmación.

## 8. La base de datos

Se llama `minimarket_aj` y tiene cuatro tablas:

| Tabla | Qué guarda | Columnas |
|---|---|---|
| `Usuario` | Las cuentas de quienes usan el sistema | `idUsuario`, `nombreUsuario`, `contraseña`, `rol` (`PROPIETARIO` o `VENDEDOR`) |
| `Producto` | El catálogo | `idProducto`, `nombre` (incluye la presentación, por ejemplo "Arroz 1 kg"), `precio`, `cantidadDisponible` |
| `Venta` | Cada venta registrada | `idVenta`, `fechaHora`, `total`, `estado` (`REGISTRADA` o `ANULADA`), `fechaAnulacion`, `motivoAnulacion` |
| `DetalleVenta` | Qué productos tuvo cada venta | `idDetalleVenta`, `idVenta`, `idProducto`, `cantidad`, `subtotal` |

**Relación:** una venta tiene uno o varios detalles, y cada detalle apunta a un producto. Las columnas `idVenta` e `idProducto` de `DetalleVenta` son **claves foráneas**: obligan a que cada detalle pertenezca a una venta y a un producto que existen.

Los diagramas (modelo conceptual, lógico y físico) están en la carpeta `diagrams` y en el informe (Figuras 13, 14 y 15).

## 9. Reglas importantes del negocio

Estas reglas están programadas en la capa de servicios y explican decisiones que no son obvias a simple vista:

1. **El precio siempre sale de la base de datos.** Aunque alguien modifique la página en su navegador, el servidor vuelve a calcular el total con el precio guardado. Así nadie puede cobrar un monto distinto.
2. **Una venta y su descuento de stock ocurren juntos o no ocurren.** Se usa una **transacción** (`@Transactional`): un grupo de operaciones que la base de datos trata como una sola. Si un producto se queda sin stock en medio del registro, no se guarda la venta **ni** se descuenta nada. Ejemplo: si dos vendedores intentan vender a la vez las últimas unidades de aceite, solo la primera venta se registra y la segunda recibe un aviso.
3. **El stock nunca queda en negativo.** El descuento solo se hace si hay unidades suficientes.
4. **Solo se puede anular la última venta registrada.** Así las ventas anteriores no se modifican y el historial es confiable. Al anularla, sus unidades vuelven al stock y quedan guardados la fecha y el motivo (máximo 200 caracteres). La venta no se borra: aparece en el historial marcada como "Anulada".
5. **Los permisos se revisan en el servidor.** Ocultar un botón no basta: si un vendedor escribe a mano la dirección de una función del propietario, el servidor responde "Acceso no permitido" (código 403). Esto lo hacen `AutenticacionInterceptor` y `PropietarioInterceptor`, configurados en `SeguridadConfig`.
6. **Límites de los datos:** el precio debe ser mayor que 0, con hasta 2 decimales y como máximo 99 999 999.99. Las unidades que llegan deben ser mayores que 0; la cantidad contada puede ser 0; ninguna cantidad puede superar 999 999 unidades.

## 10. Accesibilidad (personas con discapacidad)

El sistema sigue las pautas internacionales WCAG 2.1:

- Letra grande (18 px) y alto contraste entre el texto y el fondo (mínimo medido 6.79:1; se exige 4.5:1).
- Botones y campos de al menos 52 px de alto, fáciles de tocar.
- Cada campo tiene su etiqueta, para que los **lectores de pantalla** (programas que leen en voz alta lo que hay en la pantalla, usados por personas ciegas) los anuncien bien.
- Todo se puede usar solo con el teclado, con un borde amarillo que muestra dónde está el cursor.
- Los errores se escriben con palabras ("Error: …"), no solo con el color rojo.

## 11. Pruebas realizadas

Se ejecutaron 29 pruebas sobre el aplicativo en funcionamiento (el detalle está en el informe, sección 3.9.3): 20 funcionales (cada caso de uso con sus caminos normales y alternativos), 4 de seguridad, 1 de uso en celular y 4 de accesibilidad. Todas salieron conformes.

El proyecto **no** tiene pruebas automáticas, salvo la que viene por defecto (`MinimarketApplicationTests`), que solo comprueba que el programa arranca.

## 12. Limitaciones conocidas

Lo que hoy **no** hace el sistema y conviene saber antes de mejorarlo:

- **No hay pantallas para registrar productos nuevos ni usuarios.** Se cargan directamente en MySQL (por ejemplo, con MySQL Workbench).
- **Las contraseñas se guardan tal cual (sin cifrar).** Para usarlo en la tienda real sería recomendable guardarlas cifradas (por ejemplo, con BCrypt).
- **La venta no guarda qué vendedor la registró**, y la anulación no guarda quién la hizo (solo el propietario puede anular).
- **El historial muestra todas las ventas sin filtros** (por fecha o producto) ni paginación; con muchos meses de ventas, la página se hará larga.
- **Solo se puede anular la última venta**, por decisión del negocio.
- No hay copias de seguridad automáticas de la base de datos.

## 13. Cómo agregar una función nueva

Para mantener el orden, cualquier mejora debería seguir el mismo recorrido por las capas. Por ejemplo, para agregar "Registrar producto nuevo":

1. **DAO:** agregar el método a la interfaz `ProductoDAO` (por ejemplo, `insertar(Producto)`) e implementarlo en `ProductoDAOImpl` con su SQL.
2. **Servicio:** agregar en `ProductoServicio` la regla de negocio (validar que el nombre no esté vacío, que el precio sea válido, etc.). Si algo falla, lanzar una excepción del paquete `excepcion` con un mensaje claro.
3. **Controlador:** agregar en `ProductoControlador` los métodos que muestran el formulario (`@GetMapping`) y lo reciben (`@PostMapping`).
4. **Vista:** crear la página en `templates`, reutilizando la plantilla común (`fragmentos/plantilla.html`) para la cabecera, el menú y los mensajes. Usar etiquetas en cada campo (accesibilidad).
5. **Seguridad:** si la función es solo del propietario, agregar su dirección en `RUTAS_PROPIETARIO` de `SeguridadConfig`.
6. **Base de datos:** si se necesitan columnas o tablas nuevas, actualizar `code/scripts/minimarket_aj.sql`.
7. **Documentación:** actualizar este archivo, el informe y los diagramas.

## 14. Documentos relacionados

- `docs/proyecto-final/ProyectoFinal_Minimarket.docx`: informe completo del proyecto (análisis, diseño, pruebas).
- `docs/proyecto-final/SustentaciónTI_Minimarket.pptx` y `dialogo.txt`: presentación y guion de la sustentación.
- `diagrams/editables`: diagramas editables en draw.io y el modelo de datos en formato DBML (para dbdiagram.io).
- `diagrams/png`: los mismos diagramas exportados como imagen.
