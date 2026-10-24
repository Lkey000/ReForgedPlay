# ReForgedPlay Mod
This is an unofficial neoforge port of the [Replay Mod](https://github.com/ReplayMod/ReplayMod) for 1.20.4. 
Do not ask for support on the official Replay Mod GitHub or discord, as they do not support this. 

# ReForgedPlay Texture FIX (Unofficial)

This is an unofficial patched fork of [ReForgedPlay](https://github.com/ferriarnus/ReForgedPlay)
(the Forge port of the Replay Mod) for Minecraft 1.20.1.
It is not affiliated with the Replay Mod or its authors.
Licensed under GPL-3.0-or-later, the same as the original.

Original authors: CrushedPixel, johni0702, Ferri_Arnus.

## Changes from the original

- Fix: block textures swapped when replaying a recording after the mod list has changed.
  The replay now waits for Forge's registry synchronization to finish before it starts sending world data.
  (`ReplayPacketGate`, `Mixin_WaitForRegistrySync`)
- Fix: TrashSlot's trash slot disappearing while the Replay Mod is installed. (`TrashSlotFix`)
- Fix: clicking the timeline while paused resumes playback when Citadel is installed.
  Citadel's client tick rate update is skipped while a replay is open. (`Mixin_CitadelTickRate`)

## Compatibility

- Minecraft 1.20.1
- Tested with Forge 47.4.10, 47.4.21, 47.4.27 and 47.4.28.
- Forge versions older than 47.4.10 are not supported (a startup crash was observed on 47.0.0).
- This is a replacement for the original ReForgedPlay. Do not install both at the same time.

## Download

See the [Releases](https://github.com/Lkey000/ReForgedPlay/releases) page.

## Issues

Please do not report problems with this build to the Replay Mod authors.
Report them here: https://github.com/Lkey000/ReForgedPlay/issues

---

(original README below)
