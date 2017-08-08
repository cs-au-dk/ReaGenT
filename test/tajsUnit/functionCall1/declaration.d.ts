declare namespace IntroJs {
    interface IntroJs {}
    interface Factory {
        (): IntroJs;
    }
}
declare var introJs: IntroJs.Factory;