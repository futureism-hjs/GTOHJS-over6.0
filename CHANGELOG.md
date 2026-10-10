# GTO HJS Changelog

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
