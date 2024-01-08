package org.example;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.escpos.image.*;
import com.github.anastaciocintra.output.PrinterOutputStream;

import javax.imageio.ImageIO;
import javax.print.PrintService;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando programa ....");


        /*
        SerialPort[] ports = SerialPort.getCommPorts();

        for (SerialPort port : ports) {
            System.out.println(port.getSystemPortName() + "-" + port.getDescriptivePortName());
        }

        if (args.length >= 1) {
            SerialPort port = ports[Integer.parseInt(args[0])];
            System.out.println("Conectando a puerto : " + port.getSystemPortName());

            port.openPort();
            port.setBaudRate(38400);
            port.setNumDataBits(8);
            port.setNumStopBits(1);
            port.setRTS();

            port.addDataListener(new SerialPortDataListener() {
                @Override
                public int getListeningEvents() {
                    return SerialPort.LISTENING_EVENT_DATA_WRITTEN;
                }

                @Override
                public void serialEvent(SerialPortEvent event) {
                    if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_WRITTEN) {
                        System.out.println("All bytes were successfully transmitted!");
                    }
                }
            });

            System.out.println("ENVIANDO IMAGEN PARA IMPRIMIR");
            InputStream is = Main.class.getResourceAsStream("/ganatiempo.bmp");

            try {

                if (is != null) {
                    BufferedImage img = ImageIO.read(is);
                    int width = img.getWidth();
                    int height = img.getHeight();

                    // Convertir la imagen a monocromo
                    BufferedImage monoImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
                    Graphics2D g = monoImage.createGraphics();
                    g.drawImage(img, 0, 0, null);
                    g.dispose();

                    // Convertir la imagen a un array de bytes
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(monoImage, "bmp", baos);
                    byte[] imageBytes = baos.toByteArray();

                    // Crear el comando de imagen
                    byte[] imgCommand = new byte[]{0x1D, 0x76, 0x30, 0x00, (byte) (img.getWidth() / 8), 0x00, (byte) (img.getHeight() % 256), (byte) (img.getHeight() / 256)};

                    // Enviar el comando y los datos de la imagen a la impresora
                    port.writeBytes(imgCommand, imgCommand.length);
                    port.writeBytes(imageBytes, imageBytes.length);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.println("ENVIANDO COMANDOS DE FEED");

            // Nueva línea
            byte[] newLine = new byte[]{0x0A}; // LF
            port.writeBytes(newLine, newLine.length);
            port.writeBytes(newLine, newLine.length);
            port.writeBytes(newLine, newLine.length);
            port.writeBytes(newLine, newLine.length);
            port.writeBytes(newLine, newLine.length);

            System.out.println("Imprimir ¡Hola mundo!");

            String text = "Hola mundo";
            byte[] textLine = new byte[text.getBytes(StandardCharsets.UTF_8).length];
            port.writeBytes(text.getBytes(StandardCharsets.UTF_8), textLine.length);

            System.out.println("ENVIANDO COMANDO DE CORTE DE PAPEL");
            // Cortar el papel
            byte[] cutPaper = new byte[]{0x1B, 0x6d}; // Partcial cut
            port.writeBytes(cutPaper, cutPaper.length);

            System.out.println("FINALIZANDO COMANDOS");

        }
        System.out.println("Finalizando programa ...");*/


        if (args.length != 1) {
            System.out.println("Usage: java -jar getstart.jar (\"printer name\")");
            System.out.println("Printer list to use:");
            String[] printServicesNames = PrinterOutputStream.getListPrintServicesNames();
            for (String printServiceName : printServicesNames) {
                System.out.println(printServiceName);
            }

            System.exit(0);
        }

        printInfo(args[0]);

    }

    public static void printInfo(String printerName) {
        //this call is slow, try to use it only once and reuse the PrintService variable.
        PrintService printService = PrinterOutputStream.getPrintServiceByName(printerName);
        try {

            EscPos escpos = new EscPos(new PrinterOutputStream(printService));

            /**
             * LOGO ZONE
             */

            InputStream is = Main.class.getResourceAsStream("/ganatiempo.png");
            BufferedImage imageBufferedImage = ImageIO.read(is);

            Bitonal algorithm = new BitonalThreshold();
            // creating the EscPosImage, need buffered image and algorithm.
            EscPosImage escposImage = new EscPosImage(new CoffeeImageImpl(imageBufferedImage), algorithm);
            // this wrapper uses esc/pos sequence: "ESC '*'"
            BitImageWrapper imageWrapper = new BitImageWrapper();
            imageWrapper.setJustification(EscPosConst.Justification.Center);
            escpos.write(imageWrapper, escposImage);

            Style welcomeStyle = new Style()
                    .setFontSize(Style.FontSize._2, Style.FontSize._2)
                    .setBold(true)
                    .setJustification(EscPosConst.Justification.Center);

            Style normalBoldStyle = new Style()
                    .setFontSize(Style.FontSize._1, Style.FontSize._1)
                    .setJustification(EscPosConst.Justification.Center)
                    .setBold(true);

            Style normalStyle = new Style()
                    .setFontSize(Style.FontSize._1, Style.FontSize._1)
                    .setJustification(EscPosConst.Justification.Center);

            Style numberStyle = new Style()
                    .setFontSize(Style.FontSize._5, Style.FontSize._5)
                    .setJustification(EscPosConst.Justification.Center)
                    .setBold(true);


            //escpos.setCharacterCodeTable(EscPos.CharacterCodeTable.ISO8859_15_Latin9);

            // Bienvenido
            escpos.writeLF(welcomeStyle, "¡Bienvenido!");
            escpos.feed(1);

            // Numero de atencion
            String attentionMessage = "Tu n\u0300mero de atención";
            escpos.writeLF(normalBoldStyle, attentionMessage);

            escpos.feed(2);

            //Codigo numero
            escpos.writeLF(numberStyle, "C58");

            escpos.feed(2);

            //Queue
            escpos.writeLF(normalBoldStyle, "Caja");

            escpos.feed(2);

            escpos.writeLF(normalStyle, "BIENVENIDO A GANATIEMPO");

            escpos.feed(2);

            escpos.writeLF(normalBoldStyle, "08/01/2024 09:14:00");
            escpos.writeLF(normalStyle, "VIS\u00CDTANOS EN WWW.TEMPUSSPA.CL");

            escpos.feed(4);

            escpos.cut(EscPos.CutMode.PART);
            escpos.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

}