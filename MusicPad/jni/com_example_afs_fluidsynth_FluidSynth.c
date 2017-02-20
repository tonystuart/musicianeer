#include <jni.h>
#include <fluidsynth.h>
#include <linux/input.h>
#include <sys/ioctl.h>

JNIEXPORT void JNICALL Java_com_example_afs_fluidsynth_FluidSynth_deleteFluidAudioDriver
  (JNIEnv *env, jclass this, jlong driver) {
  	delete_fluid_audio_driver((fluid_audio_driver_t*)driver);
}

JNIEXPORT void JNICALL Java_com_example_afs_fluidsynth_FluidSynth_deleteFluidSynth
  (JNIEnv *env, jclass this, jlong synth) {
  	delete_fluid_synth((fluid_synth_t*)synth);
}

JNIEXPORT void JNICALL Java_com_example_afs_fluidsynth_FluidSynth_deleteFluidSettings
  (JNIEnv *env, jclass this, jlong settings) {
  	delete_fluid_settings((fluid_settings_t*)settings);
}

JNIEXPORT void JNICALL Java_com_example_afs_fluidsynth_FluidSynth_fluidSettingsSetstr
  (JNIEnv *env, jclass this, jlong settings, jstring hName, jstring hStr) {
	const char *name = (*env)->GetStringUTFChars(env, hName, NULL);
	const char *str = (*env)->GetStringUTFChars(env, hStr, NULL);
	fluid_settings_setstr((fluid_settings_t*)settings, name, str);
	(*env)->ReleaseStringUTFChars(env, hName, name);
	(*env)->ReleaseStringUTFChars(env, hStr, str);
}  	

JNIEXPORT jint JNICALL Java_com_example_afs_fluidsynth_FluidSynth_fluidSynthCc
  (JNIEnv *env, jclass this, jlong synth, jint chan, jint num, jint val) {
  	return fluid_synth_cc((fluid_synth_t*)synth, chan, num, val);
}

JNIEXPORT jint JNICALL Java_com_example_afs_fluidsynth_FluidSynth_fluidSynthNoteoff
  (JNIEnv *env, jclass this, jlong synth, jint chan, jint key) {
  	return fluid_synth_noteoff((fluid_synth_t*)synth, chan, key);
}

JNIEXPORT jint JNICALL Java_com_example_afs_fluidsynth_FluidSynth_fluidSynthNoteon
  (JNIEnv *env, jclass this, jlong synth, jint chan, jint key, jint vel) {
  	return fluid_synth_noteon((fluid_synth_t*)synth, chan, key, vel);
}

JNIEXPORT jint JNICALL Java_com_example_afs_fluidsynth_FluidSynth_fluidSynthPitchBend
  (JNIEnv *env, jclass this, jlong synth, jint chan, jint val) {
    return fluid_synth_pitch_bend((fluid_synth_t*)synth, chan, val);
}

JNIEXPORT jint JNICALL Java_com_example_afs_fluidsynth_FluidSynth_fluidSynthProgramChange
  (JNIEnv *env, jclass this, jlong synth, jint chan, jint program) {
    return fluid_synth_program_change((fluid_synth_t*)synth, chan, program);
}

JNIEXPORT jint JNICALL Java_com_example_afs_fluidsynth_FluidSynth_fluidSynthSetGain
  (JNIEnv *env, jclass this, jlong synth, jfloat gain) {
    fluid_synth_set_gain((fluid_synth_t*)synth, gain);
}

JNIEXPORT jlong JNICALL Java_com_example_afs_fluidsynth_FluidSynth_fluidSynthSfload
  (JNIEnv *env, jclass this, jlong synth, jstring hFilename, jint reset_presets) {
	const char *filename = (*env)->GetStringUTFChars(env, hFilename, NULL);
  	return fluid_synth_sfload((fluid_synth_t*)synth, filename, reset_presets);
	(*env)->ReleaseStringUTFChars(env, hFilename, filename);
}

JNIEXPORT fluid_audio_driver_t* JNICALL Java_com_example_afs_fluidsynth_FluidSynth_newFluidAudioDriver
  (JNIEnv *env, jclass this, jlong settings, jlong synth) {
  	return new_fluid_audio_driver((fluid_settings_t*)settings, (fluid_synth_t*)synth);
}

JNIEXPORT fluid_settings_t* JNICALL Java_com_example_afs_fluidsynth_FluidSynth_newFluidSettings
  (JNIEnv *env, jclass this) {
  	return new_fluid_settings();
}

JNIEXPORT fluid_synth_t* JNICALL Java_com_example_afs_fluidsynth_FluidSynth_newFluidSynth
  (JNIEnv *env, jclass this, jlong settings) {
	return new_fluid_synth((fluid_settings_t*)settings);
}

JNIEXPORT jint JNICALL Java_com_example_afs_fluidsynth_FluidSynth_capture
  (JNIEnv *env, jclass this, jint fd, jint value) {
	return ioctl(fd, EVIOCGRAB, value);
}

