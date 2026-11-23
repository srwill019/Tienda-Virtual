# NEXUS - Tienda Virtual

NEXUS es una aplicacion de escritorio para administrar un catalogo de ropa y simular compras. El cliente puede buscar y filtrar productos, guardarlos en el carrito o favoritos, registrar sus datos de envio y crear un pedido. El administrador puede agregar, modificar y eliminar productos, actualizar existencias, atender pedidos y deshacer la ultima atencion.

## Requisitos y ejecucion

- Java 21
- Maven
- IntelliJ IDEA con el proyecto Maven importado

En IntelliJ, abre la ventana Maven y ejecuta `javafx:run` en **Plugins > javafx**. Tambien puedes abrir la configuracion de ejecucion **TiendaVirtual JavaFX**.

Desde una terminal ubicada en la raiz del proyecto ejecuta:

```bash
mvn javafx:run
```

## Usuarios de prueba

| Rol | Usuario | Contrasena |
|---|---|---|
| Administrador | `admin` | `admin123` |
| Cliente | `cliente` | `cliente123` |

Las cuentas de clientes registradas se guardan en `usuarios.txt`. Los pedidos se guardan en `pedidos.txt` y siguen disponibles en “Mis compras” al volver a iniciar la aplicación.

## Paquetes

- `com.proyecto.modelo`: productos, usuarios, pedidos, sus items, datos de compra, estados y metodos de pago.
- `com.proyecto.estructuras`: estructuras enlazadas propias y gestion del catalogo y de usuarios.
- `com.proyecto.controlador`: contexto compartido y coordinacion de catalogo y pedidos.
- `com.proyecto.vista`: ventanas y pantallas JavaFX, construidas desde codigo Java.

## Estructuras de datos

| Estructura | Uso en el codigo | Pantalla o proceso |
|---|---|---|
| Cola enlazada (`ColaEnlazada`) | Pedidos pendientes en `GestionPedidos` | El administrador ve la cola y atiende en orden de llegada |
| Pila enlazada (`PilaEnlazada`) | Historial de pedidos atendidos en `GestionPedidos`; navegacion anterior en `PrincipalView` | Historial y accion de deshacer; boton de volver |
| Lista simple enlazada (`ListaSimpleEnlazada`) | Carrito y favoritos en `ContextoApp`; items de cada pedido en `Pedido` | Catalogo, carrito, favoritos, resumen de compra y pedidos |
| Lista doble circular (`ListaDobleCircular`) | Catalogo de productos en `GestionCatalogo` | Catalogo de cliente y gestion del administrador |

## Limitaciones

- Catalogo, existencias, carrito y favoritos se guardan localmente en `productos.txt` y `preferencias_clientes.txt`. Los pedidos y sus estados se guardan en `pedidos.txt`.
- El historial registra pedidos atendidos y su deshacer; no registra cambios del catalogo.
- El pago es simulado. Solo se selecciona contra entrega o transferencia simulada; no se solicitan ni almacenan datos bancarios.
- El proceso de compra guarda los datos de contacto y envio junto al pedido en `pedidos.txt`. No hay servicio de envio ni confirmacion externa.
- Las imagenes de productos deben estar disponibles como recursos en `src/main/resources/imagenes`.
