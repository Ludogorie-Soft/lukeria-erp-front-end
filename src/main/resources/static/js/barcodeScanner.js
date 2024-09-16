// barcodeScanner.js
class BarcodeScanner {
    constructor(videoElementId, inputElementId, toggleButtonId) {
        this.videoElement = document.getElementById(videoElementId);
        this.inputElement = document.getElementById(inputElementId);
        this.toggleButton = document.getElementById(toggleButtonId);
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
    }

    startCamera() {
        navigator.mediaDevices.getUserMedia({ video: true })
            .then((stream) => {
                this.cameraStream = stream;
                this.videoElement.srcObject = stream;
                this.toggleButton.innerHTML = '<i class="fas fa-stop"></i>'; // Промяна на иконката
                this.startScanner();  // Стартиране на сканирането с QuaggaJS
            })
            .catch((error) => {
                console.error('Error accessing camera: ', error);
            });
    }

    stopCamera() {
        if (this.cameraStream) {
            let tracks = this.cameraStream.getTracks();
            tracks.forEach(track => track.stop());
            this.cameraStream = null;
            this.videoElement.srcObject = null;
            this.toggleButton.innerHTML = '<i class="fas fa-camera"></i>'; // Промяна на иконката обратно
            this.stopScanner();  // Спиране на сканирането с QuaggaJS
        }
    }

    startScanner() {
        if (!this.isScanning) {
            Quagga.init({
                inputStream: {
                    type: "LiveStream",
                    constraints: {
                        width: 640,
                        height: 480,
                        facingMode: "environment" // Използване на задна камера, ако е налична
                    },
                    target: this.videoElement // HTML елемент за показване на потока
                },
                decoder: {
                    readers: ["ean_reader"] // Поддръжка на EAN-13 баркодове
                }
            }, (err) => {
                if (err) {
                    console.error(err);
                    return;
                }
                console.log("Initialization finished. Ready to start");
                Quagga.start();  // Започва сканирането
                this.isScanning = true;
            });

            // При откриване на баркод
            Quagga.onDetected((data) => {
                let barcode = data.codeResult.code;
                console.log("Barcode detected: " + barcode);
                this.inputElement.value = barcode;  // Поставя баркода в инпут полето
                this.stopCamera();  // Спиране на камерата и сканирането след откриване
            });
        }
    }

    stopScanner() {
        if (this.isScanning) {
            Quagga.stop();
            this.isScanning = false;
        }
    }
}

window.BarcodeScanner = BarcodeScanner;
