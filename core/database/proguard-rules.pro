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
# Room database classes, entities, DAOs
-keep class androidx.room.RoomDatabase { *; }
-keep class androidx.room.migration.Migration { *; }

-keepclassmembers class * {
    @androidx.room.* <methods>;
    @androidx.room.* <fields>;
}

# Keep Room entities and DAO classes (replace package with yours)
-keep class com.yourapp.core.database.entity.** { *; }
-keep class com.yourapp.core.database.dao.** { *; }

# Kotlin metadata
-keepclassmembers class kotlin.Metadata { *; }