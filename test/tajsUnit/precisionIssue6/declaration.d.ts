declare namespace IntroJs {
    interface IntroJs {
        addHints(): void;
        onhintclose(callback: (stepId: number) => any): void;
    }
    interface Factory {
        (querySelector: string): IntroJs;
    }
}
declare var introJs: IntroJs.Factory;
declare module 'intro.js' {
    var introJs: IntroJs.Factory;
}