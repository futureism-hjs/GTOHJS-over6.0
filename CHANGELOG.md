# GTO HJS Changelog

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

