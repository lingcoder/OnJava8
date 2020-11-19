# staticchecking/NoBasePetSpeak.py
# (c)2020 MindView LLC: see Copyright.txt
# We make no guarantees that this code is fit for any purpose.
# Visit http://OnJava8.com for more book information.
#- Speaking pets without base classes

class Cat:
    def speak(self):
       print("meow!")

class Dog:
    def speak(self):
       print("woof!")

class Bob:
    def bow(self):
       print("thank you, thank you!")
    def speak(self):
       print("Welcome to the neighborhood!")
    def drive(self):
       print("beep, beep!")

def command(pet):
    pet.speak()

pets = [ Cat(), Dog(), Bob() ]

for pet in pets:
    command(pet)

output = """
meow!
woof!
Welcome to the neighborhood!
"""
