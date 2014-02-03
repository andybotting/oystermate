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

	# Generate MDPI icon - using standard dimensions
	inkscape ${ICON} -e ${WORKSPACE}/res/drawable-mdpi/${NAME}.png -w ${WIDTH} -h ${HEIGHT}

	# Generate HDPI icon - using standard dimensions
	HDPI_WIDTH=`echo "${WIDTH}*1.5" | bc`
	HDPI_HEIGHT=`echo "${HEIGHT}*1.5" | bc`
	inkscape ${ICON} -e ${WORKSPACE}/res/drawable-hdpi/${NAME}.png -w ${HDPI_WIDTH} -h ${HDPI_HEIGHT}

	# Generate XHDPI icon - using standard dimensions
	XHDPI_WIDTH=`echo "${WIDTH}*2" | bc`
	XHDPI_HEIGHT=`echo "${HEIGHT}*2" | bc`
	inkscape ${ICON} -e ${WORKSPACE}/res/drawable-xhdpi/${NAME}.png -w ${XHDPI_WIDTH} -h ${XHDPI_HEIGHT}

	# Generate XXHDPI icon - using standard dimensions
	XXHDPI_WIDTH=`echo "${WIDTH}*3" | bc`
	XXHDPI_HEIGHT=`echo "${HEIGHT}*3" | bc`
	inkscape ${ICON} -e ${WORKSPACE}/res/drawable-xxhdpi/${NAME}.png -w ${XXHDPI_WIDTH} -h ${XXHDPI_HEIGHT}

	# Generate XXXHDPI icon - using standard dimensions
	XXXHDPI_WIDTH=`echo "${WIDTH}*4" | bc`
	XXXHDPI_HEIGHT=`echo "${HEIGHT}*4" | bc`
	inkscape ${ICON} -e ${WORKSPACE}/res/drawable-xxxhdpi/${NAME}.png -w ${XXXHDPI_WIDTH} -h ${XXXHDPI_HEIGHT}

done
