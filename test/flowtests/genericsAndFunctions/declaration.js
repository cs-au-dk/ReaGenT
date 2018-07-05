type marked$NodeCallback<T> = (d: T) => void
type marked$MarkedOptions = {
    highlight: (cb: marked$NodeCallback<string>) => void;
}
type marked$Rule = RegExp
declare module foo {
    declare class marked$Renderer {
    options: marked$MarkedOptions;
    constructor(o: marked$MarkedOptions): marked$Renderer;
}
    declare class marked$Lexer {
        static rules: marked$Rule;
    }
}