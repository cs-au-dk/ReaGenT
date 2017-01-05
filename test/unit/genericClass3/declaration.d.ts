export module module {
    class Container<T> {
        value: T;
    }
    function createNumberContainer(num: number): Container<number>;
}