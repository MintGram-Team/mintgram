<p align="center">
  <img src="https://telegram.org/img/t_logo.svg" width="180" alt="Telegram logo">
</p>

<h1 align="center">PlumGram</h1>

<p align="center">
  <a href="LICENSE">Licensed under the GNU General Public License v2.0</a>
</p>

<p align="center">
  Experimental third-party Telegram client based on
  <a href="https://github.com/DrKLO/Telegram">official sources</a>.
</p>

<p align="center">
  <a href="https://t.me/plumgram_tg">
    <img src="https://img.shields.io/badge/Channel-Telegram-229ED9?style=for-the-badge" alt="Channel Telegram">
  </a>
  <a href="https://t.me/plumgram_chat">
    <img src="https://img.shields.io/badge/Chat-Telegram-229ED9?style=for-the-badge" alt="Chat Telegram">
  </a>
  <a href="../../releases">
    <img src="https://img.shields.io/badge/Download-Releases-229ED9?style=for-the-badge" alt="Download Releases">
  </a>
</p>

## About

PlumGram is an unofficial fork of Telegram for Android with a custom visual style and privacy-focused client features.

## Features

- PlumGram branding
- PlumGram Basic theme
- Hide Read Status
- Keep Deleted Messages
- Free Voice Transcription toggle
- Custom deleted-message label colors

## Compilation Guide

1. Clone the source code.
2. Put your private values into `.env`.
3. Open the project in Android Studio. It should be opened, not imported.
4. Build the app with Android Studio or Gradle.

```bash
./gradlew :TMessagesProj_App:assembleAfatDebug
```

The APK will be generated at:

```text
TMessagesProj_App/build/outputs/apk/afat/debug/app.apk
```

## Private Values

Keep `.env`, `local.properties`, APK files, AAB files, and keystores out of git.

## Thanks To

- [Telegram](https://telegram.org)
- [Telegram Android](https://github.com/DrKLO/Telegram)
- [AyuGram](https://github.com/AyuGram)
- [exteraGram](https://github.com/exteraSquad/exteraGram)
- [Moegram](https://github.com/Moegram/Moegram#-moegram)

## License

PlumGram is distributed under the GNU General Public License v2.0. See [LICENSE](LICENSE).

## Fork Notice

PlumGram is an unofficial fork of Telegram for Android based on the official Telegram Android source code.
