@echo off

pushd %cd%
cd /d %~dp0

call test.bat 010 trainingdata/stills/aligned/grouped/010/021z010pf.jpg trainingdata/stills/aligned/grouped/010/021z010ps.jpg trainingdata/stills/aligned/grouped/010/035z010pf.jpg trainingdata/stills/aligned/grouped/010/035z010ps.jpg trainingdata/stills/aligned/grouped/010/039z010pf.jpg trainingdata/stills/aligned/grouped/010/039z010ps.jpg

popd