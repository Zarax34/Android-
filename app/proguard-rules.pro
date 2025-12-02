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

# Keep Firebase classes
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Keep model classes
-keep class com.dailytask.monitor.data.model.** { *; }

# Keep Hilt classes
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponent
-keep class * extends dagger.hilt.internal.GeneratedComponentManager

# Keep Compose classes
-keep class androidx.compose.** { *; }

# Keep QR Code classes
-keep class com.google.zxing.** { *; }
-keep class com.journeyapps.** { *; }

# Keep notification classes
-keep class com.google.firebase.messaging.** { *; }

# Keep alarm and service classes
-keep class com.dailytask.monitor.service.** { *; }
-keep class com.dailytask.monitor.receiver.** { *; }

# Suppress warnings
-dontwarn com.google.zxing.**
-dontwarn com.journeyapps.**
-dontwarn org.slf4j.**