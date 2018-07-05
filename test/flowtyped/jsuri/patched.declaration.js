declare module "jsuri" {
    declare class URI {
        // With maybe-types, null is a valid input, with optional-types, they are not.
        constructor: (path: ?string) => this;

        addQueryParam: (key: string, val: string, index: ?number) => this;
        deleteQueryParam: (key: string, val: ?string) => this;
        replaceQueryParam: (key: string, newVal: string, oldVal: ?string) => this;
        addTrailingSlash: () => this;

        setProtocol: (val: string) => this;
        protocol: (val?: string) => string | void;
        setHasAuthorityPrefix: (val: boolean) => this;
        hasAuthorityPrefix: (val: ?boolean) => boolean;
        setIsColonUri: (val: boolean) => this;
        isColonUri: (val: boolean) => void;
        isColonUri: () => boolean;
        setUserInfo: (val: string) => this;
        userInfo: (val?: string) => string;
        setHost: (val: string) => this;
        host: (val: string) => string;
        setPort: (val: string) => this;
        port: (val?: string) => string;
        setPath: (val: string) => this;
        path: (val?: string) => string;
        setQuery: (val: string) => this;
        query: (query: ?string) => string;
        setAnchor: (val: string) => this;
        anchor: (val?: string) => string | void;

        // getQueryParamValues: (key: string) => Array<string>; // TODO: Comment in, indexes in array can be null.
        getQueryParamValue: (key: string) => ?string;
        hasQueryParam: () => boolean;
        clone: () => URI;
        origin: () => string;
        scheme: () => string;
        toString: () => string;
    }

    declare module.exports: typeof URI;
}
