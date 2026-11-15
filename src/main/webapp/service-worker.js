
const CACHE_NAME = "jmts-static-v2";

const STATIC_ASSETS = [
    "/jmts/js/app.js",
    "/jmts/icons/icon-192x192-A.png",
    "/jmts/icons/icon-512x512-A.png"
];

self.addEventListener("install", (event) => {
    event.waitUntil(
        caches.open(CACHE_NAME).then((cache) => {
            return cache.addAll(STATIC_ASSETS);
        }).then(() => self.skipWaiting())
    );
});

self.addEventListener("activate", (event) => {
    event.waitUntil(
        (async () => {
            const keys = await caches.keys();

            await Promise.all(
                keys.filter((key) => key.startsWith("jmts-") &&
                    key !== CACHE_NAME)
                    .map((key) => caches.delete(key))
            );

            await self.clients.claim();
        })()
    );
});

self.addEventListener("fetch", (event) => {
    const request = event.request;

    // Never intercept non-GET requests.
    if (request.method !== "GET") {
        return;
    }

    // Let normal page navigation go directly to the server.
    if (request.mode === "navigate") {
        return;
    }

    const url = new URL(request.url);

    // Only handle requests to this origin.
    if (url.origin !== self.location.origin) {
        return;
    }

    // Cache only explicitly listed static assets.
    if (!STATIC_ASSETS.includes(url.pathname)) {
        return;
    }

    event.respondWith(
        caches.open(CACHE_NAME).then(async (cache) => {
            const cached = await cache.match(request);

            if (cached) {
                return cached;
            }

            const response = await fetch(request);

            if (response.ok) {
                await cache.put(request, response.clone());
            }

            return response;
        })
    );
});
