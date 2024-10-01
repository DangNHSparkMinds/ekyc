import pako from 'pako';

// Hàm nén và mã hóa dữ liệu từ Base64
export function compressAndEncode(imageData: any) {
    // Chuyển đổi dữ liệu ảnh từ base64 sang dữ liệu nhị phân
    const binaryString = atob(imageData.split(',')[1]);
    const len = binaryString.length;
    const bytes = new Uint8Array(len);

    for (let i = 0; i < len; i++) {
        bytes[i] = binaryString.charCodeAt(i);
    }

    // Nén dữ liệu GZIP
    const compressedData = pako.gzip(bytes);

    // Mã hóa dữ liệu Base64
    return arrayBufferToBase64(compressedData);
}

function arrayBufferToBase64(arrayBuffer: any) {
    const bytes = new Uint8Array(arrayBuffer);
    let binaryString = '';
    for (let i = 0; i < bytes.length; i++) {
        binaryString += String.fromCharCode(bytes[i]);
    }
    return btoa(binaryString);
}
