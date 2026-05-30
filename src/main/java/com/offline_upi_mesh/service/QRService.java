package com.offline_upi_mesh.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;

@Service
public class QRService {

    public String generateQr(
            String content,
            String fileName
    ) throws Exception {

        QRCodeWriter writer =
                new QRCodeWriter();

        BitMatrix matrix =
                writer.encode(
                        content,
                        BarcodeFormat.QR_CODE,
                        300,
                        300
                );

        String path =
                "qr/" + fileName + ".png";

        MatrixToImageWriter.writeToPath(
                matrix,
                "PNG",
                Path.of(path)
        );

        return path;
    }
    public String readQr(
            String filePath
    ) throws Exception {

        BufferedImage image =
                ImageIO.read(
                        new File(filePath)
                );

        LuminanceSource source =
                new BufferedImageLuminanceSource(
                        image
                );

        BinaryBitmap bitmap =
                new BinaryBitmap(
                        new HybridBinarizer(source)
                );

        return new MultiFormatReader()
                .decode(bitmap)
                .getText();
    }
}