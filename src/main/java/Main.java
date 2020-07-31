import encapsulaciones.Usuario;
import io.javalin.Javalin;
import io.javalin.plugin.rendering.JavalinRenderer;
import io.javalin.plugin.rendering.template.JavalinThymeleaf;
import services.DBStart;
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
            ctx.render("/resources/login/index.html", modelo);
        });

        app.post("/login", ctx -> {

            String user = ctx.formParam("username");
            String pass = ctx.formParam("pass");

            if(UsuarioServices.getInstancia().verifyUser(pass,user) == null){
                Map<String, Object> modelo = new HashMap<>();
                modelo.put("msg","Combinación de usuario y contraseña erroneo! Favor verificar.");

                ctx.render("/resources/login/index.html", modelo);
            }else{
                //TODO: LOGIN SUCCESFUL

            }

        });



    }


}
