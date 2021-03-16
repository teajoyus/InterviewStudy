https://www.jb51.net/article/181745.htm

# 6.0 动态申请权限 READ WRITE

#7.0采用FileProvider的形式来在应用间共享文件内容
不允许file://uri的形式在intent上传递，而应该用content://来

## 特殊目录 不需要权限 卸载应用会删除
getExternalCacheDir():/storage/emulated/0/Android/data/com.example.androidadapter/cache
getCacheDir():/data/user/0/com.example.androidadapter/cache

## 照片、视频、音频这类媒体文件
使用 MediaStore 访问，访问其他应用的媒体文件时需要 READ_EXTERNAL_STORAGE 权限。