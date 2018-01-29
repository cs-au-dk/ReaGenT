interface IPipelineFunction {
    (a: 0, b?: boolean): 0;
    (a: 1, b?: number): 1;
    (a: 2): 2;
    (a: 3, b: string): 3;
}

export module module {
    function run(callback: IPipelineFunction): true; // I run the assertions in the implementation.
}