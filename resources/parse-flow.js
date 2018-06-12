let parser = require('flow-parser');
let fs = require("fs");
if (process.argv.length !== 3) {
    throw new Error("Wrong args " + process.argv.length);
}
let fileName = process.argv[2];
let fileContent = fs.readFileSync(fileName);
let parsed = parser.parse(fileContent.toString(), {});
console.log(JSON.stringify(parsed));