# Holds Management UI Test Automation 

Application specification in Gherkin format automated in Java using Cucumber
with Selenium. This project uses Specification by Example approach.

# Tools and Technologies

 - Rest Assured, Cucumber and JAVA 
 - Maven – build and compilation tool
 - Docker - execution

# Usage

## Prerequisites

 - JRE 11 or higher
 - Maven 3.9 or higher
 - Docker (with compose) 27.1 (for containerized execution)

## Containerized Run

Make sure to have environment variables `HOLDS_USERNAME` and `HOLDS_PASSWORD` set.
You can use your LDAP credentials for local execution.

Refer to variables listed in `.env` file for further configuration.

Below runs the Holds Management application and executes tests locally without external dependencies.

    docker compose up -d

## Local Test Execution

You can also build and run tests without running the application, e.g. if you want to test against existing environment.
The run still requires Selenium and optionally wiremock. Use attached `compose-dependencies.yaml` to launch these in containers.

    docker compose -f compose-run-deps.yaml up -d

Below executes entire suite 

    mvn test

You can use `cucumber.filter.tags` option to run only scenarios with selected tag.

    mvn test -D"cucumber.filter.tags=@tag"

Available tags

- `@smoke` - for smoke testing
- `@mobile` - only scenarios relevant for mobile device screens

Refer to [feature files](/apps-holds-mgmt/src/test/src/test/resources/featurefiles/) for full list of tags.

### Running with Salesforce stubbed via Wiremock - DEPRECATED, use Docker instead

This is required for most of the test execution. We recommend using the containerized approach as it automatically takes care of dependencies. 

Download [wiremock standalone](https://repo1.maven.org/maven2/org/wiremock/wiremock-standalone/3.8.0/wiremock-standalone-3.8.0.jar)  and place the file `wiremock-standalone-VERSION.jar` file in project's `src/test/resources/wiremock` directory.

## Run the Batch File
1. Open a terminal or command prompt.
2. Navigate to the root directory of this project.
3. Run the batch file by typing:

    ```batch
    .\run_wiremock.bat
    ```

4. To specify a different port, provide the port number as an argument:

    ```batch
    .\run_wiremock.bat 9090
   ```

## Configuration

 - [testconfig.properties](/apps-holds-mgmt/src/test/src/test/resources/properties/testconfig.properties) - defines execution properties - DEPRECATED
 - [browserstack.yml](/apps-holds-mgmt/src/test/src/test/resources/properties/browserstack.yml) - [BrowserStack](https://automate.browserstack.com) configuration
 - [log4j2.properties](/apps-holds-mgmt/src/test/src/test/resources/log4j2.properties) - logging configuration

### DEPRECATED: Legacy configuration

To use your own `testconfig` file you use following syntax. This is deprecated in favor of environment variables.

    mvn test -Dconfig.file.path=/path/to/your/config/file

# Contributing

You can find steps in [feature files](/apps-holds-mgmt/src/test/src/test/resources/featurefiles/) and their implementation in [step definitions](/apps-holds-mgmt/src/test/src/test/java/stepDef/).

# Contacts

 - [Pavel Kacer](mailto:pavel.kacer@dhl.com)
