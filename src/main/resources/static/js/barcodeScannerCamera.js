class BarcodeScannerCamera {
    constructor({ videoElementId, inputElementId, toggleButtonId, modalId }) {
        this.videoElement = document.getElementById(videoElementId);
        this.inputElement = document.getElementById(inputElementId);
        this.toggleButton = document.getElementById(toggleButtonId);
        this.modal = document.getElementById(modalId);
        this.cameraStream = null;
        this.isScanning = false;
        this.initialize();
    }

    initialize() {
        this.toggleButton.addEventListener('click', () => {
            if (this.cameraStream) {
                this.stopCamera();
            } else {
                this.startCamera();
            }
        });

        const closeModal = this.modal.querySelector('.close');
        closeModal.addEventListener('click', () => {
            this.modal.style.display = 'none';
            this.stopCamera();
        });

        window.addEventListener('click', (event) => {
            if (event.target === this.modal) {
                this.modal.style.display = 'none';
                this.stopCamera();
            }
        });
    }

    startCamera() {
        navigator.mediaDevices.getUserMedia({ video: true })
            .then(stream => {
                this.cameraStream = stream;
                this.videoElement.srcObject = stream;
                this.toggleButton.innerHTML = '<i class="fas fa-stop"></i>';
                this.modal.style.display = 'flex';
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
                this.closeModal();
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
        this.modal.style.display = 'none';
    }
}

window.BarcodeScannerCamera = BarcodeScannerCamera;
