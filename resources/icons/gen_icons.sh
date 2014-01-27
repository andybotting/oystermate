#!/bin/bash

WORKSPACE="/home/andy/usr/projects/oystermate/android"

if [ "$1" ]; then
    ICONS=$1
else
    ICONS=`ls *.svg`
fi

for ICON in $ICONS; do

	NAME=`basename ${ICON} .svg`

	DIMENS=`identify ${ICON} | awk '{print $3}'`
	WIDTH=${DIMENS%x*}
	HEIGHT=${DIMENS#*x}

	# Generate XXHDPI icon - using standard dimensions
	XXHDPI_WIDTH=`echo "${WIDTH}*2" | bc`
	XXHDPI_HEIGHT=`echo "${HEIGHT}*2" | bc`
	inkscape ${ICON} -e ${WORKSPACE}/res/drawable-xxhdpi/${NAME}.png -w ${XXHDPI_WIDTH} -h ${XXHDPI_HEIGHT}

	# Generate HDPI icon - using standard dimensions
	XHDPI_WIDTH=`echo "${WIDTH}*1.5" | bc`
	XHDPI_HEIGHT=`echo "${HEIGHT}*1.5" | bc`
	inkscape ${ICON} -e ${WORKSPACE}/res/drawable-xhdpi/${NAME}.png -w ${XHDPI_WIDTH} -h ${XHDPI_HEIGHT}

	# Generate HDPI icon - using standard dimensions
	inkscape ${ICON} -e ${WORKSPACE}/res/drawable-hdpi/${NAME}.png -w ${WIDTH} -h ${HEIGHT}

	# Generate MDPI icons using HDPI size / 1.5
	M_WIDTH=`echo "${WIDTH}/1.5" | bc`
	M_HEIGHT=`echo "${HEIGHT}/1.5" | bc`
	inkscape ${ICON} -e ${WORKSPACE}/res/drawable-mdpi/${NAME}.png -w ${M_WIDTH} -h ${M_HEIGHT}

	# Generate LDPI icons using HDPI size /2 
	L_WIDTH=`echo "${WIDTH}/2" | bc`
	L_HEIGHT=`echo "${HEIGHT}/2" | bc`
	inkscape ${ICON} -e ${WORKSPACE}/res/drawable-ldpi/${NAME}.png -w ${L_WIDTH} -h ${L_HEIGHT}

done
