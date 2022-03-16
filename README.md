## Patrón Service Registry/Service Discovery

### Eureka Server
Carpeta del repo: **/eureka-server**

Pasos de configuración:
1. Agregar las dependencias:
   - **spring-cloud-starter-netflix-eureka-server**
   - spring-boot-starter-web (Opcional)
   - spring-boot-starter-actuator (Opcional)

2. Anotar la clase principal con **@EnableEurekaServer**
3. Configurar el servidor mediante las propiedades (application.properties o application.yaml según preferencias)
   En este caso le indicamos al Eureka Server, que no actúe como cliente al mismo tiempo. Además configuramos el puerto donde se ejecutará.
   
   application.yml
> ```
> server:
> port: 8888
>
> eureka:
>  client:
>    register-with-eureka: false
>    fetch-registry: false   
> ```

4. **Ejecútalo y Listo!** Ahora puedes acceder al Dashboard en http://localhost:8888/