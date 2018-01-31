type MyFunc<T3, R> = (a3: T3) => R;
// interface MyFunc<T3, R> { (a3: T3) : R} // replace type = with interface makes it also work.
declare var myThing: MyFunc<5, 1>;
// declare var myThing: (a: 5) => 1; // should be equivalent, but isn't.
