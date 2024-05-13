#!/bin/bash

if [ $# -eq 0 ]; then
    echo "No arguments provided. Pls provide Pod Tag version to start"
    exit 1
fi
PODVERSION=$1

REMOTEBUILDHOST="kogito-image-builder"
REMOTEHOSTBASEFOLDER="/root/fino-PTA/kogito"
REMOTEHOSTIMAGEFOLDER="/var/lib/docker/exports"
LOCALTMPIMAGEFOLDER="/tmp/images"

MYNAME=${PWD##*/}
MYLOWERNAME=`echo $MYNAME | tr '[:upper:]' '[:lower:]'`

#Copy the source to the VM that creates the images
ssh $REMOTEBUILDHOST mkdir $REMOTEHOSTBASEFOLDER/$MYNAME
rsync -avz . $REMOTEBUILDHOST:$REMOTEHOSTBASEFOLDER/$MYNAME
RETVAL=`ssh $REMOTEBUILDHOST s2i build $REMOTEHOSTBASEFOLDER/$MYNAME -e RUNTIME_TYPE=quarkus quay.io/kiegroup/kogito-builder:latest $MYLOWERNAME:$PODVERSION`

if [ $RETVAL -eq 0 ]
then
	ssh $REMOTEBUILDHOST rm $REMOTEHOSTIMAGEFOLDER/$MYLOWERNAME*.tar
	ssh $REMOTEBUILDHOST docker save -o $REMOTEHOSTIMAGEFOLDER/$MYLOWERNAME-$PODVERSION.tar $MYLOWERNAME:$PODVERSION
	scp $REMOTEBUILDHOST:$REMOTEHOSTIMAGEFOLDER/$MYLOWERNAME-$PODVERSION.tar $LOCALTMPIMAGEFOLDER/
	podman load -i $LOCALTMPIMAGEFOLDER/$MYLOWERNAME-$PODVERSION.tar
fi


#podman build -f src/main/docker/Dockerfile.native -t fino/$MYLOWERNAME .
#podman build -f src/main/docker/Dockerfile.jvm -t fino/$MYLOWERNAME-jvm:$PODVERSION .
