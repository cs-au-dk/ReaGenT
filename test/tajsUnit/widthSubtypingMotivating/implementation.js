/*
export module module {
    function v4(options: {random: number[]} | {rng(): number[]}, buf: number[]): number[];
}
*/

module.exports = {
    // type V4Options = {random: number[]} | {rng(): number[]};
v4: function v4 (options, buf) {
    var rnds = options.random || (options.rng || _rng)();
    for (var i = 0; i < 16; i++) {
        buf[i] = rnds[i];
    }
    return buf;
}
};