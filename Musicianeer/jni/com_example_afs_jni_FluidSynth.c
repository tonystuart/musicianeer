/* Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.
*/

#include <jni.h>
#include <fluidsynth.h>
#include <linux/input.h>
#include <sys/ioctl.h>

JNIEXPORT void JNICALL Java_com_example_afs_jni_FluidSynth_delete_1fluid_1audio_1driver
  (JNIEnv *env, jclass this, jlong driver) {
  	delete_fluid_audio_driver((fluid_audio_driver_t*)driver);
}

JNIEXPORT void JNICALL Java_com_example_afs_jni_FluidSynth_delete_1fluid_1synth
  (JNIEnv *env, jclass this, jlong synth) {
  	delete_fluid_synth((fluid_synth_t*)synth);
}

JNIEXPORT void JNICALL Java_com_example_afs_jni_FluidSynth_delete_1fluid_1settings
  (JNIEnv *env, jclass this, jlong settings) {
  	delete_fluid_settings((fluid_settings_t*)settings);
}

JNIEXPORT void JNICALL Java_com_example_afs_jni_FluidSynth_fluid_1settings_1setint
  (JNIEnv *env, jclass this, jlong settings, jstring hName, jint val) {
	const char *name = (*env)->GetStringUTFChars(env, hName, NULL);
	fluid_settings_setint((fluid_settings_t*)settings, name, val);
	(*env)->ReleaseStringUTFChars(env, hName, name);
}  	

JNIEXPORT void JNICALL Java_com_example_afs_jni_FluidSynth_fluid_1settings_1setstr
  (JNIEnv *env, jclass this, jlong settings, jstring hName, jstring hStr) {
	const char *name = (*env)->GetStringUTFChars(env, hName, NULL);
	const char *str = (*env)->GetStringUTFChars(env, hStr, NULL);
	fluid_settings_setstr((fluid_settings_t*)settings, name, str);
	(*env)->ReleaseStringUTFChars(env, hName, name);
	(*env)->ReleaseStringUTFChars(env, hStr, str);
}  	

JNIEXPORT jint JNICALL Java_com_example_afs_jni_FluidSynth_fluid_1synth_1all_1notes_1off
  (JNIEnv *env, jclass this, jlong synth, jint chan) {
  	return fluid_synth_all_notes_off((fluid_synth_t*)synth, chan);
}

JNIEXPORT jint JNICALL Java_com_example_afs_jni_FluidSynth_fluid_1synth_1all_1sounds_1off
  (JNIEnv *env, jclass this, jlong synth, jint chan) {
  	return fluid_synth_all_sounds_off((fluid_synth_t*)synth, chan);
}

JNIEXPORT jint JNICALL Java_com_example_afs_jni_FluidSynth_fluid_1synth_1cc
  (JNIEnv *env, jclass this, jlong synth, jint chan, jint num, jint val) {
  	return fluid_synth_cc((fluid_synth_t*)synth, chan, num, val);
}

JNIEXPORT jint JNICALL Java_com_example_afs_jni_FluidSynth_fluid_1synth_1channel_1pressure
  (JNIEnv *env, jclass this, jlong synth, jint chan, jint val) {
  	return fluid_synth_noteoff((fluid_synth_t*)synth, chan, val);
}

JNIEXPORT jint JNICALL Java_com_example_afs_jni_FluidSynth_fluid_1synth_1noteoff
  (JNIEnv *env, jclass this, jlong synth, jint chan, jint key) {
  	return fluid_synth_noteoff((fluid_synth_t*)synth, chan, key);
}

JNIEXPORT jint JNICALL Java_com_example_afs_jni_FluidSynth_fluid_1synth_1noteon
  (JNIEnv *env, jclass this, jlong synth, jint chan, jint key, jint vel) {
  	return fluid_synth_noteon((fluid_synth_t*)synth, chan, key, vel);
}

JNIEXPORT jint JNICALL Java_com_example_afs_jni_FluidSynth_fluid_1synth_1pitch_1bend
  (JNIEnv *env, jclass this, jlong synth, jint chan, jint val) {
    return fluid_synth_pitch_bend((fluid_synth_t*)synth, chan, val);
}

JNIEXPORT jint JNICALL Java_com_example_afs_jni_FluidSynth_fluid_1synth_1program_1change
  (JNIEnv *env, jclass this, jlong synth, jint chan, jint program) {
    return fluid_synth_program_change((fluid_synth_t*)synth, chan, program);
}

JNIEXPORT jint JNICALL Java_com_example_afs_jni_FluidSynth_fluid_1synth_1set_1channel_1type
  (JNIEnv *env, jclass this, jlong synth, jint chan, jint type) {
    int rc = fluid_synth_set_channel_type((fluid_synth_t*)synth, chan, type);
}

JNIEXPORT jint JNICALL Java_com_example_afs_jni_FluidSynth_fluid_1synth_1set_1gain
  (JNIEnv *env, jclass this, jlong synth, jfloat gain) {
    fluid_synth_set_gain((fluid_synth_t*)synth, gain);
}

JNIEXPORT jlong JNICALL Java_com_example_afs_jni_FluidSynth_fluid_1synth_1sfload
  (JNIEnv *env, jclass this, jlong synth, jstring hFilename, jint reset_presets) {
	const char *filename = (*env)->GetStringUTFChars(env, hFilename, NULL);
  	return fluid_synth_sfload((fluid_synth_t*)synth, filename, reset_presets);
	(*env)->ReleaseStringUTFChars(env, hFilename, filename);
}

JNIEXPORT fluid_audio_driver_t* JNICALL Java_com_example_afs_jni_FluidSynth_new_1fluid_1audio_1driver
  (JNIEnv *env, jclass this, jlong settings, jlong synth) {
  	return new_fluid_audio_driver((fluid_settings_t*)settings, (fluid_synth_t*)synth);
}

JNIEXPORT fluid_settings_t* JNICALL Java_com_example_afs_jni_FluidSynth_new_1fluid_1settings
  (JNIEnv *env, jclass this) {
  	return new_fluid_settings();
}

JNIEXPORT fluid_synth_t* JNICALL Java_com_example_afs_jni_FluidSynth_new_1fluid_1synth
  (JNIEnv *env, jclass this, jlong settings) {
	return new_fluid_synth((fluid_settings_t*)settings);
}

