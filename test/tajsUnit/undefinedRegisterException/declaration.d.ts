interface KnockoutBindingHandler {
    init?: (element: any) => void;
}
interface KnockoutBindingHandlers {
    [bindingHandler: string]: KnockoutBindingHandler;
}
interface KnockoutStatic {
    bindingHandlers: KnockoutBindingHandlers;
}
declare var ko: KnockoutStatic;