declare module "xterm" {
    declare export type FontWeight =
        | "normal"
        | "900";

    declare export type Theme = $Shape<{
        foreground: string,
        brightMagenta: string,
        brightCyan: string,
        brightWhite: string
    }>;

    declare export type TerminalOptions = $Shape<{
        allowTransparency: boolean,
        screenReaderMode: boolean,
    }>;

    declare export type LinkMatcherOptions = $Shape<{
        matchIndex: number
    }>;

    declare export type Disposable = {|
        dispose(): void
    |};

    declare export type Marker = {|
        Disposable,
    |};

    declare export type LocalizableStrings = {|
        blankLine: string,
    |};

    declare export class Terminal {
        // textarea: HTMLTextAreaElement;
    }
}
