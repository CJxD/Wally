@echo off

pushd %cd%
cd /d %~dp0
cd ..

if [%1]==[] (
	set /p id=Enter subject ID: 
) else (
	set id=%1
)

if [%2]==[] (
	set /p img=Enter path to image: 
) else (
	set img=%2
)

cls

for /f "tokens=2,* delims= " %%a in ("%*") do set remaining=%%b

java -cp target/lib/*;target/wally-0.0.1-SNAPSHOT.jar;target/wally-0.0.1-SNAPSHOT-tests.jar -d64 -Xmx1G wally.QueryTest %id% "%img%" %remaining%

popd

@pause