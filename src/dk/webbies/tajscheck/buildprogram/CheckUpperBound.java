package dk.webbies.tajscheck.buildprogram;

import dk.au.cs.casa.typescript.types.AnonymousType;
import dk.au.cs.casa.typescript.types.BooleanLiteral;
import dk.au.cs.casa.typescript.types.ClassInstanceType;
import dk.au.cs.casa.typescript.types.ClassType;
import dk.au.cs.casa.typescript.types.GenericType;
import dk.au.cs.casa.typescript.types.IndexType;
import dk.au.cs.casa.typescript.types.IndexedAccessType;
import dk.au.cs.casa.typescript.types.InterfaceType;
import dk.au.cs.casa.typescript.types.IntersectionType;
import dk.au.cs.casa.typescript.types.NeverType;
import dk.au.cs.casa.typescript.types.NumberLiteral;
import dk.au.cs.casa.typescript.types.ReferenceType;
import dk.au.cs.casa.typescript.types.SimpleType;
import dk.au.cs.casa.typescript.types.StringLiteral;
import dk.au.cs.casa.typescript.types.SymbolType;
import dk.au.cs.casa.typescript.types.ThisType;
import dk.au.cs.casa.typescript.types.TupleType;
import dk.au.cs.casa.typescript.types.Type;
import dk.au.cs.casa.typescript.types.TypeParameterType;
import dk.au.cs.casa.typescript.types.TypeVisitorWithArgument;
import dk.au.cs.casa.typescript.types.UnionType;
import dk.au.cs.casa.typescript.types.UnresolvedType;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.typeutil.typeContext.TypeContext;
import dk.webbies.tajscheck.typeutil.TypesUtil;
import dk.webbies.tajscheck.buildprogram.typechecks.FieldTypeCheck;
import dk.webbies.tajscheck.buildprogram.typechecks.SimpleTypeCheck;
import dk.webbies.tajscheck.buildprogram.typechecks.TypeCheck;
import dk.webbies.tajscheck.paser.AST.Expression;
import dk.webbies.tajscheck.paser.AST.Statement;
import dk.webbies.tajscheck.testcreator.test.check.Check;
import dk.webbies.tajscheck.testcreator.test.check.CheckToExpression;
import dk.webbies.tajscheck.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

import static dk.webbies.tajscheck.paser.AstBuilder.*;

/**
 * Created by erik1 on 13-12-2016.
 */
public class CheckUpperBound {
    private BenchmarkInfo info;

    CheckUpperBound(BenchmarkInfo info) {
        this.info = info;
    }


    List<Statement> checkType(Type type, TypeContext context, Expression exp, String path) {
        List<TypeCheck> typeChecks = type.accept(new CheckUpperBoundTypeVisitor(), new Arg(context));

        return typeChecks.stream().map(check -> checkToAssertions(check, exp, path)).collect(Collectors.toList());
    }

    private Statement checkToAssertions(TypeCheck typeCheck, Expression exp, String path) {
        if (typeCheck instanceof FieldTypeCheck) {
            FieldTypeCheck fieldTypeCheck = (FieldTypeCheck) typeCheck;
            String field = fieldTypeCheck.getField();
            return statement(call(function(block(
                    fieldTypeCheck.getFieldChecks().stream().map(subCheck -> checkToAssertions(subCheck, member(exp, field), path + "." + field)).collect(Collectors.toList())
            ))));
        }

        Expression checkExpression = CheckToExpression.generate(typeCheck.getCheck(), exp);
        return statement(call(identifier("assert"), checkExpression, string(path), string(typeCheck.getExpected()), exp, identifier("i")));
    }


    private static class Arg {
        private final TypeContext context;

        private Arg(TypeContext context) {
            this.context = context;
        }

        public Arg withParameters(TypeContext newParameters) {
            return new Arg(this.context.append(newParameters));
        }
    }

    // This return an empty list a lot of the time, that is because these tests are only designed to test the upper-bound. E.g. that in a union, all the cases can actually happen.
    // All the lower-bound tests are created else-where.

    private final class CheckUpperBoundTypeVisitor implements TypeVisitorWithArgument<List<TypeCheck>, Arg> {

        @Override
        public List<TypeCheck> visit(AnonymousType t, Arg arg) {
            throw new RuntimeException();
        }

        @Override
        public List<TypeCheck> visit(ClassType t, Arg arg) {
            return Collections.emptyList();
        }

        @Override
        public List<TypeCheck> visit(GenericType t, Arg arg) {
            return t.toInterface().accept(this, arg);
        }

        @Override
        public List<TypeCheck> visit(InterfaceType t, Arg arg) {
            if (info.nativeTypes.contains(t)) {
                return Collections.emptyList();
            }

            Pair<InterfaceType, TypeContext> pair = new TypesUtil(info.bench).constructSyntheticInterfaceWithBaseTypes(t, info.typeNames, info.freeGenericsFinder);
            InterfaceType inter = pair.getLeft();
            TypeContext typeContext = arg.context.append(pair.getRight());

            int signatures = inter.getDeclaredCallSignatures().size() + inter.getDeclaredConstructSignatures().size();

            if (signatures == 0) {
                return Collections.singletonList(
                        new SimpleTypeCheck(Check.not(Check.typeOf("function")), "didn't expect a function")
                );
            }

            return Collections.emptyList();
        }

        @Override
        public List<TypeCheck> visit(ReferenceType t, Arg arg) {
            TypeContext newParameters = new TypesUtil(info.bench).generateParameterMap(t);

            return t.getTarget().accept(this, arg.withParameters(newParameters));
        }

        @Override
        public List<TypeCheck> visit(SimpleType t, Arg arg) {
            return Collections.emptyList();
        }

        @Override
        public List<TypeCheck> visit(TupleType t, Arg arg) {
            return Collections.emptyList();
        }

        @Override
        public List<TypeCheck> visit(UnionType union, Arg arg) {
            return union.getElements().stream().map((type) -> {
                TypeCheck check = checkType(arg, type, info.options.checkDepthForUnions);
                return new SimpleTypeCheck(check.getCheck(), "maybe " + check.getExpected());
            }).collect(Collectors.toList());
        }

        @Override
        public List<TypeCheck> visit(UnresolvedType t, Arg arg) {
            throw new RuntimeException();
        }

        @Override
        public List<TypeCheck> visit(TypeParameterType t, Arg arg) {
            if (arg.context.containsKey(t)) {
                TypeWithContext lookup = arg.context.get(t);
                return lookup.getType().accept(this, new Arg(lookup.getTypeContext()));
            } else {
                return Collections.emptyList();
            }
        }

        @Override
        public List<TypeCheck> visit(SymbolType t, Arg arg) {
            return Collections.emptyList();
        }

        @Override
        public List<TypeCheck> visit(StringLiteral t, Arg arg) {
            return Collections.emptyList();
        }

        @Override
        public List<TypeCheck> visit(BooleanLiteral t, Arg arg) {
            return Collections.emptyList();
        }

        @Override
        public List<TypeCheck> visit(NumberLiteral t, Arg arg) {
            return Collections.emptyList();
        }

        @Override
        public List<TypeCheck> visit(IntersectionType t, Arg arg) {
            throw new RuntimeException();
        }

        @Override
        public List<TypeCheck> visit(ClassInstanceType t, Arg arg) {
            return ((ClassType)t.getClassType()).getInstanceType().accept(this, arg);
        }

        @Override
        public List<TypeCheck> visit(NeverType t, Arg arg) {
            return Collections.emptyList();
        }

        @Override
        public List<TypeCheck> visit(ThisType t, Arg arg) {
            return arg.context.getThisType().accept(this, arg);
        }

        @Override
        public List<TypeCheck> visit(IndexType t, Arg arg) {
            throw new RuntimeException();
        }

        @Override
        public List<TypeCheck> visit(IndexedAccessType t, Arg arg) {
            throw new RuntimeException();
        }
    }

    private TypeCheck checkType(Arg arg, Type type, int depth) {
        return TypeChecker.createIntersection(type.accept(new TypeChecker.CreateTypeCheckVisitor(info), new TypeChecker.Arg(arg.context, depth)));
    }
}
