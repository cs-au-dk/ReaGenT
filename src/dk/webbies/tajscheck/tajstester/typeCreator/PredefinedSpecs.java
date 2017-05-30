package dk.webbies.tajscheck.tajstester.typeCreator;

import java.net.URL;

public class PredefinedSpecs {

    public static URL getSpecFileFromKind(SpecKind specKind) {
        final String filePrefix;
        switch (specKind) {
            case ES5:
                filePrefix = "es5";
                break;
            case ES6:
                filePrefix = "es6";
                break;
            case ES5_DOM:
                filePrefix = "es5-dom";
                break;
            case ES6_DOM:
                filePrefix = "es6-dom";
                break;
            case NODE:
                filePrefix = "node";
                break;
            default:
                throw new RuntimeException("Unhandled specKind: " + specKind);
        }

        String location = String.format("/spec-files/%s.json", filePrefix);
        URL file = PredefinedSpecs.class.getResource(location);
        return file;
    }

    public enum SpecKind {
        ES5, ES6, ES5_DOM, ES6_DOM, NODE
    }
}
