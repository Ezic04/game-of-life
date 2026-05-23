const canvas = document.getElementById('grid');
const ctx = canvas.getContext('2d');
const socket = new WebSocket('ws://' + location.host + '/sim');

socket.binaryType = 'arraybuffer';

socket.onmessage = function (event) {
    const dataView = new DataView(event.data);
    const width = dataView.getInt32(0);
    const height = dataView.getInt32(4);

    if (canvas.width !== width || canvas.height !== height) {
        canvas.width = width;
        canvas.height = height;
    }

    const buffer = new Uint8Array(event.data, 8);
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