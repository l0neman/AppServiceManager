# AppServiceManager

实现一个普通应用进程中的 ServiceManager，可自由注册和获取 Binder 服务。

博客：[https://l0neman.github.io/2020/07/27/android-应用进程-servicemanager-的实现/](https://l0neman.github.io/2020/07/27/android-应用进程-servicemanager-的实现/)



## 使用

1. 使用 aidl 定义任意数量的 Binder 服务；

```java
// IFoo.aidl

package io.l0neman.example;

interface IFoo {
    int add(int x, int y);
}
```

```java
// IBar.aidl

package io.l0neman.example;

interface IBar {
    int sub(int x, int y);
}
```



2. 将任意进程中的 Binder 服务端的 Binder 引用注册到 ServiceManager 中，ServiceManager 运行于独立进程中的 ContentProvider 中；

```java
ServiceManager.addService(this, "foo", new IFoo.Stub() {
  @Override public int add(int x, int y) {
    return x + y;
  }
});

ServiceManager.addService(this, "bar", new IBar.Stub() {
  @Override public int sub(int x, int y) {
    return x - y;
  }
});
```



3. 在任意地方获取 Binder 服务端的引用，请求服务执行任务。

```java
IFoo iFoo = IFoo.Stub.asInterface(ServiceManager.getService(this, "foo"));
if (iFoo != null) {
  try {
    final int add = iFoo.add(1, 2);
    Log.d(TAG, "add: " + add);
  } catch (RemoteException ignore) {}
}

IBar iBar = IBar.Stub.asInterface(ServiceManager.getService(this, "bar"));
if (iBar != null) {
  try {
    final int sub = iBar.sub(3, 1);
    Log.d(TAG, "sub: " + sub);
  } catch (RemoteException ignore) {}
}
```



## 参考

- [https://github.com/cmzy/DroidService](https://github.com/cmzy/DroidService)
