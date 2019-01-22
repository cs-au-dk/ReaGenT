# ReaGenT / TSTest
A verifying and testing the correctness of TypeScript declaration files. 

## Setting up the project
 - Dependencies: 
    - NodeJS `sudo apt-get install nodejs npm`
    - JDK 9 (or higher) `sudo add-apt-repository ppa:linuxuprising/java && sudo apt-get install oracle-java11-installer`
    - (Chrome is required to run TSTest on browser based libraries)
    - (istanbul, only for coverage tests with TSTest) `npm install -g istanbul`
    
 - Getting the submodules `git submodule update --init --recursive`
 - Set up ts-spec-reader: 
    - `cd ts-spec-reader`
    - `npm install`
    - `npm run compile`
    - `cd ..`
 - Compile the project
    - On Mac/Unix: `./gradlew fatJar`
    - On Windows: `./gradlew.bat fatJar`
 - Set up TAJS:
    - Create a .tajsconfig file in the project root with the following informations:
    ```
    tajs = <absolute path to tajs>
    ts-spec-reader = <absolute path to ts-spec-reader>
    ```

## Running ReaGenT / TSTest
In the below I'm going to assume that a unix-like system is used, replace `main.sh` with `main.bat` for Windows. 

 - Doing a quick test that the setup worked. 
    - `./main.sh quicktest`
 - View a help message 
    - `./main.sh`    
 - Running ReaGenT on a predefined benchmark.
    - `./main.sh reagent -bench classnames`
 - Running ReaGenT on a JavaScript implementation and TypeScript declaration
    - `./main.sh reagent -js test/benchmarks/classnames/classnames.js -ts test/benchmarks/classnames/classnames.d.ts`
 - Running TSTest on a predefined benchmark
    - `./main.sh tstest -bench js-cookie`
 - Running TSTest on a JavaScript implementation and TypeScript declaration (in a browser environment)
    - `./main.sh tstest -js test/benchmarks/js-cookie/js-cookie.js -ts test/benchmarks/js-cookie/declaration.d.ts -env browser`