# GTOHJS for GTO 6.0+

> 当前适配版本：`gtohjs-dev1-for-gtocore-0.6.0-dev11-fix5.jar`。
> 适用于 GTO 0.6.0-dev11（GTOCore 26.10.1、GTCEu 26.10.17、AE2 15.2610.4）。
> 本版保留现有功能并加入七台 GTL 来源多方块；四台原订单机器在 X/Q 位允许放置物品和流体输出仓，I 位仍为坚固机械方块。
> Java 21 联网清洁构建及 dev11 客户端启动、进世界验证通过。下文历史版本信息请以本段和 [英文说明](README_EN.md) 为准。

> **当前目标：GTO 0.6.0-dev10。** 当前 JAR 为 `gtohjs-dev1-for-gtocore-0.6.0-dev10-fix4.jar`，Forge 模组版本为 `1.0.0-dev10-fix4`。Java 21 联网清洁构建、生成配方检查及原版 dev10 Core 静态注入检查已通过；按本次要求不进行客户端测试。下文涉及 dev9 的旧记录仅供历史参考。

[中文](README.md) | [English](README_EN.md)

当前版本：`gtohjs-dev1-for-gtocore-0.6.0-dev9-fix4.jar`

- [更新日志](CHANGELOG.md)
- [英文 README](README_EN.md)
- [本地构建依赖说明](libs/README.md)

GTO HJS 为 Minecraft 1.20.1 Forge 版 GregTech Odyssey 0.6.0-dev9
扩展额外的机器、配方和开发工具。新增内容沿用 GTO 的原生注册窗口。

fix4 将「由GTO HJS添加」中的「GTO HJS」改为原版 GTO 动态逐字彩虹，
其余文字保持普通颜色；同时修正配方生成器的导出与校验，并将对应的
tooltip、界面、渲染和通用辅助模块迁移为 Kotlin。
Java 21 联网清洁构建与代码层检查已通过，客户端已部署，由用户自行进行游戏测试。

本文件是默认中文版产品说明，开发时不读取。
开发时读取 `README_EN.md` 和不上传的 `README_codex.md`。

## 完整内容索引

- 22 个独立的 `gtohjs` 物品：配方编辑器、自定义多方块结构导出器、真空覆盖、
  2 个预装 AE 元件包、整体青铜框架和 16 个世界碎片。
- 23 个 `gtocore` 机器或仓室定义：13 台多方块，以及单方块采集器、
  电磁热力控制的两种形态、进气仓和 ME 总成。
- 1 个真空覆盖定义，支持真空等级 1–3。
- 4 个独立配方类型：一站式稀土处理、碎片世界采集、超维度生化处理和大型花药台。
- 268 条有限 GT 配方和 23 条工作台有序配方。fix3 测试环境还注册了
  408 条材料批量配方和 71 条 Botania 代理配方；动态数量随实际材料和配方变化。
- ME 机制：物品/流体组合输入、库存输入、可配置超级与通配符样板网格、
  代理输出及原版模式控制。
- 机器机制：17 模式、最高 MV 配方和最终 1t 的通用蒸汽厂；自定义并行与线程；
  固定 524,288 并行；发电与蒸汽阵列；热力控制；气体选择和 Botania 配方代理。
- 开发工具：机器/工作台配方生成、中键数量编辑、两点多方块扫描和 Java 结构导出。
- 元件包：普通包和超级包分别包含 129 和 17 种物品，每种预装
  `16,777,216`，满电 `20,000`，并使用独立的外部存储 UUID。

## 运行环境与构建

| 组件 | 目标版本 |
| --- | --- |
| Minecraft | 1.20.1 |
| Forge | 47.4.20 |
| Java | Java 21；Java/Kotlin 均为 JVM 21 字节码 |
| GTOCore | 0.6.0-dev9，原版 JAR 版本 26.9.5 |
| GTM / GTCEu | 26.9.70 |
| AE2 | 15.269.3 |
| Configuration | 3.1.0 |
| Kotlin | 编译器/标准库 API 2.3.20，运行环境由整合包提供 |

Botania、AppBot、LDLib、DataSyncLib 等沿用目标整合包中的依赖。
第三方 Mod JAR 不打包进 GTOHJS。ME Placement Tool for gto 是独立 Mod，
不属于 GTOHJS 的依赖。

关闭游戏，将当前 JAR 放进 dev9 实例的 `mods`，仅保留一个 GTOHJS 版本。
使用 Java 21，在项目根目录通过 Git Bash 联网清洁构建：

```bash
./gradlew clean build
```

产物位于 `build/libs/gtohjs-dev1-for-gtocore-0.6.0-dev9-fix4.jar`，
Forge 内部版本为 `1.0.0-dev9-fix4`。

## 独立物品与方块

| 名称 | 注册 ID | 用途 |
| --- | --- | --- |
| 配方编辑器 | `gtohjs:recipe_editor` | 从 GT 机器或工作台导出完整 Java 配方。 |
| 自定义多方块结构导出器 | `gtohjs:multiblock_structure_generator` | 扫描选定区域并导出 Java 结构草稿。 |
| 真空覆盖 | `gtohjs:vacuum_cover` | 为支持的机器或维护仓提供真空等级 1–3。 |
| 普通 AE 元件包 | `gtohjs:normal_ae_component_pack` | 预装 129 种普通组件。 |
| 超级 AE 元件包 | `gtohjs:super_ae_component_pack` | 预装 17 种超级样板与装配组件。 |
| 整体青铜框架 | `gtohjs:integral_bronze_framework` | 具有模型、材质、掉落与配方的结构方块。 |

16 种 `gtohjs:world_fragments_*` 分别为主世界、下界、末地、远古世界
（`reactor`）、月球、火星、金星、水星、谷神星、木卫一、木卫三、冥王星、
土卫二、土卫六、霜原星和巴纳德 C（`barnarda`）。

两个元件包复用 AE2 便携元件盒的配色，库存身份相互独立。
所有 45 个属于 HJS 的物品 ID 均保留添加者提示，包括明确归属 HJS 的机器物品；
只有「GTO HJS」使用动态彩虹。

## 所有机器与多方块仓室

以下 23 个定义均使用 `gtocore` 命名空间。

| 名称 | 注册路径 | 核心行为 |
| --- | --- | --- |
| ULV 碎片世界采集器 | `ulv_fragment_world_collection_machine` | 单方块采集器，支持等级超频。 |
| 大型碎片世界采集器 | `large_fragment_world_collection_machine` | 自定义并行、256 倍耗能与 0.25 倍耗时。 |
| 通用蒸汽厂 | `universal_steam_factory` | 17 模式、MV 及以下配方、最终耗时 1t。 |
| 一站式稀土处理厂 | `one_stop_rare_earth_processing_plant` | 独立稀土处理、原版并行与维护支持。 |
| 超维度锻炉 | `hyperdimensional_forge` | 无能源土高炉处理、固定 524,288 并行与 1t。 |
| 超维度蒸汽熔炉 | `hyperdimensional_steam_furnace` | 蒸汽熔炉处理、固定 524,288 并行与 1t。 |
| 超维度冶炼炉 | `hyperdimensional_smelter` | 高炉/合金高炉模式，温度条件与自定义并行/线程。 |
| 超维度化工厂 | `hyperdimensional_chemical_factory` | 大型化反/聚合模式、真空 4 和自定义并行/线程。 |
| 超维度生化工厂 | `hyperdimensional_biochemical_factory` | 独立生化配方类型与原版线圈运行逻辑。 |
| 进阶发电阵列 | `advanced_generator_array` | 16 台支持的发电机、固定 2 倍发电与零无线传输损耗。 |
| 蒸汽阵列 | `steam_array` | 16 台低压锅炉、1.5 倍额定产汽。 |
| 进阶蒸汽阵列 | `advanced_steam_array` | 64 台低压/高压/太阳能锅炉，太阳能沿用原版日照判定。 |
| 进阶炼金锅 | `advanced_alchemy_cauldron` | 概率输入不消耗、概率输出必定成功，排除热力仓。 |
| 大型花药台 | `large_petal_apothecary` | 魔力花园模式和 Botania 花药台代理。 |
| ME 输入总成 | `me_input_assembly` | 组合物品与流体 ME 输入。 |
| ME 库存输入总成 | `me_stocking_input_assembly` | 组合库存输入与原版模式选择。 |
| ME 超级样板总成 | `me_super_pattern_buffer` | 可配置样板分页与双向输出。 |
| ME 超级样板总成代理 | `me_super_pattern_buffer_proxy` | 绑定超级样板总成后访问槽位并转发输出。 |
| ME 超级通配符样板总成 | `me_super_wildcard_pattern_buffer` | 可配置网格、通配搜索/黑名单和输出。 |
| 电磁热力控制仓 | `electromagnetic_thermal_control_hatch` | MV 热量部件、共享温度控制和螺丝刀形态切换。 |
| 电磁热力控制机 | `electromagnetic_thermal_control_machine` | 零能耗独立热力形态和可选输出方向。 |
| 进阶无限进气仓 | `advanced_infinite_intake_hatch` | MV 进气，选择空气、氧气或氮气。 |
| 终极无限进气仓 | `ultimate_infinite_intake_hatch` | IV 进气，大容量与逐 tick 补满。 |

保留既有多方块形状、有效的持久化键和普通前面动画。

## 配方内容

| 类别 | 内容 |
| --- | --- |
| 碎片世界采集 | 254 条世界、矿物、流体和特殊资源配方。 |
| 稀土处理 | 3 条独立处理配方。 |
| 化学导入 | 4 条污泥处理与 1 条污泥电解配方。 |
| 机器导入 | 3 条机器建造配方。 |
| ME 总成 | 2 条装配配方。 |
| 生成的干细胞配方 | 小麦转干细胞的提取机配方。 |
| 工作台配方 | 22 条内置配方及生成的反应堆核心配方。 |
| 材料批量处理 | 多辊式轧机中 64 锭转 64 粉。 |
| Botania 集成 | 保留原始试剂的花药台代理配方。 |

超维度生化类型保留独立注册，不额外编造有限生化配方。

## 配方编辑器与结构导出器

对 GT 配方机器或工作台使用配方编辑器，中键点击虚拟物品/流体槽位编辑数量。
GT 配方保留物品/流体 NBT、电路、温度、魔力、能耗与时长。
工作台每个输入格的数量归一为 1，输出数量和 NBT 保留。

生成的完整 Java 配方放在 `<游戏目录>/gtohjs/recipes`，
声明 `com.gtohjs.recipes`，文件名与 public 无参类名一致。
手动放入 `src/main/java/com/gtohjs/recipes` 后重新构建并替换 Mod。
游戏不会自动加载导出的源码。

结构导出器将草稿放在 `<游戏目录>/gtohjs/structures`，使用当前原版结构格式。

## 方法化与方法结构

「方法化」是在 `com.gtohjs.methods` 放置共用方法类；
具体机器定义放在 `com.gtohjs.machines`，具体配方数据放在
`com.gtohjs.recipes`。具体定义保留 GTO 原生写法，例如 `.where`，
并优先复用 GTO 已有的参数获取和注册方法。

fix4 对应的 tooltip、UI/渲染和通用辅助职责改用 Kotlin；
机器运行逻辑、配方数据和既有 ASM 调用签名继续保留。

## 许可与第三方声明

源码使用 [LGPL-3.0-only](LICENSE)。贡献者原创材质和任务内容沿用
CC BY-NC-SA 4.0；第三方资源继续遵循其上游许可，不由 HJS 重新授权。

保留的来源包括 ExtendedAE 配方编辑器图标、GTOCore/GTCEu 框架与仓室/覆盖材质，
以及 GTLCore 世界碎片和采集器材质。dev9 适配沿用旧项目的资源。
## dev11 fix6 无限无线能量单元（开发中）

新增 `gtohjs:infinite_wireless_energy_unit`。它是 LV 无线能量单元，实际容量上限为 `2^126 - 1 EU`，单元损耗为 0.0%。与原版单元混用并形成无线能源塔后，Jade、变电站汇总和无线能源监视器以七色滚动文字显示“当前存储 / 无限 EU”；英文环境显示 Infinite。物品说明显示“容量：无限 EU”和“损耗：0.0%”。单次传输上限与电网速率逻辑保持 dev11 原样。当前已完成源码和静态锚点检查，Java 21 清洁构建及客户端验证仍待完成。
> 当前开发版本：`gtohjs-dev1-for-gtocore-0.6.0-dev11-fix7.jar`。无限无线能量单元的储能单元等级已设为 MAX，与 `gtocore:max_wireless_energy_unit` 同级；仅 MAX 等级玻璃外壳的无线能源塔会计入其容量。容量仍为 `2^126 - 1 EU`，损耗仍为 `0.0%`。沿用原版 MAX 单元贴图。Java 21 联网清洁构建与客户端无崩溃进入单人世界均已通过，客户端保持运行。
> dev11-fix7 更新：新增的无限无线能量单元保持 MAX 储能单元等级，但在任意玻璃等级的无线能源塔中均计入容量；原版无线能量单元仍受原有玻璃等级限制。新增单元不会触发错误的超等级警告。以下此前关于“仅 MAX 玻璃计入新增单元容量”的说明以此更新为准。
