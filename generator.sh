#!/bin/bash
SECONDS=0
START=$(date +%s)
time (ab  -c 4  -n 100 -g verticalE_480 http://ip/compute/20000  >  verticalE480 2>&1) | awk '{print $2}'
END=$(date +%s)
DIFF=$(( $END - $START ))
echo "It took $DIFF seconds"

#"$(($DIFF / 60))"
Min=$(($DIFF / 60))
Sec=$(($DIFF % 60))
#echo "It took $Min mintues and $Sec seconds "
echo "it tooks $Min minutes and $Sec seconds"

duration=$SECONDS
echo "$(($duration / 60)) minutes and $(($duration % 60)) seconds elapsed."
