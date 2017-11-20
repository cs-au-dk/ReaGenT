/*declare module module {
    function takesNum(numArr: Array<number>): number | undefined;
    function takesStr(strArr: Array<string>): string | undefined;
}*/
// the issue is that the two arrays gets mixed.
module.exports = {
    takesNum: function (arr) {
        return arr[1];
    },
    takesStr: function (arr) {
        return arr[1];
    }
};