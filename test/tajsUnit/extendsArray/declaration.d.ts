interface MyArray extends Array<string> {
    myMarker: true;
}

export module module {
    function instance(x: MyArray): true;
    function asString(x: MyArray): string;
    function readMarker(x: MyArray): true;
}