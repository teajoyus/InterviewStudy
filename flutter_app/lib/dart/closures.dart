/*

  闭包： 一个方法对象，像下面定义的makeAdd，它是一个方法对象
 */

//定义的是一个方法对象， 其中addBy是在创建这个方法对象的时候传入的
Function makeAdd(num addBy) {
  //方法内部还可以定义方法
  return (num i) => addBy + i; //在方法体中，也可以用到addBy
//  return addBy;//这样是不行的，因为返回值是Function，就意味着需要返回一个方法对象
}

makeAdd2(num addBy) {
//  return addBy + 2;
  return '23';
}

void main() {
  var add1 = makeAdd(2); //传入了参数
  print(
      add1(2)); //这个时候add1表示的是一个方法对象，它持有的addBy值是2，然后再调用这个方法对象的时候传入i=2，所以得到的结果就是4
  var add2 = makeAdd(3);
  print(add2(3));
  print(add2(3));


 
}
