// Service Worker para EurekaBank PWA
const CACHE_NAME = 'eurekabank-v1';
const STATIC_CACHE = 'eurekabank-static-v1';

// Recursos estáticos para cachear
const STATIC_ASSETS = [
  '/img/LogoEureka.png',
  '/img/deposito.png',
  '/img/retiro.png',
  '/img/transferencia.png',
  '/img/movimientos.png',
  '/img/icon-192x192.png',
  '/img/icon-512x512.png',
  '/manifest.json'
];

// URLs que NO deben cachearse (APIs dinámicas)
const NO_CACHE_URLS = [
  '/api/',
  '/ws-eureka',
  '/hacerDeposito',
  '/hacerRetiro',
  '/hacerTransferencia',
  '/login',
  '/logout'
];

// Instalación del Service Worker
self.addEventListener('install', (event) => {
  console.log('[SW] Instalando Service Worker...');
  event.waitUntil(
    caches.open(STATIC_CACHE)
      .then((cache) => {
        console.log('[SW] Cacheando recursos estáticos');
        return cache.addAll(STATIC_ASSETS);
      })
      .then(() => self.skipWaiting())
      .catch((error) => {
        console.error('[SW] Error al cachear:', error);
      })
  );
});

// Activación - limpiar caches antiguos
self.addEventListener('activate', (event) => {
  console.log('[SW] Activando Service Worker...');
  event.waitUntil(
    caches.keys().then((cacheNames) => {
      return Promise.all(
        cacheNames.map((cache) => {
          if (cache !== CACHE_NAME && cache !== STATIC_CACHE) {
            console.log('[SW] Eliminando cache antiguo:', cache);
            return caches.delete(cache);
          }
        })
      );
    }).then(() => self.clients.claim())
  );
});

// Interceptar peticiones
self.addEventListener('fetch', (event) => {
  const url = new URL(event.request.url);
  
  // No interceptar WebSocket ni peticiones a APIs dinámicas
  if (event.request.url.includes('/ws-eureka') || 
      NO_CACHE_URLS.some(path => url.pathname.startsWith(path))) {
    return;
  }
  
  // Estrategia: Network First para HTML, Cache First para assets
  if (event.request.headers.get('accept')?.includes('text/html')) {
    // Network First para páginas HTML
    event.respondWith(
      fetch(event.request)
        .then((response) => {
          // Clonar y cachear la respuesta
          const responseClone = response.clone();
          caches.open(CACHE_NAME).then((cache) => {
            cache.put(event.request, responseClone);
          });
          return response;
        })
        .catch(() => {
          // Si falla la red, buscar en cache
          return caches.match(event.request);
        })
    );
  } else {
    // Cache First para assets estáticos
    event.respondWith(
      caches.match(event.request)
        .then((cachedResponse) => {
          if (cachedResponse) {
            return cachedResponse;
          }
          return fetch(event.request).then((response) => {
            // Cachear nuevos recursos
            if (response.status === 200) {
              const responseClone = response.clone();
              caches.open(STATIC_CACHE).then((cache) => {
                cache.put(event.request, responseClone);
              });
            }
            return response;
          });
        })
    );
  }
});

// Escuchar mensajes del cliente
self.addEventListener('message', (event) => {
  if (event.data.action === 'skipWaiting') {
    self.skipWaiting();
  }
});

console.log('[SW] Service Worker cargado');
