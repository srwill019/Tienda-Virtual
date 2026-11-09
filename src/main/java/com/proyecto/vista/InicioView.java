package com.proyecto.vista;

import com.proyecto.controlador.ContextoApp;
import com.proyecto.modelo.Usuario;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/** Panel de inicio que muestra un mensaje segun el rol del usuario. */
public class InicioView {
    private final VBox contenido = new VBox(20);

    public InicioView(ContextoApp contexto) {
        Usuario usuario = contexto.getUsuarioActual();
        String descripcion = usuario.esAdministrador()
                ? "Desde aqui administras los productos y atiendes los pedidos."
                : "Desde aqui consultas los productos y preparas tus pedidos.";

        HBox datos = new HBox(15,
                crearDato("Rol", usuario.getRol().toString()),
                crearDato("Productos en catalogo", String.valueOf(contexto.getCatalogo().total())));
        contenido.setPadding(new Insets(10));
        contenido.getChildren().addAll(
                Estilos.titulo("Bienvenido, " + usuario.getNombreUsuario()),
                Estilos.subtitulo(descripcion),
                datos);
    }

    public Node getContenido() {
        return contenido;
    }

    private VBox crearDato(String etiqueta, String valor) {
        Label textoEtiqueta = Estilos.subtitulo(etiqueta);
        Label textoValor = new Label(valor);
        textoValor.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");
        VBox tarjeta = Estilos.tarjeta(textoEtiqueta, textoValor);
        tarjeta.setPrefWidth(230);
        return tarjeta;
    }
}
