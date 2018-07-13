declare module 'url-parse' {
    declare export type Url = {
        protocol: string,
        slashes: "false" | boolean,
        auth: string,
        username: string,
        password: string,
        host: string,
        hostname: string,
        port: string,
        pathname: string,
        query: Object | string,
        hash: string,
        href: string,
        origin: string,
        set: (part: string, value: mixed, fn?: (boolean | (value: mixed) => Object)) => Url,
        toString: () => string,

    };

    // In the library, if location is not (Object | string), it is assigned to parser. Therefore,
    // to make less confusing for the user, we type both signatures
    declare interface UrlConstructor {
        (adress: string, location: (Object | string), parser?: (boolean | string => Object)):  Url
        (adress: string, parser?: (boolean | string => Object)): Url

        location(loc?: (Object | string)): Object, // from the source: URL.location = lolcation;
        extractProtocol(address: string): {
            protocol: string,
            slashes: boolean,
            rest: string
        }
    }

    declare module.exports: UrlConstructor;
}
