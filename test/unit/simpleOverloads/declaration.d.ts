

interface MyFunction {
    (a: number) : number;
    (a: string) : string;
    (a: boolean) : boolean;
}

export module module {
    function run(calback: MyFunction): number;
}