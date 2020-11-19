# staticchecking/DogsAndRobots.py
# (c)2020 MindView LLC: see Copyright.txt
# We make no guarantees that this code is fit for any purpose.
# Visit http://OnJava8.com for more book information.

def speak(anything):
    anything.talk()

class Dog:
    def talk(self):  print("Arf!")
    def reproduce(self): pass

class Robot:
    def talk(self): print("Click!")
    def oilChange(self): pass

a = Dog()
b = Robot()

speak(a)
speak(b)

output = """
Arf!
Click!
"""
