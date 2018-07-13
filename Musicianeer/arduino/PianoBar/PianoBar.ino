#include "FastLED.h"
#include "MIDIUSB.h"

#define NUM_LEDS 50 
#define DATA_PIN 16
#define CLOCK_PIN 15

#define LOWEST_NOTE 35 // one less than Musicianeer
#define HIGHEST_NOTE 84

CRGB leds[NUM_LEDS];

void setup() { 
		Serial.begin(57600);
		FastLED.addLeds<WS2801,DATA_PIN, CLOCK_PIN>(leds, NUM_LEDS);
		LEDS.setBrightness(84);
		cycle_leds();
		Serial.print("Compiled at ");
		Serial.print(__TIME__);
		Serial.print(" on ");
		Serial.println(__DATE__);
}

void loop() { 
		midiEventPacket_t message = MidiUSB.read();
		if (message.header != 0) {
				process_message(message);
		}
}

void process_message(midiEventPacket_t message) {
		uint8_t command = message.byte1 & 0xf0;
		if (command == 0x80) {
				uint8_t channel = message.byte1 & 0xf;
				uint8_t midi_note = message.byte2;
				//Serial.print("NOTE_OFF: channel=");
				//Serial.print(channel);
				//Serial.print(", midi_note=");
				//Serial.println(midi_note);
				if (midi_note >= LOWEST_NOTE && midi_note <= HIGHEST_NOTE) {
						uint8_t led_index = message.byte2 - LOWEST_NOTE;
						leds[led_index] = 0;
						FastLED.show(); 
				}
		} else if (command == 0x90) {
				uint8_t channel = message.byte1 & 0xf;
				uint8_t midi_note = message.byte2;
				uint8_t velocity = message.byte3;
				//Serial.print("NOTE_ON: channel=");
				//Serial.print(channel);
				//Serial.print(", midi_note=");
				//Serial.print(midi_note);
				//Serial.print(", velocity=");
				//Serial.println(velocity);
				if (midi_note >= LOWEST_NOTE && midi_note <= HIGHEST_NOTE) {
						uint8_t led_index = message.byte2 - LOWEST_NOTE;
						uint8_t i = channel / 4;
						uint8_t j = channel % 4;
						uint32_t degrees = (j * 90) + ((i * 90) / 4);
						uint8_t hue = (degrees * 255) / 360;
						uint8_t brightness = ((velocity * 255) / 127);
						//Serial.print("channel="+channel);
						//Serial.print(channel);
						//Serial.print(", i=");
						//Serial.print(i);
						//Serial.print(", j=");
						//Serial.print(j);
						//Serial.print(", degrees=");
						//Serial.print(degrees);
						//Serial.print(", hue=");
						//Serial.print(hue);
						//Serial.print(", brightness=");
						//Serial.println(brightness);
						hsv2rgb_spectrum(CHSV(hue, 255, brightness), leds[led_index]);
						FastLED.show(); 
				}
		} else {
				Serial.print("Received: ");
				Serial.print(message.header, HEX);
				Serial.print("-");
				Serial.print(message.byte1, HEX);
				Serial.print("-");
				Serial.print(message.byte2, HEX);
				Serial.print("-");
				Serial.println(message.byte3, HEX);
		}
}

void fade_all() {
		for (int i = 0; i < NUM_LEDS; i++) {
				leds[i].nscale8(250);
		}
}

void cycle_leds() {
		uint8_t hue = 0;
		uint8_t delta = (255 / NUM_LEDS) / 2; // half up, half back
		if (delta == 0) {
				delta = 1;
		}
		for (int i = 0; i < NUM_LEDS; i++) {
				leds[i] = CHSV(hue, 255, 255);
				FastLED.show();
				delay(10);
				hue += delta;
		}
		for (int i = NUM_LEDS - 1; i >= 0; i--) {
				leds[i] = CHSV(hue, 255, 255);
				FastLED.show();
				delay(10);
				hue += delta;
		}
		for (int i = 0; i < NUM_LEDS; i++) {
				leds[i] = 0;
				FastLED.show();
		}
}

