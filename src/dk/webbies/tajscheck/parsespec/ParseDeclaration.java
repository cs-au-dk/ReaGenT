package dk.webbies.tajscheck.parsespec;

import dk.au.cs.casa.typescript.SpecReader;
import dk.au.cs.casa.typescript.types.GenericType;
import dk.au.cs.casa.typescript.types.InterfaceType;
import dk.au.cs.casa.typescript.types.Type;
import dk.webbies.tajscheck.util.Util;

import java.io.File;
import java.util.*;

/**
 * Created by erik1 on 01-11-2016.
 */
public class ParseDeclaration {
    public static SpecReader getTypeSpecification(Environment env, Collection<String> declarationFilePaths) {
        String runString = "node_modules/ts-type-reader/src/CLI.js --env " + env.getCliArgument();
        for (String declarationFile : declarationFilePaths) {
            runString += " \"" + declarationFile + "\"";
        }

        String cachePath = "declaration-" + env.getCliArgument() + "-" + runString.hashCode() + ".json";

        List<File> toCheckAgainst = new ArrayList<>(Arrays.asList(new File("node_modules/ts-type-reader")));
        declarationFilePaths.stream().map(File::new).forEach(toCheckAgainst::add);

        String specification;
        try {
            specification = Util.getCachedOrRunNode(cachePath, toCheckAgainst, runString);
            return new SpecReader(specification.split("\\n")[specification.split("\\n").length - 1]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<Type, String> getTypeNamesMap(SpecReader spec) {
        Map<Type, String> typeNames = new HashMap<>();
        markNamedTypes((SpecReader.Node) spec.getNamedTypes(), "", typeNames);
        return typeNames;
    }

    private static void markNamedTypes(SpecReader.Node namedTypes, String prefix, Map<Type, String> typeNames) {
        for (Map.Entry<String, SpecReader.TypeNameTree> entry : namedTypes.getChildren().entrySet()) {
            String name = prefix + entry.getKey();
            SpecReader.TypeNameTree type = entry.getValue();
            if (type instanceof SpecReader.Leaf) {
                SpecReader.Leaf leaf = (SpecReader.Leaf) type;
                if (leaf.getType() instanceof InterfaceType) {
                    typeNames.put(leaf.getType(), name);
                } else if (leaf.getType() instanceof GenericType) {
                    typeNames.put(leaf.getType(), name);
                } else {
                    throw new RuntimeException("I don't handle marking " + leaf.getType().getClass().getName() + " yet!");
                }
            } else {
                markNamedTypes((SpecReader.Node) type, name + ".", typeNames);
            }
        }
    }

    public enum Environment {
        ES5Core(5, false),
        ES5DOM(5, true),
        ES6Core(6, false),
        ES6DOM(6, true);

        public final int ESversion;
        public final boolean hasDOM;

        Environment(int ESversion, boolean hasDOM) {
            this.ESversion = ESversion;
            this.hasDOM = hasDOM;
        }

        public String getCliArgument() {
            return "es" + ESversion + (this.hasDOM ? "-dom" : "");
        }
    }
}
