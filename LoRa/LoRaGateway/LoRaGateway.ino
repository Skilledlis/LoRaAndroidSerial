#include <LoRa.h>
#include "boards.h"

#define START_BYTE 0x01
#define END_BYTE 0x04
#define SUCCESS_BYTE 0x06
#define ERROR_BYTE 0x15
#define MAX_MESSAGE_SIZE 255

void setup()
{
    initBoard();
    delay(1500);
    LoRa.setPins(RADIO_CS_PIN, RADIO_RST_PIN, RADIO_DIO0_PIN);
    if (!LoRa.begin(LoRa_frequency)) {
        Serial.println("Starting LoRa failed!");
        while (1);
    }
}

void loop(){

  //Parse LoRa and send message to SERIAL
 int packetSize = LoRa.parsePacket();
    if (packetSize) {
        uint8_t message[MAX_MESSAGE_SIZE];
        int index = 0;

        // Читаем данные из пакета
        while (LoRa.available()) {
          message[index++] = LoRa.read();
        }

        // Проверяем, что пакет не превышает максимальный размер и имеет минимальный размер
        if (index > 3 && index <= MAX_MESSAGE_SIZE) {
            uint8_t crcReceived = message[index - 1];
            uint8_t crcCalculated = calculateCRC8(message, index - 1);

            if (message[0] == START_BYTE && message[index - 2] == END_BYTE && crcReceived == crcCalculated) {
              int rssi = LoRa.packetRssi();
              float snr = LoRa.packetSnr();

              // Отправляем идентификатор успеха и текст сообщения в Serial
              Serial.write(SUCCESS_BYTE);
              Serial.write(message + 1, index - 3);
              Serial.write('\n');

              char buffer[50];
              snprintf(buffer, sizeof(buffer), "RSSI: %d, SNR: %.2f\n", rssi, snr);
              Serial.write(buffer);

            
              u8g2->clearBuffer();
              char buf[256];
              u8g2->drawStr(0, 26, "Received OK!");
              snprintf(buf, sizeof(buf), "RSSI:%i", rssi);
              u8g2->drawStr(0, 40, buf);
              snprintf(buf, sizeof(buf), "SNR:%.1f", snr);
              u8g2->drawStr(0, 56, buf);
              u8g2->sendBuffer();
             
            } else {
                // Отправляем идентификатор ошибки в Serial
                Serial.write(ERROR_BYTE);

                u8g2->clearBuffer();
                u8g2->drawStr(0, 26, "Received FAIL CRC!");
                u8g2->sendBuffer();
            }
        } else {
            // Отправляем идентификатор ошибки в Serial
            Serial.write(ERROR_BYTE);

            u8g2->clearBuffer();
            u8g2->drawStr(0, 26, "Received FAIL message!");
            u8g2->sendBuffer();
        }
    }
}


