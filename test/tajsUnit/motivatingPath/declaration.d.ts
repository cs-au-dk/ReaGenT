// Type definitions for Pathjs v0.8.4
// Project: https://github.com/mtrpcic/pathjs
// Definitions by: Lokesh Peta <https://github.com/lokeshpeta/>
// Definitions: https://github.com/DefinitelyTyped/DefinitelyTyped


interface IPathRoute {
    // enter(fns: Function|Function[]): IPathRoute;
    exit(fn: (spurious: true) => void): IPathRoute; // <- that is a spurious argument, put in for demonstration.
}

interface IPath {
    map(path: string): IPathRoute;
    dispatch(passed_route: string): void;
}

declare var Path: IPath;
