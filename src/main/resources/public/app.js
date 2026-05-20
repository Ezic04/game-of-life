const canvas = document.getElementById('grid');
const ctx = canvas.getContext('2d');
const socket = new WebSocket('ws://' + location.host + '/sim');

socket.binaryType = 'arraybuffer';

socket.onmessage = function (event) {
    const buffer = new Uint8Array(event.data);
    const width = canvas.width;
    const height = canvas.height;
    const imageData = ctx.createImageData(width, height);
    const data = imageData.data;

    let pixelIndex = 0;
    for (let i = 0; i < buffer.length; i++) {
        const byte = buffer[i];
        for (let bit = 7; bit >= 0; --bit) {
            const isAlive = (byte >> bit) & 1;
            const color = isAlive ? 255 : 0;

            data[pixelIndex] = color;
            data[pixelIndex + 1] = color;
            data[pixelIndex + 2] = color;
            data[pixelIndex + 3] = 255;

            pixelIndex += 4;
        }
    }

    ctx.putImageData(imageData, 0, 0);
};