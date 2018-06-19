/*
declare module "webfontloader" {
    declare class WebFont {
        foo: string;
        static bar: number;
    }

    declare var myInstance: WebFont;
    declare var typeOfClass: typeof WebFont;
}

*/

function WebFont () {
    this.foo = "string";
}
WebFont.bar = 123;

module.exports = {
    WebFont: WebFont,
    myInstance: new WebFont(),
    typeOfClass: WebFont
}