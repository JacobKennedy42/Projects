//testing function pointers. Based on https://support.microsoft.com/en-ie/help/30580/how-to-declare-an-array-of-pointers-to-functions-in-visual-c
//https://stackoverflow.com/questions/3758121/how-can-i-store-function-pointer-in-vector?rq=1
//https://stackoverflow.com/questions/1485983/calling-c-class-methods-via-a-function-pointer

#include <stdio.h>
#include <iostream> //cout, endl
#include <vector> //vector

using namespace std;

void foo1 ()
{
	cout << "foo1" << endl;
}

void foo2 ()
{
	cout << "foo2" << endl;
}

void foo3 ()
{
	cout << "foo3" << endl;
}

//array of pointers to functions
void (*funcptr[])() = {foo1, foo2, foo3};
//vector of pointers to functions
vector<void (*)()> funcs {foo1, foo2, foo3};

void bar1 ()
{
	cout << "bar1." << endl;
}

void bar2 ()
{
	cout << "bar2." << endl;
}

void bar3 ()
{
	cout << "bar3." << endl;
}

class Object
{
private:
	int x;
	//vector of pointers to friend functions
	vector<void (*)()> objFuncs;

	friend void bar1();
	friend void bar2();
	friend void bar3();

public:
	Object(): x(5), objFuncs{bar1, bar2, bar3}{};

	void print()
	{
		for (auto f : objFuncs)
		{
			(*f)();
		}
	}
};

class Object2;

void bat1(Object2*);
void bat2(Object2*);
void bat3(Object2*);
void bat4(Object2*);

class Object2
{
private:
	int x;
	//vector of pointers to friend functions that modify object variables
	vector<void (*)(Object2*)> objFuncs;

	friend void bat1(Object2*);
	friend void bat2(Object2*);
	friend void bat3(Object2*);
	friend void bat4(Object2*);

public:
	Object2(): x(5), objFuncs{bat1, bat2, bat3, bat4}{};

	void print()
	{
		for (auto f : objFuncs)
		{
			(*f)(this);
		}
	}
};

void bat1 (Object2* inObj)
{
	inObj->x = 0;
	cout << "bat1. x = " << inObj->x << endl;
}

void bat2 (Object2* inObj)
{
	++(inObj->x);
	cout << "bat2. x = " << inObj->x << endl;
}

void bat3 (Object2* inObj)
{
	--(inObj->x);
	cout << "bat3. x = " << inObj->x << endl;
}

void bat4 (Object2* inObj)
{
	cout << "bat4: ";
	(*(inObj->objFuncs)[1])(inObj);
}

class Object3
{
private:
	int x;
	//vector of pointers to private methods
	vector<void (Object3::*)()> objFuncs;
	void objBat1()
	{
		x = 0;
		cout << "objBat1. x = " << x << endl;
	}
	void objBat2()
	{
		++x;
		cout << "objBat2. x = " << x << endl;
	}
	void objBat3()
	{
		--x;
		cout << "objBat3. x = " << x << endl;
	}
	void objBat4()
	{
		cout << "objBat4: ";
		(*this.*(objFuncs[1]))();
	}
public:

	Object3() : x(5), objFuncs{&Object3::objBat1, &Object3::objBat2, &Object3::objBat3}
	{
		//methods can also be added via push_back
		objFuncs.push_back(&Object3::objBat4);
	}

	void print()
	{
		for (auto f : objFuncs)
		{
			(*this.*f)();
		}
	}

};

int main()
{
	(*funcptr[0])();
	(*funcptr[1])();
	(*funcptr[2])();
	(*funcs[0])();
	(*funcs[1])();
	(*funcs[2])();

	Object o;
	o.print();

	Object2 o2;
	o2.print();

	Object3 o3;
	o3.print();
}
