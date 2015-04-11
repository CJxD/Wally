@echo off

pushd %cd%
cd /d %~dp0

call test.bat 010 trainingdata/stills/aligned/grouped/010/021z010pf.jpg

popd