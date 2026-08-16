# Privacy Policy — Clip Assist

**Last updated: 15 August 2026**

Clip Assist is a free, open-source accessibility tool for Android. This policy explains, plainly,
what the app does and does not do with your information.

## The short version

**Clip Assist collects no data, stores no personal information, and sends nothing anywhere.**
The app has no servers, no analytics, no advertising, and no account. Everything it does happens
entirely on your own device.

## Data we collect

**None.** Clip Assist does not collect, transmit, sell, or share any personal or device data. It
makes no network connections of any kind.

The only thing the app saves is a single setting on your device: which store you picked from the
in-app list. This is stored locally in Android's private app storage, is never transmitted, and is
deleted when you uninstall the app.

## The Accessibility Service

Clip Assist uses Android's Accessibility Service API — the same mechanism screen readers use — and
you must explicitly enable it in your device's Accessibility settings before the app can do
anything.

While you have pressed **Start**, the service:

- reads the content of the screen in the coupon app you are using, in order to find buttons
  labelled "Clip", and
- taps those buttons on your behalf, and scrolls the list.

This screen content is used **only in the moment, in memory, to locate and tap coupon buttons.**
It is never recorded, saved to disk, logged, or transmitted off your device. Clip Assist does not
read, store, or transmit passwords, payment details, messages, or any other personal information
that may be on screen.

The service acts only while you have started it, and stops the moment you press **Stop**, hide the
floating control, or turn the service off in Android settings.

## Permissions and why they exist

| Permission | Purpose |
|---|---|
| Accessibility Service | Locate the on-screen "Clip" buttons and tap them for you. |
| Display over other apps | Show the floating Start/Stop control on top of your coupon app, so you can always stop it. |
| Foreground service + notification | Keep the floating control reliably available during a clipping session. |

## Children

Clip Assist is not directed at children and collects no data from anyone, including children.

## Third parties

Clip Assist contains no third-party analytics, advertising, tracking, or crash-reporting SDKs. No
third party receives any information from this app.

Clip Assist is independent and is **not affiliated with, endorsed by, or sponsored by any
retailer**. When you use it with a store's app, your interaction with that store is governed by
that store's own privacy policy and terms of service, not this one.

## Open source

Clip Assist is open source. You can read exactly what it does, and verify every claim in this
policy, at: https://github.com/Assyrian-Capital/click-clip

## Changes to this policy

If this policy changes, the updated version will be published at this address with a revised "last
updated" date.

## Contact

Questions about this policy: **nirarib@gmail.com**
