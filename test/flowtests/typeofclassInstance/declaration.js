declare module "webfontloader" {
    declare class WebFont {
        foo: string;
        static bar: number;
    }

    declare var myInstance: WebFont;
    declare var typeOfClass: typeof WebFont;
}
