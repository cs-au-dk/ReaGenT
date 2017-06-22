package dk.webbies.tajscheck.tajstester;

import dk.au.cs.casa.typescript.types.ClassType;
import dk.au.cs.casa.typescript.types.InterfaceType;
import dk.au.cs.casa.typescript.types.Type;
import dk.brics.tajs.flowgraph.Function;
import dk.brics.tajs.lattice.*;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.testcreator.test.Test;
import dk.webbies.tajscheck.typeutil.TypesUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Consider the following class hierarchy
 *
 *        T  (return type in the declaration file)
 *        |
 *       / \
 *      /   \
 *     T'   T'' (subclasses of T, T' in the declaration file, T'' not in the declaration file)
 *      \   /
 *       \ /
 *        |
 *        C (concrete type returned)
 *
 *  in this case, an undeclared property p in C, could belong to T', T'', C or even T, since we don't know about T''.
 *  The best thing we can do is to associate it with T.
 *
 */
public class ConcreteTypesLearner {

    private final BenchmarkInfo info;
    private final Set<Type> allTypes;

    private final Map<TypeMember, Value> functionValues = new HashMap<>();


    ConcreteTypesLearner(BenchmarkInfo info) {
        this.info = info;
        this.allTypes = TypesUtil.collectAllTypes(info.getSpec().getGlobal());
    }

    public void printInfo() {

        double avg = functionValues.values().stream().mapToInt(x->x.getAllObjectLabels().size()).sum() / (double)functionValues.size();
        System.out.println("- Recordered function type members: " + functionValues.size());
        System.out.println("- Average functions per type members: " + avg);

    }

    private Set<Type> getSubtypes(Type supertype) {
        //FIXME: Why all base type is not taking contexts and returning type with contexts ?
        return allTypes.stream().filter(subtype -> TypesUtil.getAllBaseTypes(subtype).contains(supertype)).collect(Collectors.toSet());
    }


    public static class TypeMember {

        final PKey memberName;

        final TypeWithContext containerType;

        TypeMember(PKey memberName, TypeWithContext containerType) {
            this.memberName = memberName;
            this.containerType = containerType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TypeMember that = (TypeMember) o;

            if (memberName != null ? !memberName.equals(that.memberName) : that.memberName != null) return false;
            return containerType != null ? !containerType.equals(that.containerType) : that.containerType != null;
        }

        @Override
        public int hashCode() {
            int result = memberName != null ? memberName.hashCode() : 0;
            result = 31 * result + (containerType != null ? containerType.hashCode() : 0);
            return result;
        }
    }


    void learnConcreteType(Value v, State s, TypeWithContext tc) {
        if(tc.getType() instanceof InterfaceType) {
            InterfaceType itype = (InterfaceType)tc.getType();
            for (ObjectLabel l : v.getAllObjectLabels()) {
                Map<PKey, Value> properties = UnknownValueResolver.getProperties(l, s);
                List<PKey> undeclared = properties.keySet().stream().filter(p -> p.isStringKey() && itype.getDeclaredProperties().containsKey(p.asStringKey().asString())).collect(Collectors.toList());
                for(PKey property : properties.keySet()) {
                    Set<ObjectLabel> functions = properties.get(property).getAllObjectLabels().stream().filter(f -> f.getKind() == ObjectLabel.Kind.FUNCTION).collect(Collectors.toSet());
                    if(!functions.isEmpty()) {
                      TypeMember member = new TypeMember(property, tc);
                      Value prev = functionValues.getOrDefault(member, Value.makeNone());
                      functionValues.put(member, prev.join(Value.makeObject(functions)));
                    }
                }
            }
        }
    }


}
