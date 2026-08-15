# Interférence

A real-time sound wave oscillator Android app that uses the accelerometer to dynamically adjust the frequency. It leverages the high-performance [Oboe](https://github.com/google/oboe) audio library for low-latency sound synthesis.

## Features

- **Multiple Waveforms**: Supports Sine, Square, Sawtooth, Triangle, Noise, and a unique "Radioactive" (Geiger-like) mode.
- **Sensor Integration**: Uses device orientation to control wave parameters in real-time.
- **Wave Manipulation**: Adjust distortion, exponential scaling, and inversion.
- **High Performance**: Built with C++ and Oboe for minimal audio latency.
- **Modern UI**: Developed using Jetpack Compose and Material 3.

## Downloads

The easiest way to get Interférence is by grabbing the latest release directly from GitHub or by adding it to Obtainium.

<div align="center">
  <a href="https://github.com/JustinMichaudOuellette/Interference/releases/latest"><img alt="Get it on GitHub" height="100" src="https://raw.githubusercontent.com/JustinMichaudOuellette/Lumiere/main/assets/images/badge_github.png"></a>
  <a href="https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%22ca.justinmo.interference%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FJustinMichaudOuellette%2FInterference%22%2C%22author%22%3A%22JustinMichaudOuellette%22%2C%22name%22%3A%22Interference%22%2C%22preferredApkIndex%22%3A0%2C%22additionalSettings%22%3A%22%7B%5C%22includePrereleases%5C%22%3Atrue%2C%5C%22fallbackToOlderReleases%5C%22%3Atrue%2C%5C%22filterReleaseTitlesByRegEx%5C%22%3A%5C%22%5C%22%2C%5C%22filterReleaseNotesByRegEx%5C%22%3A%5C%22%5C%22%2C%5C%22verifyLatestTag%5C%22%3Afalse%2C%5C%22sortMethodChoice%5C%22%3A%5C%22date%5C%22%2C%5C%22useLatestAssetDateAsReleaseDate%5C%22%3Afalse%2C%5C%22releaseTitleAsVersion%5C%22%3Afalse%2C%5C%22trackOnly%5C%22%3Afalse%2C%5C%22versionExtractionRegEx%5C%22%3A%5C%22%5C%22%2C%5C%22matchGroupToUse%5C%22%3A%5C%22%5C%22%2C%5C%22versionDetection%5C%22%3Atrue%2C%5C%22releaseDateAsVersion%5C%22%3Afalse%2C%5C%22useVersionCodeAsOSVersion%5C%22%3Afalse%2C%5C%22apkFilterRegEx%5C%22%3A%5C%22%5C%22%2C%5C%22invertAPKFilter%5C%22%3Afalse%2C%5C%22autoApkFilterByArch%5C%22%3Atrue%2C%5C%22appName%5C%22%3A%5C%22Interference%5C%22%2C%5C%22appAuthor%5C%22%3A%5C%22JustinMichaudOuellette%5C%22%2C%5C%22shizukuPretendToBeGooglePlay%5C%22%3Afalse%2C%5C%22allowInsecure%5C%22%3Afalse%2C%5C%22exemptFromBackgroundUpdates%5C%22%3Afalse%2C%5C%22skipUpdateNotifications%5C%22%3Afalse%2C%5C%22about%5C%22%3A%5C%22%5C%22%2C%5C%22refreshBeforeDownload%5C%22%3Afalse%7D%22%2C%22overrideSource%22%3A%22GitHub%22%7D"><img alt="Get it on Obtainium" height="100" src="https://github.com/user-attachments/assets/713d71c5-3dec-4ec4-a3f2-8d28d025a9c6"></a>
</div>

## Screenshots

<p align="center">
  <img src="screenshot.png" width="400" alt="Interférence Screenshot">
</p>

## Requirements

- Android 8.0 (API level 27) or higher.
- A device with an accelerometer for full sensor-based interaction.

## Building

To build the project, use the following Gradle command:

```bash
./gradlew :app:assembleDebug
```

## License

This project is licensed under the **GNU General Public License v3.0**. See the [LICENSE](LICENSE) file for details.

## Credits

Developed by **Justin Michaud-Ouellette**.
Website: [justinmo.ca](https://www.justinmo.ca)
