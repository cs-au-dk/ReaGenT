interface IntroJs {
    hideHints(): void;
    onhintclose(callback: (stepId: number) => any): void;
}
declare function introJs(): IntroJs;