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