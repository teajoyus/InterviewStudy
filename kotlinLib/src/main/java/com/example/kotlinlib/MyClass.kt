package com.example.kotlinlib

object MyClass {
    @JvmStatic
    fun main(args: Array<String>) {
        val dataClassDemo = DataClassDemo("1", "2", "3", "4")
        print(dataClassDemo)
        var (ss, dd) = dataClassDemo
        println(dd)
        println(ss)
        println("$ss hshsh $dd,${dataClassDemo.valStrDef}")
        val interfaceDemo = object : InterfaceDemo {

        }
        println(interfaceDemo.getMax())
        val s1 = StringBuffer().append("33").toString()
        val s2 = "3" + "3"
        println(s1 == s2)
        println(s1 === s2)
        println(s1.haha())

        MethodParamsDemo().setOnClickListener {
            print("2222222222222222")
        }
        DefineDemo().haha
    }

    inline fun String.haha(): Int {
        return 2
    }
}