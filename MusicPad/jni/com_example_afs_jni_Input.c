#include <jni.h>
#include <linux/input.h>
#include <sys/ioctl.h>
#include <unistd.h>

JNIEXPORT jint JNICALL Java_com_example_afs_jni_Input_capture
  (JNIEnv *env, jclass this, jint fd, jboolean value) {
	// See EVIOCGRAB in drivers/input/evdev.c
	return ioctl(fd, EVIOCGRAB, value);
}

JNIEXPORT jshort JNICALL Java_com_example_afs_jni_Input_read_1key_1code
  (JNIEnv *env, jclass this, jint fd) {
	int rc;
	struct input_event input_event;
	while ((rc = read(fd, &input_event, sizeof(input_event))) != -1) {
		if (input_event.type == 1) { // key
			if (input_event.value == 0) {
				return input_event.code; // up
			} else if (input_event.value == 1) {
				return -input_event.code; // down
			}
		}
	}
	return 0;
}

