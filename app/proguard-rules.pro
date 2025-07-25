# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keepattributes Exceptions,InnerClasses,*Annotation*,Signature,EnclosingMethod

-dontshrink
-dontoptimize
-dontpreverify
#-dontnote
-ignorewarnings

-keepclassmembers enum * {
    public static <methods>;
}

# 避免混淆Annotation、内部类、泛型、匿名类
-keepattributes *Annotation*,InnerClasses,Signature,EnclosingMethod

-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keep class * extends android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

-keep,allowshrinking class * extends dji.publics.DJIUI.** {
    public <methods>;
}
#加固后的AAR，其内容无法被混淆工具识别，所以MSDK外部依赖的类，必须被Keep。
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.* { *; }
-keep class dji.** { *; }
-keep class com.dji.** { *; }
-keep class djimrtc.** { *; }
-keep class com.google.** { *; }
-keep class org.bouncycastle.** { *; }
-keep class org.** { *; }
-keep class com.squareup.wire.** { *; }
#-keep class sun.misc.Unsafe { *; }
-keep class com.secneo.** {*;}
-keep class io.reactivex.**{*;}
-keep class okhttp3.**{*;}
-keep class okio.**{*;}
-keep class org.bouncycastle.**{*;}
-keep class sun.**{*;}
-keep class java.**{*;}
-keep class com.amap.api.**{*;}
-keep class com.here.**{*;}
-keep class com.mapbox.**{*;}
-keep class retrofit2.**{*;}

-keepclasseswithmembers,allowshrinking class * {
    native <methods>;
}

-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

-keep class androidx.appcompat.widget.SearchView { *; }

-keepclassmembers class * extends android.app.Service
-keepclassmembers public class * extends android.view.View {
    void set*(***);
    *** get*();
}
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}
-keep class kotlin.** { *; }
-keep class androidx.** { *; }
-keep class android.** { *; }
-keep class com.android.** { *; }
-keep class android.media.** { *; }
-keep class okio.** { *; }
-keep class com.lmax.disruptor.** {
    *;
}

-dontwarn com.mapbox.services.android.location.LostLocationEngine
-dontwarn com.mapbox.services.android.location.MockLocationEngine
#-keepclassmembers class * implements android.arch.lifecycle.LifecycleObserver {
#    <init>(...);
#}
# ViewModel's empty constructor is considered to be unused by proguard
#-keepclassmembers class * extends android.arch.lifecycle.ViewModel {
#    <init>(...);
#}
# keep Lifecycle State and Event enums values
#-keepclassmembers class android.arch.lifecycle.Lifecycle$State { *; }
#-keepclassmembers class android.arch.lifecycle.Lifecycle$Event { *; }
# keep methods annotated with @OnLifecycleEvent even if they seem to be unused
# (Mostly for LiveData.LifecycleBoundObserver.onStateChange(), but who knows)
#-keepclassmembers class * {
#    @android.arch.lifecycle.OnLifecycleEvent *;
#}

#-keepclassmembers class * implements android.arch.lifecycle.LifecycleObserver {
#    <init>(...);
#}

#-keep class * implements android.arch.lifecycle.LifecycleObserver {
#    <init>(...);
#}
-keepclassmembers class android.arch.** { *; }
-keep class android.arch.** { *; }
-dontwarn android.arch.**


#<------------ utmiss config start------------>
-keep class dji.sdk.utmiss.** { *; }
-keep class utmisslib.** { *; }
#<------------ utmiss config end------------>

# 地图相关
#-keep class com.dji.mapkit.amap.provider.AMapProvider {*;}
#-keep class com.dji.mapkit.maplibre.provider.MapLibreProvider {*;}
-keep class com.dji.mapkit.core.** {*;}
-keep class com.autonavi.** {*;}


## keep 千寻相关接口
-keep class com.qx.wz.dj.rtcm.** {*;}
# 保留 DJI SDK 相关的类
-keep class dji.v5.common.error.DJIPipeLineError { *; }
-keep class dji.v5.common.error.IDJIError { *; }
-keep class dji.v5.manager.mop.DataResult { *; }
-keep class dji.v5.manager.mop.Pipeline { *; }
-keep class dji.v5.manager.mop.PipelineManager { *; }
-keep class dji.v5.utils.common.DJIExecutor { *; }
-keep class dji.sdk.keyvalue.value.mop.TransmissionControlType { *; }
-keep class dji.sdk.keyvalue.value.mop.PipelineDeviceType { *; }
-keep class dji.v5.common.register.DJISDKInitEvent { *; }
-keep class dji.v5.manager.SDKManager { *; }
-keep class dji.v5.manager.interfaces.SDKManagerCallback { *; }
