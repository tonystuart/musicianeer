#include "FastLED.h"
#include "MIDIUSB.h"

#define DATA_PIN 16
#define CLOCK_PIN 15
#define NUM_LEDS 144 

#define LOWEST_NOTE 36
#define HIGHEST_NOTE 84
#define NOTE_COUNT (HIGHEST_NOTE - LOWEST_NOTE) + 1

#define SPECTRUM_DELAY 40
#define INACTIVITY_THRESHOLD (1000L * 60 * 2)
#define RAMPUP_THRESHOLD 50

// 120 bpm = 2 bps = 500 ms/b

CRGB leds[NUM_LEDS];

uint8_t m_hue[NOTE_COUNT];
uint8_t m_saturation[NOTE_COUNT];
uint8_t m_brightness[NOTE_COUNT];

uint8_t hue = 0;
uint32_t rampup_timer = 0;
uint32_t inactivity_timer = 0;
bool is_display_spectrum = true;

void setup() { 
		Serial.begin(9600);
		delay(1000);
		Serial.print("Compiled at ");
		Serial.print(__TIME__);
		Serial.print(" on ");
		Serial.println(__DATE__);
		delay(1000);
		FastLED.addLeds<SK9822, DATA_PIN, CLOCK_PIN, BGR>(leds, NUM_LEDS);
		LEDS.setBrightness(84);
}

void loop() { 
		uint32_t current_millis = millis();
		midiEventPacket_t message = MidiUSB.read();
		if (m_brightness[0] && m_brightness[NOTE_COUNT-1]) {
				display_note_to_led_map();
		} else if (message.header != 0) {
				if (is_display_spectrum) {
						clear_leds();
						is_display_spectrum = false;
				}
				inactivity_timer = current_millis;
				process_message(message);
		} else if (is_display_spectrum) {
				display_spectrum();
		} else if (current_millis < inactivity_timer) { // millis timer wrap (approx every 50 days)
				inactivity_timer = millis();
		} else if ((current_millis - inactivity_timer) > INACTIVITY_THRESHOLD) {
				is_display_spectrum = true;
		} else if ((current_millis - rampup_timer) > RAMPUP_THRESHOLD) {
				rampup_timer = current_millis;
				display_rampup();
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
				uint8_t led_index = map_note_to_led(message.byte2 - LOWEST_NOTE);
				leds[led_index] = 0;
				FastLED.show(); 
		}
}

void display_note_on(midiEventPacket_t message) {
		uint8_t midi_note = message.byte2;
		if (midi_note >= LOWEST_NOTE && midi_note <= HIGHEST_NOTE) {
				uint8_t note_index = message.byte2 - LOWEST_NOTE;
				uint8_t channel = message.byte1 & 0xf;
				uint8_t velocity = message.byte3;
				uint8_t led_index = map_note_to_led(note_index);
				uint8_t hue = map_channel_to_hue(channel);
				uint8_t saturation = is_sharp(midi_note) ? 192 : 255;
				uint8_t brightness = map_velocity_to_brightness(velocity);
				hsv2rgb_spectrum(CHSV(hue, saturation, brightness), leds[led_index]);
				m_hue[note_index] = hue;
				m_saturation[note_index] = saturation;
				m_brightness[note_index] = brightness;
				FastLED.show(); 
		}
}

void display_rampup() {
		for (int i = 0; i < NOTE_COUNT; i++) {
				if (m_brightness[i] > 0 && m_brightness[i] < 128) {
						m_brightness[i] += 2;
						// Serial.print("ms=");
						// Serial.print(millis());
						// Serial.print(", note=");
						// Serial.print(i);
						// Serial.print(", brightness=");
						// Serial.println(m_brightness[i]);
						uint8_t led_index = map_note_to_led(i);
						hsv2rgb_spectrum(CHSV(m_hue[i], m_saturation[i], m_brightness[i]), leds[led_index]);
				}
		}
		FastLED.show();
}

void display_note_to_led_map() {
		for (int i = 0; i < NOTE_COUNT; i++) {
				leds[map_note_to_led(i)] = CRGB(64,0,0);
		}
		FastLED.show();
}

uint8_t map_channel_to_hue(int channel) {
		uint8_t i = channel / 4;
		uint8_t j = channel % 4;
		uint32_t degrees = (j * 90) + ((i * 90) / 4);
		uint8_t hue = (degrees * 255) / 360; // integer muldiv
		return hue;
}

uint8_t map_velocity_to_brightness(int velocity) {
		return ((velocity * 255) / 127); // integer muldiv
}

uint8_t map_note_to_led(int note) {
		// Note 0 -> Led 1 (LOWEST_NOTE)
		// Note 23 -> Led 47
		// Note 24 -> Led 48 (Middle C)
		// Note 47 -> Led 94
		// Note 48 -> Led 96 (HIGHEST_NOTE)
		uint8_t led = note * 2 + 1;
		if (note >= 24) {
				led--;
		}
		return led;
}

boolean sharps[] = { false, true, false, true, false, false, true, false, true, false, true, false};

boolean is_sharp(int midi_note) {
		uint8_t note_type = midi_note % 12;
		return sharps[note_type];
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

