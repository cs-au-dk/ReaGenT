/*
interface IPipelineFunction {
    (a: 0, b?: boolean): 0;
    (a: 1, b?: number): 1;
    (a: 2): 2;
    (a: 3, b: string): 3;
}
 */

function assertEquals(a, b) {
    if (a !== b) {
        console.log()
    }
    return a === b;
}

module.exports = {
    run: function (callback) {
        switch (Math.random() * 6 | 0) {
            case 0:
                return assertEquals(callback(0), 0);
            case 1:
                return assertEquals(callback(0, true), 0);
            case 2:
                return assertEquals(callback(1), 1);
            case 3:
                return assertEquals(callback(1, 123), 1);
            case 4:
                return assertEquals(callback(2), 2);
            case 5:
                return assertEquals(callback(3, "str"), 3);
            default:
                return false; // SHOULD NEVER HAPPEN.
        }
    }
};