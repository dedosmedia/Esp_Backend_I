## Patrón de diseño: Service Registry/Service Discovery

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



## Patrón de diseño: Central Configuration

### Config Server
Carpeta del repo: **/config-server**

Pasos de configuración:
1. Agregar las dependencias:
   - **spring-cloud-config-server**
   - spring-boot-starter-web (Opcional)
   - spring-boot-starter-actuator (Recomendado)
   - spring-cloud-starter-netflix-eureka-client (Recomendado)

2. Anotar la clase principal con **@EnableConfigServer**
3. Configurar el servidor mediante las propiedades (application.properties o application.yaml según preferencias)
   En este caso le indicamos al Config Server desde que origen va a ir a buscar las configuraciones (en este caso github). Además configuramos el puerto donde se ejecutará.
   
   application.yml
> ```
> server:
> port: 8891
> 
> spring:
>   application:
>     name: config-server
>   cloud:
>     config:
>       server:
>         git:
>           uri: https://github.com/dedosmedia/spring-cloud-config-server-configuration
> ```

4. **Ejecútalo y Listo!** El servidor de configuraciones debería estar activo.



### Config Client
Carpeta del repo: **/config-client**

Pasos de configuración:
1. Agregar las dependencias:
   - **spring-cloud-starter-config**
   - spring-boot-starter-web (Opcional)
   - spring-boot-starter-actuator (Recomendado)
   - spring-cloud-starter-netflix-eureka-client (Recomendado)
2. Configurar el cliente mediante las propiedades (application.properties o application.yaml según preferencias) para indicarle de dónde obtener las configuraciones.
   En este caso le indicamos al Config Client que las tome desde el servidor de configuraciones, pasandole la url. No configuramos el puerto donde se ejecutará porque se configurará dinámicamente desde el yml.
   Agregamos también el nombre del microservicio, este último es muy importante porque así mismo es el nombre del archivo yml que intentará obtener del servidor de configuraciones.
   
   application.yml
> ```
> spring:
>   application:
>     name: config-client
>   config:
>     import: configserver:http://localhost:8891
> ```

3. Para probar que funcione creamos una clase */controller/TestController.java* anotada con @RestController y estamos sirviendo un enpdoint con @GetMapping("/message").  
Mediante la anotación @Value("${message}") podemos imprimir dicha variable y confirmar que es el valor configurado en el yml de Github.

4. **Ejecútalo** (Debe estar corriendo Config Server) **y Listo!** El cliente debería estar corriendo y haber tomado la configuración del yml como estaba en Github.


## Comunicación entre microservicios (Spring Cloud OpenFeign)

### Feign Client
Carpeta del repo: **/feign-client**

Pasos de configuración:
1. Agregar las dependencias:
   - **spring-cloud-starter-openfeign**
   - **spring-cloud-starter-netflix-eureka-client**
   - spring-boot-starter-web (Opcional)
   - spring-boot-starter-actuator (Recomendado)
2. Anotar la clase principal con **@EnableFeignClients**
3. Crear las interfaces de Feign, anotarlas con **@FeignClient(name='...')**
>   ```
>   @FeignClient(name='nombre-de-ms-a-consumir')
>   public interface ProductClient {
>      @RequestMapping(method = RequetsMethod.GET, value="/products")
>      List<ProductDTO> getProducts();
>   }
>   ```
4. Hacer uso del cliente de Feign inyectandolo donde necesitemos hacer el llamado al API.
>   ```
>   @Service
>   public class ProductService {
>
>      @AutoWired
>      private ProductClient productClientFeign;
>
>      public List<ProductDTO> fetchAllProducts(){
>         return productFeignClient.getProducts();
>      }
>   }
>   ```

5. Se ha creado un MessageService donde se inyecta el Cliente. Luego se ha creado un MessageController donde se inyecta el MessageService. Ese controller expone un endpoint /mymessage que internamente usa el cliente de feign para consumir el endpoint de otro microservicio (config-client en este caso)
6. **Listo!** Ahora este microservicio ya puede consumir el endpoint del otro microservicio del ecosistema sin conocer si quiera su ubicación, unicamente necesitamos el nombre del MS.
Puedes probar en: http://localhost:9501/mymmesage
