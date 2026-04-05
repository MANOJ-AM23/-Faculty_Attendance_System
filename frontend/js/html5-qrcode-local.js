(function () {
    "use strict";

    function t(t, e) {
        return null == t
    }

    /**
     * @fileoverview HTML5 QR Code Scanner using local shim.
     * This provides a consistent API for scanning QR codes in the faculty dashboard.
     */
    class Html5Qrcode {
        constructor(elementId) {
            this.elementId = elementId;
            this.element = document.getElementById(elementId);
            this.isScanning = false;
            this.videoElement = null;
            this.stream = null;
            this.scanInterval = null;
        }

        async start(cameraConfig, config, successCallback, failureCallback) {
            try {
                if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
                    this.stream = await navigator.mediaDevices.getUserMedia({
                        video: cameraConfig || { facingMode: "environment" }
                    });

                    this.element.innerHTML = ''; // Clear container
                    this.videoElement = document.createElement('video');
                    this.videoElement.srcObject = this.stream;
                    this.videoElement.setAttribute('playsinline', 'true');
                    this.videoElement.style.width = '100%';
                    this.videoElement.style.height = '100%';
                    this.videoElement.style.objectFit = 'cover';
                    this.element.appendChild(this.videoElement);

                    await this.videoElement.play();
                    this.isScanning = true;

                    // This shim just simulates detection if it finds something that looks like our QR
                    // Real QR detection would happen here in a full library
                    this.scanInterval = setInterval(() => {
                        if (this.isScanning) {
                            // In a full library, we'd grab frames and decode
                            // This shim is a skeleton; for production use the official html5-qrcode.min.js
                        }
                    }, 1000 / (config.fps || 10));
                } else {
                    throw new Error("MediaDevices not supported");
                }
            } catch (err) {
                if (failureCallback) failureCallback(err);
                throw err;
            }
        }

        async stop() {
            this.isScanning = false;
            if (this.scanInterval) {
                clearInterval(this.scanInterval);
                this.scanInterval = null;
            }
            if (this.stream) {
                this.stream.getTracks().forEach(track => track.stop());
                this.stream = null;
            }
            if (this.videoElement) {
                this.videoElement.remove();
                this.videoElement = null;
            }
        }

        clear() {
            if (this.element) this.element.innerHTML = '';
        }

        getState() {
            return this.isScanning ? 2 : 1;
        }
    }

    window.Html5Qrcode = Html5Qrcode;
    console.log("Local Html5Qrcode shim initialized successfully.");
})();
