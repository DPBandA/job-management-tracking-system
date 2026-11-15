if ('serviceWorker' in navigator) {
    navigator.serviceWorker.register('/jmts/service-worker.js')
            .then(reg => console.log('✅ ADASSA Service Worker registered:', reg))
            .catch(err => console.error('❌ ADASSA Service Worker registration failed:', err));
}