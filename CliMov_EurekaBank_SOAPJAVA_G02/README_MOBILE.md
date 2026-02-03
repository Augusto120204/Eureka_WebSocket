# Aplicación Móvil EurekaBank - Android

## Descripción
Aplicación móvil de banca en línea para EurekaBank con sincronización en tiempo real mediante WebSocket.

## Características
- Login de usuario
- Selección de cajero con estado en tiempo real (ocupado/disponible)
- Selección de cuenta
- Operaciones bancarias: Depósito, Retiro, Transferencia, Consulta de Movimientos
- Bloqueo de operaciones en tiempo real (si un usuario está realizando una operación en una cuenta, otros usuarios no pueden hacer la misma operación)
- Sincronización con la aplicación web mediante WebSocket STOMP

## Flujo de la Aplicación
1. **Login** - El usuario ingresa sus credenciales
2. **Selección de Cajero** - Se muestra una lista de cajeros disponibles. Los cajeros ocupados se marcan visualmente
3. **Selección de Cuenta** - El usuario selecciona una de sus cuentas
4. **Menú de Operaciones** - El usuario puede realizar depósitos, retiros, transferencias o consultar movimientos
5. **Ejecución de Operación** - Las operaciones se bloquean automáticamente para evitar conflictos

## Arquitectura

### Componentes Principales
- **WebSocketManager**: Singleton que gestiona la conexión STOMP con el servidor
- **SessionManager**: Almacena información de sesión (usuario, cajero, cuenta, operación actual)
- **RestApiClient**: Cliente Retrofit para las APIs REST del servidor
- **SoapApiService**: Cliente para los servicios SOAP del banco

### Sincronización en Tiempo Real
La aplicación se conecta al WebSocket del servidor en ws://[host]:8081/ws-eureka y:
- Recibe actualizaciones cuando un cajero es ocupado/liberado
- Recibe actualizaciones cuando una operación es bloqueada/liberada
- Envía su sessionId para identificarse en el servidor

### Endpoints REST Utilizados
- POST /api/estado/cajero/ocupar - Ocupar un cajero
- POST /api/estado/cajero/liberar - Liberar un cajero
- POST /api/estado/operacion/bloquear - Bloquear una operación
- POST /api/estado/operacion/liberar - Liberar una operación

### Servicios SOAP Utilizados
- 	raerEmpleados - Obtener lista de cajeros
- 	raerCuentas - Obtener lista de cuentas
- loginCliente - Autenticar usuario
- hacerDeposito - Realizar depósito
- hacerRetiro - Realizar retiro
- hacerTransferencia - Realizar transferencia
- erMovimientos - Consultar movimientos

## Configuración

### Requisitos
- Android Studio
- Android SDK API 24+
- Servidor Spring Boot corriendo en puerto 8081
- Servidor SOAP corriendo en puerto 8080

### Configurar URL del Servidor
Editar el archivo RestApiClient.java:
`java
private static final String BASE_URL = "http://10.0.2.2:8081/";
`

Para dispositivo físico, cambiar 10.0.2.2 por la IP local de tu computadora.

Editar el archivo RetrofitClient.java (cliente SOAP):
`java
private static final String BASE_URL = "http://10.0.2.2:8080/WS_EUREKABANK_SoapJava_G02/";
`

### Permisos
La aplicación requiere los siguientes permisos (ya configurados en AndroidManifest.xml):
- INTERNET - Para comunicación con el servidor
- ACCESS_NETWORK_STATE - Para verificar conectividad

## Uso

### Credenciales de Prueba
Usuario: (consultar base de datos)
Contraseña: (consultar base de datos)

### Probar Sincronización
1. Abrir la aplicación web en un navegador
2. Abrir la aplicación móvil en el emulador/dispositivo
3. Iniciar sesión en ambas
4. Seleccionar un cajero en una aplicación  debe aparecer ocupado en la otra
5. Seleccionar una cuenta y comenzar una operación  los botones deben deshabilitarse en la otra aplicación

## Dependencias Principales
- Retrofit 2.9.0 - Cliente HTTP
- StompProtocolAndroid 1.6.6 - Cliente WebSocket STOMP
- RxJava2 - Programación reactiva
- Gson - Serialización JSON
- RecyclerView - Listas
- Material Components - UI

## Estructura de Paquetes
`
ec.edu.monster.climov_eurekabank_soapjava_g02/
 adapter/           # Adaptadores de RecyclerView
 controller/        # Controladores (MVP)
 model/
    data/
       api/      # Cliente SOAP
       rest/     # Cliente REST
    dto/          # Data Transfer Objects
    pojo/         # Plain Old Java Objects
 utils/            # Utilidades (SessionManager)
 view/             # Actividades (UI)
 websocket/        # WebSocketManager
`

## Troubleshooting

### No se puede conectar al servidor
- Verificar que el servidor esté corriendo
- Verificar la URL en RestApiClient.java y RetrofitClient.java
- Para emulador usar 10.0.2.2, para dispositivo físico usar la IP local

### WebSocket no conecta
- Verificar que el puerto 8081 esté abierto
- Verificar que el servidor Spring Boot tenga habilitado WebSocket
- Revisar logs en Logcat con filtro "WebSocket"

### Los estados no se sincronizan
- Verificar que ambas aplicaciones (web y móvil) estén conectadas al mismo servidor
- Revisar la consola del servidor para ver los mensajes STOMP
- Verificar que el sessionId se esté enviando correctamente

## Autor
Grupo 02 - Arquitectura de Software ESPE
