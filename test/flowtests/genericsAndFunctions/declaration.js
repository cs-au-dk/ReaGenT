type marked$NodeCallback<T> = (d: T) => void
declare class marked$Renderer {
    options: marked$MarkedOptions;
    constructor(o?: marked$MarkedOptions): marked$Renderer;
}
type marked$MarkedOptions = {
    highlight: (c: string, l: string, cb: marked$NodeCallback<string>) => void;
}
type marked$Rule = RegExp
declare class marked$Lexer {
    static rules: { [key: string]: marked$Rule };
}