# ProGuard rules for SAVIA
# Keep Room entities
-keep class com.savia.camaguey.data.model.** { *; }
-keep class com.savia.camaguey.data.local.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Retrofit
-keepattributes Signature
-keepattributes Exceptions
-keep class retrofit2.** { *; }
-keep class com.squareup.okhttp3.** { *; }

# OSMDroid
-keep class org.osmdroid.** { *; }

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule
