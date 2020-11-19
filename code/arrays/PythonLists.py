# arrays/PythonLists.py
# (c)2020 MindView LLC: see Copyright.txt
# We make no guarantees that this code is fit for any purpose.
# Visit http://OnJava8.com for more book information.

aList = [1, 2, 3, 4, 5]
print(type(aList)) # <type 'list'>
print(aList) # [1, 2, 3, 4, 5]
print(aList[4]) # 5   Basic list indexing
aList.append(6) # lists can be resized
aList += [7, 8] # Add a list to a list
print(aList) # [1, 2, 3, 4, 5, 6, 7, 8]
aSlice = aList[2:4]
print(aSlice) # [3, 4]

class MyList(list): # Inherit from list
    # Define a method; 'this' pointer is explicit:
    def getReversed(self):
        reversed = self[:] # Copy list using slices
        reversed.reverse() # Built-in list method
        return reversed

# No 'new' necessary for object creation:
list2 = MyList(aList)
print(type(list2)) # <class '__main__.MyList'>
print(list2.getReversed()) # [8, 7, 6, 5, 4, 3, 2, 1]

output = """
<class 'list'>
[1, 2, 3, 4, 5]
5
[1, 2, 3, 4, 5, 6, 7, 8]
[3, 4]
<class '__main__.MyList'>
[8, 7, 6, 5, 4, 3, 2, 1]
"""
