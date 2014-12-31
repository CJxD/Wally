#!/bin/bash

mkdir $1/grouped

for file in `ls -f $1`
do
	name=${file:6:13}
	session=${name:1:3}
	subject=${name:4:3}
	stance=${name:8:1}

	mkdir $1/grouped/$subject
	cp $1/$file $1/grouped/$subject/$name
done
