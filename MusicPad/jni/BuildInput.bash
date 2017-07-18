set -x

rm -f com_example_afs_jni_Input.o
rm -f libinput_jni.so

JAVA_HOME=$(readlink -f $(which javac) | sed "s:/bin/javac::")
LIB=../lib/$(uname -m)

gcc -c -fPIC \
  -Wno-int-to-pointer-cast \
  -Wno-pointer-to-int-cast \
  -Werror \
  -fmax-errors=1 \
  -I$JAVA_HOME/include \
  -I$JAVA_HOME/include/linux \
  -o com_example_afs_jni_Input.o \
  com_example_afs_jni_Input.c

gcc -shared \
  -o libinput_jni.so \
  com_example_afs_jni_Input.o

mkdir -p $LIB
mv libinput_jni.so $LIB
rm -f com_example_afs_jni_Input.o
