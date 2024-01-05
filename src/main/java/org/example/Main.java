package org.example;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando programa ....");


        SerialPort[] ports = SerialPort.getCommPorts();

        for (SerialPort port : ports) {
            System.out.println(port.getSystemPortName() + "-" + port.getDescriptivePortName());
        }

        if (args.length >= 1) {
            SerialPort port = ports[1];
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

            System.out.println("Imprimir ¡Hola mundo!");

            String text = "Hola mundo";
            byte[] textLine = new byte[text.getBytes(StandardCharsets.UTF_8).length];
            port.writeBytes(text.getBytes(StandardCharsets.UTF_8), textLine.length);

            System.out.println("ENVIANDO COMANDOS DE FEED");

            // Nueva línea
            byte[] newLine = new byte[]{0x0A}; // LF
            port.writeBytes(newLine, newLine.length);
            port.writeBytes(newLine, newLine.length);
            port.writeBytes(newLine, newLine.length);
            port.writeBytes(newLine, newLine.length);
            port.writeBytes(newLine, newLine.length);

            System.out.println("ENVIANDO COMANDO DE CORTE DE PAPEL");
            // Cortar el papel
            byte[] cutPaper = new byte[]{0x1B, 0x6d}; // Partcial cut
            port.writeBytes(cutPaper, cutPaper.length);

            System.out.println("FINALIZANDO COMANDOS");

        }
        System.out.println("Finalizando programa ...");
    }

}