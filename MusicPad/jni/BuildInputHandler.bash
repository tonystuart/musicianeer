set -x

rm -f com_example_afs_jni_InputHandler.o
rm -f libinputhandler_jni.so

JAVA_HOME=$(readlink -f $(which javac) | sed "s:/bin/javac::")
LIB=../lib/$(uname -m)

gcc -c -fPIC \
  -Wno-int-to-pointer-cast \
  -Wno-pointer-to-int-cast \
  -Werror \
  -fmax-errors=1 \
  -I$JAVA_HOME/include \
  -I$JAVA_HOME/include/linux \
  -o com_example_afs_jni_InputHandler.o \
  com_example_afs_jni_InputHandler.c

gcc -shared \
  -o libinputhandler_jni.so \
  com_example_afs_jni_InputHandler.o

mkdir -p $LIB
mv libinputhandler_jni.so $LIB
rm -f com_example_afs_jni_InputHandler.o
