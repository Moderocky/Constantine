Constantine
=====

### Opus #28

A paradigm for constant conditions in Java.

## Preamble

Java lacks a true (or enforceable) idea of a constant.
The `final` keyword makes a variable immutable, but only in the sense that it cannot change its reference.
This having been said, a `final` variable is not necessarily a constant variable: it is unlikely to have constant
conditions.

For example, we can assign a mutable list to a final variable and,
while the list itself can't be swapped out with another, there are no restrictions on modifying its contents.

Enter the constant: a thing made entirely of constants. The constant is not just immutable,
it's constant right down to the primitive level, constant to the bone.
It is a thing constructed entirely from (constructions of) raw data and, as such, it can be represented as
-- and reconstructed from -- that raw data.

This means that a constant is able to be stored (in its deconstructed form) in the class file constant pool,
therefore it is something that _has the potential to be_ a language-level literal.

## Provided Resources

Constantine provides two kinds of resource: the `Constant` and the `Constantive`.
Both are interfaces that can be implemented by regular classes in order to give them the value of constant conditions,
and to ensure that they are, in fact, constant.

### Constant Types

A constant (implementing `org.valross.constantine.Constant`) is a type,
instances of which are completely unmodifiable and constructed only from other constants.
For simplicity, primitives, strings, primitive wrappers and other Java-provided 'constables' are assumed to be constant,
since they don't implement the Constant interface.

A constant can be **described**. The description of a constant is the means to make it from its constituent parts.
This is the part that is used when a constant is stored within a class file or the like.
Creating the description itself is handled automatically by the API, but two things must be provided by the implementor:
Firstly, the parameters for the canonical constructor (the accessible one that should be used to re-create the object)
and secondly, the 'serial' or the set of (constant) values that need to be fed to the constructor to re-create this
exact object.

A variation of this is provided specifically for records, which handles the canonical parameters and the serial (albeit
reflectively).

### Constantive Types

A constantive (implementing `org.valross.constantine.Constantive`) is a type,
instances of which are capable of being **resolved**.
The resolution (or finalisation, although that word has various negative associations) of an instance
is when it is "finished" and can no longer be edited.
In other words, the resolution of an object is when it's ready to become a constant.

A simple example of this might be a constantive 'builder' class, which allows a data structure to be easily mutated.
When the building is finished and the dataset is ready, we might want to return the finished product, which will be a
constant.
Another good example might be a mutable list that, once submitted, is resolved to a constant, immutable form.

Therefore, a constantive is something that can effectively be turned into a constant and, by extension,
that we can sort of treat as a constant in the meantime.

All constants are also constantive, since they are already resolved to themselves.

## Usage

### Classes

When implementing the `Constant` interface, a class must conform to the specifications listed above.
An example is shown below.

```java
import org.valross.constantine.Constant;

import java.lang.constant.Constable;

public final class Person // our class is final, something else shouldn't extend it and add non-constant things
    implements Constant { // it's okay to extend it with another constant type or seal it, though

    public final String name; // all of our data is final and immutable
    public final int age; // even though these aren't Constant, they're effectively constant

    public Person(String name, int age) { // we have a canonical constructor
        this.name = name;
        this.age = age;
    }

    @Override
    public Constable[] serial() {
        return new Constable[] {name, age}; // these are the things needed to make a new Person(name, age)
    }

    @Override
    public Class<?>[] canonicalParameters() {
        return new Class[] {String.class, int.class}; // this is what the canonical constructor looks like
    }

}
```

### Records

A record is the ideal way to express a constant object: it has a definite, visible canonical constructor,
definite, visible, necessarily-final and accessible members, and all these things are enforced at the source level.
Nearly all constant types _ought to be_ records.

Making a record constant is a very difficult task, as seen below:

```java
import org.valross.constantine.RecordConstant;

public record Day(String name)
    implements RecordConstant {

}
```

### Constantives

A constantive class implements `org.valross.constantine.Constantive`.
It must implement the method `Constant constant()`, providing a constant version of its data.

## Constant Arrays

An observant reader may have noticed that Java arrays do not satisfy the definition of a constant,
since their contents is mutable.
To cover this, the `org.valross.constantine.Array` class is provided, which can hold a fixed collection of constants.

Internally, the backing array used by the Array class is made effectively final by being unreachable;
nothing is able to access it in order to change its data, and all copies provided by accessors are cloned from the
original.

The Array class also functions as a constant Collection, which can be iterated over.

