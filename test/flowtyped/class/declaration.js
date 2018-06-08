declare module "webfontloader" {
    declare type WebFontConfig = string;
    declare class WebFont {
        load(config: WebFontConfig): void;
    }

    declare module.exports: (a: WebFont) => WebFont;
}
