interface Id {
    (x: number) : number;
    (x: string) : string;
    (x: boolean) : boolean;
}

export module module {
    function foo(c: Id) : true;
}