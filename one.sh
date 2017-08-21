#! /bin/sh
java -Xmx512M -Djava.util.Arrays.useLegacyMergeSort=true -cp target:lib/ECLA.jar:lib/DTNConsoleConnection.jar core.DTNSim $*
