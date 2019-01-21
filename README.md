# TAJSCheck
A sound dynamic/static check of TypeScript declaration files. 

## Setting up the project
 - Dependencies: 
    - NodeJS
    - IntelliJ
    - TypeScript 3.0 `npm install -g typescript@2.2`
    - (istanbul, for coverage tests) `npm install -g istanbul`
 - Getting the submodules `git submodule update --init --remote`
 - Checkout the `erik-experiments` branch in the TAJS-private submodule. 
 - Checkout the `v2` branch in the ts-spec-reader submodule.
 - Set up ts-spec-reader: 
    - `cd ts-spec-reader`
    - `npm install`
    - `tsc --module commonjs src/*.ts`
 - Set up TAJS:
    - Create a .tajsconfig file in the project root with the following informations:
    ```
    tajs = <absolute path to tajs>
    ts-spec-reader = <absolute path to ts-spec-reader>
    ```
    
## Tests
To get a feeling of what TAJSCheck does, run the `UnitTests` class. 

Afterwards, look in the test/unit folder, there is a lot of tests, most of which are designed to have some error in either the declaration or implementation:

Each test is a folder, containing 3 files: 
- `declaration.d.ts`, containing a sample declaration for a library
- `implementation.js`, containing the corresponding library implementation
- `test.js` our driver, which loads `implementation.js`, and tests that it behaves correctly. 
