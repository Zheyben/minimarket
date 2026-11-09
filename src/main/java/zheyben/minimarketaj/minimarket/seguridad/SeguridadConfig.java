package zheyben.minimarketaj.minimarket.seguridad;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Indica qué direcciones necesitan sesión iniciada y cuáles son exclusivas del propietario
@Configuration
public class SeguridadConfig implements WebMvcConfigurer {

    // Direcciones que se pueden abrir sin haber iniciado sesión.
    // Incluye los estilos y scripts, que la pantalla de inicio de sesión también necesita.
    private static final String[] RUTAS_PUBLICAS = {"/login", "/error", "/css/**", "/js/**"};

    // Direcciones exclusivas del propietario (CUS03, CUS06, CUS07 y CUS08)
    private static final String[] RUTAS_PROPIETARIO = {
            "/productos/*/precio",
            "/productos/*/cantidad", "/productos/*/cantidad/*",
            "/ventas/historial",
            "/ventas/*/anular"
    };

    private final AutenticacionInterceptor autenticacionInterceptor;
    private final PropietarioInterceptor propietarioInterceptor;

    public SeguridadConfig(AutenticacionInterceptor autenticacionInterceptor,
                           PropietarioInterceptor propietarioInterceptor) {
        this.autenticacionInterceptor = autenticacionInterceptor;
        this.propietarioInterceptor = propietarioInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registro) {
        registrarControlDeSesion(registro);
        registrarControlDePropietario(registro);
    }

    // Primero se comprueba que haya sesión; después, el rol
    private void registrarControlDeSesion(InterceptorRegistry registro) {
        registro.addInterceptor(autenticacionInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(RUTAS_PUBLICAS)
                .order(1);
    }

    private void registrarControlDePropietario(InterceptorRegistry registro) {
        registro.addInterceptor(propietarioInterceptor)
                .addPathPatterns(RUTAS_PROPIETARIO)
                .order(2);
    }
}
