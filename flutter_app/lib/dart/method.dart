/*
1、方法可以没有指定返回值，会根据运行时确定具体类型

2、对于top-level function、static method的对象之间是相等的，而如果是不同实例的instance method 对象则是不相等的

3、虽然指定了类型，但是也可不写返回值。默认会在末尾插入return null;语句

*/

void main() {
  var add = makeAdd(2);
  print(add.runtimeType); //会根据运行时确定具体类型

  print(makeAdd2(22));//也可以编译通过，只是返回null
}


//方法可以没有指定返回值，会根据运行时确定具体类型
makeAdd(num addBy) {
  return addBy + 2;
//  return '23';
}

//虽然指定了类型，但是也可不写返回值。默认会在末尾插入return null;语句
int makeAdd2(num addBy) {

}
