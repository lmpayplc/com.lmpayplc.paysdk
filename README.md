#### LemaPaySDK介绍

乐马支付sdk是由乐马支付（后简称乐马）应用为第三方提供调用乐马进行支付的SDK。SDK提供了第三方支付，签约代扣，第三方登陆认证三个功能

#### 依赖配置

1. 配置厂库地址：

```groovy
maven {
    url "https://raw.githubusercontent.com/lmpayplc/com.lmpayplc.paysdk/master"
}
```

2. 依赖路径

```groovy
implementation 'com.awesomeglobal.paysdk:awesome_pay_sdk:0.6.12' 
```

#### 申请appid，machId和appkey.

1. 初始化 sdk

```kotlin
lmAuth = LMAPIFactory.createLMAPI(this, appId)
lmAuth.registerApp(appId) 
```

2. 实现回调Activity

```xml

<activity android:name="com.awesome.lemapaysdkdemo.lmapi.LmEntryPayActivity"
    android:exported="true" />

<activity android:name="com.awesome.lemapaysdkdemo.lmapi.LmEntryActivity" android:exported="true"
android:launchMode="singleTask" android:taskAffinity="com.awesome.lemapaysdkdemo" /> 
```

#### 功能接入

##### 支付接入

1. 调用支付功能

```kotlin
    val req = PayReq()
req.openId = App.appId
req.language = "zh-cn"
req.mustH5 = mustH5
req.isTest = 1// 2-测试 3-⽣产
req.sign_data = createSdkSign(t)
val sendReq = App.lmAuth.sendReq(req)
```

`注`sign_data字段建议接入方服务器返回给移动端。

2. 生成sign_data数据

```kotlin
    /**
 * 该支付数据由LMPay服务器生成返回。
 * The payment data is generated and returned by the LM Pay server
 * {
"app_id" : "2003101655018656525",
"order_id" : "2108191738497089407",
"nonce_str": "84970891629368447479",
"timestamp": 1629368447479,
"sign": "58EE70B86482C199FC65D47876879E95"
}
 */
private fun createSdkSign(order_id: String): String {
    val param = TreeMap<String, String>()
    param["app_id"] = App.appId
    param["order_id"] = order_id
    param["nonce_str"] = OrderIdGenerateUtil.getRandomString(32)
    param["timestamp"] = (System.currentTimeMillis() / 1000).toString()
    param["sign_type"] = "MD5"
    param["sign"] = SignUtil.getSign(param, App.appkey)
    val toJson = App.gson.toJson(param)
    val res = BASE64Encoder().encode(toJson.toByteArray()).replace("\n", "")
    return res
}
```

`注`order_id由乐马服务器生成；

3. 配置支付回调activity 在更目录下新建名为lmapi的文件夹，在该目录下新建名为LmEntryPayActivity的activity。

```kotlin
import com.awesome.lemapaysdkdemo.moudles.app.App
import com.awesomeglobal.paysdk.modelbase.BaseReq
import com.awesomeglobal.paysdk.modelbase.BaseResp
import com.awesomeglobal.paysdk.openapi.ILMAPIEventHandler


class LmEntryPayActivity : Activity(), ILMAPIEventHandler {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.lmAuth.handleIntent(intent, this)
    }

    override fun onReq(baseReq: BaseReq?) {
//        Log.i("baseReq", "baseReq = ${App.gson.toJson(baseReq)}")
    }

    override fun onResp(baseResp: BaseResp) {
        if (baseResp.errCode == 0) {//
            Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, baseResp.errStr, Toast.LENGTH_SHORT).show()
            finish()
        }
    }
} 
```

##### 签约代扣

1. 调用签约

```kotlin
val sendAuth = PaySignReq()
sendAuth.openId = App.appId
sendAuth.sign_data = createPaySignReq()
sendAuth.mustH5 = isH5()
//scope = "pay_sign" 签约免密支付
sendAuth.isTest = 1//2-内⽹ 1-测试 3-⽣产
App.lmAuth.sendReq(sendAuth)
```

2. sign_data生成方式，用于本地测试。生成请使用服务器生成

```kotlin

private fun createPaySignReq(): String {
    val param = TreeMap<String, String>()
    param["app_id"] = App.appId
    param["mach_id"] = App.machId
    param["nonce_str"] = OrderIdGenerateUtil.getRandomString(32)//从服务器获取
    param["co_sign_id"] = OrderIdGenerateUtil.getRandomString(32)//从服务器获取

    param["timestamp"] = (System.currentTimeMillis() / 1000).toString()
    param["sign_type"] = "MD5"
    param["sign"] = SignUtil.getSign(param, App.appkey)

    val toJson = App.gson.toJson(param)
    val res = BASE64Encoder().encode(toJson.toByteArray()).replace("\n", "")
    return res
}
```

3. 配置回调Activity

```kotlin
import com.awesomeglobal.paysdk.modelbase.BaseReq
import com.awesomeglobal.paysdk.modelbase.BaseResp
import com.awesomeglobal.paysdk.openapi.ILMAPIEventHandler

class LmEntryActivity : Activity(), ILMAPIEventHandler {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lmentry)
        App.lmAuth.handleIntent(intent, this)
    }

    override fun onReq(p0: BaseReq?) {

    }

    override fun onResp(baseResp: BaseResp) {
        if (baseResp.errCode == 0) {
            Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, baseResp.errStr, Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}

```

##### 授权登录

```kotlin
val sendAuth = SendAuth.Req()
//scope = "login" 为授权登录
sendAuth.scope = "login"
sendAuth.mustH5 = isH5()
sendAuth.state = "第三⽅⽤户⾃定义的值，可不填写"
sendAuth.isTest = 1//2-内⽹ 1-测试 3-⽣产
App.lmAuth.sendReq(sendAuth)

```

4. 异常情况处理
    1. 异常信息为：Connect to raw.githubusercontent.com:
       443 [raw.githubusercontent.com/0.0.0.0, raw.githubusercontent.com/0:0:0:0:0:0:0:0] failed:
       Connection refused (Connection refused)
       处理方法 https://blog.csdn.net/trickGenous/article/details/105592465
    2. 配置vpn全局代理翻墙