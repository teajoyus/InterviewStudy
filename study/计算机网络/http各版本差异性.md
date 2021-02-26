
# http1.1

支持了长连接,也就是keep-alive，可以让TCP建立连接之后不会断开继续下一个http

断电续传的能力，在header头增加了一个range，可以指定文件分片的范围来请求资源的某部分，会返回206的状态码

host主机 由于虚拟主机导致多台主机共用一个ip，所以在头部多了一个host字段来做区分，如果没有的话则会返回一个400 Bad request的错误码
缓存处理


https://www.cnblogs.com/heluan/p/8620312.html