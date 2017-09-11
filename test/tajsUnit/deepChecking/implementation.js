/*export function foo(): {
    foo: {
        bar: {
            baz: {
                quz: {
                    value: 2
                }
            }
        }
    }
};*/

module.exports = function (x) {
    return {
        foo: {
            bar: {
                baz: {
                    quz: {
                        value: 3 // <- WRONG
                    }
                }
            }
        }
    }
};