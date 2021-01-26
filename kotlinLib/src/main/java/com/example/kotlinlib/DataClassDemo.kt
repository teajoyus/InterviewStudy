package com.example.kotlinlib

/**
 *  author : linmh
 *  date : 2021/1/25 17:18
 *  description :
 */
data class DataClassDemo(
        val valStrNullable: String?,
        val valStrNoNullable: String,
        var varStrNullable: String?,
        var varStrNoNullable: String,
        val valStrDef: String = "val_Str_Def",
        var varStrDef: String = "var_Str_Def"
)

class EntryDemo() {
    var string: String? = null

    //自定义
    operator fun component1(): String? {
        return string
    }

    companion object{
        val aaa = 1
        var bbb:String? = null
        const val haha = 2


    }
}