# Case Técnico: Integração com HubSpot
Este documento oferece uma explicação técnica detalhada sobre a resolução do teste 
técnico de integração com o HubSpot. Ele inclui a descrição do uso das bibliotecas, 
a documentação técnica e as instruções para a execução do projeto.
<br>
<br>
O projeto envolve o desenvolvimento de uma API REST em Java utilizando o framework Spring Boot. Essa API atua como um serviço integrado ao HubSpot e disponibiliza os seguintes endpoints obrigatórios:

1. Geração da Authorization URL:
   * Endpoint responsável por gerar e retornar a URL de autorização para iniciar o
   fluxo OAuth com o HubSpot.
2. Processamento do Callback OAuth:
   * Endpoint recebe o código de autorização fornecido pelo HubSpot e realiza a
   troca pelo token de acesso.

3. Criação de Contatos:
   * Endpoint que faz a criação de um Contato no CRM através da API. O endpoint
   deve respeitar as políticas de rate limit definidas pela API.
4. Recebimento de Webhook para Criação de Contatos:
   * Endpoint que escuta e processa eventos do tipo "contact.creation", enviados
   pelo webhook do HubSpot.

# Arquitetura do Projeto
O projeto é estruturado em pacotes específicos para cada funcionalidade, adotando uma abordagem que reflete o princípio "S" do conceito SOLID. Esse princípio visa delegar responsabilidades específicas a diferentes partes do sistema, promovendo maior coesão e manutenção.<br>
<br>
Estrutura de pacotes:

```
com.example.meetime_test_app  
├── annotation  
├── aspect  
├── builder  
├── config  
├── controller  
├── dto  
├── exception  
├── interceptor  
├── service  
└── utils  
```
## Annotation
Este pacote contém as anotações essenciais para o projeto. As anotações são utilizadas para definir comportamentos específicos no sistema por meio de metadados.
Neste projeto, foi desenvolvida uma anotação personalizada para permitir a configuração do rate limit no endpoint de criação de contatos.

````java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimited {
}
````

```java
@RateLimited
@ResponseStatus(HttpStatus.CREATED)
@PostMapping
public Mono<CreateContactResponse> createContact(@Valid @RequestBody CreateContactRequest createContactRequest) {
  return this.contactService.createContact(createContactRequest);
}
```

## Aspect
Este pacote contém os aspectos utilizados no projeto, baseados no conceito de Programação Orientada a Aspectos (AOP). 
Os aspectos possibilitam encapsular comportamentos que podem ser aplicados a diferentes partes do código, 
sem a necessidade de modificá-las diretamente. Neste projeto, foi desenvolvido um aspecto associado à anotação de rate limit, responsável por implementar a lógica por trás desse recurso.

```java
@Before("@annotation(rateLimited)")
    public void checkRateLimit(RateLimited rateLimited) throws Throwable {
        ConsumptionProbe probe = rateLimitBucket.tryConsumeAndReturnRemaining(1);

        if (!probe.isConsumed()) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,"Rate limit exceeded, try again later.");
        }
    }
```

A implementação da lógica de rate limit foi feita utilizando a biblioteca ***Bucket4j***. A utilização dessa biblioteca se deu pela facilidade de configuração e pela sua robustez, visando a escalabilidade do projeto. Ela permite o controle preciso sobre o número de requisições permitidas 
em um determinado período e oferece recursos adicionais como persistência utilizano cache com Redis e escalabilidade distribuída. <br>
<br>
O ***Bucket4j*** trabalha com a ideia de baldes e tokens. Resumidamente, Um balde contém um determinado número de tokens que podem ser consumidos. A cada requisição, é consumido um token do balde, e se o mesmo estiver vazio, a requisição é rejeitada. Dessa forma, é possível implementar a lógica de rate limit.

## Builder
Pacote dedicado à construção de corpos de requisição e resposta para a integração com a API do HubSpot.

```java
// Exemplo do request body de autenticação no fluxo OAuth2 com HubSpot

public static MultiValueMap<String, String>buildAuthenticate(String code, String clientId, String clientSecret, String redirectUri) {
  MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
  requestBody.add("grant_type", "authorization_code");
  requestBody.add("client_id", clientId);
  requestBody.add("client_secret", clientSecret);
  requestBody.add("redirect_uri", redirectUri);
  requestBody.add("code", code);

  return requestBody;
}
```

## Config
Pacote responsável pelas configurações dos recursos necessários para a construção do sistema.

### Bucket4j
A documentação da API do HubSpot informa que um usuário gratuito possui um limite de 110 requisições em um intervalo de 10 segundos. Com base nessa especificação, foi configurado o rate limit para o endpoint de criação de contatos.
```java
@Bean
public Bucket rateLimitBucket() {
   return Bucket.builder()
           .addLimit(limit -> limit.capacity(110).refillGreedy(
                   110, Duration.ofSeconds(10))
           )
           .build();
}
```

### Spring WebClient
O Spring WebClient é um cliente HTTP assíncrono que segue o conceito de programação reativa. Ele foi escolhido 
no lugar do antigo RestTemplate pelo fato de que o RestTemplate trabalha de forma síncrona, bloqueando a thread em execução e aguardando a resposta
da solicitação HTTP. Já o Spring WebClient, como funciona de forma assíncrona, não bloqueia a thread em execução e traz mais performance para o sistema, sendo uma alternativa mais escalável para integrações com APIs externas.

```java
@Bean
public WebClient webClient() {
  return WebClient.builder()
          .baseUrl(apiUrl)
          .filter(authHeaderFilter())
          .build();
}
```

```java
private ExchangeFilterFunction authHeaderFilter() {
  return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
      String accessToken = OAuth2TokenHolder.getToken();
      ClientRequest modifiedRequest = ClientRequest.from(clientRequest)
              .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
              .build();
      return Mono.just(modifiedRequest);
  });
}
```
A configuração foi feita a partir da URL da API do HubSpot. Também foi criado um filtro para interceptar a requisição para o HubSpot e incluir o token OAuth2 fornecido pelo usuário no Header Authorization.

### WebConfig
Configuração feita para incluir a validação do token OAuth2 na API do HubSpot ao receber uma requisição no endpoint de criação contatos.

```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
  registry.addInterceptor(new OAuth2TokenInterceptor(apiUrl))
          .addPathPatterns("/contact")
          .excludePathPatterns("/contact/webhook");
}
```
É importante destacar que o webhook criado para receber os eventos de "contact.creation" do HubSpot é excluído da validação de token do usuário.

## Controller
Pacote responsável pelos controladores do sistema, cuja função é expor rotas HTTP, receber as requisições, encaminhar as informações para a camada de serviços (casos de uso) e retornar as respostas correspondentes.

```java
// Exemplo de controlador do sistema

@GetMapping
public void startOAuthFlow(HttpServletResponse httpServletResponse) {
  httpServletResponse.setHeader(LOCATION, oAuthUrl);
  httpServletResponse.setStatus(302);
}
```

## DTO
Pacote responsável pelos DTOs (Data Transfer Objects), que são objetos usados para transferir dados entre sistemas ou camadas de uma aplicação.
```java
//Exemplo de DTO

@Getter
@Setter
public class CreateContactRequest {
    @NotBlank(message = "email must not be blank")
    private String email;

    @NotBlank(message = "firstName must not be blank")
    private String firstName;

    @NotBlank(message = "lastName must not be blank")
    private String lastName;
}
```
A biblioteca Lombok foi utilizada para reduzir a escrita de código boilerplate, como getters e setters.

## Exception
Pacote responsável pelo tratamento específico de exceções do sistema.
```java
@ResponseStatus(HttpStatus.BAD_REQUEST)
@ExceptionHandler(MethodArgumentNotValidException.class)
public Map<String, Object> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
  List<String> errors = new ArrayList<>();

  for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      errors.add(error.getDefaultMessage());
  }

  return ApiResponseErrorBuilder.buildValidationResponseError(errors, request);
}
```
Este é um exemplo de um ExceptionHandler do sistema, responsável por capturar e formatar de maneira clara as exceções relacionadas à validação do request body.

## Interceptor
Pacote responsável pelos interceptadores de requisição. Neste sistema, foi desenvolvido um interceptor (conforme mostrado anteriormente no **WebConfig**) para validar o token OAuth2 fornecido pelo usuário no cabeçalho Authorization, antes de prosseguir com a requisição.

```java
@Override
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
  String authorizationHeader = request.getHeader("Authorization");

  if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
      return false;
  }

  String accessToken = authorizationHeader.substring(7);

  if (!isTokenValid(accessToken)) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
      return false;
  }

  OAuth2TokenHolder.setToken(accessToken);
  return true;
}
```

```java
private boolean isTokenValid(String accessToken) {
  String userInfoUrl = "/oauth/v1/access-tokens/" + accessToken;

  try {
      TokenValidationResponse tokenResponse = webClient.get()
              .uri(userInfoUrl)
              .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
              .accept(MediaType.APPLICATION_JSON)
              .retrieve()
              .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(Throwable.class))
              .bodyToMono(TokenValidationResponse.class)
              .block();

      return tokenResponse != null && tokenResponse.getUserId() != null;
  } catch (Exception e) {
      return false;
  }
}
```
O primeiro método tem como objetivo extrair o token do cabeçalho Authorization e verificar 
sua validade. Para isso, ele realiza algumas verificações, como garantir que o cabeçalho 
seja nulo e que comece com o padrão "Bearer". Se o cabeçalho estiver corretamente formatado, 
o método prossegue chamando a validação do token na API do HubSpot. Caso o token seja válido, ele é guardado no OAuth2TokenHolder, para ser utilizado nas chamadas a API do HubSpot.
<br>
<br>
O segundo método é responsável pela chamada HTTP para a API do HubSpot. Se a API retornar um erro, ele cai no "catch" e retorna que o token não é válido. Caso o token seja validado pelo HubSpot, o método verifica se a resposta foi serializada corretamente e, se sim, retorna que o token é válido.

## Service
Pacote responsável pelos casos de uso e regras de negócio do sistema.
```java
public Mono<CreateContactResponse> createContact(CreateContactRequest createContactRequest) {
  Map<String, Object> requestBody = ContactRequestBuilder.buildCreateContact(createContactRequest);

  return webClient.post()
          .uri(endpoint)
          .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
          .bodyValue(requestBody)
          .retrieve()
          .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                  Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "Contact already exists")))
          .bodyToMono(CreateContactResponse.class);
}
```
Este é um exemplo de um caso de uso para a criação de um contato na camada de serviços. A API do HubSpot pode retornar um erro se já existir um usuário com o mesmo e-mail fornecido. Nesse caso, é realizada uma tratativa específica para esse erro, retornando ```409 CONFLICT```.

## Utils
Pacote responsável por classes utilitárias do sistema. Foi implementada uma classe para guardar o token fornecido no contexto da
requisição, para ser utilizado na integração com a API do HubSpot.
```java
public class OAuth2TokenHolder {
 private static final ThreadLocal<String> tokenHolder = new ThreadLocal<>();

 public static void setToken(String token) {
     tokenHolder.set(token);
 }

 public static String getToken() {
     return tokenHolder.get();
 }

 public static void clear() {
     tokenHolder.remove();
 }
}
```
A informação do token é guardada no contexto da thread em execução. Ao final da requisição, a informação é limpa no método ```afterCompletion``` do interceptor mostrado anteriormente.

```java
@Override
public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
  OAuth2TokenHolder.clear();
}
```

# Endpoints Obrigatórios

## Autenticação OAuth2

* ```/oauth```
```java
@GetMapping
public void startOAuthFlow(HttpServletResponse httpServletResponse) {
  httpServletResponse.setHeader(LOCATION, oAuthUrl);
  httpServletResponse.setStatus(302);
}
```
Este endpoint é responsável pelo início do fluxo de autenticação OAuth2 com o HubSpot. Ele apenas redireciona o usuário para 
a URL do serviço do HubSpot.

![image info](./imgs/OAuthHubSpot.PNG)

Você deverá selecionar a sua conta HubSpot e permitir que ela seja conectada ao aplicativo criado. É importante ressaltar que
**contas de desenvolvedor** não podem ser usadas no fluxo OAuth.