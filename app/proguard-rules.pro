#Flutter Wrapper
-keep class io.flutter.app.** { *; }
-keep class io.flutter.plugin.**  { *; }
-keep class io.flutter.util.**  { *; }
-keep class io.flutter.view.**  { *; }
-keep class io.flutter.**  { *; }
-keep class io.flutter.plugins.**  { *; }

-dontwarn javax.annotation.Nullable  
-dontwarn javax.annotation.ParametersAreNonnullByDefault  

-keep class com.facebook.** { *; }
-keep class com.androidquery.** { *; }
-keep class com.google.** { *; }
-keep class org.acra.** { *; }
-keep class org.apache.** { *; }
-keep class com.mobileapptracker.** { *; }
-keep class com.nostra13.** { *; }
-keep class net.simonvt.** { *; }
-keep class android.support.** { *; }
-keep class com.nnacres.app.model.** { *; }

# Suppress warnings if you are NOT using IAP:
 -dontwarn com.nnacres.app.**
 -dontwarn com.androidquery.**
 -dontwarn com.google.**
 -dontwarn org.acra.**
 -dontwarn org.apache.**
 -dontwarn com.mobileapptracker.**
 -dontwarn com.nostra13.**
 -dontwarn net.simonvt.**
 -dontwarn android.support.**

-keepclasseswithmembernames class * { native <methods>; }

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }

-dontnote io.netty.**, org.yaml.**

-dontwarn java.beans.**
-dontwarn org.yaml.**
# netty (partial)
-dontwarn io.netty.**

-keepattributes Signature,InnerClasses,*Annotation*
-keepclasseswithmembers class io.netty.** {
    *;
}
-keepnames class io.netty.** {
    *;
}


-dontwarn java.beans.FeatureDescriptor.**
-dontwarn sun.misc.**




-dontwarn java.lang.reflect.Modifier


-dontwarn java.lang.ClassValue


-dontwarn java.lang.reflect.AnnotatedType


-dontwarn java.lang.reflect.Constructor


-dontwarn java.lang.reflect.Method