package dk.webbies.tajscheck.benchmark.options;

import dk.webbies.tajscheck.util.Util;

public interface OptionsI {
    Builder getBuilder();

    interface Builder {
        CheckOptions build();
    }
}
