package com.arraywork.summer.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * Image Utilities
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/04/28
 */
public class ImageUtils {

    private static final int IMAGE_WIDTH = 320;
    private static final int IMAGE_HEIGHT = 320;
    private static final int FRONT_COLOR = 0x000000;
    private static final int BACKGROUND_COLOR = 0xFFFFFF;

    /** Generate qrcode image by content (Depends on com.google.zxing) */
    public static BufferedImage createQrCode(String content) throws WriterException {
        Map<EncodeHintType, Object> hints = new HashMap();
        // ErrorCorrectionLevel：L = 7% (default), M = 15%, Q = 25%, H = 30%
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        hints.put(EncodeHintType.MARGIN, 1);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        BitMatrix bitMatrix = multiFormatWriter.encode(content,
            BarcodeFormat.QR_CODE, IMAGE_WIDTH, IMAGE_HEIGHT, hints);

        BufferedImage bufferedImage = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_BGR);
        for (int x = 0; x < IMAGE_WIDTH; x++) {
            for (int y = 0; y < IMAGE_HEIGHT; y++) {
                bufferedImage.setRGB(x, y, bitMatrix.get(x, y) ? FRONT_COLOR : BACKGROUND_COLOR);
            }
        }
        return bufferedImage;
    }

    /** Get dimension from input stream */
    public static int[] getDimension(InputStream is) throws IOException {
        int c1 = is.read();
        int c2 = is.read();
        int c3 = is.read();
        int width = 0, height = 0;
        String mimeType = null;

        if (c1 == 'G' && c2 == 'I' && c3 == 'F') {  // GIF
            is.skip(3);
            width = readInt(is, 2, false);
            height = readInt(is, 2, false);
            mimeType = "image/gif";
        } else if (c1 == 0xFF && c2 == 0xD8) {      // JPG
            while (c3 == 255) {
                int marker = is.read();
                int len = readInt(is, 2, true);
                if (marker == 192 || marker == 193 || marker == 194) {
                    is.skip(1);
                    height = readInt(is, 2, true);
                    width = readInt(is, 2, true);
                    mimeType = "image/jpeg";
                    break;
                }
                is.skip(len - 2);
                c3 = is.read();
            }
        } else if (c1 == 137 && c2 == 80 && c3 == 78) { // PNG
            is.skip(15);
            width = readInt(is, 2, true);
            is.skip(2);
            height = readInt(is, 2, true);
            mimeType = "image/png";
        } else if (c1 == 66 && c2 == 77) {              // BMP
            is.skip(15);
            width = readInt(is, 2, false);
            is.skip(2);
            height = readInt(is, 2, false);
            mimeType = "image/bmp";
        } else if (c1 == 'R' && c2 == 'I' && c3 == 'F') {   // WEBP
            byte[] bytes = new byte[27];
            is.read(bytes, 0, 27);
            width = ((int) bytes[24] & 0xff) << 8 | ((int) bytes[23] & 0xff);
            height = ((int) bytes[26] & 0xff) << 8 | ((int) bytes[25] & 0xff);
            mimeType = "image/webp";
        } else {
            int c4 = is.read();
            if ((c1 == 'M' && c2 == 'M' && c3 == 0 && c4 == 42)
                || (c1 == 'I' && c2 == 'I' && c3 == 42 && c4 == 0)) { // TIFF
                boolean bigEndian = c1 == 'M';
                int ifd = 0;
                int entries;
                ifd = readInt(is, 4, bigEndian);
                is.skip(ifd - 8);
                entries = readInt(is, 2, bigEndian);
                for (int i = 1; i <= entries; i++) {
                    int tag = readInt(is, 2, bigEndian);
                    int fieldType = readInt(is, 2, bigEndian);
                    int valOffset;
                    if ((fieldType == 3 || fieldType == 8)) {
                        valOffset = readInt(is, 2, bigEndian);
                        is.skip(2);
                    } else {
                        valOffset = readInt(is, 4, bigEndian);
                    }
                    if (tag == 256) {
                        width = valOffset;
                    } else if (tag == 257) {
                        height = valOffset;
                    }
                    if (width > 0 && height > 0) {
                        mimeType = "image/tiff";
                        break;
                    }
                }
            }
        }
        if (mimeType == null) {
            throw new IOException("Unsupported image type");
        }
        return new int[] { width, height };
    }

    /** Read int */
    private static int readInt(InputStream is, int noOfBytes, boolean bigEndian) throws IOException {
        int ret = 0;
        int sv = bigEndian ? ((noOfBytes - 1) * 8) : 0;
        int cnt = bigEndian ? -8 : 8;
        for (int i = 0; i < noOfBytes; i++) {
            ret |= is.read() << sv;
            sv += cnt;
        }
        return ret;
    }

}