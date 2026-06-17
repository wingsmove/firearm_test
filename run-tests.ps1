# Compiles all sources + tests and runs the JUnit 5 suite.
# Usage: ./run-tests.ps1

# Don't treat the JVM's stderr warnings as terminating errors.
$PSNativeCommandUseErrorActionPreference = $false

$jar = "lib/junit-platform-console-standalone-1.11.4.jar"
$sources = Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName }

javac -cp $jar -d out $sources
if ($LASTEXITCODE -ne 0) { throw "Compilation failed" }

java -jar $jar execute -cp out --scan-classpath --details=tree
exit $LASTEXITCODE
