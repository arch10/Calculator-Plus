# Add project specific ProGuard rules here.
# For more details, see http://developer.android.com/guide/developing/tools/proguard.html

# Keep source file names and line numbers for readable crash stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ── BigMath (ch.obermuhlner:big-math) ────────────────────────────────────────
# The library uses reflection internally for MathContext and BigDecimalMath.
-keep class ch.obermuhlner.math.big.** { *; }
-dontwarn ch.obermuhlner.math.big.**

# ── Custom Views inflated from XML ───────────────────────────────────────────
-keep class com.gigaworks.tech.calculator.ui.view.** {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# ── Enums (used in AppPreference via name()/valueOf()) ───────────────────────
-keepclassmembers enum com.gigaworks.tech.calculator.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ── Firebase Crashlytics — preserve stack traces ─────────────────────────────
-keepattributes *Annotation*
-keep public class * extends java.lang.Exception

# ── TapTargetView ────────────────────────────────────────────────────────────
-keep class com.getkeepsafe.taptargetview.** { *; }

# ── Google Play Review API ───────────────────────────────────────────────────
-keep class com.google.android.play.core.review.** { *; }
