@echo off

pushd %cd%
cd /d %~dp0

call query.bat 15 trainingdata/stills/aligned/grouped/010/021z010pf.jpg

popd