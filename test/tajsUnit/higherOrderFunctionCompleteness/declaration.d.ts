
interface Container {
    value: string
}

export module module {
    function foo(callback: (str: Container) => boolean): boolean;
}