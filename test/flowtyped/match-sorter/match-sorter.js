'use strict';

function createCommonjsModule(fn, module) {
    return module = { exports: {} }, fn(module, module.exports), module.exports;
}

var diacritics = createCommonjsModule(function (module) {
// Diacritics.js
//
// Started as something to be an equivalent of the Google Java Library diacritics library for JavaScript.
// Found this: http://jsperf.com/diacritics/6 and converted it into a reusable module.
//
// @author Nijiko Yonskai
// @license MIT
// @copyright Nijikokun 2013 <nijikokun@gmail.com>
    (function (name, definition) {
        if ('object' != 'undefined' && module.exports) module.exports = definition();
        else if (typeof undefined == 'function' && undefined.amd) undefined(definition);
        else this[name] = definition();
    })('Diacritics', function () {
        // Create public object
        var output = {
            map: {}
        };

        // Create private reference map.
        var reference = [
            {'base':' ',    'letters':'\u00A0'},
            {'base':'A',    'letters':'\u0041\u24B6\uFF21\u00C0\u00C1\u00C2\u1EA6\u1EA4\u1EAA\u1EA8\u00C3\u0100\u0102\u1EB0\u1EAE\u1EB4\u1EB2\u0226\u01E0\u00C4\u01DE\u1EA2\u00C5\u01FA\u01CD\u0200\u0202\u1EA0\u1EAC\u1EB6\u1E00\u0104\u023A\u2C6F'},
            {'base':'AA',   'letters':'\uA732'},
            {'base':'AE',   'letters':'\u00C6\u01FC\u01E2'},
            {'base':'AO',   'letters':'\uA734'},
            {'base':'AU',   'letters':'\uA736'},
            {'base':'AV',   'letters':'\uA738\uA73A'},
            {'base':'AY',   'letters':'\uA73C'},
            {'base':'B',    'letters':'\u0042\u24B7\uFF22\u1E02\u1E04\u1E06\u0243\u0182\u0181'},
            {'base':'C',    'letters':'\u0043\u24B8\uFF23\u0106\u0108\u010A\u010C\u00C7\u1E08\u0187\u023B\uA73E'},
            {'base':'D',    'letters':'\u0044\u24B9\uFF24\u1E0A\u010E\u1E0C\u1E10\u1E12\u1E0E\u0110\u018B\u018A\u0189\uA779'},
            {'base':'DZ',   'letters':'\u01F1\u01C4'},
            {'base':'Dz',   'letters':'\u01F2\u01C5'},
            {'base':'E',    'letters':'\u0045\u24BA\uFF25\u00C8\u00C9\u00CA\u1EC0\u1EBE\u1EC4\u1EC2\u1EBC\u0112\u1E14\u1E16\u0114\u0116\u00CB\u1EBA\u011A\u0204\u0206\u1EB8\u1EC6\u0228\u1E1C\u0118\u1E18\u1E1A\u0190\u018E'},
            {'base':'F',    'letters':'\u0046\u24BB\uFF26\u1E1E\u0191\uA77B'},
            {'base':'G',    'letters':'\u0047\u24BC\uFF27\u01F4\u011C\u1E20\u011E\u0120\u01E6\u0122\u01E4\u0193\uA7A0\uA77D\uA77E'},
            {'base':'H',    'letters':'\u0048\u24BD\uFF28\u0124\u1E22\u1E26\u021E\u1E24\u1E28\u1E2A\u0126\u2C67\u2C75\uA78D'},
            {'base':'I',    'letters':'\u0049\u24BE\uFF29\u00CC\u00CD\u00CE\u0128\u012A\u012C\u0130\u00CF\u1E2E\u1EC8\u01CF\u0208\u020A\u1ECA\u012E\u1E2C\u0197'},
            {'base':'J',    'letters':'\u004A\u24BF\uFF2A\u0134\u0248'},
            {'base':'K',    'letters':'\u004B\u24C0\uFF2B\u1E30\u01E8\u1E32\u0136\u1E34\u0198\u2C69\uA740\uA742\uA744\uA7A2'},
            {'base':'L',    'letters':'\u004C\u24C1\uFF2C\u013F\u0139\u013D\u1E36\u1E38\u013B\u1E3C\u1E3A\u0141\u023D\u2C62\u2C60\uA748\uA746\uA780'},
            {'base':'LJ',   'letters':'\u01C7'},
            {'base':'Lj',   'letters':'\u01C8'},
            {'base':'M',    'letters':'\u004D\u24C2\uFF2D\u1E3E\u1E40\u1E42\u2C6E\u019C'},
            {'base':'N',    'letters':'\u004E\u24C3\uFF2E\u01F8\u0143\u00D1\u1E44\u0147\u1E46\u0145\u1E4A\u1E48\u0220\u019D\uA790\uA7A4'},
            {'base':'NJ',   'letters':'\u01CA'},
            {'base':'Nj',   'letters':'\u01CB'},
            {'base':'O',    'letters':'\u004F\u24C4\uFF2F\u00D2\u00D3\u00D4\u1ED2\u1ED0\u1ED6\u1ED4\u00D5\u1E4C\u022C\u1E4E\u014C\u1E50\u1E52\u014E\u022E\u0230\u00D6\u022A\u1ECE\u0150\u01D1\u020C\u020E\u01A0\u1EDC\u1EDA\u1EE0\u1EDE\u1EE2\u1ECC\u1ED8\u01EA\u01EC\u00D8\u01FE\u0186\u019F\uA74A\uA74C'},
            {'base':'OI',   'letters':'\u01A2'},
            {'base':'OO',   'letters':'\uA74E'},
            {'base':'OU',   'letters':'\u0222'},
            {'base':'P',    'letters':'\u0050\u24C5\uFF30\u1E54\u1E56\u01A4\u2C63\uA750\uA752\uA754'},
            {'base':'Q',    'letters':'\u0051\u24C6\uFF31\uA756\uA758\u024A'},
            {'base':'R',    'letters':'\u0052\u24C7\uFF32\u0154\u1E58\u0158\u0210\u0212\u1E5A\u1E5C\u0156\u1E5E\u024C\u2C64\uA75A\uA7A6\uA782'},
            {'base':'S',    'letters':'\u0053\u24C8\uFF33\u1E9E\u015A\u1E64\u015C\u1E60\u0160\u1E66\u1E62\u1E68\u0218\u015E\u2C7E\uA7A8\uA784'},
            {'base':'T',    'letters':'\u0054\u24C9\uFF34\u1E6A\u0164\u1E6C\u021A\u0162\u1E70\u1E6E\u0166\u01AC\u01AE\u023E\uA786'},
            {'base':'Th',   'letters':'\u00DE'},
            {'base':'TZ',   'letters':'\uA728'},
            {'base':'U',    'letters':'\u0055\u24CA\uFF35\u00D9\u00DA\u00DB\u0168\u1E78\u016A\u1E7A\u016C\u00DC\u01DB\u01D7\u01D5\u01D9\u1EE6\u016E\u0170\u01D3\u0214\u0216\u01AF\u1EEA\u1EE8\u1EEE\u1EEC\u1EF0\u1EE4\u1E72\u0172\u1E76\u1E74\u0244'},
            {'base':'V',    'letters':'\u0056\u24CB\uFF36\u1E7C\u1E7E\u01B2\uA75E\u0245'},
            {'base':'VY',   'letters':'\uA760'},
            {'base':'W',    'letters':'\u0057\u24CC\uFF37\u1E80\u1E82\u0174\u1E86\u1E84\u1E88\u2C72'},
            {'base':'X',    'letters':'\u0058\u24CD\uFF38\u1E8A\u1E8C'},
            {'base':'Y',    'letters':'\u0059\u24CE\uFF39\u1EF2\u00DD\u0176\u1EF8\u0232\u1E8E\u0178\u1EF6\u1EF4\u01B3\u024E\u1EFE'},
            {'base':'Z',    'letters':'\u005A\u24CF\uFF3A\u0179\u1E90\u017B\u017D\u1E92\u1E94\u01B5\u0224\u2C7F\u2C6B\uA762'},
            {'base':'a',    'letters':'\u0061\u24D0\uFF41\u1E9A\u00E0\u00E1\u00E2\u1EA7\u1EA5\u1EAB\u1EA9\u00E3\u0101\u0103\u1EB1\u1EAF\u1EB5\u1EB3\u0227\u01E1\u00E4\u01DF\u1EA3\u00E5\u01FB\u01CE\u0201\u0203\u1EA1\u1EAD\u1EB7\u1E01\u0105\u2C65\u0250\u0251'},
            {'base':'aa',   'letters':'\uA733'},
            {'base':'ae',   'letters':'\u00E6\u01FD\u01E3'},
            {'base':'ao',   'letters':'\uA735'},
            {'base':'au',   'letters':'\uA737'},
            {'base':'av',   'letters':'\uA739\uA73B'},
            {'base':'ay',   'letters':'\uA73D'},
            {'base':'b',    'letters':'\u0062\u24D1\uFF42\u1E03\u1E05\u1E07\u0180\u0183\u0253'},
            {'base':'c',    'letters':'\u0063\u24D2\uFF43\u0107\u0109\u010B\u010D\u00E7\u1E09\u0188\u023C\uA73F\u2184'},
            {'base':'d',    'letters':'\u0064\u24D3\uFF44\u1E0B\u010F\u1E0D\u1E11\u1E13\u1E0F\u0111\u018C\u0256\u0257\uA77A'},
            {'base':'dz',   'letters':'\u01F3\u01C6'},
            {'base':'e',    'letters':'\u0065\u24D4\uFF45\u00E8\u00E9\u00EA\u1EC1\u1EBF\u1EC5\u1EC3\u1EBD\u0113\u1E15\u1E17\u0115\u0117\u00EB\u1EBB\u011B\u0205\u0207\u1EB9\u1EC7\u0229\u1E1D\u0119\u1E19\u1E1B\u0247\u025B\u01DD'},
            {'base':'f',    'letters':'\u0066\u24D5\uFF46\u1E1F\u0192\uA77C'},
            {'base':'ff',   'letters':'\uFB00'},
            {'base':'fi',   'letters':'\uFB01'},
            {'base':'fl',   'letters':'\uFB02'},
            {'base':'ffi',  'letters':'\uFB03'},
            {'base':'ffl',  'letters':'\uFB04'},
            {'base':'g',    'letters':'\u0067\u24D6\uFF47\u01F5\u011D\u1E21\u011F\u0121\u01E7\u0123\u01E5\u0260\uA7A1\u1D79\uA77F'},
            {'base':'h',    'letters':'\u0068\u24D7\uFF48\u0125\u1E23\u1E27\u021F\u1E25\u1E29\u1E2B\u1E96\u0127\u2C68\u2C76\u0265'},
            {'base':'hv',   'letters':'\u0195'},
            {'base':'i',    'letters':'\u0069\u24D8\uFF49\u00EC\u00ED\u00EE\u0129\u012B\u012D\u00EF\u1E2F\u1EC9\u01D0\u0209\u020B\u1ECB\u012F\u1E2D\u0268\u0131'},
            {'base':'j',    'letters':'\u006A\u24D9\uFF4A\u0135\u01F0\u0249'},
            {'base':'k',    'letters':'\u006B\u24DA\uFF4B\u1E31\u01E9\u1E33\u0137\u1E35\u0199\u2C6A\uA741\uA743\uA745\uA7A3'},
            {'base':'l',    'letters':'\u006C\u24DB\uFF4C\u0140\u013A\u013E\u1E37\u1E39\u013C\u1E3D\u1E3B\u017F\u0142\u019A\u026B\u2C61\uA749\uA781\uA747'},
            {'base':'lj',   'letters':'\u01C9'},
            {'base':'m',    'letters':'\u006D\u24DC\uFF4D\u1E3F\u1E41\u1E43\u0271\u026F'},
            {'base':'n',    'letters':'\x6E\xF1\u006E\u24DD\uFF4E\u01F9\u0144\u00F1\u1E45\u0148\u1E47\u0146\u1E4B\u1E49\u019E\u0272\u0149\uA791\uA7A5\u043B\u0509'},
            {'base':'nj',   'letters':'\u01CC'},
            {'base':'o',    'letters':'\u07C0\u006F\u24DE\uFF4F\u00F2\u00F3\u00F4\u1ED3\u1ED1\u1ED7\u1ED5\u00F5\u1E4D\u022D\u1E4F\u014D\u1E51\u1E53\u014F\u022F\u0231\u00F6\u022B\u1ECF\u0151\u01D2\u020D\u020F\u01A1\u1EDD\u1EDB\u1EE1\u1EDF\u1EE3\u1ECD\u1ED9\u01EB\u01ED\u00F8\u01FF\u0254\uA74B\uA74D\u0275'},
            {'base':'oe',   'letters':'\u0152\u0153'},
            {'base':'oi',   'letters':'\u01A3'},
            {'base':'ou',   'letters':'\u0223'},
            {'base':'oo',   'letters':'\uA74F'},
            {'base':'p',    'letters':'\u0070\u24DF\uFF50\u1E55\u1E57\u01A5\u1D7D\uA751\uA753\uA755'},
            {'base':'q',    'letters':'\u0071\u24E0\uFF51\u024B\uA757\uA759'},
            {'base':'r',    'letters':'\u0072\u24E1\uFF52\u0155\u1E59\u0159\u0211\u0213\u1E5B\u1E5D\u0157\u1E5F\u024D\u027D\uA75B\uA7A7\uA783'},
            {'base':'s',    'letters':'\u0073\u24E2\uFF53\u00DF\u015B\u1E65\u015D\u1E61\u0161\u1E67\u1E63\u1E69\u0219\u015F\u023F\uA7A9\uA785\u1E9B'},
            {'base':'ss',   'letters':'\xDF'},
            {'base':'t',    'letters':'\u0074\u24E3\uFF54\u1E6B\u1E97\u0165\u1E6D\u021B\u0163\u1E71\u1E6F\u0167\u01AD\u0288\u2C66\uA787'},
            {'base':'th',   'letters':'\u00FE'},
            {'base':'tz',   'letters':'\uA729'},
            {'base':'u',    'letters': '\u0075\u24E4\uFF55\u00F9\u00FA\u00FB\u0169\u1E79\u016B\u1E7B\u016D\u00FC\u01DC\u01D8\u01D6\u01DA\u1EE7\u016F\u0171\u01D4\u0215\u0217\u01B0\u1EEB\u1EE9\u1EEF\u1EED\u1EF1\u1EE5\u1E73\u0173\u1E77\u1E75\u0289'},
            {'base':'v',    'letters':'\u0076\u24E5\uFF56\u1E7D\u1E7F\u028B\uA75F\u028C'},
            {'base':'vy',   'letters':'\uA761'},
            {'base':'w',    'letters':'\u0077\u24E6\uFF57\u1E81\u1E83\u0175\u1E87\u1E85\u1E98\u1E89\u2C73'},
            {'base':'x',    'letters':'\u0078\u24E7\uFF58\u1E8B\u1E8D'},
            {'base':'y',    'letters':'\u0079\u24E8\uFF59\u1EF3\u00FD\u0177\u1EF9\u0233\u1E8F\u00FF\u1EF7\u1E99\u1EF5\u01B4\u024F\u1EFF'},
            {'base':'z',    'letters':'\u007A\u24E9\uFF5A\u017A\u1E91\u017C\u017E\u1E93\u1E95\u01B6\u0225\u0240\u2C6C\uA763'}
        ];

        // Generate reference mapping
        for (var i = 0, refLength = reference.length; i < refLength; i++){
            var letters = reference[i].letters.split("");

            for (var j = 0, letLength = letters.length; j < letLength; j++){
                output.map[letters[j]] = reference[i].base;
            }
        }

        /**
         * Clean accents (diacritics) from string.
         *
         * @param  {String} input String to be cleaned of diacritics.
         * @return {String}
         */
        output.clean = function (input) {
            if (!input || !input.length || input.length < 1) {
                return "";
            }

            var string = "";
            var letters = input.split("");
            var index = 0;
            var length = letters.length;
            var letter;

            for (; index < length; index++) {
                letter = letters[index];
                string += letter in output.map ? output.map[letter] : letter;
            }

            return string;
        };

        return output;
    });
});

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) {
    return typeof obj;
} : function (obj) {
    return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj;
};

var _extends = Object.assign || function (target) {
    for (var i = 1; i < arguments.length; i++) {
        var source = arguments[i];

        for (var key in source) {
            if (Object.prototype.hasOwnProperty.call(source, key)) {
                target[key] = source[key];
            }
        }
    }

    return target;
};

/**
 * @name match-sorter
 * @license MIT license.
 * @copyright (c) 2017 Kent C. Dodds
 * @author Kent C. Dodds <kent@doddsfamily.us>
 */

var rankings = {
    CASE_SENSITIVE_EQUAL: 9,
    EQUAL: 8,
    STARTS_WITH: 7,
    WORD_STARTS_WITH: 6,
    STRING_CASE: 5,
    STRING_CASE_ACRONYM: 4,
    CONTAINS: 3,
    ACRONYM: 2,
    MATCHES: 1,
    NO_MATCH: 0
};

var caseRankings = {
    CAMEL: 0.8,
    PASCAL: 0.6,
    KEBAB: 0.4,
    SNAKE: 0.2,
    NO_CASE: 0
};

matchSorter.rankings = rankings;
matchSorter.caseRankings = caseRankings;

/**
 * Takes an array of items and a value and returns a new array with the items that match the given value
 * @param {Array} items - the items to sort
 * @param {String} value - the value to use for ranking
 * @param {Object} options - Some options to configure the sorter
 * @return {Array} - the new sorted array
 */
function matchSorter(items, value) {
    var options = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};

    // not performing any search/sort if value(search term) is empty
    if (!value) return items;

    var keys = options.keys,
        _options$threshold = options.threshold,
        threshold = _options$threshold === undefined ? rankings.MATCHES : _options$threshold;

    var matchedItems = items.reduce(function (matches, item, index) {
        var _getHighestRanking = getHighestRanking(item, keys, value, options),
            rank = _getHighestRanking.rank,
            keyIndex = _getHighestRanking.keyIndex;

        if (rank >= threshold) {
            matches.push({ item: item, rank: rank, index: index, keyIndex: keyIndex });
        }
        return matches;
    }, []);
    return matchedItems.sort(sortRankedItems).map(function (_ref) {
        var item = _ref.item;
        return item;
    });
}

/**
 * Gets the highest ranking for value for the given item based on its values for the given keys
 * @param {*} item - the item to rank
 * @param {Array} keys - the keys to get values from the item for the ranking
 * @param {String} value - the value to rank against
 * @param {Object} options - options to control the ranking
 * @return {Number} - the highest ranking
 */
function getHighestRanking(item, keys, value, options) {
    if (!keys) {
        return { rank: getMatchRanking(item, value, options), keyIndex: -1 };
    }
    var valuesToRank = getAllValuesToRank(item, keys);
    return valuesToRank.reduce(function (_ref2, _ref3, i) {
        var rank = _ref2.rank,
            keyIndex = _ref2.keyIndex;
        var itemValue = _ref3.itemValue,
            attributes = _ref3.attributes;

        var newRank = getMatchRanking(itemValue, value, options);
        var minRanking = attributes.minRanking,
            maxRanking = attributes.maxRanking;

        if (newRank < minRanking && newRank >= rankings.MATCHES) {
            newRank = minRanking;
        } else if (newRank > maxRanking) {
            newRank = maxRanking;
        }
        if (newRank > rank) {
            rank = newRank;
            keyIndex = i;
        }
        return { rank: rank, keyIndex: keyIndex };
    }, { rank: rankings.NO_MATCH, keyIndex: -1 });
}

/**
 * Gives a rankings score based on how well the two strings match.
 * @param {String} testString - the string to test against
 * @param {String} stringToRank - the string to rank
 * @param {Object} options - options for the match (like keepDiacritics for comparison)
 * @returns {Number} the ranking for how well stringToRank matches testString
 */
function getMatchRanking(testString, stringToRank, options) {
    /* eslint complexity:[2, 12] */
    testString = prepareValueForComparison(testString, options);
    stringToRank = prepareValueForComparison(stringToRank, options);

    // too long
    if (stringToRank.length > testString.length) {
        return rankings.NO_MATCH;
    }

    // case sensitive equals
    if (testString === stringToRank) {
        return rankings.CASE_SENSITIVE_EQUAL;
    }

    var caseRank = getCaseRanking(testString);
    var isPartial = isPartialOfCase(testString, stringToRank, caseRank);
    var isCasedAcronym = isCaseAcronym(testString, stringToRank, caseRank);

    // Lower casing before further comparison
    testString = testString.toLowerCase();
    stringToRank = stringToRank.toLowerCase();

    // case insensitive equals
    if (testString === stringToRank) {
        return rankings.EQUAL + caseRank;
    }

    // starts with
    if (testString.indexOf(stringToRank) === 0) {
        return rankings.STARTS_WITH + caseRank;
    }

    // word starts with
    if (testString.indexOf(' ' + stringToRank) !== -1) {
        return rankings.WORD_STARTS_WITH + caseRank;
    }

    // is a part inside a cased string
    if (isPartial) {
        return rankings.STRING_CASE + caseRank;
    }

    // is acronym for a cased string
    if (caseRank > 0 && isCasedAcronym) {
        return rankings.STRING_CASE_ACRONYM + caseRank;
    }

    // contains
    if (testString.indexOf(stringToRank) !== -1) {
        return rankings.CONTAINS + caseRank;
    } else if (stringToRank.length === 1) {
        // If the only character in the given stringToRank
        //   isn't even contained in the testString, then
        //   it's definitely not a match.
        return rankings.NO_MATCH;
    }

    // acronym
    if (getAcronym(testString).indexOf(stringToRank) !== -1) {
        return rankings.ACRONYM + caseRank;
    }

    // will return a number between rankings.MATCHES and
    // rankings.MATCHES + 1 depending  on how close of a match it is.
    return getClosenessRanking(testString, stringToRank);
}

/**
 * Generates an acronym for a string.
 *
 * @param {String} string the string for which to produce the acronym
 * @returns {String} the acronym
 */
function getAcronym(string) {
    var acronym = '';
    var wordsInString = string.split(' ');
    wordsInString.forEach(function (wordInString) {
        var splitByHyphenWords = wordInString.split('-');
        splitByHyphenWords.forEach(function (splitByHyphenWord) {
            acronym += splitByHyphenWord.substr(0, 1);
        });
    });
    return acronym;
}

/**
 * Returns a score base on the case of the testString
 * @param {String} testString - the string to test against
 * @returns {Number} the number of the ranking,
 * based on the case between 0 and 1 for how the testString matches the case
 */
function getCaseRanking(testString) {
    var containsUpperCase = testString.toLowerCase() !== testString;
    var containsDash = testString.indexOf('-') >= 0;
    var containsUnderscore = testString.indexOf('_') >= 0;

    if (!containsUpperCase && !containsUnderscore && containsDash) {
        return caseRankings.KEBAB;
    }

    if (!containsUpperCase && containsUnderscore && !containsDash) {
        return caseRankings.SNAKE;
    }

    if (containsUpperCase && !containsDash && !containsUnderscore) {
        var startsWithUpperCase = testString[0].toUpperCase() === testString[0];
        if (startsWithUpperCase) {
            return caseRankings.PASCAL;
        }

        return caseRankings.CAMEL;
    }

    return caseRankings.NO_CASE;
}

/**
 * Returns whether the stringToRank is one of the case parts in the testString (works with any string case)
 * @example
 * // returns true
 * isPartialOfCase('helloWorld', 'world', caseRankings.CAMEL)
 * @example
 * // returns false
 * isPartialOfCase('helloWorld', 'oworl', caseRankings.CAMEL)
 * @param {String} testString - the string to test against
 * @param {String} stringToRank - the string to rank
 * @param {Number} caseRanking - the ranking score based on case of testString
 * @returns {Boolean} whether the stringToRank is one of the case parts in the testString
 */
function isPartialOfCase(testString, stringToRank, caseRanking) {
    var testIndex = testString.toLowerCase().indexOf(stringToRank.toLowerCase());

    switch (caseRanking) {
        case caseRankings.SNAKE:
            return testString[testIndex - 1] === '_';
        case caseRankings.KEBAB:
            return testString[testIndex - 1] === '-';
        case caseRankings.PASCAL:
        case caseRankings.CAMEL:
            return testIndex !== -1 && testString[testIndex] === testString[testIndex].toUpperCase();
        default:
            return false;
    }
}

/**
 * Check if stringToRank is an acronym for a partial case
 * @example
 * // returns true
 * isCaseAcronym('super_duper_file', 'sdf', caseRankings.SNAKE)
 * @param {String} testString - the string to test against
 * @param {String} stringToRank - the acronym to test
 * @param {Number} caseRank - the ranking of the case
 * @returns {Boolean} whether the stringToRank is an acronym for the testString
 */
function isCaseAcronym(testString, stringToRank, caseRank) {
    var splitValue = null;
    switch (caseRank) {
        case caseRankings.SNAKE:
            splitValue = '_';
            break;
        case caseRankings.KEBAB:
            splitValue = '-';
            break;
        case caseRankings.PASCAL:
        case caseRankings.CAMEL:
            splitValue = /(?=[A-Z])/;
            break;
        default:
            splitValue = null;
    }

    var splitTestString = testString.split(splitValue);
    return stringToRank.toLowerCase().split('').reduce(function (correct, char, charIndex) {
        var splitItem = splitTestString[charIndex];
        return correct && splitItem && splitItem[0].toLowerCase() === char;
    }, true);
}

/**
 * Returns a score based on how spread apart the
 * characters from the stringToRank are within the testString.
 * A number close to rankings.MATCHES represents a loose match. A number close
 * to rankings.MATCHES + 1 represents a loose match.
 * @param {String} testString - the string to test against
 * @param {String} stringToRank - the string to rank
 * @returns {Number} the number between rankings.MATCHES and
 * rankings.MATCHES + 1 for how well stringToRank matches testString
 */
function getClosenessRanking(testString, stringToRank) {
    var charNumber = 0;
    function findMatchingCharacter(matchChar, string, index) {
        for (var j = index; j < string.length; j++) {
            var stringChar = string[j];
            if (stringChar === matchChar) {
                return j + 1;
            }
        }
        return -1;
    }

    var firstIndex = findMatchingCharacter(stringToRank[0], testString, 0);
    if (firstIndex < 0) {
        return rankings.NO_MATCH;
    }
    charNumber = firstIndex;
    for (var i = 1; i < stringToRank.length; i++) {
        var matchChar = stringToRank[i];
        charNumber = findMatchingCharacter(matchChar, testString, charNumber);
        var found = charNumber > -1;
        if (!found) {
            return rankings.NO_MATCH;
        }
    }

    var spread = charNumber - firstIndex;
    return function (spread) {
        var matching = spread - stringToRank.length + 1;
        var ranking = rankings.MATCHES + 1 / matching;
        return ranking;
    }(spread);
}

/**
 * Sorts items that have a rank, index, and keyIndex
 * @param {Object} a - the first item to sort
 * @param {Object} b - the second item to sort
 * @return {Number} -1 if a should come first, 1 if b should come first
 * Note: will never return 0
 */
function sortRankedItems(a, b) {
    var aFirst = -1;
    var bFirst = 1;
    var aRank = a.rank,
        aIndex = a.index,
        aKeyIndex = a.keyIndex;
    var bRank = b.rank,
        bIndex = b.index,
        bKeyIndex = b.keyIndex;

    if (aRank === bRank) {
        if (aKeyIndex === bKeyIndex) {
            return aIndex < bIndex ? aFirst : bFirst;
        } else {
            return aKeyIndex < bKeyIndex ? aFirst : bFirst;
        }
    } else {
        return aRank > bRank ? aFirst : bFirst;
    }
}

/**
 * Prepares value for comparison by stringifying it, removing diacritics (if specified)
 * @param {String} value - the value to clean
 * @param {Object} options - {keepDiacritics: whether to remove diacritics}
 * @return {String} the prepared value
 */
function prepareValueForComparison(value, _ref4) {
    var keepDiacritics = _ref4.keepDiacritics;

    value = '' + value; // toString
    if (!keepDiacritics) {
        value = diacritics.clean(value);
    }
    return value;
}

/**
 * Gets value for key in item at arbitrarily nested keypath
 * @param {Object} item - the item
 * @param {Object|Function} key - the potentially nested keypath or property callback
 * @return {Array} - an array containing the value(s) at the nested keypath
 */
function getItemValues(item, key) {
    if ((typeof key === 'undefined' ? 'undefined' : _typeof(key)) === 'object') {
        key = key.key;
    }
    var value = void 0;
    if (typeof key === 'function') {
        value = key(item);
        // eslint-disable-next-line no-negated-condition
    } else if (key.indexOf('.') !== -1) {
        // handle nested keys
        value = key.split('.').reduce(function (itemObj, nestedKey) {
            return itemObj ? itemObj[nestedKey] : null;
        }, item);
    } else {
        value = item[key];
    }
    // concat because `value` can be a string or an array
    // eslint-disable-next-line
    return value != null ? [].concat(value) : null;
}

/**
 * Gets all the values for the given keys in the given item and returns an array of those values
 * @param {Object} item - the item from which the values will be retrieved
 * @param {Array} keys - the keys to use to retrieve the values
 * @return {Array} objects with {itemValue, attributes}
 */
function getAllValuesToRank(item, keys) {
    return keys.reduce(function (allVals, key) {
        var values = getItemValues(item, key);
        if (values) {
            values.forEach(function (itemValue) {
                allVals.push({
                    itemValue: itemValue,
                    attributes: getKeyAttributes(key)
                });
            });
        }
        return allVals;
    }, []);
}

/**
 * Gets all the attributes for the given key
 * @param {Object|String} key - the key from which the attributes will be retrieved
 * @return {Object} object containing the key's attributes
 */
function getKeyAttributes(key) {
    if (typeof key === 'string') {
        key = { key: key };
    }
    return _extends({
        maxRanking: Infinity,
        minRanking: -Infinity
    }, key);
}

var libStar = /*#__PURE__*/Object.freeze({
    default: matchSorter,
    rankings: rankings
});

var lib = matchSorter;

Object.assign(lib, Object.keys(libStar).reduce(function (e, prop) {
    if (prop !== 'default') {
        // eslint-disable-next-line import/namespace
        e[prop] = libStar[prop];
    }
    return e;
}, {}));

module.exports = lib;