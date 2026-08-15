# Proguard rules for the app module
-keep class ca.justinmo.interference.InterferenceJNI { *; }
-keep class ca.justinmo.interference.InterferenceApplication { *; }

# Keep library classes used by the app
-keep class ca.justinmo.library.** { *; }
