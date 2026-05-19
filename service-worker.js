const CACHE_NAME = 'sezr-focus-v1';

const CORE_ASSETS = [
  './',
  './focus.html',
  './focus-style.css',
  './focus-app.js',
  './focus-config.js',
  './manifest.json',
  './icons/icon-192.png',
  './icons/icon-512.png'
];

self.addEventListener('install', event => {
  self.skipWaiting();
  event.waitUntil(
    caches.open(CACHE_NAME).then(cache => cache.addAll(CORE_ASSETS))
  );
});

self.addEventListener('activate', event => {
  event.waitUntil(
    caches.keys().then(keys =>
      Promise.all(keys.filter(key => key !== CACHE_NAME).map(key => caches.delete(key)))
    )
  );
  self.clients.claim();
});

self.addEventListener('fetch', event => {
  const request = event.request;

  if (request.method !== 'GET') return;

  event.respondWith(
    caches.match(request).then(cached => {
      if (cached) return cached;

      return fetch(request).then(response => {
        const copy = response.clone();
        caches.open(CACHE_NAME).then(cache => {
          if (request.url.startsWith(self.location.origin)) {
            cache.put(request, copy);
          }
        });
        return response;
      }).catch(() => {
        if (request.mode === 'navigate') {
          return caches.match('./focus.html');
        }
      });
    })
  );
});
