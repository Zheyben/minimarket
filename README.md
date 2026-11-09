# Minimarket A&J – Sistema web de ventas

Prototipo desarrollado para el Proyecto Final del curso **Análisis y diseño de sistemas de información** (Universidad Tecnológica del Perú). Es un sistema web para la bodega Minimarket A&J (Ate, Lima) que se usa desde el navegador del celular y permite:

- Iniciar sesión con el rol de **vendedor** o de **propietario**.
- Consultar el catálogo y buscar productos con su precio y cantidad disponible.
- Registrar ventas: calcula el total con los precios guardados y descuenta el inventario.
- Solo el propietario: cambiar precios, sumar la mercadería que llega o corregir cantidades, ver el historial de ventas y anular la última venta (queda registrada con su motivo).

Está organizado con los patrones **MVC, DAO y Capas** y se conecta a una base de datos **MySQL**.

## Requisitos

- Java (JDK) 21.
- MySQL Workbench 8.0 para ejecutar el script y crear la base de datos.

## Cómo ejecutarlo

**1. Crear la base de datos.** Ejecuta el script [`../scripts/minimarket_aj.sql`](../scripts/minimarket_aj.sql), que crea la base `minimarket_aj` con sus tablas, productos y usuarios de prueba.

- En MySQL Workbench: *File → Open SQL Script*, elige el archivo y pulsa el rayo (*Execute*).
- O desde CMD, en la carpeta `code/scripts` (si `mysql` está en el PATH): `mysql -u root -p < minimarket_aj.sql`

> Si la base ya existe (por ejemplo, creada con una versión anterior del script), elimínala antes con `DROP DATABASE minimarket_aj;` y vuelve a ejecutar el script.

**2. Configurar la conexión.** En `src/main/resources/application.properties`, cambia el usuario y la contraseña por los de tu MySQL:

```properties
spring.datasource.username=root
spring.datasource.password=tu_contraseña
```

**3. Iniciar el sistema.** Abre un cmd desde la carpeta y ejecutas:

```bash
# Windows (CMD o PowerShell)
.\mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```

También puedes abrir la carpeta en IntelliJ IDEA y ejecutar la clase `MinimarketApplication`.

**4. Abrir el sistema** en el navegador: <http://localhost:8080>

Para usarlo desde un celular, conéctalo a la misma red Wi-Fi que la computadora y abre `http://<IP-de-la-computadora>:8080`.

## Usuarios de prueba

| Usuario | Contraseña | Rol |
|---|---|---|
| Sebastián | propietario123 | Propietario |
| Joaquín | vendedor123 | Vendedor |

El usuario también se acepta sin tilde ni mayúscula (por ejemplo, `sebastian`).
