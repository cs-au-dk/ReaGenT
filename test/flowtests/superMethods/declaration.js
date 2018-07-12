declare module Prismic {
    declare class Textable {
        asText() : string;
    }

    declare class Text mixins Textable {
        constructor(data: "string") : void;
    }
    declare class Num mixins Textable {
        constructor(data: 123) : void;
    }
    declare module.exports: {
        Fragments: {
            Text: typeof Text
            Number: typeof Num
        }
    };
}
