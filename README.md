___
## Patrón de diseño: Central Configuration
___
### Config Server
Carpeta del repo: **/config-server**
Descripción: Es un servicio que se encargará de centralizar las configuraciones (application.yaml o application.properties) de  otros microservicios, y las servirá mediante endpoints (http://config-server/nombre-servicio/profile)


Pasos de configuración:
1. Agregar las dependencias:
   - **spring-cloud-config-server**
   - spring-boot-starter-actuator (Recomendado)

2. Anotar la clase principal con **@EnableConfigServer**
3. Configurar el servidor mediante el archivo de configuración local (application.properties o application.yaml según preferencias)
   En este caso le indicamos al Config Server desde que origen va a ir a buscar las configuraciones (spring.cluod.config.server.git).

   application.yml  (configuración para trabajar con github)
> ```
> spring:
>   application:
>     name: config-server
>   cloud:
>     config:
>       server:
>         git:
>           uri: https://github.com/dedosmedia/spring-cloud-config-server-configuration
>
o alternativamente este  application.yml para configuración desde disco
> ```
> spring:
>   application:
>     name: config-server
>   profile:
>     active: native
> 
>   cloud:
>     config:
>       native:
>         searchLocations: classpath:/config    # Subcarpeta dentro de resources
> ```
4. En el servidor de configuraciones crearemos un .yml por cada servicio que queramos configurar y debe tener el mismo nombre que tenga spring.application.name en cada microservicio
   **Nota:** Nunca agregar spring.config.import en estos .yml, esta parte solo se pone en el application.yml local de cada microservicio.
5. **Ejecútalo y Listo!** El servidor de configuraciones debería estar activo y puede servir configuraciones para otros MS.


### Config Client
Carpeta del repo: **/config-client**
Descripción: Cualquier servicio que quiera obtener sus configuraciones desde el servidor central debe implementar lo descrito en los siguientes pasos

Pasos de configuración:
1. Agregar las dependencias:
   - **spring-cloud-starter-config**
   - spring-boot-starter-actuator (Recomendado)
   
2. Configurar el cliente mediante las propiedades (application.properties o application.yaml según preferencias) para indicarle de dónde obtener las configuraciones.
   En este caso le indicamos al Config Client que las tome desde el servidor de configuraciones pasandole su url (spring.config.import). Agregamos también el nombre del microservicio, este último es muy importante porque así mismo es el nombre del archivo yml que intentará obtener del servidor de configuraciones. La palabra optional: permite que el servidor levante a pesar de no llegar a encontrar el servidor de configuraciones (en ese caso se configurará unicamente con la configuración local o valroes default).
   
   application.yml
> ```
> spring:
>   application:
>     name: config-client
>   config:
>     import: optional:configserver:http://localhost:8888
> ```
3. **Ejecútalo** (Debe estar corriendo el Config Server primero) **y Listo!** El cliente debería estar corriendo y haber tomado la configuración del yml proporcionado por el servidor de configuraciones.

___
## Patrón de diseño: Service Registry/Service Discovery
___

### Eureka Server
Carpeta del repo: **/eureka-server**

Descripción: Es un servicio que se encargará de mantener un registro de todos los microservicios de nuestro ecosistema, así mismo permitirá ser consultado para el descubrimiento de servicios por parte de aquellos MS que lo requieran consultar.

Pasos de configuración:
1. Agregar las dependencias:
   - **spring-cloud-starter-netflix-eureka-server**
   - **spring-cloud-starter-config** (Opcional, si este MS se va a configurar trayendo la configuración del servidor de configuraciones)
   - spring-boot-starter-actuator (Recomendado)
   
2. Anotar la clase principal con **@EnableEurekaServer**
3. Configurar el servidor mediante las propiedades (application.properties o application.yaml según preferencias)
   En este caso le indicamos al Eureka Server, que no actúe como cliente al mismo tiempo. Además configuramos el puerto donde se ejecutará.
   
   application.yml
> ```
> spring:
>   application:
>     name: service-registry
> server:
> port: 8761
>
> eureka:
>  client:
>    register-with-eureka: false
>    fetch-registry: false   
>    service-url:
>      defaultZone: http://localhost:8761/eureka
>   server:
>     enableSelfPreservation: false # No se debe hacer en producción
>   instance:
>     preferIpAddress: true
> ```

4. **Ejecútalo y Listo!** Ahora puedes acceder al Dashboard en http://localhost:8761/


### Eureka Client
Carpeta del repo: **/eureka-client**
Descripción: Cualquier microservicio del ecosistema que quiera ser registrado en Eureka, para su posterior descubrimiento, debería implementar lo siguiente:

Pasos de configuración:
1. Agregar las dependencias:
   - **spring-cloud-starter-netflix-eureka-client**
   - **spring-cloud-starter-config** (Opcional, si este MS se va a configurar trayendo la configuración del servidor de configuraciones)
   - spring-boot-starter-actuator (Recomendado)
      
2. Agregar la anotación a nuestra clase principal **@EnableDiscoveryClient** que permite descubrir los clientes sin importar que implementación de Service Registry se esté ejecutando (Consult, Zookeeper, Eureka) (Hay otro equivalente que es **@EnableEurekaClient** pero esta solo funcionaría para descubrir servicios desde Eureka)
3. Configurar nuestra aplicación mediante propiedades (application.properties o application.yaml según preferencias) para que pueda registrarse en el Eureka Server.
   En este caso le indicamos la dirección donde conectar el Eureka Server. Además configuramos el puerto donde se ejecutará y el nombre que tendrá este microservicio al registrarse.
   
   application.yml
> ```
> server:
>   port: 9000
> 
> spring:
>   application:
>     name: eureka-client
> 
> eureka:
>   client:
>     register-with-eureka: true
>     fetch-registry: true
>     service-url:
>       defaultZone: http://localhost:8761/eureka/
> ```

3. **Ejecútalo** (Debe estar corriendo Eureka Server) **y Listo!** Ahora puedes verificar en el Dashboard de Eureka Server como se registra tu MS en http://localhost:8761/

___
## Comunicación entre microservicios (Spring Cloud OpenFeign)
___

### Feign Client
Carpeta del repo: **/feign-client**
Descripción: Se usa para permitir la comunicación directa entre microservicios, es decir que puedan consumir la API que expongan. 

Pasos de configuración (En el microservicio que va a consumir la API):
1. Agregar las dependencias:
   - **spring-cloud-starter-openfeign**
   - **spring-cloud-starter-netflix-eureka-client** (Necesario si queremos invocar otros microservicios solo por su nombre, sin saber IP ni puerto)
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
>   // ProductService.java
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

5. Crear un controller ProductController donde se inyecte el ProductService. Ese controller expone un endpoint /mymessage que internamente usa el cliente de feign para consumir un recurso de otro microservicio.
2. **Listo!** Ahora este microservicio ya puede consumir el endpoint del otro microservicio del ecosistema sin conocer si quiera su ubicación, unicamente necesitamos el nombre del MS.

___
### Balanceador de carga desde el cliente
___

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

___
## Patrón de diseño: Edge Server (Spring Cloud Gateway)
___

### API Gateway
Carpeta del repo: **/api-gateway**

Pasos de configuración:
1. Agregar las dependencias:
   - **spring-cloud-starter-gateway**
   - **spring-cloud-starter-netflix-eureka-client**  (Para service discovery)
   - **spring-cloud-starter-config**  (Para ser un config client)
   
   Nota: **Nunca** importar la dependencia spring-boot-starter-web en Gateway. Es incorrecto, Gateway se hizo con Spring webflux.

2. Configurar reglas de navegabilidad
  application.yml
> ```
> server:
>   port: 8080
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
Pueden ser filtros ya provistos por Spring o creados por nosotros mismos. En el application.yaml anterior se agregaron filtros de Spring a la ruta /users/. Hay filtros PreFilter y PostFilter. Un PreFilter filtra la request y un PostFilter filtra la response. Pueden haber filtros globales **default-filters:** que se aplican a todas las llamadas al API y no unicamente a una ruta particular.
4. Crear filtros personalizados si se desea.
En este caso se hacen clases que hereden de **AbstractGatewayFilterFactory** y sobreescribir el método **public GatewayFilter apply(Config config)**

___
## Spring Security
___

La seguridad se puede implementar a nivel del microservicio o directamente desde el API Gateway, aquí se explicará seguridad desde el Gateway mediante autenticación por medio de oAuth2.
Pasos de configuración:
1. Agregar las dependencias (en el gateway):
   - **spring-boot-starter-oauth2-client** (Para autenticación mediante oauth)
2. Configurar un default-filter en el application.yml.  Será un **TokenRelay** que se encargará de pasar el Header de Auhorization, desde el Gateway y hacía los microservicios, para que ellos puedan usar el JWT para verificarlo contra el Authentication Server de oAuth2
> ```
> spring:
>   cloud:
>     gateway:
>       default-filters: 
>         - TokenRelay
> ```
3. Configurar el cliente de oAuth en application.yml
> ```
> security:
>     oauth2:
>       client:
>         registration:
>           google:
>             client-id: [CLIENT ID GENERADO EN EL AUTH PROVIDER]
>             client-secret: [SECRET GENERADO EN EL AUTH PROVIDER]
>             scope: openid,profile,email
>             redirect-uri: http://localhost:8080/login/oauth2/code/google
>         provider:
>           google:
>             issuer-uri: https://accounts.google.com
> ```
4. Crear una clase de configuración en el Gateway, donde se configure todo el tema de seguridad, que rutas no necesitan estar autenticadas, cuales sí, y demás configuraciones del security. Se debe crear un @Bean.
En este caso habilitamos que se pueda hacer login mediante oAuth2, que se pueda hacer logout, que cualquier petición deba ser autenticada, y se deshabilita cors.
(Notese que en Gateway se habla de Exchanges en lugar de Requests, Gateway usa Spring Webflux y no Spring Web, por lo tanto la configuración es un poco diferente a la de asgurar un microservicio sin gateway)
> ```
> @Configuration
> public class SecurityConfiguration {
>     @Bean
>     SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, ReactiveClientRegistrationRepository client) throws Exception {
>         http.oauth2Login();
>         http.logout(logoutSpec -> logoutSpec.logoutSuccessHandler(
>                 new OidcClientInitiatedServerLogoutSuccessHandler(client)
>         ));
>         http.authorizeExchange().anyExchange().authenticated();
>         http.cors().disable();
>         return http.build();
>     }
> }
> ```
5. En el microservicio que recibe la petición del Gateway.
   Se deben agregar las siguientes dependencias:
   **spring-boot-starter-oauth2-client**
   **spring-security-oauth2-jose**
   **spring-boot-starter-oauth2-resource-server**
   **spring-boot-starter-security**
También se debe configurar el application.yml indicandole que éste actuará como un servidor de recursos y en que provider puede verificar el JWT.
> ```
>  spring:
>    security:
>     oauth2:
>       resourceserver:
>         jwt:
>           issuer-uri: https://accounts.google.com
> ```
6. Por último es necesario que este microservicio configure la seguridad, con una clase de configuración, indicandole que todas las peticiones deben estar autenticadas y que funcionará como un ResourceServer con JWT.
> ```@EnableWebSecurity
> public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
>     @Override
>     protected void configure(HttpSecurity http) throws Exception {
>         http.
>                 authorizeRequests()
>                 .anyRequest().authenticated()
>                 .and()
>                 .oauth2ResourceServer()
>                 .jwt();
>     }
> }
> ```

7. **Listo!** Con esta configuración el Gateway ya debería estar enviando una Header hacia los microservicios con el JWT de Authorization.
**Nota**: En el caso de google, la configuración es un poco más compleja y requiere de otras clases adicionales. Por simplicidad no se muestra aquí.

7. En caso que el MS que recibe la petición desde Gateway, requiera comunicarse con otro MS usando Feign, será necesario también enviarle ese JWT, para lo cual sería necesario agregar una configuración para el Feign. Este clase intercepta la petición, extrae el Header de Authorization y lo inyecta en la petición que hace el Feign al otro MS.
> ```
> 
> public class FeignConfiguration {
>     private static final Pattern BEARER_TOKEN_HEADER_PATTERN = Pattern.compile("^Bearer (?<token>[a-zA-Z0-9-._~+/]+=*)$", Pattern.CASE_INSENSITIVE);
>     @Bean
>     public RequestInterceptor requestInterceptor() {
>         return requestTemplate -> {
>             final String authorization = HttpHeaders.AUTHORIZATION;
>             ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
>             if (Objects.nonNull(requestAttributes)) {
>                 String authorizationHeader = requestAttributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
>                 Matcher matcher = BEARER_TOKEN_HEADER_PATTERN.matcher(authorizationHeader);
>                 if (matcher.matches()) {
>                     //  Clear token Head   Avoid contagion
>                     requestTemplate.header(authorization);
>                     requestTemplate.header(authorization, authorizationHeader);
>                 }
>             }
>         };
>     }
> }
> ```
La configuración de la interface de Feign quedaría así:
> ```
> 
> @FeignClient( name = "serie-service", configuration = {FeignConfiguration.class })
> public interface SerieRepository {
>     /*
>     El @PathVariable fue obligatorio, en caso contrario el Feign me estaba pasando la petición GET como POST
>     al otro microservicio y rompía..
>      */
>     @GetMapping("/series/{genre}")
>     ResponseEntity<List<Serie>> findByGenre(@PathVariable String genre);
> }
> ```

___
## Patrón de diseño: Circuit Breaker (Resielience4J)
___
### Módulo Circuit Breaker / Retry
Carpeta del repo: **/feign-client**

Pasos de configuración:
1. Agregar las dependencias:
   - **spring-cloud-starter-circuitbreaker-resilience4j**  (Para circuit breaker)
2. Configurar instancias del circuit breaker en el 
  application.yml, y también las instancias del Retry
> ```
> resilience4j
>   circuitbreaker:
>     instances:
>        backendA:
>          sliding-window-type: COUNT_BASED
>          sliding-window-size: 5
>          failure-rate-threshold: 50
>          wait-duration-in-open-state: 15s
>          permitted-number-of-calls-in-half-open-state: 3
>          register-health-indicator: true
>          allow-health-indicator-to-fail: false
>          automatic-transition-from-open-to-half-open-enabled: true
>   retry:
>     instances:
>       backendA:
>         maxAttempts: 3
>         waitDuration: 10000
>         retryExceptions:
>           - feign.FeignException$InternalServerError
> ```
3. Agregar la anotación @CircuitBreaker y @Retry en el método que hace uso del feign para la llamada a otro microservicio.
> ```   
> @CircuitBreaker( name = 'backendA', fallbackMethod = 'metodoFallback')
> @Retry ( name = 'backendA')
> public function servicioA(String abc) {
>   ... // en este método es donde se usar el repository que invoca el API del otro servicio
> }
>
> private void metodoFallback(String abc, CallNotPermittedException ex) {
>   log.info("Se llama al fallback... aquí la lógica de fallback");
> }
>
> ```



