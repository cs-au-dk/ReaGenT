interface Container<T> {
    value: T,
    duplicateFlag: boolean,
    obj: Object,
    num: Number,
    str: String
}


// TODO: Check a container that extends a generic type.
export module moment {
    function id1<T extends Date>(f : () => T) : Container<T>;
    function id2<T extends number[]>(f : () => T) : Container<T>;
}

