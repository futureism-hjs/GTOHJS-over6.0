# GTOHJS for GTO 6.0+

[中文](README.md) | English

Current development version: `gtohjs-dev1-for-gtocore-0.6.0-dev11-fix6.jar`

- [Changelog](CHANGELOG.md)
- [Local build dependencies](libs/README.md)

GTO HJS expands the Minecraft 1.20.1 Forge edition of GregTech Odyssey
0.6.0-dev11 with additional machines, recipes and development tools. It registers
its content through GTO's native loading windows. The original GTOCore,
GTOLib and GTOSeal files are read-only references.

Fix6 adds `gtohjs:infinite_wireless_energy_unit`. It is an LV wireless energy
unit with a real capacity limit of `2^126 - 1 EU` and no unit loss. In a formed
wireless energy substation it shows the capacity as animated Infinite/无限 in
Jade, the substation summary and the wireless energy monitor. Its item tooltip
shows infinite capacity and 0.0% loss. Grid transfer and rate limits retain
their dev11 behavior. Java 21 online clean build passed, and the deployed dev11
client entered a single-player world without crashing. Under the project client
test rule, that world entry passes acceptance; the client remains running.

Fix5 preserves the current source changes and targets dev11's recipe content,
AE Key inventory and ME recipe-handler APIs. It adds seven GTL-derived
multiblocks. The four order-processing structures accept item and fluid output
hatches on their original X/Q service positions while I remains robust casing.
Java 21 online clean build and generated recipe checks passed. The original
dev11 client passed all addon load checks and entered a single-player world;
the user confirmed testing passed.

## Complete content overview

- 23 independent `gtohjs` items: Recipe Editor, Custom Multiblock Structure
  Exporter, Vacuum Cover, two preloaded AE Component Packs, Integral Bronze
  Framework, Infinite Wireless Energy Unit and sixteen world fragments.
- 30 `gtocore` machine or part definitions: twenty multiblocks plus the
  collector, thermal forms, intake hatches and ME assemblies/buffers.
- One Vacuum Cover definition with vacuum levels 1-3.
- Seven additional recipe types: Rare Earth Processing, Fragment World
  Collection, Hyperdimensional Biochemical Processing, Large Petal Apothecary,
  Platinum Refining, Exotic Proliferation and Lightning Processing.
- 268 finite GT recipes and 23 shaped crafting recipes. Dynamic material
  processing and Botania proxy families registered 408 and 71 recipes in the
  fix3 test environment; their totals depend on the loaded materials and recipes.
- ME features: combined item/fluid inputs, stocking inputs, configurable super
  and wildcard pattern grids, proxy outputs and inherited native mode controls.
- Machine features: 17-mode MV-limited steam processing, custom parallel/thread
  controls, fixed 524,288 parallelism, generator/boiler arrays, temperature
  control, selectable intake gases and Botania recipe integration.
- Development tools: machine/workbench recipe generation, middle-click item/
  fluid quantities, two-point multiblock scanning and Java structure export.
- Component packs: 129 normal or 17 super component types, each preloaded at
  `16,777,216`, charged to `20,000`, with an independent external-storage UUID.

## Runtime and development dependencies

| Component | Target |
| --- | --- |
| Minecraft | 1.20.1 |
| Forge | 47.4.20 |
| Java | Java 21; Java and Kotlin bytecode target JVM 21 |
| GTOCore | dev11, original artifact version 26.10.1 |
| GTM / GTCEu | 26.10.17 |
| AE2 | 15.2610.4 |
| Configuration | 3.1.0 |
| Kotlin | Compiler/stdlib API 2.3.20; runtime supplied by the existing pack |

Botania, AppBot, LDLib, DataSyncLib and related integrations use the versions
already supplied by the target dev11 pack. Third-party Mod JARs are not embedded
in GTOHJS. The separately published ME Placement Tool for gto is not a dependency.

## Install and build

Close Minecraft, place the current GTOHJS JAR in the dev11 instance's `mods`
directory, and keep only one GTOHJS version there. Keep the original GTOCore and
GTOSeal artifacts supplied by the pack.

Use Java 21 and network access. From the project root in Git Bash, with the
Java 21 runtime configured, run:

```bash
./gradlew clean build
```

The artifact is written to
`build/libs/gtohjs-dev1-for-gtocore-0.6.0-dev11-fix6.jar`.
Its numeric Forge loader version is `1.0.0-dev11-fix6`.

## Standalone items and block

| Name | Registry ID | Function |
| --- | --- | --- |
| Recipe Editor | `gtohjs:recipe_editor` | Exports complete Java recipes from GT machines or a crafting table. |
| Custom Multiblock Structure Exporter | `gtohjs:multiblock_structure_generator` | Scans a selected cuboid and exports a Java structure draft. |
| Vacuum Cover | `gtohjs:vacuum_cover` | Supplies vacuum levels 1-3 to supported machines or maintenance hatches. |
| Normal AE Component Pack | `gtohjs:normal_ae_component_pack` | Preloads 129 normal component types. |
| Super AE Component Pack | `gtohjs:super_ae_component_pack` | Preloads 17 super-buffer and assembler component types. |
| Integral Bronze Framework | `gtohjs:integral_bronze_framework` | Structure block with its own model, texture, loot and crafting recipe. |
| Infinite Wireless Energy Unit | `gtohjs:infinite_wireless_energy_unit` | LV wireless energy unit; `2^126 - 1 EU` capacity sentinel and 0.0% unit loss. |

The sixteen `gtohjs:world_fragments_*` items represent Overworld, Nether, End,
Ancient World (`reactor`), Moon, Mars, Venus, Mercury, Ceres, Io, Ganymede,
Pluto, Enceladus, Titan, Glacio and Barnarda C (`barnarda`).

Both AE packs reuse AE2 portable-cell tinting, keep independent storage identities
and preload every listed type at `16,777,216`. The added-by tooltip covers these
items and all explicitly owned machine items; only the "GTO HJS" name is rainbow.

## All machines and multiblock parts

All 30 definitions use the `gtocore` namespace.

| Name | Registry path | Core behavior |
| --- | --- | --- |
| ULV Fragment World Collection Machine | `ulv_fragment_world_collection_machine` | Single-block collector with tiered overclocking. |
| Large Fragment World Collection Machine | `large_fragment_world_collection_machine` | Custom parallelism, 256x energy and 0.25x duration. |
| Universal Steam Factory | `universal_steam_factory` | Seventeen modes, MV-and-below recipes and final 1t duration. |
| One-Stop Rare Earth Processing Plant | `one_stop_rare_earth_processing_plant` | Dedicated rare-earth processing with native parallel and maintenance support. |
| Hyperdimensional Forge | `hyperdimensional_forge` | Primitive blast-furnace processing without energy, fixed 524,288 parallelism and 1t. |
| Hyperdimensional Steam Furnace | `hyperdimensional_steam_furnace` | Steam furnace processing with fixed 524,288 parallelism and 1t. |
| Hyperdimensional Smelter | `hyperdimensional_smelter` | Blast/alloy-blast modes, required temperature and custom parallel/threads. |
| Hyperdimensional Chemical Factory | `hyperdimensional_chemical_factory` | Large chemical/polymerization modes, vacuum level 4 and custom parallel/threads. |
| Hyperdimensional Biochemical Factory | `hyperdimensional_biochemical_factory` | Independent biochemical type and native coil-based runtime. |
| Advanced Generator Array | `advanced_generator_array` | Sixteen supported generators, fixed 2x generation and zero wireless transmission loss. |
| Steam Array | `steam_array` | Sixteen low-pressure boilers and 1.5x nominal steam output. |
| Advanced Steam Array | `advanced_steam_array` | Sixty-four low/high-pressure or solar boilers; solar mode uses native sunlight checks. |
| Advanced Alchemy Cauldron | `advanced_alchemy_cauldron` | Non-consumed chance inputs, guaranteed chance outputs and thermal-hatch exclusion. |
| Large Petal Apothecary | `large_petal_apothecary` | Mana-garden modes and Botania petal-apothecary recipe proxies. |
| ME Input Assembly | `me_input_assembly` | Combined item and fluid ME inputs. |
| ME Stocking Input Assembly | `me_stocking_input_assembly` | Combined stocking inputs and native mode selection. |
| ME Super Pattern Buffer | `me_super_pattern_buffer` | Configurable pattern pages and bidirectional outputs. |
| ME Super Pattern Buffer Proxy | `me_super_pattern_buffer_proxy` | Bound super-buffer access and output forwarding. |
| ME Super Wildcard Pattern Buffer | `me_super_wildcard_pattern_buffer` | Configurable wildcard grid, native search/blacklist behavior and outputs. |
| Electromagnetic Thermal Control Hatch | `electromagnetic_thermal_control_hatch` | MV heat part, shared temperature controls and screwdriver conversion. |
| Electromagnetic Thermal Control Machine | `electromagnetic_thermal_control_machine` | Standalone zero-energy heat form and selectable output direction. |
| Advanced Infinite Intake Hatch | `advanced_infinite_intake_hatch` | MV intake with selectable air, oxygen or nitrogen. |
| Ultimate Infinite Intake Hatch | `ultimate_infinite_intake_hatch` | IV intake with large capacity and per-tick refill. |
| Plasma Machine Tool | `plasma_machine_tool` | Order-aware processing with item and fluid outputs on X/Q. |
| Hadron Catalytic Refinery | `hadron_catalytic_refinery` | Order-aware processing with item and fluid outputs on X/Q. |
| Quantum Mass Spectrum Array | `quantum_mass_spectrum_array` | Order-aware processing with item and fluid outputs on X/Q. |
| Superconducting Fusion Assembler | `superconducting_fusion_assembler` | Order-aware processing with item and fluid outputs on X/Q. |
| Neutron Control Factory | `neutron_control_factory` | GTL-derived neutron processing structure. |
| Platinum Refining Matrix | `platinum_refining_matrix` | GTL-derived platinum refining structure. |
| Dragon Field Proliferation Core | `dragon_field_proliferation_core` | GTL-derived proliferation structure. |

Existing multiblock shapes, useful persistence keys and ordinary front animations
are retained.

## Recipe content

| Family | Content |
| --- | --- |
| Fragment World Collection | 254 world, ore, fluid and special-resource recipes. |
| Rare Earth Processing | Three dedicated processing recipes. |
| Chemical imports | Four sludge-processing recipes and one sludge electrolysis recipe. |
| Machine imports | Three machine-construction recipes. |
| ME assemblies | Two assembly recipes. |
| Generated stem cells | Wheat-to-stem-cells extractor recipe. |
| Shaped crafting | Twenty-two builtin crafts and the generated reactor-core recipe. |
| Bulk material processing | 64 ingots to 64 dust in the native cluster mill. |
| Botania integration | Petal-apothecary proxies retaining the original reagent. |

The biochemical type preserves its existing independent registration; this
adaptation does not invent a new finite biochemical recipe set.

## Recipe Editor and structure exporter

Use the Recipe Editor on a GT recipe machine or a crafting table. Middle-click
its phantom item/fluid slots to edit quantities. GT exports retain item/fluid
NBT, circuit, temperature, mana, EU/t and duration. Workbench input counts are
one per occupied slot; output counts and NBT remain intact.

Generated recipe files are written to `<gameDir>/gtohjs/recipes`. Each file
declares `com.gtohjs.recipes`, and its filename matches its public no-argument
Java class. Copy the complete class into `src/main/java/com/gtohjs/recipes`,
then rebuild and replace GTOHJS. Exported files are not loaded automatically.

The structure exporter writes drafts under `<gameDir>/gtohjs/structures`.
It preserves the current native structure format and is separate from recipe export.

## Methodization (方法化) and method structure (方法结构)

Shared methods belong in `com.gtohjs.methods`; concrete machine definitions
belong in `com.gtohjs.machines`; concrete recipe data belongs in
`com.gtohjs.recipes`. Keep native GTO builder data, including `.where`, visible
in concrete definitions. Reuse existing GTO parameter and registration methods.

Fix4 uses Kotlin for corresponding tooltip, UI/render and general helper
responsibilities while retaining Java machine runtime, Java recipe data and
the existing ASM call signatures. Development records contain implementation
and verification details in the local workspace; this README describes the
delivered features. Development reads this English README and the ignored
README_codex.md path guide. README.md is the Chinese product document.

## License and third-party notices

Source code uses [LGPL-3.0-only](LICENSE). Original contributor-owned textures
and quest content retain the prior CC BY-NC-SA 4.0 content license. Third-party
assets remain under their upstream licenses and are not relicensed by HJS.

Retained asset provenance includes ExtendedAE's Recipe Editor icon, GTOCore/
GTCEu framework and hatch/cover overlays, and GTLCore world-fragment and collector
textures. This adaptation reuses these assets from the read-only old project.
This source workspace and its local validation JAR do not constitute a newly
published upstream or third-party release.
