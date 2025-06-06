package com.frenadol.goalify.Security; // Asegúrate que el paquete sea correcto para tu proyecto

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(MvcConfig.class);

    // Esta propiedad DEBE COINCIDIR con la que usas en ArticuloTiendaService
    // y debe estar definida en tu application.properties
    @Value("${app.upload.dir:${user.home}/goalify_uploads/market-items}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // La URL base que usará el frontend para pedir las imágenes.
        // Debe coincidir con la parte de la URL después de "localhost:8080"
        // en tu `imagenPreviewUrl` (que es "/uploads/market-items/").
        String resourceHandlerPath = "/uploads/market-items/**";

        // La ubicación física en el servidor donde están guardadas las imágenes.
        Path resolvedUploadPath = Paths.get(this.uploadDir).toAbsolutePath().normalize();
        String resourceLocations = "file:" + resolvedUploadPath.toString() + "/";

        logger.info("Configurando ResourceHandler para servir imágenes estáticas:");
        logger.info("Peticiones a '{}' se servirán desde '{}'", resourceHandlerPath, resourceLocations);

        registry.addResourceHandler(resourceHandlerPath)
                .addResourceLocations(resourceLocations);

        // Si tienes otras carpetas de recursos estáticos (como 'assets' si no están en 'static' o 'public' por defecto)
        // podrías añadirlas aquí también. Por ejemplo:
        // registry.addResourceHandler("/assets/**")
        //         .addResourceLocations("classpath:/static/assets/"); // o "file:/ruta/a/tus/assets/"
    }
}