#!/bin/sh

# variables a configurar
VERSION=0.1
JAVA_HOME=/usr/lib/jvm/java-6-sun

# otras variables
SRC=../src
CFG=../cfg
DIST=../dist
DOC=../doc
JAVAC=$JAVA_HOME/bin/javac
JAR=$JAVA_HOME/bin/jar
TEMPDIR=./tmp

# crear directorio temporal y de destino
rm -fr $TEMPDIR
mkdir -p $TEMPDIR/build
rm -fr $DIST
mkdir $DIST

# compilar
$JAVAC -d $TEMPDIR/build $SRC/com/supertorpe/musicscoregen/*.java

# empaquetar
mkdir $TEMPDIR/build/META-INF
echo "Manifest-Version: 1.0" > $TEMPDIR/build/META-INF/MANIFEST.MF
echo "Version: $VERSION" >> $TEMPDIR/build/META-INF/MANIFEST.MF
echo "Main-Class: com.supertorpe.musicscoregen.MusicScoreGen" >> $TEMPDIR/build/META-INF/MANIFEST.MF
jar -cvfM $TEMPDIR/MusicScoreGen.jar -C $TEMPDIR/build .
zip  -j $DIST/MusicScoreGen_v$VERSION.zip $TEMPDIR/MusicScoreGen.jar $CFG/musicscoregen.properties $DOC/leeme.txt ./run.sh

# borrar directorio temporal
rm -fr $TEMPDIR
