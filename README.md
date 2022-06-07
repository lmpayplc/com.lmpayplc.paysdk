1. 基本配置
    1. 配置厂库地址：

```groovy
maven {
    url "https://raw.githubusercontent.com/lmpayplc/com.lmpayplc.paysdk/tree/master"
}
```

2. 依赖路径

```groovy
implementation 'com.awesomeglobal.paysdk:awesome_pay_sdk:0.6.12' 
```

4. 异常情况处理
    1. 异常信息为：Connect to raw.githubusercontent.com:
       443 [raw.githubusercontent.com/0.0.0.0, raw.githubusercontent.com/0:0:0:0:0:0:0:0] failed:
       Connection refused (Connection refused)
       处理方法 https://blog.csdn.net/trickGenous/article/details/105592465
    2. 配置vpn全局代理翻墙