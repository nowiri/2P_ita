import com.google.gson.Gson;
import encapsulaciones.DataWS;
import encapsulaciones.Formulario;
import encapsulaciones.Ubicacion;
import encapsulaciones.Usuario;
import io.javalin.Javalin;
import io.javalin.plugin.rendering.JavalinRenderer;
import io.javalin.plugin.rendering.template.JavalinThymeleaf;
import services.DBStart;
import services.FormularioServices;
import services.UbicacionServices;
import services.UsuarioServices;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        //JAVALIN INIT
        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("/public"); //STATIC FILES -> /resources/public
        }).start(7000);

        //REGISTER THYMELEAF IN JAVALIN
        JavalinRenderer.register(JavalinThymeleaf.INSTANCE, ".html");

        //DATABASE INIT
        DBStart.getInstancia().init();

        //DEFAULT USER
        Usuario admin = new Usuario("admin","admin","Administrador", "Admin");
        UsuarioServices.getInstancia().editar(admin);

        /**
         * LOGIN !
         */

        app.get("/login", ctx -> {
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("msg","");
            ctx.render("public/login/index.html", modelo);
        });

        app.post("/login", ctx -> {

            String user = ctx.formParam("username");
            String pass = ctx.formParam("pass");

            if(UsuarioServices.getInstancia().verifyUser(pass,user) == null){
                Map<String, Object> modelo = new HashMap<>();
                modelo.put("msg","Combinación de usuario y contraseña erroneo! Favor verificar.");

                ctx.render("public/login/index.html", modelo);
            }else{
                Usuario logged = UsuarioServices.getInstancia().find(user);
                ctx.cookie("loggedUser", logged.getUsername());
                ctx.redirect("/form/index.html");
            }

        });

        app.get("/formulario", ctx -> {
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("usuario", ctx.sessionAttribute("loggedUser"));
            ctx.render("public/form/index.html");
        });

        app.post("/newFormulario", ctx -> {
            String nombre, apellido, provincia, nivelAcad, longitud, latitud;
            Usuario usuario = UsuarioServices.getInstancia().find(ctx.cookie("loggedUser"));

            longitud = ctx.formParam("longitud");
            latitud = ctx.formParam("latitud");
            nombre = ctx.formParam("first_name");
            apellido = ctx.formParam("last_name");
            provincia = ctx.formParam("subject");
            nivelAcad = ctx.formParam("nivelacad");

            Formulario form = new Formulario((nombre+" "+apellido),provincia,nivelAcad,usuario);
            FormularioServices.getInstancia().crear(form);

            Ubicacion ubicacion = new Ubicacion(longitud,latitud,form);
            UbicacionServices.getInstancia().crear(ubicacion);

            ctx.redirect("/form/index.html");

        });

        app.ws("/sincronizarForms", ws -> {

            ws.onConnect(ctx -> {
                System.out.println("Conexión Iniciada - "+ctx.getSessionId());
            });

            ws.onMessage(ctx -> {
                System.out.println("Mensaje Recibido de "+ctx.getSessionId()+" ====== ");
                System.out.println("Mensaje: "+ctx.message());
                System.out.println("================================");

                Gson g = new Gson();
                DataWS dws = g.fromJson(ctx.message(), DataWS.class);

                String nombre = dws.getNombre()+" "+dws.getApellido();
                String latitud = dws.getLatitud();
                String longitud = dws.getLongitud();
                String provincia = dws.getProvincia();
                String nivelacad = dws.getNivelacad();
                String usuario = dws.getUsuario();

                Usuario Usuario = UsuarioServices.getInstancia().find(usuario);
                Formulario form = new Formulario(nombre,provincia,nivelacad,Usuario);
                FormularioServices.getInstancia().crear(form);

                Ubicacion ubicacion = new Ubicacion(longitud,latitud,form);
                UbicacionServices.getInstancia().crear(ubicacion);

            });

            ws.onBinaryMessage(ctx -> {
                System.out.println("Mensaje Recibido Binario "+ctx.getSessionId()+" ====== ");
                System.out.println("Mensaje: "+ctx.data().length);
                System.out.println("================================");
            });

            ws.onClose(ctx -> {
                System.out.println("Conexión Cerrada - "+ctx.getSessionId());
            });

            ws.onError(ctx -> {
                System.out.println("Ocurrió un error en el WS");
            });
        });

        app.get("/mapas", ctx -> {
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("klk", "uaaaaa!!");
            ctx.render("public/mapas/index.html", modelo);
        });

    }

}
