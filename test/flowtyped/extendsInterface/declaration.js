declare module "extendsInterface" {
    declare export interface Base {
        foo: string;
    }
    declare export interface Extended extends Base {
        bar: number;
    }

    declare var myVar: Extended;
}
