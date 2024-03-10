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

