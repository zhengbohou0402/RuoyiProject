@echo off
echo.
echo [Ϣ] ʹJarWeb̡
echo.

cd %~dp0
cd ../icm-admin/target

set JAVA_OPTS=-Xms256m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m

java -jar %JAVA_OPTS% icm-admin.jar

cd bin
pause