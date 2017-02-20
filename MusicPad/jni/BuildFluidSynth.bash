set -x

rm -f com_example_afs_fluidsynth_FluidSynth.o
rm -f libfluidsynth_jni.so

JAVA_HOME=$(readlink -f $(which javac) | sed "s:/bin/javac::")
LIB=../lib/$(uname -p)

gcc -c -fPIC \
  -Wno-int-to-pointer-cast \
  -Wno-pointer-to-int-cast \
  -Werror \
  -fmax-errors=1 \
  -I$JAVA_HOME/include \
  -I$JAVA_HOME/include/linux \
  -o com_example_afs_fluidsynth_FluidSynth.o \
  com_example_afs_fluidsynth_FluidSynth.c

gcc -shared \
  -o libfluidsynth_jni.so \
  com_example_afs_fluidsynth_FluidSynth.o \
  -lfluidsynth

mv libfluidsynth_jni.so $LIB
rm -f com_example_afs_fluidsynth_FluidSynth.o
