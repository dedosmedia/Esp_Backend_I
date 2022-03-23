## Patrón de diseño: Service Registry/Service Discovery

### Eureka Server
Carpeta del repo: **/eureka-server**

Pasos de configuración:
1. Agregar las dependencias:
   - **spring-cloud-starter-netflix-eureka-server**
   - **spring-cloud-starter-config** (Recomendado)
   - spring-boot-starter-web (Recomendado)
   - spring-boot-starter-actuator (Recomendado)
   

2. Anotar la clase principal con **@EnableEurekaServer**
3. Configurar el servidor mediante las propiedades (application.properties o application.yaml según preferencias)
   En este caso le indicamos al Eureka Server, que no actúe como cliente al mismo tiempo. Además configuramos el puerto donde se ejecutará.
   
   application.yml
> ```
> server:
> port: 8761
>
> eureka:
>  client:
>    register-with-eureka: false
>    fetch-registry: false   
> ```

4. **Ejecútalo y Listo!** Ahora puedes acceder al Dashboard en http://localhost:8761/


### Eureka Client
Carpeta del repo: **/eureka-client**

Pasos de configuración:
1. Agregar las dependencias:
   - **spring-cloud-starter-netflix-eureka-client**
   - spring-boot-starter-web (Recomendado)
   - spring-boot-starter-actuator (Recomendado)
2. Agregar la anotación a nuestra clase principal **@EnableDiscoveryClient** que permite descubrir los clientes sin importar que implementación de Service Registry haya (Consult, Zookeeper, Eureka) (Hay otro equivalente que es **@EnableEurekaClient** pero esta solo funcionaría para descubrir servicios desde Eureka)
3. Configurar nuestra aplicación mediante propiedades (application.properties o application.yaml según preferencias) para que pueda registrarse en el Eureka Server.
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
>       defaultZone: http://localhost:8761/eureka/
> ```

3. **Ejecútalo** (Debe estar corriendo Eureka Server) **y Listo!** Ahora puedes verificar en el Dashboard de Eureka Server como se registra tu MS en http://localhost:8761/



## Patrón de diseño: Central Configuration

### Config Server
Carpeta del repo: **/config-server**

Pasos de configuración:
1. Agregar las dependencias:
   - **spring-cloud-config-server**
   - spring-boot-starter-web (Recomendando)
   - spring-boot-starter-actuator (Recomendado)

2. Anotar la clase principal con **@EnableConfigServer**
3. Configurar el servidor mediante las propiedades (application.properties o application.yaml según preferencias)
   En este caso le indicamos al Config Server desde que origen va a ir a buscar las configuraciones (en este caso github). Además configuramos el puerto donde se ejecutará.

   application.yml  (configuración para trabajar congithub)
> ```
> server:
> port: 8888
> 
> spring:
>   application:
>     name: config-server
>   cloud:
>     config:
>       server:
>         git:
>           uri: https://github.com/dedosmedia/spring-cloud-config-server-configuration
>
> ```
    o usar application.yml (configuración extraida de disco)
> ```
> server:
> port: 8888
> 
> spring:
>   application:
>     name: config-server
>   cloud:
>     config:
>       native:
>         searchLocations: classpath:/config
> ```

1. **Ejecútalo y Listo!** El servidor de configuraciones debería estar activo.



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
>     import: configserver:http://localhost:8888
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


### Balanceador de carga desde el cliente
Carpeta del repo: **/feign-client**  (se agrega el load balancer dentro de este MS)

Pasos de configuración:
1. Agregar las dependencias:
   - **spring-cloud-starter-loadbalancer**
2.  Anotar la aplicación con **@EnableDiscoveryClient** para habilitar el descubrimiento de instancias de los servicios requeridos. (Debería también esar ya anotada con **@EnableFeignClients**)
3. Ya con esa configuración de los puntos anteriores la app Feign-Client podría consumir un endpoint de otro MS, y si ese MS tiene 2 o más instancias, cada petición que haga Feign-Client debería ser contestada por las diferentes instancias del otro MS. Por defecto Feign usará Round-Robin.
4. Para personalizar el modo de balancear las cargas creamos una clase /configuration/**CustomLoadBalancerConfiguration.java** 

> ```
> public class CustomLoadBalancerConfiguration {
>   @Bean
>   ReactorLoadBalancer<ServiceInstance> randomLoadBalancer(Environment environmbet, LoadBalancerClientFactory loadBalancerClientFactory) {
>     String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
>     return new RandomLoadBalancer(loadBlancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class),name);
>   }
> }
> ```
5. Anotar la interface del cliente de Feign con:
> ```
> @FeignClient(name="config-client")
> @LoadBalancerClient( name="..." configuration=CustomLoadBalancerConfiguration.class)
> public interface MessageClient { 
>   ...
> }
> ```
6. **Listo**, deberíamos tener un balanceador de carga desde el lado del cliente operando según CustomLoadBalancerConfiguration.java


## Patrón de diseño: Edge Server (Spring Cloud Gateway)

### API Gateway
Carpeta del repo: **/api-gateway**

Pasos de configuración:
1. Agregar las dependencias:
   - **spring-cloud-starter-gateway**
   - **spring-cloud-starter-netflix-eureka-client**
   - spring-boot-starter-web (Opcional)
2. Configurar reglas de navegabilidad
  application.yml
> ```
> server:
>   port: 8891
> 
>  spring:
>    cloud:
>      gateway:
>        default-filters: 
>          - LogFilter   # Un filtro que se aplica a todas las peticiones al gateway
>        routes:
>          - id: rutaId  # Identificador de la ruta
>            uri:  lb://nombre-ms  # o  http://localhost:8761, si no está balanceado
>            predicates: # Reglas para saber si la request se enruta por esta ruta
>            - Path=/product/**    # Si el path empieza por product, usa esta ruta
>          - id: ruta2Id
>            uri: lb://nombre-otro-ms
>            predicates:    
>            - Path=/users/**
>            filters:
>              - AddRequestHeader=nombre-de-header, valor de header   # PREFILTER
>              - AddResponseHeader=nombre-de-header, valor de header  # POSTFILTER
> ```
3. Configurar filtros en cada ruta (o globalmente). 
Pueden ser filtros ya provistos por Spring o creados por nosotros mismos. En el application.yaml anterior se agregaron filtros de Spring a la ruta /users/ (**filters:**). Hay filtros PreFilter y PostFilter. Un PreFilter filtra la request y un PostFilter filtra la response. Pueden haber filtros globales (**default-filters:**) que se aplican a todas las llamadas al API y no unicamente a una ruta particular.
4. Crear filtros personalizados si se desea.
En este caso se hacen clases que hereden de **AbstractGatewayFilterFactory** y sobreescribir el método **public GatewayFilter apply(Config config)**

