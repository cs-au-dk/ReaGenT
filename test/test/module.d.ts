/*
interface Container<T> extends Base<T> {
    value: T
}

interface Base<T> {
    baseValue: T
}


export module moment {
    function id1<T extends Date>(f : () => T) : Container<T> | null;
    function id2<T extends number[]>(f : () => T) : Container<T> | null;
}

*/


export module moment {
    var foo : {
        foo1: {
            foo2: {
                foo3: boolean,
            }
        },
        bar1: {
            bar2: {
                bar3: {
                    bar4: boolean
                }
            }
        }
    }
}