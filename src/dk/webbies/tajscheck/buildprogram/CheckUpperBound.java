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
import dk.webbies.tajscheck.TypeContext;
import dk.webbies.tajscheck.buildprogram.typechecks.FieldTypeCheck;
import dk.webbies.tajscheck.buildprogram.typechecks.SimpleTypeCheck;
import dk.webbies.tajscheck.buildprogram.typechecks.TypeCheck;
import dk.webbies.tajscheck.paser.AST.Expression;
import dk.webbies.tajscheck.paser.AST.Statement;
import dk.webbies.tajscheck.testcreator.test.check.CheckToExpression;

import java.util.*;
import java.util.stream.Collectors;

import static dk.webbies.tajscheck.paser.AstBuilder.*;

/**
 * Created by erik1 on 13-12-2016.
 */
public class CheckUpperBound {
    private final Set<Type> nativeTypes;
    private final Map<Type, String> typeNames;
    private final TestProgramBuilder.TypeParameterIndexer indexer;

    CheckUpperBound(Set<Type> nativeTypes, Map<Type, String> typeNames, TestProgramBuilder.TypeParameterIndexer indexer) {
        this.nativeTypes = nativeTypes;
        this.typeNames = typeNames;
        this.indexer = indexer;
    }


    List<Statement> checkType(Type type, TypeContext context, Expression exp, String path) {
        List<TypeCheck> typeChecks = type.accept(new CheckUpperBoundTypeVisitor(), new Arg(context, 1)); // TODO: Depth out in some sort of option.

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
        return statement(call(identifier("assert"), checkExpression, string(path), string(typeCheck.getExpected()), exp));
    }


    private static class Arg {
        private final TypeContext context;
        private final int depth;

        private Arg(TypeContext context, int depth) {
            this.context = context;
            this.depth = depth;
        }
    }

    private final class CheckUpperBoundTypeVisitor implements TypeVisitorWithArgument<List<TypeCheck>, Arg> {

        @Override
        public List<TypeCheck> visit(AnonymousType t, Arg a) {
            throw new RuntimeException();
        }

        @Override
        public List<TypeCheck> visit(ClassType t, Arg a) {
            throw new RuntimeException();
        }

        @Override
        public List<TypeCheck> visit(GenericType t, Arg a) {
            throw new RuntimeException();
        }

        @Override
        public List<TypeCheck> visit(InterfaceType t, Arg a) {
            return Collections.emptyList(); // TODO: Make a test, where this is tested (more depth needed).
        }

        @Override
        public List<TypeCheck> visit(ReferenceType t, Arg a) {
            throw new RuntimeException();
        }

        @Override
        public List<TypeCheck> visit(SimpleType t, Arg a) {
            throw new RuntimeException();
        }

        @Override
        public List<TypeCheck> visit(TupleType t, Arg a) {
            throw new RuntimeException();
        }

        @Override
        public List<TypeCheck> visit(UnionType union, Arg arg) {
            return union.getElements().stream().map((type) -> {
                TypeCheck check = checkType(arg, type);
                return new SimpleTypeCheck(check.getCheck(), "maybe " + check.getExpected());
            }).collect(Collectors.toList());
        }

        @Override
        public List<TypeCheck> visit(UnresolvedType t, Arg a) {
            throw new RuntimeException();
        }

        @Override
        public List<TypeCheck> visit(TypeParameterType t, Arg a) {
            throw new RuntimeException();
        }

        @Override
        public List<TypeCheck> visit(SymbolType t, Arg a) {
            throw new RuntimeException();
        }

        @Override
        public List<TypeCheck> visit(StringLiteral t, Arg a) {
            throw new RuntimeException();
        }

        @Override
        public List<TypeCheck> visit(BooleanLiteral t, Arg a) {
            throw new RuntimeException();
        }

        @Override
        public List<TypeCheck> visit(NumberLiteral t, Arg a) {
            throw new RuntimeException();
        }

        @Override
        public List<TypeCheck> visit(IntersectionType t, Arg a) {
            throw new RuntimeException();
        }

        @Override
        public List<TypeCheck> visit(ClassInstanceType t, Arg a) {
            throw new RuntimeException();
        }

        @Override
        public List<TypeCheck> visit(NeverType t, Arg a) {
            throw new RuntimeException();
        }

        @Override
        public List<TypeCheck> visit(ThisType t, Arg a) {
            throw new RuntimeException();
        }

        @Override
        public List<TypeCheck> visit(IndexType t, Arg a) {
            throw new RuntimeException();
        }

        @Override
        public List<TypeCheck> visit(IndexedAccessType t, Arg a) {
            throw new RuntimeException();
        }
    }

    private TypeCheck checkType(Arg arg, Type type) {
        return CheckType.createIntersection(type.accept(new CheckType.CreateTypeCheckVisitor(nativeTypes, indexer, typeNames), new CheckType.Arg(arg.context, arg.depth)));
    }
}
