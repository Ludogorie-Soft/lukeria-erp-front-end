class BarcodeScanner {
    constructor({ videoElementId, inputElementId, toggleButtonId }) {
        this.videoElement = document.getElementById(videoElementId);
        this.inputElement = document.getElementById(inputElementId);
        this.toggleButton = document.getElementById(toggleButtonId);
        this.cameraStream = null;
        this.isScanning = false;
        this.initialize();
    }

    initialize() {
        // Constructor remains unchanged
    }

    toggleCamera() {
        this.cameraStream ? this.stopCamera() : this.startCamera();
    }

    startCamera() {
        navigator.mediaDevices.getUserMedia({ video: true })
            .then(stream => {
                this.cameraStream = stream;
                this.videoElement.srcObject = stream;
                this.toggleButton.innerHTML = '<i class="fas fa-stop"></i>';
                this.startScanner();
            })
            .catch(error => console.error('Error accessing camera:', error));
    }

    stopCamera() {
        if (this.cameraStream) {
            this.cameraStream.getTracks().forEach(track => track.stop());
            this.cameraStream = null;
            this.videoElement.srcObject = null;
            this.toggleButton.innerHTML = '<i class="fas fa-camera"></i>';
            this.stopScanner();
        }
    }

    startScanner() {
        if (!this.isScanning) {
            Quagga.init({
                inputStream: {
                    type: 'LiveStream',
                    constraints: { width: 640, height: 480, facingMode: 'environment' },
                    target: this.videoElement
                },
                decoder: { readers: ['ean_reader'] }
            }, err => {
                if (err) {
                    console.error(err);
                    return;
                }
                console.log('Initialization finished. Ready to start');
                Quagga.start();
                this.isScanning = true;
            });

            Quagga.onDetected(data => {
                this.inputElement.value = data.codeResult.code;
                this.stopCamera();
                this.closeModal(); // Затваряне на модалния прозорец
                searchByBarcode();
            });
        }
    }

    stopScanner() {
        if (this.isScanning) {
            Quagga.stop();
            this.isScanning = false;
        }
    }

    closeModal() {
        const modal = document.getElementById('cameraModal');
        modal.style.display = 'none'; // Затваряне на модалния прозорец
    }
}

window.BarcodeScanner = BarcodeScanner;
