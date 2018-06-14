package dk.webbies.tajscheck.util;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Created by Erik Krogh Kristensen on 21-10-2015.
 */
public class Pair<A, B> {
    public final A left;
    public final B right;

    public Pair(A left, B right) {
        this.left = left;
        this.right = right;
    }

    public A getLeft() {
        return left;
    }

    public B getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(left, pair.left) &&
                Objects.equals(right, pair.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public String toString() {
        return "{" +
                "1st=" + left +
                ", 2nd=" + right +
                '}';
    }

    public static <S, T> dk.brics.tajs.util.Pair<S, T> toTAJS(dk.webbies.tajscheck.util.Pair<S, T> pair) {
        return dk.brics.tajs.util.Pair.make(pair.left, pair.right);
    }

    public static <T> Collector<? super T, List<T>, Pair<T, T>> collector() {
        return new Collector<>() {
            @Override
            public Supplier<List<T>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<List<T>, T> accumulator() {
                return List::add;
            }

            @Override
            public BinaryOperator<List<T>> combiner() {
                return Util::concat;
            }

            @Override
            public Function<List<T>, Pair<T, T>> finisher() {
                return list -> {
                    assert list.size() == 2;
                    return new Pair<>(list.get(0), list.get(1));
                };
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Collections.singleton(Characteristics.UNORDERED);
            }
        };
    }
}
