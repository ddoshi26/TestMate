@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  Test-Mate startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and TEST_MATE_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\testmate-0.0.1.jar;%APP_HOME%\lib\commons-cli-1.3.jar;%APP_HOME%\lib\slf4j-api-1.7.21.jar;%APP_HOME%\lib\aws-java-sdk-1.11.38.jar;%APP_HOME%\lib\jsoup-1.9.2.jar;%APP_HOME%\lib\javax.servlet-api-3.0.1.jar;%APP_HOME%\lib\aws-java-sdk-support-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-simpledb-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-servicecatalog-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-simpleworkflow-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-storagegateway-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-route53-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-s3-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-importexport-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-sts-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-sqs-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-rds-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-redshift-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-elasticbeanstalk-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-glacier-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-iam-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-datapipeline-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-elasticloadbalancing-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-elasticloadbalancingv2-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-emr-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-elasticache-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-elastictranscoder-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-ec2-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-dynamodb-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-sns-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-cloudtrail-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-cloudwatch-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-logs-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-events-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-cognitoidentity-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-cognitosync-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-directconnect-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-cloudformation-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-cloudfront-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-kinesis-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-opsworks-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-ses-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-autoscaling-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-cloudsearch-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-cloudwatchmetrics-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-codedeploy-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-codepipeline-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-kms-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-config-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-lambda-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-ecs-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-ecr-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-cloudhsm-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-ssm-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-workspaces-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-machinelearning-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-directory-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-efs-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-codecommit-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-devicefarm-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-elasticsearch-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-waf-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-marketplacecommerceanalytics-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-inspector-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-iot-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-api-gateway-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-acm-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-gamelift-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-dms-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-marketplacemeteringservice-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-cognitoidp-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-discovery-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-applicationautoscaling-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-snowball-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-core-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-models-1.11.38.jar;%APP_HOME%\lib\aws-java-sdk-swf-libraries-1.11.22.jar;%APP_HOME%\lib\jmespath-java-1.0.jar;%APP_HOME%\lib\httpclient-4.5.2.jar;%APP_HOME%\lib\ion-java-1.0.0.jar;%APP_HOME%\lib\jackson-databind-2.6.6.jar;%APP_HOME%\lib\jackson-dataformat-cbor-2.6.6.jar;%APP_HOME%\lib\joda-time-2.8.1.jar;%APP_HOME%\lib\httpcore-4.4.4.jar;%APP_HOME%\lib\commons-codec-1.9.jar;%APP_HOME%\lib\jackson-annotations-2.6.0.jar;%APP_HOME%\lib\jackson-core-2.6.6.jar;%APP_HOME%\lib\commons-logging-1.2.jar

@rem Execute Test-Mate
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %TEST_MATE_OPTS%  -classpath "%CLASSPATH%" controllers/testmate %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable TEST_MATE_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%TEST_MATE_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
