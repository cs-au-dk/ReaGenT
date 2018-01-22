declare namespace IntroJs {
    interface Step {
        intro: string;
    }
    interface IntroJs {
        addHints(): void;
        onhintclick(callback: (item: Step) => any): void;
    }
    interface Factory {
        (): IntroJs;
    }
}
declare var introJs: IntroJs.Factory;
declare module 'intro.js' {
    var introJs: IntroJs.Factory;
}