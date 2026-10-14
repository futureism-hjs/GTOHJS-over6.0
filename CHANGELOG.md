# GTO HJS Changelog

## dev1-for-gtocore-0.6.0-dev15-fix3 vs 5.0per2-for-gtocore-0.5.6-beta (2026-10-14)

对比基准 / Comparison baseline: local 0.5.6-beta build
`gtohjs-5.0per2-for-gtocore-0.5.6-beta.jar` → current dev15 addon
`gtohjs-dev1-for-gtocore-0.6.0-dev15-fix3.jar`.

### 中文

- **适配新版 GTO：** 将 0.5.6per2 的机器、多方块、配方、ME 样板组件、工具与
  AE 元件包迁移到 GTO 0.6.0-dev15，适配 GTOCore 26.10.5、GTCEu 26.10.41
  和 AE2 15.2610.8 的接口。
- **新增自适配电网：** 加入四类能源/激光仓、自适配电网终端和便携网络配置闪存，
  使用 dev15 原生电网余额与路由。终端可替换无线能源塔的
  `gtocore:wireless_energy_tower_frame`；网络配置闪存显示“由 GTO HJS 添加”。
- **新增无限无线能源塔存储单元：** 容量为 `2^126−1 EU`，每方向吞吐为
  `Long.MAX_VALUE EU/t`，零损耗，支持任意玻璃等级。六面共用 A→B→C→D
  四帧动画，每帧 10 tick；指定吞吐显示位置将最大值显示为“无限”。
- **贴图与界面资源：** 调整控制器、热控仓及自适配电网终端正面 overlay 的
  图集注册；为新增存储单元使用新的资源索引文件名，避免沿用旧方块 ID 的缓存。
- **版本兼容：** 0.5.6per2 的 JAR 不适用于 dev15。无限存储单元采用独立的
  `gtohjs:infinite_wireless_energy_storage_unit` ID；0.5.6per2 中没有该物品。

### English

- **New GTO target:** Ported the 0.5.6per2 machines, multiblocks, recipes,
  ME pattern components, tools and AE component packs to GTO 0.6.0-dev15,
  adapting to GTOCore 26.10.5, GTCEu 26.10.41 and AE2 15.2610.8.
- **Adaptive wireless grid:** Added four energy/laser hatch types, an adaptive
  grid terminal and a portable network configuration flash using dev15's
  native grid balance and routing. The terminal replaces a
  `gtocore:wireless_energy_tower_frame` block. The flash shows the existing
  "Added by GTO HJS" attribution.
- **Infinite wireless tower storage unit:** Added `2^126−1 EU` capacity,
  `Long.MAX_VALUE EU/t` throughput in each direction, zero loss and support
  for any glass tier. All six faces use the A→B→C→D animation at 10 ticks per
  frame; selected throughput values display Infinite at the maximum value.
- **Models and overlays:** Updated front-sprite registration for the
  controller, thermal hatch and adaptive grid terminal. A new resource-index
  filename avoids reusing a cache containing the previous block ID.
- **Version compatibility:** The 0.5.6per2 JAR does not target dev15. The new
  storage unit has its own `gtohjs:infinite_wireless_energy_storage_unit` ID;
  this item was absent from 0.5.6per2.

---

## Intermediate 0.6 development history

## dev1-for-gtocore-0.6.0-dev15 - 2026-10-14 (development)

- Rename the addon block and item registry ID to
  `gtohjs:infinite_wireless_energy_storage_unit`; the old ID has no migration.
- Use the approved A-B-C-D four-frame texture on all six faces at 10 ticks per
  frame. Set input and output throughput to `Long.MAX_VALUE EU/t`, retain the
  `2^126 - 1 EU` capacity, zero loss and any-glass behavior, and add tooltip
  lines for throughput and glass compatibility.
- Format the station throughput row and bank input/output limit values as
  Infinite/无限 at `Long.MAX_VALUE`; finite values retain native formatting.
  Java 21 online IDEA MCP clean build and package checks passed. Client
  acceptance remains pending; the local development record has the evidence.

## dev1-for-gtocore-0.6.0-dev15 - 2026-10-12 (development)

- Retarget GTOCore 26.10.5, GTCEu 26.10.41, AE2 15.2610.8 and DataSyncLib
  26.10.9 directly; no compatibility branch for earlier baselines.
- Migrate machine tips, AE key maps, recipe keys and Botania proxy recipe type
  to the dev15 APIs. Keep the existing recipe conversion and validation logic.
- Move the wireless tower structure and capacity injections to the dev15
  dynamic structure and provider registration. Preserve native storage and
  transfer units alongside the addon infinite unit. Remove the obsolete
  account-rate bridge because the native account-rate field was removed.
- Migrate the two remaining adaptive-net render files to Kotlin while keeping
  their Java call sites. Java 21 online clean build and crash-free dev15 world
  entry pass. Register missing controller and electromagnetic thermal fronts
  explicitly during atlas preparation; visual confirmation is pending.

## dev1-for-gtocore-0.6.0-dev11-fix9 - 2026-10-10 (development)

- Inject the adaptive-net locale entries through the established Kotlin client
  language overlay and provide a readable fallback for its status components.
- Expose reusable read-only methods for infinite wireless energy unit
  parameters, native grid account data, and adaptive terminal/hatch state.

## dev1-for-gtocore-0.6.0-dev11-fix8 - 2026-10-09 (development)

- Add the adaptive wireless grid's four hatches, tower-only terminal, portable
  frequency flash and server-wide saved frequency reservations.
- Derive physical template specifications from registered dev11 wireless hatch
  abilities; retain native grid balance, voltage reach and relay routing while
  lifting account rate only for adaptive ports.
- Add terminal and hatch status interfaces, Jade details, a runtime template
  export command and a star-map node-card frequency line.
- Align the power hatch front with the adaptive input, align both laser fronts
  with the native IV 256A wireless input, draw the approved frequency-tab icon,
  and localize the terminal, hatch and configuration flash messages.
- Replace deprecated resource-location constructors across the project and
  update the two renderer atlas checks to the current block-atlas constant.

## dev1-for-gtocore-0.6.0-dev11-fix7 - 2026-10-08 (development)

- Set the infinite wireless energy unit's storage tier to `GTValues.MAX`,
  matching `gtocore:max_wireless_energy_unit`. Reuse the native MAX texture.
  Substations count the addon unit at every glass casing tier. Native units
  retain their tier check; the over-tier warning remains for excluded native
  units and ignores the accepted addon unit.
- Keep the `2^126 - 1 EU` sentinel, 0.0% loss, display overrides and native
  transfer limits. Java 21 online clean build, addon-only deployment and
  user-confirmed crash-free single-player world entry passed.

## dev1-for-gtocore-0.6.0-dev11-fix6 - 2026-10-08 (development)

- Added an LV infinite wireless energy unit with a `2^126 - 1 EU` capacity
  sentinel and 0.0% unit loss. The substation predicate accepts native units
  and the addon unit together while preserving the native match context.
- Added animated localized infinite storage text to the monitor, shared grid
  summary, station capacity row and Jade progress bar. Finite storage and
  transfer-rate paths retain their native implementations.
- Verified the original dev11 Core bytecode anchors and corrected the wireless
  predicate bridge to scan the generated structure lambda. Java 21 online
  clean build, addon-only deployment and crash-free single-player world entry
  passed; the client remains running under the project test rule.
- Replaced two deprecated calls in `GTOHJS.java` with Forge 47.4.20-supported
  constructor injection and `ResourceLocation.fromNamespaceAndPath`.

## dev1-for-gtocore-0.6.0-dev11-fix5 - 2026-10-06

- Retargeted the preserved working tree to original GTO 0.6.0-dev11 Core
  26.10.1, GTCEu 26.10.17 and AE2 15.2610.4. Migrated recipe content,
  AE Key inventories, stocking input transactions and ME output handlers to
  the dev11 contracts.
- Added seven GTL-derived multiblocks and three dedicated recipe types. The
  four order-processing machines accept native item and fluid export hatches,
  including parts with the same abilities, on X/Q while I remains casing.
- Adapted the advanced generator array's loss override to dev11 EnergyPort
  kinds while retaining the stock array's configured loss.
- Java 21 online IDEA MCP `./gradlew clean build` passed, including generated
  recipe checks. The original dev11 client passed all twelve CoreMod markers,
  30 machine IDs, twenty multiblock checks, main-menu startup and single-player
  world entry. The user confirmed testing passed. The installed addon matches
  the built JAR by SHA-256; original Core and Seal were not replaced.

## Dev10 source repair - 2026-10-05

- Repaired the item and fluid recipe-output path for the ME Super Pattern
  Buffer and ME Super Wildcard Pattern Buffer on dev10. Their shared handler now
  declares the item/fluid content types required by GTCEu's controller output
  grouping; the late-bound super-buffer proxy uses the same contract.
- The previous full dev10 check failed the stale `Native rainbow not reused`
  assertion after the separate tooltip edit. Fix5 updated the check and the
  dev11 clean build passes.

## dev1-for-gtocore-0.6.0-dev10-fix4 - 2026-10-04

Startup repair after the initial dev10 deployment: complete the two custom
ME pattern buffers' ability registration after their blocks resolve, and
refresh GTCEu's cached ability views before multiblock patterns are checked.
The version identifiers remain dev1/fix4.

Retargeted the existing fix4 feature set directly to GTO 0.6.0-dev10; dev9
compatibility is outside this build. The artifact and Forge versions change
only their target label from dev9 to dev10, retaining dev1 and fix4.

- Compiles against the original dev10 Core and its GTCEu 26.10.8 and AE2
  15.2610.2 dependencies. Build metadata and loader dependency ranges match.
- Reuses the existing thirteen machine structures through GTCEu's current
  `hasStructure()` / `getStructure()` APIs and the current two-argument
  `PageView` constructor.
- Registers both custom ME buffers through native `PatternBufferType.Builder`,
  retaining configurable slots, output abilities and their renderer.
- Uses the current UIPro number widgets for coil controls and removes the
  configurator packet callbacks removed from dev10.
- Keeps the existing fix4 recipe generation, Kotlin modules, IDs and CoreMod
  hook implementation; changes only the checker/diagnostic target wording.

Validation: Java 21 online `./gradlew clean build` and all five generated
recipe compilation samples passed. All twelve packaged CoreMod transformations
and public static helper signatures passed against the original dev10 Core
and nested GTCEu. The dev10 client then completed startup, validated all 23
machine IDs and thirteen multiblock patterns, entered an existing single-player
world, and exited normally. Further gameplay behavior remains unverified.
The whole Core `src` comparison is available in the dedicated GTO-for-Codex
Git review branch `review/gtocore-dev9-dev10-src`.

## dev1-for-gtocore-0.6.0-dev9-fix4 - 2026-10-04

Compared with the previous clean build `dev1-for-gtocore-0.6.0-dev9-fix3`:

Fixed:

- The added-by tooltip now uses GTO's native dynamic per-character rainbow
  for "GTO HJS" only. The surrounding words retain ordinary formatting.
- Recipe exports now use `gtohjs/recipes` and give the correct destination,
  `src/main/java/com/gtohjs/recipes`. Both generated Java templates declare
  `com.gtohjs.recipes`.
- Java source escaping now preserves quotes, slashes, line breaks and control
  characters in generated NBT strings. Missing item/fluid IDs fail explicitly
  instead of accepting a registry default.
- Generated GT recipes now check saved quantities, NBT, energy, duration,
  circuit, temperature and mana. Generated shaped recipes check their output,
  all nine slots and final loaded IDs. The checked-in stem-cell and reactor-core
  providers use these checks.
- Corrected the previous validation description: fix3 registered 23 shaped
  raw IDs, but only 22 builtins had final checks; the generated reactor's
  inherited empty callbacks did not verify it.

Changed:

- Migrated twelve tooltip, UI/render and utility modules to Kotlin 2.3.20.
  Both Java and Kotlin target JVM 21, while Java/ASM entry signatures remain stable.
- Reused the existing Kotlin tooltip and structure parser implementations,
  preserving dev9 structure integration and original dimensions.
- Centralized the editor's source generation in one Kotlin method helper;
  generated recipe data remains ordinary Java classes included manually.
- Recorded the actual dev9 Core inventory of 41 Kotlin files and rewrote the
  README and changelog in the previous project's feature-oriented style.

Verification: Java 21.0.5 online `./gradlew clean build` through IDEA MCP passed.
Five actual generated samples compile against dev9, production discovery works
for both directories and JARs, and Java/SNBT round-trip controls pass. Twelve
original CoreMod targets and all packaged static bridge signatures pass.
Fix3 is backed up and fix4 deployed. No fix4 client was launched; practical
recipe, tooltip, UI and gameplay tests belong to the user.

## dev1-for-gtocore-0.6.0-dev9-fix3 - 2026-10-03

Compared with the previous clean build `dev1-for-gtocore-0.6.0-dev9-fix2`:

Added:

- Restored thermal/intake parts, ME assemblies and super pattern buffers,
  proxy outputs, Vacuum Cover, both component packs, developer tools and
  configurable pattern grids.
- Restored the two ME processing and seven shaped recipes omitted by fix2;
  the finite baseline is 268 GT recipes and 23 shaped raw IDs.
- Added the "Added by GTO HJS" line to all 45 owned item IDs, using AQUA at this stage.

Changed:

- Adapted the existing features to dev9's native UI, recipe-search, capacity
  and proxy APIs while retaining original IDs and useful persistence keys.
- Extended the existing CoreMod path for cover, proxy and buffer-layout behavior.

Verification: Java 21 online clean build, deployment, original-client startup
and world entry passed. Logs validate 23 machine IDs, 268 finite GT recipes,
408 bulk material recipes, 22 builtin crafts and 71 Botania proxies.
Minecraft tooltip generation passed for all 45 owned items. Further gameplay
tests are user-owned; the generated reactor validation gap was found and
corrected in fix4.

## dev1-for-gtocore-0.6.0-dev9-fix2 - 2026-10-03

Compared with the previous repaired clean build `dev1-for-gtocore-0.6.0-dev9-fix1`:

Added:

- Adapted all thirteen existing multiblocks using dev9's native structure
  builders, retaining original machine IDs, dimensions and predicates.
- Added 266 finite GT recipes, sixteen shaped recipes, bulk material processing
  and Botania petal proxies within the approved machine/recipe scope.
- Added Integral Bronze Framework and sixteen world-fragment items/resources.

Changed:

- Reused shared recipe discovery and the original GTO lifecycle save window.
- Left chambers/hatches, tools, component packs and other excluded features
  for the next authorized node.

Verification: Java 21 online clean build, original-bytecode checks and deployment
passed. Codex stopped after deployment without starting the client.

## dev1-for-gtocore-0.6.0-dev9-fix1 - 2026-10-03

Added:

- Registered the ULV Fragment World Collection Machine through shared methods
  under `com.gtohjs.methods`, with concrete data under `com.gtohjs.machines`.
- Retained its original ID, empty recipe type, native runtime and overlays.
  This node registered the machine only, with no processing or crafting recipes.

Fixed:

- Separated the filename's requested version label from Forge's numeric loader
  version, correcting the addon rejection reported on the first restart.

Verification: Java 21 online clean build and deployment passed. The repaired
client reached the menu and confirmed native machine/type registration.
