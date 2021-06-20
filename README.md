# sonar-stryker-net-plugin

[![Quality gate](https://sonarcloud.io/api/project_badges/quality_gate?project=Trendyol_sonar-stryker-net-plugin)](https://sonarcloud.io/dashboard?id=Trendyol_sonar-stryker-net-plugin)

## Getting started

As of now the sonar-stryker-net-plugin is not distributed via any artifact repository. 
However, it can be installed manually:

### Install plugin to SonarQube

1. Make sure you have git and maven installed locally. Make sure you have a running SonarQube server (or installed locally).
2. Clone this repository
    ```bash
    git clone git@github.com:mstfymrtc/sonar-stryker-net-plugin.git
    ```
3. Package the sonar plugin
    ```bash
    cd sonar-stryker-net-plugin
    mvn package
    ```
4. Copy the `target/sonar-stryker-net-plugin.jar` to the downloads folder of your SonarQube instance (for example: `/sonarqube-8.0/extensions/downloads`)
5. Restart SonarQube
6. Log on to the SonarQuberu web interface, go to 'rules'. Two new rules should be present under language "JavaScript". Enable these rules for the profile of choice:
    * Lurking Mutants
    * Survived Mutants

### Run Stryker.NET

Next, setup Stryker.NET in your project. See [stryker-mutator.github.io](https://stryker-mutator.github.io). Make sure to enable the `'json'` reporter. 
This will record all reporter events and write them to disk (`StrykerOutput/{analysis folder by date}/reports/mutation-report.json` by default). 
Make sure that works before proceeding.

### Report survived mutants

Run sonar in your projects folder using [dotnet-sonar-scanner](https://docs.sonarqube.org/latest/analysis/scan/sonarscanner-for-msbuild/)

An example for running dotnet-sonar-scanner:

```
# sonar.sh
SONAR_USER="admin"
SONAR_PASS="12345"
SONAR_HOST_URL="http://localhost:9000"
SONAR_PROJECT_NAME="test-project"
dotnet-sonarscanner begin \
      /k:$SONAR_PROJECT_NAME \
      /n:$SONAR_PROJECT_NAME \
      /d:sonar.host.url=$SONAR_HOST_URL \
      /d:sonar.login=$SONAR_USER \
      /d:sonar.password=$SONAR_PASS \
      /d:sonar.cs.opencover.reportsPaths="./tests/coverage.opencover.xml" 
dotnet build -nologo -consoleLoggerParameters:NoSummary -verbosity:quiet --no-restore
dotnet-sonarscanner end \
      /d:sonar.login=$SONAR_USER \
      /d:sonar.password=$SONAR_PASS 

```
