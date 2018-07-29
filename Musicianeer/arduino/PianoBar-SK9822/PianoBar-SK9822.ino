#include "FastLED.h"
#include "MIDIUSB.h"

#define NUM_LEDS 144 
#define DATA_PIN 16
#define CLOCK_PIN 15

#define LOWEST_NOTE 36
#define HIGHEST_NOTE 84

#define NOTE_COUNT ((HIGHEST_NOTE - LOWEST_NOTE) + 1) // +1 to go from index to count

#define INACTIVITY_THRESHOLD (1000L * 60 * 2)
//#define INACTIVITY_THRESHOLD (1000 * 10)
#define SPECTRUM_DELAY 40

CRGB leds[NUM_LEDS];

uint8_t hue = 0;
uint32_t inactivity_timer = 0;
bool is_display_spectrum = true;

uint8_t note_to_led_map[NOTE_COUNT] = { 1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29, 31, 33, 35, 37, 39, 41, 43, 45, 47, 48, 50, 52, 54, 56, 58, 60, 62, 64, 66, 68, 70, 72, 74, 76, 78, 80, 82, 84, 86, 88, 90, 92, 94, 96 };

void setup() { 
		Serial.begin(57600);
		FastLED.addLeds<SK9822, DATA_PIN, CLOCK_PIN, BGR>(leds, NUM_LEDS);
		LEDS.setBrightness(84);
		Serial.print("Compiled at ");
		Serial.print(__TIME__);
		Serial.print(" on ");
		Serial.println(__DATE__);
}

void loop() { 
		uint32_t current_millis = millis();
		midiEventPacket_t message = MidiUSB.read();
		if (message.header != 0) {
				if (is_display_spectrum) {
						clear_leds();
						is_display_spectrum = false;
				}
				inactivity_timer = millis();
				process_message(message);
		} else if (is_display_spectrum) {
				display_spectrum();
		} else if (current_millis < inactivity_timer) { // millis timer wrap (approx every 50 days)
				inactivity_timer = millis();
		} else if ((current_millis - inactivity_timer) > INACTIVITY_THRESHOLD) {
				is_display_spectrum = true;
		}
}

void process_message(midiEventPacket_t message) {
		uint8_t command = message.byte1 & 0xf0;
		if (command == 0x80) {
				display_note_off(message);
		} else if (command == 0x90) {
				display_note_on(message);
		} else {
				print_unsupported_event(message);
		}
}

void clear_leds() {
		for (int i = 0; i < NUM_LEDS; i++) {
				leds[i] = 0;
		}
		FastLED.show();
}

void display_spectrum() {
		for (int i = 0, j = 1; j < NUM_LEDS; i++, j++) {
				leds[i] = leds[j];
		}
		leds[NUM_LEDS-1] = CHSV(hue++, 255, 255);
		FastLED.show();
		delay(SPECTRUM_DELAY);
}

void display_note_off(midiEventPacket_t message) {
		uint8_t channel = message.byte1 & 0xf;
		uint8_t midi_note = message.byte2;
		if (midi_note >= LOWEST_NOTE && midi_note <= HIGHEST_NOTE) {
				uint8_t led_index = note_to_led_map[message.byte2 - LOWEST_NOTE];
				leds[led_index] = 0;
				FastLED.show(); 
		}
}

void display_note_on(midiEventPacket_t message) {
		uint8_t channel = message.byte1 & 0xf;
		uint8_t midi_note = message.byte2;
		uint8_t velocity = message.byte3;
		if (midi_note >= LOWEST_NOTE && midi_note <= HIGHEST_NOTE) {
				uint8_t led_index = note_to_led_map[message.byte2 - LOWEST_NOTE];
				uint8_t i = channel / 4;
				uint8_t j = channel % 4;
				uint32_t degrees = (j * 90) + ((i * 90) / 4);
				uint8_t hue = (degrees * 255) / 360;
				uint8_t brightness = ((velocity * 255) / 127);
				hsv2rgb_spectrum(CHSV(hue, 255, brightness), leds[led_index]);
				FastLED.show(); 
		}
}

void print_unsupported_event(midiEventPacket_t message) {
		Serial.print(message.header, HEX);
		Serial.print("-");
		Serial.print(message.byte1, HEX);
		Serial.print("-");
		Serial.print(message.byte2, HEX);
		Serial.print("-");
		Serial.println(message.byte3, HEX);
}

void show_note_to_led_map() {
		for (int i = 0; i < NOTE_COUNT; i++) {
			leds[note_to_led_map[i]] = CRGB(64,0,0);
			FastLED.show();
		}
}

