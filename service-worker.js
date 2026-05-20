const CACHE_NAME='sezr-focus-asama1-v1';
const CORE_ASSETS=['./focus.html','./focus-style.css','./focus-app.js','./focus-config.js','./manifest.json'];
self.addEventListener('install',e=>{self.skipWaiting();e.waitUntil(caches.open(CACHE_NAME).then(c=>c.addAll(CORE_ASSETS)))});
self.addEventListener('activate',e=>{e.waitUntil(caches.keys().then(keys=>Promise.all(keys.filter(k=>k!==CACHE_NAME).map(k=>caches.delete(k)))));self.clients.claim()});
self.addEventListener('fetch',e=>{if(e.request.method!=='GET')return;e.respondWith(caches.match(e.request).then(c=>c||fetch(e.request).catch(()=>{if(e.request.mode==='navigate')return caches.match('./focus.html')})))});
