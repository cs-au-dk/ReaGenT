/*interface Id {
    (x: number) : number;
    (x: string) : string;
    (x: boolean) : boolean;
}

export module module {
    function foo(c: Id) : true;
}*/
module.exports = {
    foo: function (c) {
        return typeof c(123) === "number" && typeof c("string") === "string" && typeof c(true) === "boolean";
    }
};