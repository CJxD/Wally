@echo off

pushd %cd%
cd /d %~dp0
cd ..

if [%1]==[] (
	set /p n=Enter number of matches to find: 
) else (
	set n=%1
)

if [%2]==[] (
	set /p img=Enter path to image: 
) else (
	set img=%2
)

cls

for /f "tokens=2,* delims= " %%a in ("%*") do set remaining=%%b

java -cp target/lib/*;target/wally-0.0.1-SNAPSHOT.jar -d64 -Xmx1G com.cjwatts.wally.QueryEngine %n% "%img%" %remaining%

popd

@pause