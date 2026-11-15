if ('serviceWorker' in navigator) {
    navigator.serviceWorker.register('/jmts/service-worker.js')
            .then(reg => console.log('✅ JMTS Service Worker registered:', reg))
            .catch(err => console.error('❌ JMTS Service Worker registration failed:', err));
}