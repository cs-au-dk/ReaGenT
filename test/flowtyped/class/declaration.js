declare module "webfontloader" {
    declare type WebFontConfig = {
        foo: string;
        bar: string
    };
    declare class WebFont {
        load(config: WebFontConfig): void;
        foo(config: WebFontConfig): void;
    }

    declare module.exports: (a: WebFont) => WebFont;
}
