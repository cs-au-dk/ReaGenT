declare module 'minimist' {
    declare type minimistOptions = {
        string?: string | Array<string>,
        boolean?: boolean | string | Array<string>,
        alias?: { [arg: string]: string | Array<string> },
        default?: { [arg: string]: any },
        stopEarly?: boolean,
        // TODO: Strings as keys don't work...
        // '--'? boolean,
        unknown?: (param: string) => boolean
    };

    declare type minimistOutput = {
        _: Array<string | number | boolean>,
        [flag: string]: any // <- the either this, or change the "default" property in minimistOptions.
    };

    declare module.exports: (argv: Array<string>, opts?: minimistOptions) => minimistOutput;
}
