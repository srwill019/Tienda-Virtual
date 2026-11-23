# Manual de usuario - NEXUS

NEXUS es una tienda de ropa para hombre. Permite explorar prendas, elegir una talla, hacer pedidos y consultar su estado. También incluye un acceso de administración para gestionar el catálogo y atender pedidos.

## 1. Iniciar la aplicación

Abre el proyecto en IntelliJ IDEA y ejecuta **TiendaVirtual JavaFX**. También puedes abrir la ventana **Maven** y ejecutar `javafx:run`.

Al iniciar, aparece la pantalla de acceso. Selecciona la pestaña **Cliente** o **Administrador** según el tipo de cuenta.

### Cuentas de prueba

| Tipo de cuenta | Usuario | Contraseña |
| --- | --- | --- |
| Cliente | `cliente` | `cliente123` |
| Administrador | `admin` | `admin123` |

Para crear una cuenta, selecciona **¿No tienes cuenta? Regístrate**. Escribe un nombre de usuario de al menos 3 caracteres, una contraseña de al menos 4 caracteres y repítela para confirmar. Las cuentas nuevas son de cliente.

## 2. Usar la tienda como cliente

Al ingresar, el menú lateral muestra **Catálogo**, **Carrito**, **Favoritos** y **Mis compras**. Selecciona una opción para abrir esa sección. El botón de flecha superior permite volver a la pantalla anterior.

### Explorar el catálogo

En **Catálogo** puedes buscar por nombre o identificador y filtrar por disponibilidad, precio mínimo y máximo, o mostrar solo tus favoritos. Usa **Limpiar filtros** para restablecerlos.

Cada tarjeta muestra la foto, el nombre y el precio de la prenda. Pulsa una tarjeta para abrir sus detalles, ver la imagen ampliada y leer la descripción. En esa ventana:

1. Elige la talla.
2. Pulsa **Agregar al carrito** para añadir la prenda con esa talla.
3. Si quieres guardar la prenda, pulsa **Agregar a favoritos**.

Las opciones disponibles son **S, M, L y XL** para camisetas y buzos; **28, 30, 32, 34 y 36** para jeans; y **Única** para gorras. La disponibilidad se controla con el stock total del producto, no con inventario separado por talla.

### Carrito y favoritos

En **Carrito** se muestran las prendas añadidas, su talla y su precio. Puedes quitar una prenda con **Quitar del carrito**. Pulsa **Continuar al proceso de compra** para revisar el resumen y completar los datos.

En **Favoritos** puedes revisar las prendas guardadas y quitarlas de esa lista. Guardar una prenda en favoritos no la añade al carrito.

### Finalizar una compra

Revisa el resumen, que incluye la foto, el nombre, la talla, la cantidad, el subtotal y el total. Completa los datos de contacto y envío, selecciona un método de pago y pulsa **Confirmar compra**.

Los campos requeridos son:

- Nombre completo.
- Documento, solo números.
- Teléfono, de 7 a 10 números.
- Correo electrónico con formato válido.
- Dirección, con al menos 5 caracteres.
- Ciudad.
- Método de pago: **Contra entrega** o **Transferencia simulada**.

Si el stock no alcanza, la aplicación muestra un mensaje y no registra el pedido. Cuando la compra se completa, aparece un recibo con el pedido, los datos de contacto y envío, las fotos de los productos, sus tallas, cantidades, subtotales y el total.

### Consultar compras anteriores

Abre **Mis compras** para ver los pedidos de la cuenta actual, del más reciente al más antiguo. Cada pedido muestra su estado, fecha, datos de envío y pago, productos, fotos, tallas y total. Si tienes varios pedidos, desplázate hacia abajo para verlos todos.

Los pedidos nuevos comienzan en estado **PENDIENTE**. El estado cambia a **ATENDIDO** cuando el administrador procesa el pedido.

## 3. Usar la aplicación como administrador

En la pantalla de acceso, selecciona **Administrador** e ingresa con una cuenta administradora. El menú ofrece **Gestionar catálogo**, **Agregar producto**, **Historial** y **Pedidos pendientes**.

### Gestionar el catálogo

En **Gestionar catálogo** puedes revisar las prendas, actualizar su stock, modificar sus datos o eliminarlas. Para eliminar un producto, confirma la acción en la ventana que aparece. Un producto asociado a pedidos no se puede eliminar.

### Agregar o modificar una prenda

En **Agregar producto**, completa el nombre, la descripción, el precio y el stock inicial. La imagen es opcional. Para elegir una foto del computador:

1. Pulsa **Elegir archivo**.
2. Selecciona una imagen PNG, JPG, JPEG o GIF.
3. Pulsa **Guardar producto**.

La aplicación copia la imagen al proyecto y la usa en el catálogo, el carrito, el recibo y los pedidos. Para modificar una prenda existente, abre **Gestionar catálogo** y pulsa **Modificar** en su tarjeta.

### Atender pedidos

En **Pedidos pendientes** se muestran los pedidos en orden de llegada. Pulsa **Atender siguiente** para procesar el primero. Si no hay pedidos, la lista aparece vacía. Si el stock ya no alcanza, el pedido permanece pendiente y se muestra un mensaje.

### Revisar el historial

En **Historial** aparecen los pedidos atendidos. Pulsa **Deshacer última operación** para devolver el pedido más reciente a la cola de pendientes y reintegrar sus unidades al stock.

## 4. Guardado de información

- Las cuentas de cliente registradas se conservan en `usuarios.txt`.
- Los pedidos y sus estados se conservan en `pedidos.txt` y vuelven a aparecer al iniciar la aplicación.
- El catálogo, los productos nuevos, sus cambios y el stock se conservan en `productos.txt`.
- El carrito y los favoritos se guardan por cuenta en `preferencias_clientes.txt`.
- Las fotos elegidas se copian a `src/main/resources/imagenes`.

Las compras realizadas antes de que se habilitara el guardado de pedidos no se pueden recuperar si no están en `pedidos.txt`. Los cambios de catálogo hechos antes de que se habilitara el guardado de productos tampoco se pueden recuperar si no están en `productos.txt`.

## 5. Alcance de la compra

La tienda registra pedidos y permite simular su atención. Los métodos de pago son demostrativos: no se procesa ningún pago, no se guardan datos bancarios y no se coordina un envío real.
