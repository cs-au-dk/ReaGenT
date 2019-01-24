# ReaGenT / TSTest
A tool for verifying and testing the correctness of TypeScript declaration files. 

## Setting up the project
 - Dependencies: 
    - Git (to download submodules) `sudo apt-get install git`
    - NodeJS `sudo apt-get install nodejs npm`
    - JDK 9 (or higher) `sudo add-apt-repository ppa:linuxuprising/java && sudo apt-get install oracle-java11-installer`
    - Chrome (only to run TSTest on browser based libraries)
    - Istanbul (only for coverage tests with TSTest) `npm install -g istanbul`
    
 - Getting the submodules `git submodule update --init --recursive`   
   (this can take several minutes, look if there is network activity if you think it might be stuck)
 - Set up ts-spec-reader: 
    - `cd ts-spec-reader`
    - `npm install`
    - `npm run compile`
    - `cd ..`
 - Compile the project
    - On Mac/Unix: `./gradlew fatJar`
    - On Windows: `./gradlew.bat fatJar`


Running some TSTest benchmark requires installing Chrome.  
Follow the instructions on this page to install Chrome: https://www.google.com/chrome/  
An additional package is needed on Ubuntu for the Chrome integration: sudo apt -y install libgconf2-4

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
    - `./main.sh tstest -bench classnames`
 - Running TSTest on a JavaScript implementation and TypeScript declaration (in a browser environment)
    - `./main.sh tstest -js test/benchmarks/js-cookie/js-cookie.js -ts test/benchmarks/js-cookie/declaration.d.ts -env browser`
