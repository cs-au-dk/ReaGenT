type marked$AlignFlag = 'left' | 'right' | 'center'

type marked$NodeCallback<T> = (e: ?Error, d: ?T) => void

declare class marked$Renderer {
    options: marked$MarkedOptions;
    code: (c: string, l: string) => string;
    blockquote: (q: string) => string;
    html: (h: string) => string;
    heading: (t: string, l: number) => string;
    hr: () => string;
    list: (b: string, o: boolean) => string;
    listitem: (t: string) => string;
    paragraph: (t: string) => string;
    table: (h: string, b: string) => string;
    tablerow: (c: string) => string;
    tablecell: (c: string, f: ?marked$AlignFlag) => string;
    heading: (t: string, l: number) => string;
    strong: (t: string) => string;
    em: (t: string) => string;
    codespan: (c: string) => string;
    br: () => string;
    del: (t: string) => string;
    link: (h: string, ti: string, te: string) => string;
    image: (h: string, ti: string, te: string) => string;
    text: (t: string) => string;
    constructor(o?: marked$MarkedOptions): marked$Renderer;
}

type marked$HighlightFunction = ((c: string, l: ?string) => string) // ctrl + f ".highlight(". It is only used once in the implementation. Possibly benign that the string is a maybe-type.


type marked$MarkedOptions = {
    highlight?: ?marked$HighlightFunction;
    renderer?: marked$Renderer;
    gfm?: boolean;
    tables?: boolean;
    breaks?: boolean;
    pedantic?: boolean;
    sanitize?: boolean;
    smartLists?: boolean;
    smartypants?: boolean;
}

/*
 * marked$Tokens
 */

type marked$Space = { type: 'space'; }
type marked$Code = { type: 'code'; text: string; lang?: string; }
type marked$Heading = { type: 'heading'; depth: number; text: string; }
type marked$Table = { type: 'table'; header: string; align: Array<marked$AlignFlag> ; cells: Array<Array<string>> }
type marked$Hr = { type: 'hr'; }
type marked$BlockquoteStart = { type: 'blockquote_start' }
type marked$BlockquoteEnd = { type: 'blockquote_end' }
type marked$ListStart = { type: 'list_start' }
type marked$ListEnd = { type: 'list_end' }
type marked$Paragraph = { type: 'paragraph'; pre?: boolean; text: string; }
type marked$Html = { type: 'html'; pre: boolean; text: string; }
type marked$Text = { type: 'text'; text: string; }

type marked$Token =
    marked$Space
    | marked$Code
    | marked$Heading
    | marked$Table
    | marked$Hr
    | marked$BlockquoteStart
    | marked$BlockquoteEnd
    | marked$ListStart
    | marked$ListEnd
    | marked$Paragraph
    | marked$Html
    | marked$Text

type marked$Link = {
    title: ?string;
    href: string;
}

type marked$Tokens = { links: {[tag: string]: marked$Link} } & Array<marked$Token>;

type marked$NoopRule = {
    (i: mixed): void;
    exec: (i: mixed) => void;
}

type marked$Rule = RegExp | marked$NoopRule

type marked$lex = (t: string) => marked$Tokens;

declare class marked$Lexer {
    static lex: (t: string, o?: marked$MarkedOptions) => marked$Tokens;
    static rules: { [key: string]: marked$Rule }; // Not entirely correct, but looks bening.

    constructor(o?: marked$MarkedOptions): marked$Lexer;

    rules: { [key: string]: marked$Rule }; // Not entirely correct, but looks bening.
    lex: marked$lex;
    tokens: marked$Tokens;
    options: marked$MarkedOptions;
}

declare class marked$Parser {
    static parse: (t: marked$Tokens, o?: marked$MarkedOptions) => string;
    parse: (t: marked$Tokens) => string;
    next: () => marked$Token; // actually returns a maybe marked$Token. But that seems benign (peek() should be called first).
    peek: () => marked$Token | 0;
    parseText: () => string;
    tok: () => string;
    tokens: marked$Tokens; // The "links" property is not always present, but it seems benign, because the links property is present if the parse method has been called. (Maybe a pull-request to initialize "links" in the Parser constructor?)
    token: ?marked$Token;
    options: marked$MarkedOptions;
    renderer: marked$Renderer;
    constructor(o?: marked$MarkedOptions): marked$Parser;
}

declare class marked$InlineLexer {
    static rules: {[name: string] : marked$Rule}; // Benign.
    static output: (s: string, l: Array<marked$Link>, o?: marked$MarkedOptions) => string;
    output: (s: string) => string;
    outputLink: (c: Array<string>, l: marked$Link) => string;
    smartypants: (t: string) => string;
    mangle: (t: string) => string;
    options: marked$MarkedOptions;
    links: Array<marked$Link>;
    rules: {[name: string] : marked$Rule};
    renderer: marked$Renderer;
    constructor(l: Array<marked$Link>, o?: marked$MarkedOptions): marked$InlineLexer;
}

type marked$Marked = {
    (md: string, o: marked$MarkedOptions, cb: marked$NodeCallback<string>): void;
    (md: string, cb: marked$NodeCallback<string>): void;
    (md: string, o?: marked$MarkedOptions): string;
    setOptions: (o: marked$MarkedOptions) => void;
    defaults: marked$MarkedOptions;
    Parser: typeof marked$Parser;
    parser: typeof marked$Parser.parse;
    Lexer: typeof marked$Lexer;
    lexer: typeof marked$Lexer.lex;
    InlineLexer: typeof marked$InlineLexer;
    inlineLexer: typeof marked$InlineLexer.output;
    Renderer: typeof marked$Renderer;
    parse: marked$Marked;
}


declare module marked {
    declare module.exports: marked$Marked;
}

