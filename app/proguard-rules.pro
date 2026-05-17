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

# ── Menu XML onClick handlers (called via reflection by SupportMenuInflater) ──
-keepclassmembers class com.gigaworks.tech.calculator.ui.main.MainActivity {
    public void changeAngleType(android.view.MenuItem);
}

# ── Navigation fragments (instantiated by class name from about_nav_graph.xml) ─
-keep class com.gigaworks.tech.calculator.ui.about.fragment.** { <init>(); }

# ── Hilt — keep Application class so R8 doesn't break GeneratedComponentManagerHolder instanceof check ─
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
