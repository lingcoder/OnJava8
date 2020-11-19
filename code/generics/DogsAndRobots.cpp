// generics/DogsAndRobots.cpp
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
#include <iostream>
using namespace std;

class Dog {
public:
  void speak() { cout << "Arf!" << endl; }
  void sit() { cout << "Sitting" << endl; }
  void reproduce() {}
};

class Robot {
public:
  void speak() { cout << "Click!" << endl; }
  void sit() { cout << "Clank!" << endl; }
  void oilChange() {}
};

template<class T> void perform(T anything) {
  anything.speak();
  anything.sit();
}

int main() {
  Dog d;
  Robot r;
  perform(d);
  perform(r);
}
/* Output:
Arf!
Sitting
Click!
Clank!
*/
