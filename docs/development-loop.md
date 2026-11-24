# Development Loop

Studio, Gradle, and adb run in WSL. The emulator runs on Windows. WSL is in
mirrored networking mode, so both sides share `localhost` and the emulator
appears in WSL adb with no extra setup.

## Start

```bash
studio "projects/Final Project"

# Emulator, on the Windows side
emu-win Pixel_8
adb devices          # wait for: emulator-5554  device
```

`emu-win` is a local shell helper (`~/.local/bin/emu-win`, not part of this
repo). The equivalent by hand:

```bash
cd /mnt/c && cmd.exe /c "%LOCALAPPDATA%\Android\Sdk\emulator\emulator.exe -avd Pixel_8"
```

## Edit and run

| Change | How |
|--------|-----|
| `@Composable` body | Live Edit — no rebuild (Settings > Editor > Live Edit) |
| XML layouts, strings, colours | Apply Changes (Ctrl+F10) |
| Kotlin, Gradle files, manifest | Run `app` in Studio, or `installDebug` below |

```bash
cd "projects/Final Project"
./gradlew installDebug && adb shell am start -n com.beezy.app/.MainActivity
```

An install replaces the APK and restarts the app. The emulator keeps running, so
build as often as needed — only start the emulator once.

## Stop and recover

```bash
adb emu kill                        # clean shutdown, writes a usable snapshot
emu-win Pixel_8 -no-snapshot-load   # cold boot when the device shows as offline
```

Shut the emulator down with `adb emu kill` or its ⋮ menu, not the window's close
button. Closing the window mid-session can leave a bad quick-boot snapshot, and
the next start stays `offline` until the emulator cold-boots.

One adb server owns port 5037. The Windows-side `adb` cannot bind while the WSL
server holds it, so keep adb on one side of the boundary.

## SDK paths

- SDK: `~/Android/Sdk` (WSL), `%LOCALAPPDATA%\Android\Sdk` (Windows)
- adb key: the Windows pair is copied into `~/.android/` so both sides authorise
  the same devices
- Project SDK pointer: `local.properties` → `sdk.dir`
