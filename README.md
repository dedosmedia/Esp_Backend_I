## Patrón Service Registry/Service Discovery

### Eureka Client
Carpeta del repo: **/eureka-client**

Pasos de configuración:
1. Agregar las dependencias:
   - **spring-cloud-starter-netflix-eureka-client**
   - spring-boot-starter-web (Recomendado)
   - spring-boot-starter-actuator (Recomendado)
2. Configurar nuestra aplicación mediante propiedades (application.properties o application.yaml según preferencias) para que pueda registrarse en el Eureka Server.
   En este caso le indicamos la dirección donde conectar el Eureka Server. Además configuramos el puerto donde se ejecutará y el nombre que tendrá este microservicio al registrarse.
   
   application.yml
> ```
> server:
> port: 9000
> 
> spring:
>   application:
>     name: eureka-client
> 
> eureka:
>   client:
>     service-url:
>       defaultZone: http://localhost:8888/eureka/
> ```

3. **Ejecútalo** (Debe estar corriendo Eureka Server) **y Listo!** Ahora puedes verificar en el Dashboard de Eureka Server como se registra tu MS en http://localhost:8888/