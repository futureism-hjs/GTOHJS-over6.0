package com.gtohjs.methods;

import java.nio.file.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;
import javax.tools.ToolProvider;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

/** Real generator -> javac with the active dev10 API -> production discovery/ABI checks. No game launch. */
public final class GeneratedRecipeCodeCheck {
    private static void require(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
    private static void reject(Runnable action) {
        try { action.run(); }
        catch (IllegalArgumentException expected) { return; }
        throw new AssertionError("Invalid generated data was accepted");
    }
    private static RecipeSourceGenerator.StackSpec stack(String id, int amount, String nbt) {
        return new RecipeSourceGenerator.StackSpec(id, amount, nbt);
    }
    private static ClassNode node(byte[] bytes) {
        ClassNode node = new ClassNode();
        new ClassReader(bytes).accept(node, 0);
        return node;
    }
    private static boolean calls(ClassNode node, String owner, String name) {
        for (MethodNode method : node.methods) for (var instruction : method.instructions) {
            if (instruction instanceof MethodInsnNode call && call.owner.equals(owner) && call.name.equals(name)) return true;
        }
        return false;
    }
    private static void verifyDiscovery(URL location, String controls) throws Exception {
        try (URLClassLoader loader = new URLClassLoader(new URL[]{location},
                GeneratedRecipeCodeCheck.class.getClassLoader()) {
            @Override protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                if (name.equals("com.gtohjs.methods.RecipeSourceDiscovery")) {
                    Class<?> result = findLoadedClass(name);
                    if (result == null) result = findClass(name);
                    if (resolve) resolveClass(result);
                    return result;
                }
                return super.loadClass(name, resolve);
            }
        }) {
            require(controls.equals(loader.loadClass("EscapeRoundTrip").getMethod("value").invoke(null)),
                    "Escaped Java literal did not round-trip");
            Class<?> discovery = loader.loadClass("com.gtohjs.methods.RecipeSourceDiscovery");
            var method = discovery.getDeclaredMethod("discover", Class.class); method.setAccessible(true);
            List<?> gt = (List<?>) method.invoke(null, GTRecipeBatchSource.class);
            List<?> crafting = (List<?>) method.invoke(null, CraftingRecipeSource.class);
            require(gt.size() == 3 && crafting.size() == 2, "Production discovery lost generated providers");
            for (Object provider : gt) require(provider.getClass().getConstructor().getParameterCount() == 0, "Bad GT provider ABI");
            for (Object provider : crafting) require(provider.getClass().getConstructor().getParameterCount() == 0, "Bad craft provider ABI");
        }
    }
    public static void main(String[] args) throws Exception {
        Path root = Path.of(args[0]), resources = Path.of(args[2]);
        Path sources = root.resolve("sources"), classes = root.resolve("classes");
        Files.createDirectories(sources); Files.createDirectories(classes);
        String controls = "quotes \" slash \\ dollar $ newline\ncarriage\r tab\t back\b form\f nul\0 unit\u001f del\u007f next\u0085 中文";
        String snbt = "{note:\"" + controls.replace("\\", "\\\\").replace("\"", "\\\"") + "\"}";
        require(controls.equals(net.minecraft.nbt.TagParser.parseTag(snbt).getString("note")), "Native SNBT parse lost data");
        try { net.minecraft.nbt.TagParser.parseTag("{note:"); throw new AssertionError("Invalid SNBT accepted"); }
        catch (com.mojang.brigadier.exceptions.CommandSyntaxException expected) {}
        var none = new RecipeSourceGenerator.StackSpec[0];
        var ins = new RecipeSourceGenerator.StackSpec[]{
                stack("minecraft:stone", 127, snbt), stack("minecraft:dirt", 2, "")};
        var outs = new RecipeSourceGenerator.StackSpec[]{stack("minecraft:diamond", 3, "{x:1}")};
        var fin = new RecipeSourceGenerator.StackSpec[]{stack("minecraft:water", 2147483647, snbt)};
        var fout = new RecipeSourceGenerator.StackSpec[]{stack("minecraft:lava", 1, "")};
        var grid = new RecipeSourceGenerator.StackSpec[9];
        grid[0] = stack("minecraft:stone", 64, "{x:1}");
        grid[2] = stack("minecraft:stone", 1, "{x:1}");
        grid[4] = stack("minecraft:stone", 1, "{x:2}");
        grid[8] = stack("minecraft:dirt", 127, "");
        Map<String,String> emitted = new LinkedHashMap<>();
        emitted.put("fix4_gt_plain", RecipeSourceGenerator.gt("gtceu:extractor", "fix4_gt_plain",
                ins, outs, none, none, 8, 20, 0, 0, 0));
        emitted.put("fix4_gt_metadata", RecipeSourceGenerator.gt("gtceu:assembler", "fix4_gt_metadata",
                ins, outs, fin, fout, Long.MAX_VALUE, Integer.MAX_VALUE, 24, 3600, Long.MIN_VALUE));
        emitted.put("fix4_gt_generation", RecipeSourceGenerator.gt("gtceu:extractor", "fix4_gt_generation",
                ins, outs, none, none, -32, 7, 0, 0, 0));
        emitted.put("fix4_craft_grid", RecipeSourceGenerator.crafting("fix4_craft_grid",
                stack("minecraft:diamond", 12, snbt), grid));
        var singleton = new RecipeSourceGenerator.StackSpec[9]; singleton[4] = stack("minecraft:dirt", 1, "");
        emitted.put("fix4_craft_single", RecipeSourceGenerator.crafting("fix4_craft_single",
                stack("minecraft:stone", 1, ""), singleton));
        List<String> files = new ArrayList<>();
        for (var entry : emitted.entrySet()) {
            String className = RecipeSourceGenerator.className(entry.getKey());
            String source = entry.getValue();
            require(source.startsWith("package com.gtohjs.recipes;"), "Wrong generated package");
            require(source.contains("public " + className + "() {}"), "No public no-arg constructor");
            require(!source.contains(".save("), "Untrusted generated save call");
            Path path = sources.resolve(className + ".java");
            Files.writeString(path, source); files.add(path.toString());
        }
        String craft = emitted.get("fix4_craft_grid");
        require(craft.contains("\"A A\", \" B \", \"  C\""), "Blank cells or NBT-distinct symbols lost");
        require(craft.contains("itemStack(\"minecraft:stone\", 1,"), "Craft input not normalized");
        require(craft.contains("itemStack(\"minecraft:diamond\", 12,"), "Craft output count lost");
        require(!craft.contains("itemStack(\"minecraft:stone\", 64,"), "Craft input quantity leaked");
        Path roundTrip = sources.resolve("EscapeRoundTrip.java");
        Files.writeString(roundTrip, "public class EscapeRoundTrip { public static String value() { return \"" +
                RecipeSourceGenerator.escapeJava(controls) + "\"; } }");
        files.add(roundTrip.toString());
        reject(() -> stack("missing namespace", 1, ""));
        reject(() -> stack("minecraft:stone", 0, ""));
        reject(() -> RecipeSourceGenerator.className("../BadName"));
        reject(() -> RecipeSourceGenerator.gt("gtceu:extractor", "ok", none, none, none, none, 8, 0, 0, 0, 0));
        reject(() -> RecipeSourceGenerator.crafting("empty", stack("minecraft:stone",1,""), new RecipeSourceGenerator.StackSpec[9]));
        require(!RecipeSourceGenerator.className("a-b").equals(RecipeSourceGenerator.className("a_b")), "Class-name collision");
        List<String> compilerArgs = new ArrayList<>(List.of("--release", "21", "-encoding", "UTF-8",
                "-classpath", args[1], "-d", classes.toString()));
        compilerArgs.addAll(files);
        int status = ToolProvider.getSystemJavaCompiler().run(null, System.out, System.err, compilerArgs.toArray(String[]::new));
        require(status == 0, "Actual generated sources did not compile against dev10 API");
        // Put the exact production scanner beside fixtures so its own code source is their root.
        Path scanner = classes.resolve("com/gtohjs/methods/RecipeSourceDiscovery.class");
        Files.createDirectories(scanner.getParent());
        try (var input = GeneratedRecipeCodeCheck.class.getResourceAsStream("/com/gtohjs/methods/RecipeSourceDiscovery.class")) {
            Files.copy(Objects.requireNonNull(input), scanner, StandardCopyOption.REPLACE_EXISTING);
        }
        verifyDiscovery(classes.toUri().toURL(), controls);
        Path fixtureJar = root.resolve("generated-providers.jar");
        try (var output = new java.util.jar.JarOutputStream(Files.newOutputStream(fixtureJar));
             var paths = Files.walk(classes)) {
            for (Path path : paths.filter(Files::isRegularFile).toList()) {
                output.putNextEntry(new java.util.jar.JarEntry(classes.relativize(path).toString().replace('\\', '/')));
                Files.copy(path, output);
                output.closeEntry();
            }
        }
        verifyDiscovery(fixtureJar.toUri().toURL(), controls);
        for (String id : emitted.keySet()) {
            ClassNode generated = node(Files.readAllBytes(classes.resolve("com/gtohjs/recipes/" +
                    RecipeSourceGenerator.className(id) + ".class")));
            require(generated.version == Opcodes.V21, "Generated class is not JVM21");
            require(!calls(generated, "com/gtolib/api/recipe/RecipeBuilder", "save"), "Generated untrusted save bytecode");
            if (id.contains("gt_")) {
                require(calls(generated, "com/gtohjs/methods/RecipeSourceSupport", "validateGeneratedGT"), "GT data check missing");
                require(calls(generated, "com/gtolib/api/recipe/RecipeBuilder", "buildRawRecipe"), "Native duration observation missing");
            } else require(calls(generated, "com/gtohjs/methods/RecipeSourceSupport", "rememberCrafting"), "Craft data check missing");
        }
        for (String type : List.of("client/AddedByTooltip", "client/GTOHJSConfigScreenTheme",
                "client/GTOHJSItemColorRegistration", "client/renderer/AmprosiumPatternBufferRenderer",
                "client/renderer/ElectromagneticThermalControlRenderer", "machines/ElectromagneticThermalControlUI",
                "methods/HyperdimensionalPatternResources", "methods/HyperdimensionalTooltips",
                "methods/PatternLayoutMethods", "methods/RecipeDraftWriter", "methods/StructureDraftWriter",
                "methods/RecipeSourceSupport")) {
            try (var input = GeneratedRecipeCodeCheck.class.getResourceAsStream("/com/gtohjs/" + type + ".class")) {
                require(node(Objects.requireNonNull(input).readAllBytes()).version == Opcodes.V21, "Migrated Kotlin not JVM21: " + type);
            }
        }
        try (var input = GeneratedRecipeCodeCheck.class.getResourceAsStream("/com/gtohjs/client/AddedByTooltip.class")) {
            var tooltip = node(input.readAllBytes());
            require(calls(tooltip, "com/gtocore/api/lang/ComponentSupplier", "scrollFullColor"), "Native rainbow not reused");
            require(tooltip.methods.stream().anyMatch(m -> m.name.equals("onTooltip") &&
                    (m.access & Opcodes.ACC_STATIC) != 0), "Forge static event ABI missing");
        }
        require(Files.readString(resources.resolve("assets/gtohjs/lang/zh_cn.json")).contains("\"由%s添加\""),
                "Chinese attribution segmentation lost");
        require(Files.readString(resources.resolve("assets/gtohjs/lang/en_us.json")).contains("\"Added by %s\""),
                "English attribution segmentation lost");
        System.out.println("PASS five actual generator samples compiled against dev10/JVM21, production directory/JAR discovery 3 GT + 2 crafting");
        System.out.println("PASS control-character Java/SNBT round-trip, quantities/NBT/blank-grid source, invalid data and collision controls");
        System.out.println("PASS twelve Kotlin class targets and static native-rainbow event ABI; no game/runtime acceptance claimed");
    }
}
