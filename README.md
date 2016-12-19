# TAJSCheck
A sound dynamic/static check of TypeScript declaration files. 

## Setting up the project
 - Dependencies: 
    - NodeJS
    - IntelliJ
    - TypeScript 2.1 or later
 - Getting the submodules `git submodule update --init --remote`
 - Set up ts-spec-reader: 
    - `cd ts-spec-reader`
    - `npm install`
    - `tsc --module commonjs src/*.ts`
    
## Tests
To get a feeling of what TAJSCheck does, run the `UnitTests` class. 

Afterwards, look in the test/unit folder, there is a lot of tests, most of which are designed to have some error in either the declaration or implementation:

Each test is a folder, containing 3 files: 
- `declaration.d.ts`, containing a sample declaration for a library
- `implementation.js`, containing the corresponding library implementation
- `test.js` our driver, which loads `implementation.js`, and tests that it behaves correctly. 
