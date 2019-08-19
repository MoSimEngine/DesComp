# Copyright (c) Microsoft Corporation 2015, 2016

# The Z3 Python API requires libz3.dll/.so/.dylib in the 
# PATH/LD_LIBRARY_PATH/DYLD_LIBRARY_PATH
# environment variable and the PYTHONPATH environment variable
# needs to point to the `python' directory that contains `z3/z3.py'
# (which is at bin/python in our binary releases).

# If you obtained example.py as part of our binary release zip files,
# which you unzipped into a directory called `MYZ3', then follow these
# instructions to run the example:

# Running this example on Windows:
# set PATH=%PATH%;MYZ3\bin
# set PYTHONPATH=MYZ3\bin\python
# python example.py

# Running this example on Linux:
# export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:MYZ3/bin
# export PYTHONPATH=MYZ3/bin/python
# python example.py

# Running this example on macOS:
# export DYLD_LIBRARY_PATH=$DYLD_LIBRARY_PATH:MYZ3/bin
# export PYTHONPATH=MYZ3/bin/python
# python example.py


from z3 import *

x = Real('x')
y = Real('y')
s = Solver()
s.add(x + y > 5, x > 1, y > 1)
print(s.check())
print(s.model())


x = Int('x')
y = Int('y')
print(simplify(x + y + 2*x + 3))
print(simplify(x < y + x + 2))
print(simplify(And(x + 1 >= 3, x**2 + x**2 + y**2 + 2 >= 5)))

x, y = Ints('x y')
F = x * x
G = x + x
prove(F == G)


#Warteschlangen

#FIFO Queue Trennung zwischen Einfgen Event und Bearbeiten Event


# Use I as an alias for IntSort()
I = IntSort()
# A is an array from integer to integer
A = Array('A', I, I)
B = Array('B', I, I)
x = Int('x')
random = Int('r')
print ("FIFO vs RANDOM SELECT")
prove(Select(Store(A,x,0),0) == Select(Store(A,x,0),random))

s = Solver()
s.add(Select(Store(A,x,1),0) == Select(Store(A,x,0),random))
print(s.check())
print(s.model())


print ("FIFO vs LIFO")
prove(Select(Store(A,x,1),0) == Select(Store(A,x,0),0))

s = Solver()
s.add(Select(Store(A,x,1),0) == Select(Store(A,x,0),0))
print(s.check())
print(s.model())
print ("Test equal write")

# from neo4j import GraphDatabase


# driver = GraphDatabase.driver("bolt://35.193.233.92:7687", auth=("neo4j", "ZtA6tdah1FxB"))
# result = driver.session().read_transaction("MATCH (n:Event) RETURN n LIMIT 25", "")
# result.single()[0]
