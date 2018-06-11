declare module "yargs" {
    declare type Argv = {
        $0: string,
        [key: string]: mixed
    };

    declare type Options = $Shape<{
        alias: string,
        array: boolean,
        boolean: boolean,
        coerce: (arg: mixed) => mixed,
        config: boolean,
        configParser: (configPath: string) => { [key: string]: mixed },
        conflicts: string | { [key: string]: string },
        count: boolean,
        default: mixed,
        defaultDescription: string,
        demandOption: boolean | string,
        desc: string,
        describe: string,
        description: string,
        global: boolean,
        group: string,
        implies: string | { [key: string]: string },
        nargs: number,
        normalize: boolean,
        number: boolean,
        requiresArg: boolean,
        skipValidation: boolean,
        string: boolean,
        type: "array" | "boolean" | "count" | "number" | "string"
    }>;

    declare type CommonModuleObject = {|
        command?: string,
        aliases?: string,
        builder?: { [key: string]: Options } | ((yargsInstance: Yargs) => mixed),
        handler?: (argv: Argv) => void
    |};

    declare type ModuleObjectDesc = {|
        ...CommonModuleObject,
        desc?: string | false
    |};

    declare type ModuleObjectDescribe = {|
        ...CommonModuleObject,
        describe?: string | false
    |};

    declare type ModuleObjectDescription = {|
        ...CommonModuleObject,
        description?: string | false
    |};

    declare type ModuleObject =
        | ModuleObjectDesc
        | ModuleObjectDescribe
        | ModuleObjectDescription;

    declare class Yargs {

    }

    declare module.exports: Yargs;
}
