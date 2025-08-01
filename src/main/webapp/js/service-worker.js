self.addEventListener("install", (e) => {
    e.waitUntil(
            caches.open("jmts-cache").then((cache) => {
        return cache.addAll([
            "/jmts/index.xhtml",
            "/jmts/css/jmts.css",
            "/jmts/js/app.js",
            "/jmts/icons/icon-192x192.png",
            "/jmts/icons/icon-512x512.png"
        ]);
    })
            );
});

self.addEventListener("fetch", (e) => {
    e.respondWith(
            caches.match(e.request).then((response) => {
        return response || fetch(e.request);
    })
            );
});