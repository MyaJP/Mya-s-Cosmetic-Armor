# Mya's Cosmetic Armor

Wear armor just for looks. Your real armor still protects you, while other players see the cosmetic armor you chose.

This mod is a **fork of [Cosmetic Armor Reworked](https://github.com/zlainsama/CosmeticArmorReworked) by zlainsama (LainMI)**, rewritten for **Fabric and NeoForge on Minecraft 1.21.1 – 26.3**.

## Features
- A cosmetic armor screen, opened from the small button in the player inventory
- 4 cosmetic armor slots, shown instead of your real armor
- **All on/off switch** (next to the button)
  - Off (dark): cosmetic armor is shown. The switch on each slot hides that armor piece.
  - On (white): real armor is shown. The switch on each slot shows the cosmetic armor for that piece only.
  - Toggling the all on/off switch resets the per-slot switches.
- 2x2 crafting grid, like the normal inventory
- Synced to every player on the server
- Cosmetic armor is kept on death

Install on both the server and the client. The Fabric version requires Fabric API.

## Building
Each folder under `fabric/` and `neoforge/` is a separate Gradle project for one Minecraft version.

```
cd fabric/1.21.1
../gradlew build
```

On Windows, `build-all.bat` builds every version and copies the jars to `dist/`.

## License
This mod is distributed under the **Minecraft Mod Public License (MMPL) 1.0.1**, the same license as the original Cosmetic Armor Reworked. See [LICENSE.txt](LICENSE.txt).

---

# Mya's Cosmetic Armor（日本語）

見た目だけの防具を着られるMODです。本物の防具で守られたまま、ほかの人には選んだ見た目の防具が見えます。

zlainsama（LainMI）さんの [Cosmetic Armor Reworked](https://github.com/zlainsama/CosmeticArmorReworked) の**フォーク**で、Minecraft 1.21.1～26.3 の Fabric と NeoForge 向けに書き直したものです。

## できること
- インベントリの小さいボタンから、見た目の防具の画面を開けます
- 見た目の防具の欄が4つあり、本物の防具の代わりに表示されます
- **全切替スイッチ**（ボタンの横）
  - 暗いとき：見た目の防具を表示します。各欄のスイッチはその部位を隠します
  - 白いとき：本物の防具を表示します。各欄のスイッチを押した部位だけ見た目の防具になります
  - 全切替を押すと、各欄のスイッチは全部オフに戻ります
- 普通のインベントリと同じ 2x2 のクラフト欄
- サーバーにいる全員に同じように見えます
- 見た目の防具は死んでも落ちません

サーバーとクライアントの両方に入れてください。Fabric版は Fabric API が必要です。

## ライセンス
元の Cosmetic Armor Reworked と同じ **Minecraft Mod Public License (MMPL) 1.0.1** で配布しています。[LICENSE.txt](LICENSE.txt) を見てください。
