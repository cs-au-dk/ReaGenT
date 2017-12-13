
export module module {
    function callToString1(x: {foo: true}): string;
    function callToString2(x: object): string;
    function callToString3(x: () => void): string;
}