#!/bin/bash

dirname="grouped"

mkdir $1/$dirname 2>/dev/null

for file in `ls -f $1`
do
	name=${file:6:13}
	session=${name:1:3}
	subject=${name:4:3}
	stance=${name:8:1}
	
	#if [ "$stance" == "f" ]; then
	#	continue
	#fi

	mkdir $1/$dirname/$subject 2>/dev/null
	cp $1/$file $1/$dirname/$subject/$name
done
