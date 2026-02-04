GearshiftScript is a small dynamically but strictly typed multi-paradigm beginner-friendly toy programming language.
This language's syntax was designed to be readable almost like natural english and understandable for beginners, while still being flexible enough for more advanced programmers.
This language supports several OOP (Composition, basic Encapsulation, constructors) and functional (First-class functions, Closures, Iterators, Expression-oriented style, Default arguments, Higher-order functions) and mixed programming patterns.

This language wasn't made to be a production language.
# Installation
You can download [the latest release](https://github.com/Gigores/GearshiftScript/releases)
# Usage
```bash
java -jar gearshiftscript.jar                 # shell
java -jar gearshiftscript.jar <file>          # file
java -jar gearshiftscript.jar <file> --debug  # displays an AST and tokens of the file
```
## Syntax
### Variables
```
let a
let b = 10
b = 15
```
### String manipulations
```
let a = "string" + " concatination"
# "string concatination"

let b = "string" * 3
# "stringstringstring"
```
### Operators
```
let num = 5 + 10  # 15
num = 5 - 10      # -5
num = 5 * 10      # 50
num = 5 / 10      # 0.5
num = 5 ^ 2       # 25

num += 15         # 40
num -= 20         # 20
num /= 2          # 10
num *= 4          # 40
num ^= 2          # 1600

num++             # 1601
num--             # 1600

# All assignment operators return the
# new value of the variable.
# That means, that you can do this:
# let c = 10
# while (c = c - 1) > 0 do
#     println(c)  # prints 10 times
# end
# and more.

let bool = true and false  # false
bool = true or false       # true
bool = not true            # false

bool = 10 > 5              # true
bool = 10 < 5              # false
bool = 10 >= 5             # true
bool = 10 <= 5             # true
bool = 10 == 5             # false
bool = 10 != 5             # true
```
### Printing
```
# Implicitly converts everything to string before printing

print("1")
print(2)
# 12

println(true)
println(null)
# true
# null
```
### User Input
```
# Returns a string
let a = input()
```
### If Statements
```
if condition then
    println("statement")
end

if condition1 then
    println("statement1")
else if condition2 then
    println("statement2")
else
    println("statement3")
end
```
### While Loops
```
while condition do
    println("statement")
end
```
### Functions
```
function function1() is
    println("function")
end
function1()
# function

function f2() is
    # anonymous function
    return function(arg) is
        println(arg + "!")
    end
end
f2()("hello")
# hello!

# Default argument values
function greet(who, how="hello, ") is
    println(how + who)
end

greet("Simon")                # hello, Simon
greet("George", "welcome, ")  # welcome, George

# Function always returns the value of the last expression executed (even if it's null)
function add(a, b) is
    a + b
end
println(add(1, 1))
```
### Lists
```
# Technically a linked list

######################
### CREATING LISTS ###
######################

let list = [1, 2, 3]
println(list)
# [1, 2, 3]

println(list[0])
# 1

list[0] = 10
println(list)
# [10, 2, 3]

###################
### BASIC STATE ###
###################

println(list.size())
# 3

println(list.isEmpty())
# false

list.clear()
println(list.isEmpty(), list)
# true []

##############
### ADDING ###
##############

list = [1, 2, 3].add(4)
println(list)
# [1, 2, 3, 4]

list = [1, 2, 3].addAll([4, 5, 6])
println(list)
# [1, 2, 3, 4, 5, 6]

list = [1, 2, 3].addFirst(4)
println(list)
# [4, 1, 2, 3]

list = [1, 2, 3].addFirstAll([4, 5, 6])
println(list)
# [6, 5, 4, 1, 2, 3]

###############
### REMOVAL ###
###############

list = [1, 2, 3].remove(2)
println(list)
# [1, 3]

list = [1, 2, 3]
let removed = list.removeAt(1)
println(list, removed)
# [1, 3] 2

list = [1, 2, 3]
removed = list.removeFirst()
println(list, removed)
# [2, 3] 1

list = [1, 2, 3]
removed = list.removeLast()
println(list, removed)
# [1, 2] 3

##############
### SEARCH ###
##############

println([1, 2, 3].contains(1))
# true

println([1, 2, 3].indexOf(2))
# 1
```
### Maps
```
let map = { "a": 1, "b": 2, "c": 3 }
println(map["a"])  # 1.0

map["a"] = 2
println(map["a"])  # 2.0

map["d"] = 4
println(map)  # { a: 2.0, b: 2.0, c: 3.0, d: 4.0 }

let map2 = { "e": 5 }
map.merge(map2)
println(map)  # { a: 2.0, b: 2.0, c: 3.0, d: 4.0, e: 5.0 }

println(map.keyList())  # [a, b, c, d, e]
```
### For Loop and iteration
```
for _ in 5 do
    print("|")
end
print("\n")
# |||||

for i in [1, 2, 3] do
    print(String(i) + "  ")
end
print("\n")
# 1.0  2.0  3.0

for i in "string" do
    print(i)
end
print("\n")
# string
```
Iterators are technically a function, that returns null when there is no more elements to iterate over.
```
function oneToTen() is
    let a = 0
    return function() is
        a = a + 1
        if a > 10 then return null end
        return a
    end
end
for i in oneToTen() do
    println(i)
end
# 1.0
# 2.0
# 3.0
# 4.0
# 5.0
# 6.0
# 7.0
# 8.0
# 9.0
# 10.0
```
#### Ranges
```
# You can use this syntax for ranges
for i in 1..5 do
    print(i, " ")
end
# 1.0  2.0  3.0  4.0  5.0
for i in 0..10 step 2 do
    print(i, " ")
end
# 0.0  2.0  4.0  6.0  8.0  10.0

# And this syntax isn't for-loop-exclusive
let a = (1 + 3)..(10^(2 + 3)) step 10^3
for i in a do
    println(i)
end
```
### Structs
```
struct Vector is
    let x = 0
    let y = 0
end

let v = new Vector
println(v.x, v.y) # 0.0 0.0
```
```
# Structure method

struct Point is
    let x = 0
    let y = 0
    function add(other) is
        this.x += other.x
        this.y += other.y
    end
end

let a = new Point
a.x = 10; a.y = 5

let b = new Point
b.x = 2; b.y = 2

a.add(b)
println(a.x, a.y)  # 12.0 7.0
```
```
# Class method

struct Point is
    let x = 0
    let y = 0
    static function add(a, b) is
        let result = new Point
        result.x = a.x + b.x
        result.y = a.y + b.y
        result
    end
end

let a = new Point
a.x = 10; a.y = 5

let b = new Point
b.x = 2; b.y = 2

let result = Point.add(a, b)
println(result.x, result.y)  # 12.0 7.0
```
```
# Constructor

struct Point is
    let x
    let y
    function __init(x, y) is
        this.x = x
        this.y = y
    end
end

let a = new Point(10, 10)
println(a.x, a.y)
```
### Type Checking
```
# Returns a string with the type of the given value
println(typeOf(true))       # Boolean
println(typeOf(10))         # Number
println(typeOf("hello"))    # String
println(typeOf([1, 2, 3]))  # List
println(typeOf({}))         # Map
println(typeOf(null))       # Null
println(typeOf(println))    # Function
println(typeOf(1..10))      # Range
println(typeOf(Point))      # Struct
println(typeOf(new Point))  # Point

# throws an error if the value is not of the expected type
ensureType(10, "String")
ensureType("hello", "Number", "expected number :(")  # custom message
```
### Type Casting
```
# TO NUMBER
println(Number("23"))   # 23.0
println(Number(true))   # 1.0
println(Number(false))  # 0.0
println(Number(null))   # 0.0

# TO STRING
println(String(12))         # 12
println(String(true))       # true
println(String([1, 2, 3]))  # [1, 2, 3]

# TO BOOLEAN
println(Boolean(12))         # true
println(Boolean(0))          # false
println(Boolean([1, 2, 3]))  # true
println(Boolean([]))         # false
println(Boolean(null))       # false
```
