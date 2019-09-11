# Additional proguard rules for instrumentation testing

-keep class rx.plugins.** { *; }
-keep class org.junit.** { *; }
-keep class co.netguru.android.testcommons.** { *; }
-dontwarn org.hamcrest.**
