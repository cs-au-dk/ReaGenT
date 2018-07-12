declare module Mousetrap {
    declare function bind(
        key: string | Array<string>,
        fn: (e: Event | {||}, combo?: string) => mixed, // the "|{}" is because this line: self._directMap[keys + ':' + action]({}, keys);
        eventType?: string
    ): void;
    declare function unbind(key: string | Array<string>): void;
    declare function trigger(key: string): void;
    declare var stopCallback: (
        e: KeyboardEvent,
        element: Element,
        combo: string
    ) => boolean;
    declare function reset(): void;
}
