/**
 * Created by erik1 on 27-10-2016.
 */

var moment = require("moment");

var assertionFailures = new Set();

function assert(cond, message) {
    if (!cond) {
        assertionFailures.add(message);
    }
    return cond;
}

var moment_1;
var moment_2;
var moment_3;
var moment_4;
var moment_5;
var moment_6;
var moment_7;
var moment_8;
var moment_9;
var moment_10;
var moment_11;



function getMoment() {
    switch (Math.random() * 11 | 0) {
        case 0: return moment_1;
        case 1: return moment_2;
        case 2: return moment_3;
        case 3: return moment_4;
        case 4: return moment_5;
        case 5: return moment_6;
        case 6: return moment_7;
        case 7: return moment_8;
        case 8: return moment_9;
        case 9: return moment_10;
        case 10: return moment_11;
    }
}
function checkTypeMomentParsingFlags(data) {
    assert(typeof data.empty === "boolean", 1);
    if (typeof data.meridiem !== "undefined") {
        assert(typeof data.meridiem === "string", "ParsingFlags.meridiem should be a string, was a " + typeof data.meridiem);
    }
    assert(typeof data.nullInput === "boolean", 3);
    assert(typeof data.invalidFormat === "boolean", 4);
    assert(typeof data.userInvalidated === "boolean", 5);
    assert(typeof data.iso === "boolean", 6);
    assert(typeof data.overflow === "number", 7);
    assert(typeof data.charsLeftOver === "number", 8);
    if (typeof data.invalidMonth !== "undefined") {
        assert(typeof data.invalidMonth === "number", "ParsingFlags.invalidMonth should be a number, was " + typeof data.invalidMonth);
    }
    assert(data.unusedTokens instanceof Array, "ParsingFlags.unusedTokens should be an array, was: " + JSON.stringify(data.unusedTokens));
    assert(data.unusedInput instanceof Array, "ParsingFlags.unusedInput should be an array, was: " + JSON.stringify(data.unusedInput));
    assert(data.parsedDateParts instanceof Array, "ParsingFlags.parsedDateParts should be an array, was: " + JSON.stringify(data.parsedDateParts))

}

function checkTypeMomentCreationData(data) {
    var result = true;
    assert(typeof data === "object", "Creation data should be an object");
    if (data.input) {
        result &= assert(typeof data.input === "string", "CreationData.input should be a string, was " + typeof data.input)
    }
    if (data.format) {
        result &= assert(typeof data.format === "string", "CreationData.format should be a string")
    }
    if (data.locale) {
        // Empty interface, nothing to check
    }
    result &= assert(typeof data.isUTC === "boolean", "CreationData.isUTC should be a boolean");
    result &= assert(typeof data.strict === "boolean", "CreationData.strict should be a boolean, was " + typeof data.strict);
}

for (var i = 0; i < 1000; i++) {
    var foo = (Math.random() * 20) | 0;
    try {
        switch (foo) {
            case 1:
                if (moment_1) {
                    break;
                }
                moment_1 = moment();
                break;
            case 2:
                if (moment_2) {
                    break;
                }
                moment_2 = moment(123);
                break;
            case 3:
                if (moment_3) {
                    break;
                }
                moment_3 = moment([123, 234]);
                break;
            case 4:
                if (moment_4) {
                    break;
                }
                moment_4 = moment("Abstract string");
                break;
            case 5:
                if (moment_5) {
                    break;
                }
                moment_5 = moment("Abstract string", "Another string");
                break;
            case 6:
                if (moment_6) {
                    break;
                }
                moment_6 = moment("Abstract string", "Another string", false);
                break;
            case 7:
                if (moment_7) {
                    break;
                }
                moment_7 = moment("Abstract string", "Another string", "language");
                break;
            case 8:
                if (moment_8) {
                    break;
                }
                moment_8 = moment("Abstract string", "Another string", "language", false);
                break;
            case 9:
                if (moment_9) {
                    break;
                }
                moment_9 = moment(new Date());
                break;
            case 10:
                if (moment_10) {
                    break;
                }
                moment_10 = moment(moment_1);
                break;
            case 11:
                if (moment_11) {
                    break;
                }
                moment_11 = moment({});
                break;
            case 12:
                var myMoment = getMoment();
                if (myMoment) {
                    assert(typeof myMoment.format() == "string", "format should be string 1");
                }
                break;
            case 13:
                var myMoment = getMoment();
                if (myMoment) {
                    assert(typeof myMoment.format("string") == "string", "format should be string 2");
                }
                break;
            case 14:
                var myMoment = getMoment();
                if (myMoment) {
                    var data = myMoment.creationData();
                    var isValid = checkTypeMomentCreationData(data);
                }
                break;
            case 15:
                var myMoment = getMoment();
                if (myMoment) {
                    var data = myMoment.parsingFlags();
                    var isValid = checkTypeMomentParsingFlags(data);
                }
        }
    } catch(e) {
        // ignored.
    }
}

assertionFailures = Array.from(assertionFailures);
for (var i = 0; i < assertionFailures.length; i++) {
    console.log(assertionFailures[i]);
}

console.log();
