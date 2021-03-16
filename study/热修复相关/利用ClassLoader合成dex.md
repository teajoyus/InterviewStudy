# PathClassLoader与DexClassLoader的区别
PathClassLoader是系统默认用的，对应/data/app目录下的。
针对于已安装的apk，因为它不能指定dex的解压路径，里面写死了/data/dalvik-cache/路径下


DexClassLoader可以加载任意目录下的dex/jar/apk/zip文件。
热修复的话，也是要从DexClassLoader下手

但是后面的版本8.0感觉他们没啥差别了
