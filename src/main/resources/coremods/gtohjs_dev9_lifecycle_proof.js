var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
var IincInsnNode = Java.type('org.objectweb.asm.tree.IincInsnNode');
var IntInsnNode = Java.type('org.objectweb.asm.tree.IntInsnNode');
var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
var LdcInsnNode = Java.type('org.objectweb.asm.tree.LdcInsnNode');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

function buildCatalogGTRecipes(recipeIndexLocal) {
    var instructions = new InsnList();
    var check = new LabelNode();
    var end = new LabelNode();

    instructions.add(ASMAPI.buildMethodCall(
        'com/gtohjs/methods/RecipeRegistrationMethods',
        'beginGTRegistration',
        '()V',
        ASMAPI.MethodType.STATIC
    ));
    instructions.add(new InsnNode(Opcodes.ICONST_0));
    instructions.add(new VarInsnNode(Opcodes.ISTORE, recipeIndexLocal));
    instructions.add(check);
    instructions.add(new VarInsnNode(Opcodes.ILOAD, recipeIndexLocal));
    instructions.add(ASMAPI.buildMethodCall(
        'com/gtohjs/methods/RecipeRegistrationMethods',
        'gtRecipeCount',
        '()I',
        ASMAPI.MethodType.STATIC
    ));
    instructions.add(new JumpInsnNode(Opcodes.IF_ICMPGE, end));

    // RecipeType.recipeBuilder(...) and RecipeBuilder.save() remain literal
    // instructions in Data.commonInit(), after RecipeFilter.init().
    instructions.add(new VarInsnNode(Opcodes.ILOAD, recipeIndexLocal));
    instructions.add(ASMAPI.buildMethodCall(
        'com/gtohjs/methods/RecipeRegistrationMethods',
        'gtRecipeType',
        '(I)Lcom/gtolib/api/recipe/RecipeType;',
        ASMAPI.MethodType.STATIC
    ));
    instructions.add(new VarInsnNode(Opcodes.ILOAD, recipeIndexLocal));
    instructions.add(ASMAPI.buildMethodCall(
        'com/gtohjs/methods/RecipeRegistrationMethods',
        'gtRawId',
        '(I)Lnet/minecraft/resources/ResourceLocation;',
        ASMAPI.MethodType.STATIC
    ));
    instructions.add(new MethodInsnNode(
        Opcodes.INVOKEVIRTUAL,
        'com/gtolib/api/recipe/RecipeType',
        'recipeBuilder',
        '(Lnet/minecraft/resources/ResourceLocation;)Lcom/gtolib/api/recipe/RecipeBuilder;',
        false
    ));
    instructions.add(new InsnNode(Opcodes.DUP));
    instructions.add(new VarInsnNode(Opcodes.ILOAD, recipeIndexLocal));
    instructions.add(ASMAPI.buildMethodCall(
        'com/gtohjs/methods/RecipeRegistrationMethods',
        'configureGTRecipe',
        '(Lcom/gtolib/api/recipe/RecipeBuilder;I)V',
        ASMAPI.MethodType.STATIC
    ));
    instructions.add(new MethodInsnNode(
        Opcodes.INVOKEVIRTUAL,
        'com/gtolib/api/recipe/RecipeBuilder',
        'save',
        '()Lcom/gregtechceu/gtceu/api/recipe/GTRecipeDefinition;',
        false
    ));
    instructions.add(new VarInsnNode(Opcodes.ILOAD, recipeIndexLocal));
    instructions.add(ASMAPI.buildMethodCall(
        'com/gtohjs/methods/RecipeRegistrationMethods',
        'acceptGTRecipe',
        '(Lcom/gregtechceu/gtceu/api/recipe/GTRecipeDefinition;I)V',
        ASMAPI.MethodType.STATIC
    ));
    instructions.add(new IincInsnNode(recipeIndexLocal, 1));
    instructions.add(new JumpInsnNode(Opcodes.GOTO, check));
    instructions.add(end);
    instructions.add(ASMAPI.buildMethodCall(
        'com/gtohjs/methods/RecipeRegistrationMethods',
        'completeGTRegistration',
        '()V',
        ASMAPI.MethodType.STATIC
    ));
    return instructions;
}

function buildCatalogMaterialRecipes(materialLocal, recipeIndexLocal) {
    var instructions = new InsnList();
    var check = new LabelNode();
    var end = new LabelNode();

    instructions.add(new VarInsnNode(Opcodes.ALOAD, materialLocal));
    instructions.add(ASMAPI.buildMethodCall(
        'com/gtohjs/methods/RecipeRegistrationMethods',
        'beginMaterialRecipes',
        '(Lcom/gregtechceu/gtceu/api/data/chemical/material/Material;)Z',
        ASMAPI.MethodType.STATIC
    ));
    instructions.add(new JumpInsnNode(Opcodes.IFEQ, end));
    instructions.add(new InsnNode(Opcodes.ICONST_0));
    instructions.add(new VarInsnNode(Opcodes.ISTORE, recipeIndexLocal));
    instructions.add(check);
    instructions.add(new VarInsnNode(Opcodes.ILOAD, recipeIndexLocal));
    instructions.add(ASMAPI.buildMethodCall(
        'com/gtohjs/methods/RecipeRegistrationMethods',
        'materialRecipeCount',
        '()I',
        ASMAPI.MethodType.STATIC
    ));
    instructions.add(new JumpInsnNode(Opcodes.IF_ICMPGE, end));

    instructions.add(new VarInsnNode(Opcodes.ALOAD, materialLocal));
    instructions.add(new VarInsnNode(Opcodes.ILOAD, recipeIndexLocal));
    instructions.add(ASMAPI.buildMethodCall(
        'com/gtohjs/methods/RecipeRegistrationMethods',
        'materialRecipeType',
        '(Lcom/gregtechceu/gtceu/api/data/chemical/material/Material;I)Lcom/gtolib/api/recipe/RecipeType;',
        ASMAPI.MethodType.STATIC
    ));
    instructions.add(new VarInsnNode(Opcodes.ALOAD, materialLocal));
    instructions.add(new VarInsnNode(Opcodes.ILOAD, recipeIndexLocal));
    instructions.add(ASMAPI.buildMethodCall(
        'com/gtohjs/methods/RecipeRegistrationMethods',
        'materialRawId',
        '(Lcom/gregtechceu/gtceu/api/data/chemical/material/Material;I)Lnet/minecraft/resources/ResourceLocation;',
        ASMAPI.MethodType.STATIC
    ));
    instructions.add(new MethodInsnNode(
        Opcodes.INVOKEVIRTUAL,
        'com/gtolib/api/recipe/RecipeType',
        'recipeBuilder',
        '(Lnet/minecraft/resources/ResourceLocation;)Lcom/gtolib/api/recipe/RecipeBuilder;',
        false
    ));
    instructions.add(new InsnNode(Opcodes.DUP));
    instructions.add(new VarInsnNode(Opcodes.ALOAD, materialLocal));
    instructions.add(new VarInsnNode(Opcodes.ILOAD, recipeIndexLocal));
    instructions.add(ASMAPI.buildMethodCall(
        'com/gtohjs/methods/RecipeRegistrationMethods',
        'configureMaterialRecipe',
        '(Lcom/gtolib/api/recipe/RecipeBuilder;Lcom/gregtechceu/gtceu/api/data/chemical/material/Material;I)V',
        ASMAPI.MethodType.STATIC
    ));
    instructions.add(new MethodInsnNode(
        Opcodes.INVOKEVIRTUAL,
        'com/gtolib/api/recipe/RecipeBuilder',
        'save',
        '()Lcom/gregtechceu/gtceu/api/recipe/GTRecipeDefinition;',
        false
    ));
    instructions.add(new VarInsnNode(Opcodes.ALOAD, materialLocal));
    instructions.add(new InsnNode(Opcodes.SWAP));
    instructions.add(new VarInsnNode(Opcodes.ILOAD, recipeIndexLocal));
    instructions.add(ASMAPI.buildMethodCall(
        'com/gtohjs/methods/RecipeRegistrationMethods',
        'acceptMaterialRecipe',
        '(Lcom/gregtechceu/gtceu/api/data/chemical/material/Material;' +
            'Lcom/gregtechceu/gtceu/api/recipe/GTRecipeDefinition;I)V',
        ASMAPI.MethodType.STATIC
    ));
    instructions.add(new IincInsnNode(recipeIndexLocal, 1));
    instructions.add(new JumpInsnNode(Opcodes.GOTO, check));
    instructions.add(end);
    return instructions;
}

function buildCustomRecipes(recipeIndexLocal) {
    var instructions = buildCatalogGTRecipes(recipeIndexLocal);
    instructions.add(ASMAPI.buildMethodCall(
        'com/gtohjs/methods/RecipeRegistrationMethods',
        'registerCraftingRecipes',
        '()V',
        ASMAPI.MethodType.STATIC
    ));
    return instructions;
}


var FIX2_0 = {
            'target': {
                'type': 'METHOD',
                'class': 'com.gtocore.common.machine.multiblock.steam.BaseSteamMultiblockMachine',
                'methodName': 'addDisplayText',
                'methodDesc': '(Ljava/util/List;)V'
            },
            'transformer': function(method) {
                var nodes = method.instructions.toArray();
                var injected = 0;
                for (var i = 0; i < nodes.length; i++) {
                    if (nodes[i].getOpcode() === Opcodes.RETURN) {
                        var hook = new InsnList();
                        hook.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        hook.add(new VarInsnNode(Opcodes.ALOAD, 1));
                        hook.add(ASMAPI.buildMethodCall(
                            'com/gtohjs/methods/UniversalSteamFactoryModeSupport',
                            'appendDisplayText',
                            '(Lcom/gtocore/common/machine/multiblock/steam/BaseSteamMultiblockMachine;' +
                                'Ljava/util/List;)V',
                            ASMAPI.MethodType.STATIC
                        ));
                        method.instructions.insertBefore(nodes[i], hook);
                        injected++;
                    }
                }
                if (injected === 0) {
                    throw new Error('GTOHJS could not find RETURN in BaseSteamMultiblockMachine.addDisplayText');
                }
                if (method.maxStack < 2) {
                    method.maxStack = 2;
                }
                ASMAPI.log('INFO', 'GTOHJS injected universal-steam recipe-mode display');
                return method;
            }
        };

var FIX2_1 = {
            'target': {
                'type': 'METHOD',
                'class': 'com.gtocore.common.machine.multiblock.steam.BaseSteamMultiblockMachine',
                'methodName': 'getRealRecipe',
                'methodDesc': '(Lcom/gregtechceu/gtceu/api/recipe/handler/RecipeHandlerUnit;' +
                    'Lcom/gregtechceu/gtceu/api/recipe/GTRecipe;)' +
                    'Lcom/gregtechceu/gtceu/api/recipe/GTRecipe;'
            },
            'transformer': function(method) {
                var nodes = method.instructions.toArray();
                if (nodes.length === 0) {
                    throw new Error('GTOHJS found an empty BaseSteamMultiblockMachine.getRealRecipe');
                }

                var accepted = new LabelNode();
                var hook = new InsnList();
                hook.add(new VarInsnNode(Opcodes.ALOAD, 0));
                hook.add(new VarInsnNode(Opcodes.ALOAD, 2));
                hook.add(ASMAPI.buildMethodCall(
                    'com/gtohjs/methods/UniversalSteamFactoryModeSupport',
                    'isRecipeWithinMvLimit',
                    '(Lcom/gtocore/common/machine/multiblock/steam/BaseSteamMultiblockMachine;' +
                        'Lcom/gregtechceu/gtceu/api/recipe/GTRecipe;)Z',
                    ASMAPI.MethodType.STATIC
                ));
                hook.add(new JumpInsnNode(Opcodes.IFNE, accepted));
                hook.add(new InsnNode(Opcodes.ACONST_NULL));
                hook.add(new InsnNode(Opcodes.ARETURN));
                hook.add(accepted);
                method.instructions.insertBefore(nodes[0], hook);

                var durationLocks = 0;
                for (var i = 0; i < nodes.length; i++) {
                    if (nodes[i].getOpcode() === Opcodes.ARETURN) {
                        var durationHook = new InsnList();
                        durationHook.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        durationHook.add(new InsnNode(Opcodes.SWAP));
                        durationHook.add(ASMAPI.buildMethodCall(
                            'com/gtohjs/methods/UniversalSteamFactoryModeSupport',
                            'lockRecipeDuration',
                            '(Lcom/gtocore/common/machine/multiblock/steam/BaseSteamMultiblockMachine;' +
                                'Lcom/gregtechceu/gtceu/api/recipe/GTRecipe;)' +
                                'Lcom/gregtechceu/gtceu/api/recipe/GTRecipe;',
                            ASMAPI.MethodType.STATIC
                        ));
                        method.instructions.insertBefore(nodes[i], durationHook);
                        durationLocks++;
                    }
                }
                if (durationLocks === 0) {
                    throw new Error('GTOHJS could not find ARETURN in BaseSteamMultiblockMachine.getRealRecipe');
                }
                if (method.maxStack < 2) {
                    method.maxStack = 2;
                }
                ASMAPI.log('INFO', 'GTOHJS injected universal-steam MV recipe limit and 1t duration lock');
                return method;
            }
        };

var FIX2_2 = {
            'target': {
                'type': 'METHOD',
                'class': 'com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine',
                'methodName': 'createUI',
                'methodDesc': '(Lnet/minecraft/world/entity/player/Player;)Lcom/lowdragmc/lowdraglib/gui/modular/ModularUI;'
            },
            'transformer': function(method) {
                var nodes = method.instructions.toArray();
                if (nodes.length === 0) {
                    throw new Error('GTOHJS found an empty SteamParallelMultiblockMachine.createUI');
                }
                var passThrough = new LabelNode();
                var hook = new InsnList();
                hook.add(new VarInsnNode(Opcodes.ALOAD, 0));
                hook.add(new VarInsnNode(Opcodes.ALOAD, 1));
                hook.add(ASMAPI.buildMethodCall(
                    'com/gtohjs/methods/UniversalSteamFactoryModeSupport',
                    'createFancyUIForDisplay',
                    '(Lcom/gregtechceu/gtceu/api/machine/feature/multiblock/IDisplayUIMachine;' +
                        'Lnet/minecraft/world/entity/player/Player;)Lcom/lowdragmc/lowdraglib/gui/modular/ModularUI;',
                    ASMAPI.MethodType.STATIC
                ));
                hook.add(new InsnNode(Opcodes.DUP));
                hook.add(new JumpInsnNode(Opcodes.IFNULL, passThrough));
                hook.add(new InsnNode(Opcodes.ARETURN));
                hook.add(passThrough);
                hook.add(new InsnNode(Opcodes.POP));
                method.instructions.insertBefore(nodes[0], hook);
                if (method.maxStack < 2) {
                    method.maxStack = 2;
                }
                ASMAPI.log('INFO', 'GTOHJS injected universal-steam Fancy UI adapter');
                return method;
            }
        };

var FIX2_3 = {
            'target': {
                'type': 'METHOD',
                'class': 'com.gtocore.data.recipe.generated.GTOMaterialRecipeHandler',
                'methodName': 'processIngot',
                'methodDesc': '(Lcom/gregtechceu/gtceu/api/data/chemical/material/Material;)V'
            },
            'transformer': function(method) {
                var nodes = method.instructions.toArray();
                if (nodes.length === 0) {
                    throw new Error('GTOHJS found an empty GTOMaterialRecipeHandler.processIngot()V');
                }
                var injected = 0;
                for (var i = 0; i < nodes.length; i++) {
                    if (nodes[i].getOpcode() === Opcodes.INVOKESTATIC &&
                        nodes[i].owner === 'com/gtohjs/methods/RecipeRegistrationMethods' &&
                        nodes[i].name === 'beginMaterialRecipes') {
                        injected++;
                    }
                }
                if (injected !== 0) {
                    throw new Error('GTOHJS found an existing bulk cluster injection in processIngot()V');
                }
                var materialRecipeIndexLocal = method.maxLocals;
                method.maxLocals += 1;
                method.instructions.insertBefore(nodes[0],
                    buildCatalogMaterialRecipes(0, materialRecipeIndexLocal));
                if (method.maxStack < 6) {
                    method.maxStack = 6;
                }
                ASMAPI.log('INFO', 'GTOHJS injected bulk cluster recipe generation into ' +
                    'GTOMaterialRecipeHandler.processIngot(Material)');
                return method;
            }
        };

var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var FieldNode = Java.type('org.objectweb.asm.tree.FieldNode');
var BOOTSTRAP = 'com/gtohjs/bootstrap/GTOHJSBootstrap';

function requireOne(count, description) {
    if (count !== 1) throw new Error('GTOHJS dev9 expected one ' + description + ', found ' + count);
}

function findMethod(clazz, name) {
    var found = null;
    var count = 0;
    for (var i = 0; i < clazz.methods.size(); i++) {
        var method = clazz.methods.get(i);
        if (method.name === name && method.desc === '()V') { found = method; count++; }
    }
    requireOne(count, clazz.name + '.' + name + '()V');
    return found;
}

function requireMarkerAbsent(clazz, marker) {
    for (var i = 0; i < clazz.fields.size(); i++) {
        if (clazz.fields.get(i).name === marker) throw new Error('GTOHJS duplicate transform: ' + clazz.name);
    }
}

function recordMarker(clazz, marker) {
    var access=Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC;
    var value=null;
    if((clazz.access & Opcodes.ACC_INTERFACE)!==0) { access |= Opcodes.ACC_FINAL; value=0; }
    clazz.fields.add(new FieldNode(access, marker, 'Z', null, value));
}

function bridgeCall(name) {
    // Generate a deferred call; Forge forbids Java.type of custom helpers.
    return ASMAPI.buildMethodCall(BOOTSTRAP, name, '()V', ASMAPI.MethodType.STATIC);
}

function staticBridge(owner, field, desc, callback, marker) {
    return function(clazz) {
        if (clazz.name !== owner) throw new Error('Unexpected GTOHJS owner: ' + clazz.name);
        requireMarkerAbsent(clazz, marker);
        var method = findMethod(clazz, '<clinit>');
        var nodes = method.instructions.toArray();
        var anchor = null;
        var anchors = 0;
        var returns = 0;
        for (var i = 0; i < nodes.length; i++) {
            var node = nodes[i];
            if (node.getOpcode() === Opcodes.RETURN) returns++;
            if (node.getOpcode() === Opcodes.PUTSTATIC && node.owner === owner &&
                    node.name === field && node.desc === desc) { anchor = node; anchors++; }
        }
        requireOne(anchors, owner + '.' + field + ':' + desc);
        requireOne(returns, owner + ' static initializer return');
        var terminal = anchor.getNext();
        while (terminal !== null && terminal.getOpcode() < 0) terminal = terminal.getNext();
        if (terminal === null || terminal.getOpcode() !== Opcodes.RETURN) {
            throw new Error('GTOHJS terminal registration anchor changed: ' + owner);
        }
        // All checks precede mutation. No new branch, stack slot or local variable.
        method.instructions.insertBefore(terminal, bridgeCall(callback));
        recordMarker(clazz, marker);
        ASMAPI.log('ERROR', '[GTOHJS/PROOF] transformed ' + owner + ': 1/1');
        return clazz;
    };
}

function recipeBridge(clazz) {
    var owner = 'com/gtocore/data/Data';
    if (clazz.name !== owner) throw new Error('Unexpected GTOHJS recipe owner');
    var marker = 'gtohjs$dev9$recipe';
    requireMarkerAbsent(clazz, marker);
    var method = findMethod(clazz, 'commonInit');
    var nodes = method.instructions.toArray();
    var initializeIndex = -1, filterIndex = -1, finishIndex = -1;
    var initializes = 0, filters = 0, finishes = 0;
    for (var i = 0; i < nodes.length; i++) {
        var node = nodes[i];
        if (node.getOpcode() !== Opcodes.INVOKESTATIC || node.desc !== '()V') continue;
        if (node.owner === 'com/gtolib/api/recipe/RecipeBuilder' && node.name === 'initialization') {
            initializeIndex = i; initializes++;
        } else if (node.owner === 'com/gtocore/data/recipe/RecipeFilter' && node.name === 'init') {
            filterIndex = i; filters++;
        } else if (node.owner === 'com/gtolib/api/recipe/RecipeBuilder' && node.name === 'finish') {
            finishIndex = i; finishes++;
        }
    }
    requireOne(initializes, 'RecipeBuilder.initialization()V');
    requireOne(filters, 'RecipeFilter.init()V');
    requireOne(finishes, 'RecipeBuilder.finish()V');
    if (!(initializeIndex < filterIndex && filterIndex < finishIndex)) {
        throw new Error('GTOHJS recipe lifecycle order changed');
    }
    var recipeIndex = method.maxLocals;
    method.maxLocals += 1;
    method.instructions.insertBefore(nodes[finishIndex], buildCustomRecipes(recipeIndex));
    var completed = new InsnList();
    completed.add(ASMAPI.buildMethodCall('com/gtohjs/methods/Fix2RegistrationMethods','afterRecipeFinish','()V',ASMAPI.MethodType.STATIC));
    completed.add(bridgeCall('recipeBridge'));
    method.instructions.insert(nodes[finishIndex], completed);
    method.maxStack = Math.max(method.maxStack, 6);
    recordMarker(clazz, marker);
    ASMAPI.log('ERROR', '[GTOHJS/PROOF] transformed ' + owner + ': 1/1');
    return clazz;
}

function uniqueMethod(clazz, name, desc) {
    var count=0, found=null;
    for(var i=0;i<clazz.methods.size();i++) { var method=clazz.methods.get(i);
        if(method.name===name && method.desc===desc) { found=method;count++; }
    }
    requireOne(count,clazz.name+'.'+name+desc); return found;
}
function methodGroup(definitions, marker) {
    return function(clazz) {
        requireMarkerAbsent(clazz,marker);
        for(var i=0;i<definitions.length;i++) { var d=definitions[i];
            var method=uniqueMethod(clazz,d.target.methodName,d.target.methodDesc);
            d.transformer(method);
        }
        recordMarker(clazz,marker); return clazz;
    };
}
function nextOpcode(node) { var n=node.getNext(); while(n!==null && n.getOpcode()<0)n=n.getNext(); return n; }
function generatorBridge(clazz) {
    var marker='gtohjs$fix2$generator'; requireMarkerAbsent(clazz,marker);
    var constructor=uniqueMethod(clazz,'<init>','(Lcom/gregtechceu/gtceu/api/blockentity/MetaMachineBlockEntity;)V');
    var recipe=uniqueMethod(clazz,'getRealRecipe','(Lcom/gregtechceu/gtceu/api/recipe/handler/RecipeHandlerUnit;Lcom/gregtechceu/gtceu/api/recipe/GTRecipe;)Lcom/gregtechceu/gtceu/api/recipe/GTRecipe;');
    var tick=uniqueMethod(clazz,'handleTickRecipe','(Lcom/gregtechceu/gtceu/api/recipe/GTRecipe;)Z');
    var rules='com/gtocore/config/GTORules', helper='com/gtohjs/methods/AdvancedGeneratorArraySupport';
    var limit=null,multiplier=null,portKind=null,deposits=[];
    var nodes=constructor.instructions.toArray();
    for(var i=0;i<nodes.length;i++) if(nodes[i].getOpcode()===Opcodes.GETSTATIC) {
        if(nodes[i].owner===rules && nodes[i].name==='GENERATOR_ARRAY_LIMIT') {
            if(limit!==null)throw new Error('Duplicate generator limit'); limit=nextOpcode(nodes[i]);
        } else if(nodes[i].owner==='com/gtocore/api/wireless/energy/PortKind' && nodes[i].name==='GENERATOR_ARRAY') {
            if(portKind!==null)throw new Error('Duplicate generator port kind'); portKind=nodes[i];
        }
    }
    nodes=recipe.instructions.toArray();
    for(var i=0;i<nodes.length;i++) if(nodes[i].getOpcode()===Opcodes.GETSTATIC && nodes[i].owner===rules && nodes[i].name==='GENERATOR_ARRAY_MULTIPLY') {
        if(multiplier!==null)throw new Error('Duplicate generator multiplier'); multiplier=nextOpcode(nodes[i]);
    }
    nodes=tick.instructions.toArray();
    for(var i=0;i<nodes.length;i++) if(nodes[i].getOpcode()===Opcodes.INVOKEVIRTUAL &&
        nodes[i].owner==='com/gtocore/api/wireless/energy/EnergyPort' && nodes[i].name==='depositAll' && nodes[i].desc==='(JI)Z')deposits.push(nodes[i]);
    if(limit===null || limit.owner!=='com/gtolib/api/rule/IntRule' || limit.name!=='get' || limit.desc!=='()I')throw new Error('Generator dev9 limit anchor changed');
    if(multiplier===null || multiplier.owner!=='com/gtolib/api/rule/DoubleRule' || multiplier.name!=='get' || multiplier.desc!=='()D')throw new Error('Generator dev9 multiplier anchor changed');
    if(portKind===null || portKind.desc!=='Lcom/gtocore/api/wireless/energy/PortKind;')throw new Error('Generator dev11 port kind anchor changed');
    if(deposits.length!==1)throw new Error('Generator dev11 deposit anchor changed: '+deposits.length);
    var insns=new InsnList();insns.add(new VarInsnNode(Opcodes.ALOAD,1));insns.add(new InsnNode(Opcodes.SWAP));
    insns.add(ASMAPI.buildMethodCall(helper,'resolveLimit','(Lcom/gregtechceu/gtceu/api/blockentity/MetaMachineBlockEntity;I)I',ASMAPI.MethodType.STATIC));
    constructor.instructions.insert(limit,insns);
    insns=new InsnList();insns.add(new VarInsnNode(Opcodes.ALOAD,0));
    insns.add(ASMAPI.buildMethodCall(helper,'resolveMultiplierValue','(DLcom/gtocore/common/machine/multiblock/generator/GeneratorArrayMachine;)D',ASMAPI.MethodType.STATIC));
    recipe.instructions.insert(multiplier,insns);
    insns=new InsnList();insns.add(new VarInsnNode(Opcodes.ALOAD,1));
    insns.add(ASMAPI.buildMethodCall(helper,'resolvePortKind','(Lcom/gtocore/api/wireless/energy/PortKind;Lcom/gregtechceu/gtceu/api/blockentity/MetaMachineBlockEntity;)Lcom/gtocore/api/wireless/energy/PortKind;',ASMAPI.MethodType.STATIC));
    constructor.instructions.insert(portKind,insns);
    constructor.maxStack=Math.max(constructor.maxStack,6);recipe.maxStack+=1;
    recordMarker(clazz,marker);return clazz;
}
function coverBridge(clazz) {
    var marker='gtohjs$fix3$cover';requireMarkerAbsent(clazz,marker);
    var method=uniqueMethod(clazz,'<clinit>','()V'),nodes=method.instructions.toArray(),returns=[];
    for(var i=0;i<nodes.length;i++)if(nodes[i].getOpcode()===Opcodes.RETURN)returns.push(nodes[i]);
    requireOne(returns.length,'GTOCovers initializer return');
    method.instructions.insertBefore(returns[0],ASMAPI.buildMethodCall('com/gtohjs/methods/VacuumCoverRegistration','register','()V',ASMAPI.MethodType.STATIC));
    recordMarker(clazz,marker);return clazz;
}
function proxyBridge(clazz) {
    var marker='gtohjs$fix3$proxy';requireMarkerAbsent(clazz,marker);
    var method=uniqueMethod(clazz,'getRecipeHandlers','()Ljava/util/List;'),nodes=method.instructions.toArray(),returns=[];
    for(var i=0;i<nodes.length;i++)if(nodes[i].getOpcode()===Opcodes.ARETURN)returns.push(nodes[i]);
    requireOne(returns.length,'proxy handler return');
    var tail=new InsnList();tail.add(new VarInsnNode(Opcodes.ALOAD,0));
    tail.add(ASMAPI.buildMethodCall('com/gtohjs/methods/PatternProxyMethods','append','(Ljava/util/List;Lcom/gtocore/common/machine/multiblock/part/ae/MEPatternBufferProxyPartMachine;)Ljava/util/List;',ASMAPI.MethodType.STATIC));
    method.instructions.insertBefore(returns[0],tail);method.maxStack+=1;
    recordMarker(clazz,marker);return clazz;
}
function patternLayoutBridge(clazz) {
    var marker='gtohjs$fix3$layout';requireMarkerAbsent(clazz,marker);
    var machine='Lcom/gtocore/common/machine/multiblock/part/ae/MEPatternPartMachine;';
    var count=uniqueMethod(clazz,'pageCount','('+machine+')I');
    var first=count.instructions.getFirst(),fallback=new LabelNode(),prefix=new InsnList();
    prefix.add(new VarInsnNode(Opcodes.ALOAD,0));
    prefix.add(ASMAPI.buildMethodCall('com/gtohjs/methods/PatternLayoutMethods','pageCount','('+machine+')I',ASMAPI.MethodType.STATIC));
    prefix.add(new InsnNode(Opcodes.DUP));prefix.add(new JumpInsnNode(Opcodes.IFLT,fallback));prefix.add(new InsnNode(Opcodes.IRETURN));
    prefix.add(fallback);prefix.add(new InsnNode(Opcodes.POP));count.instructions.insertBefore(first,prefix);count.maxStack=Math.max(count.maxStack,2);
    var descriptor='('+machine+'ILjava/util/function/Consumer;ILjava/util/function/Supplier;)Lcom/gregtechceu/gtceu/uipro/elements/PageView;';
    var pages=uniqueMethod(clazz,'patternPages',descriptor),nodes=pages.instructions.toArray(),continueOld=new LabelNode(),header=new InsnList();
    header.add(new VarInsnNode(Opcodes.ALOAD,0));header.add(new VarInsnNode(Opcodes.ILOAD,1));header.add(new VarInsnNode(Opcodes.ALOAD,2));header.add(new VarInsnNode(Opcodes.ILOAD,3));header.add(new VarInsnNode(Opcodes.ALOAD,4));
    header.add(ASMAPI.buildMethodCall('com/gtohjs/methods/PatternLayoutMethods','pages',descriptor,ASMAPI.MethodType.STATIC));
    header.add(new InsnNode(Opcodes.DUP));header.add(new JumpInsnNode(Opcodes.IFNULL,continueOld));header.add(new InsnNode(Opcodes.ARETURN));header.add(continueOld);header.add(new InsnNode(Opcodes.POP));
    pages.instructions.insertBefore(nodes[0],header);pages.maxStack=Math.max(pages.maxStack,5);
    recordMarker(clazz,marker);return clazz;
}
function vacuumBridge(clazz) {
    var marker='gtohjs$fix3$vacuum';requireMarkerAbsent(clazz,marker);
    var desc='(Lcom/gregtechceu/gtceu/api/recipe/handler/IRecipeHandlerHolder;Lcom/gregtechceu/gtceu/api/recipe/handler/RecipeHandlerUnit;Lcom/gregtechceu/gtceu/api/recipe/GTRecipeDefinition;)Z';
    var method=uniqueMethod(clazz,'testCondition',desc),fields=0;
    for(var i=0;i<clazz.fields.size();i++){var f=clazz.fields.get(i);if(f.name==='tier'&&f.desc==='I')fields++;}
    requireOne(fields,'VacuumCondition tier field');
    var label=new LabelNode(),prefix=new InsnList();prefix.add(new VarInsnNode(Opcodes.ALOAD,0));
    prefix.add(new FieldInsnNode(Opcodes.GETFIELD,clazz.name,'tier','I'));prefix.add(new VarInsnNode(Opcodes.ALOAD,1));
    prefix.add(ASMAPI.buildMethodCall('com/gtohjs/methods/VacuumCoverSupport','satisfies','(ILcom/gregtechceu/gtceu/api/recipe/handler/IRecipeHandlerHolder;)Z',ASMAPI.MethodType.STATIC));
    prefix.add(new JumpInsnNode(Opcodes.IFEQ,label));prefix.add(new InsnNode(Opcodes.ICONST_1));prefix.add(new InsnNode(Opcodes.IRETURN));prefix.add(label);
    method.instructions.insertBefore(method.instructions.getFirst(),prefix);method.maxStack=Math.max(method.maxStack,2);
    recordMarker(clazz,marker);return clazz;
}
function dev11WirelessUnitBridge(clazz) {
    var marker='gtohjs$dev11$wirelessUnit';requireMarkerAbsent(clazz,marker);
    var anchor=null,count=0;
    for(var m=0;m<clazz.methods.size();m++) {
        var nodes=clazz.methods.get(m).instructions.toArray();
        for(var i=0;i<nodes.length;i++) { var n=nodes[i];
            if(n.getOpcode()===Opcodes.INVOKESTATIC && n.owner==='com/gtocore/api/pattern/GTOPredicates' &&
                    n.name==='wirelessEnergyUnit' && n.desc==='()Lcom/gregtechceu/gtceu/api/pattern/TraceabilityPredicate;') {anchor=n;count++;}
        }
    }
    requireOne(count,'MultiBlockG wireless unit predicate');
    anchor.owner='com/gtohjs/methods/InfiniteWirelessEnergyPredicate';
    var towerMethod=null;
    for(var m2=0;m2<clazz.methods.size();m2++) {
        var current=clazz.methods.get(m2), currentNodes=current.instructions.toArray();
        for(var j2=0;j2<currentNodes.length;j2++) if(currentNodes[j2]===anchor) towerMethod=current;
    }
    if(towerMethod===null) throw new Error('MultiBlockG tower structure method missing');
    var towerNodes=towerMethod.instructions.toArray(), casingCall=null,casingCount=0;
    for(var t=0;t<towerNodes.length;t++) {
        var call=towerNodes[t];
        if(call.name!=='wherePart' || call.owner!=='com/gregtechceu/gtceu/api/machine/multiblockpro/Symbols') continue;
        for(var back=t-1;back>=Math.max(0,t-12);back--) {
            var prior=towerNodes[back];
            if(prior.getOpcode()===Opcodes.BIPUSH && prior.operand===65) {casingCall=call;casingCount++;break;}
        }
    }
    requireOne(casingCount,'wireless tower A casing predicate');
    towerMethod.instructions.insertBefore(casingCall,ASMAPI.buildMethodCall('com/gtohjs/adaptivenet/AdaptiveNetStructurePredicate',
        'towerCasing','(Lcom/gregtechceu/gtceu/api/pattern/TraceabilityPredicate;)Lcom/gregtechceu/gtceu/api/pattern/TraceabilityPredicate;',ASMAPI.MethodType.STATIC));
    recordMarker(clazz,marker);return clazz;
}
function adaptiveEnergyPortBridge(clazz) {
    var marker='gtohjs$adaptive$rate';requireMarkerAbsent(clazz,marker);
    var allowed=uniqueMethod(clazz,'allowed','(JI)J'),nodes=allowed.instructions.toArray(),bucket=null,count=0;
    for(var i=0;i<nodes.length;i++) if(nodes[i].getOpcode()===Opcodes.GETFIELD && nodes[i].name==='rateBucket') {bucket=nodes[i];count++;}
    requireOne(count,'EnergyPort.allowed bucket');
    var label=new LabelNode(),branch=new InsnList();
    branch.add(new VarInsnNode(Opcodes.ALOAD,0));
    branch.add(ASMAPI.buildMethodCall('com/gtohjs/adaptivenet/AdaptiveGridRatePolicy','bypass',
        '(Lcom/gtocore/api/wireless/energy/EnergyPort;)Z',ASMAPI.MethodType.STATIC));
    branch.add(new JumpInsnNode(Opcodes.IFEQ,label));
    branch.add(new VarInsnNode(Opcodes.LLOAD,1));branch.add(new InsnNode(Opcodes.LRETURN));branch.add(label);
    allowed.instructions.insertBefore(bucket.getPrevious(),branch);
    var names=[['shortfall','(JJ)J'],['serveTick','()V']];
    for(var n=0;n<names.length;n++) {
        var method=uniqueMethod(clazz,names[n][0],names[n][1]),ins=method.instructions.toArray(),rate=null,rateCount=0;
        for(var k=0;k<ins.length;k++) if(ins[k].getOpcode()===Opcodes.GETFIELD &&
                ins[k].owner==='com/gtocore/api/wireless/energy/EnergyAccount' && ins[k].name==='rate' && ins[k].desc==='J') {rate=ins[k];rateCount++;}
        requireOne(rateCount,'EnergyPort.'+names[n][0]+' rate');
        var suffix=new InsnList();suffix.add(new VarInsnNode(Opcodes.ALOAD,0));
        suffix.add(ASMAPI.buildMethodCall('com/gtohjs/adaptivenet/AdaptiveGridRatePolicy','effectiveRate',
            '(JLcom/gtocore/api/wireless/energy/EnergyPort;)J',ASMAPI.MethodType.STATIC));
        method.instructions.insert(rate,suffix);method.maxStack=Math.max(method.maxStack,4);
    }
    recordMarker(clazz,marker);return clazz;
}
function adaptiveNodeCardBridge(clazz) {
    var marker='gtohjs$adaptive$nodeCard';requireMarkerAbsent(clazz,marker);
    var method=uniqueMethod(clazz,'build','(Lcom/gregtechceu/gtceu/uipro/UIElement;Lcom/gtocore/common/wireless/energy/map/GridCardData;)V');
    var nodes=method.instructions.toArray(),tail=null,count=0;
    for(var i=0;i<nodes.length;i++) if(nodes[i].getOpcode()===Opcodes.RETURN) {tail=nodes[i];count++;}
    requireOne(count,'GridNodeCard.build return');
    var call=new InsnList();call.add(new VarInsnNode(Opcodes.ALOAD,0));call.add(new VarInsnNode(Opcodes.ALOAD,1));
    call.add(ASMAPI.buildMethodCall('com/gtohjs/adaptivenet/AdaptiveNetMapPanel','append',
        '(Lcom/gregtechceu/gtceu/uipro/UIElement;Ljava/lang/Object;)V',ASMAPI.MethodType.STATIC));
    method.instructions.insertBefore(tail,call);method.maxStack=Math.max(method.maxStack,2);
    recordMarker(clazz,marker);return clazz;
}
function dev11MonitorStorageBridge(clazz) {
    var marker='gtohjs$dev11$monitorStorage';requireMarkerAbsent(clazz,marker);
    var method=uniqueMethod(clazz,'getComponentArray','()[Lnet/minecraft/network/chat/Component;');
    var nodes=method.instructions.toArray(),anchor=null,count=0;
    for(var i=0;i<nodes.length;i++) { var n=nodes[i];
        if(n.getOpcode()===Opcodes.INVOKESTATIC && n.owner==='com/gtocore/common/wireless/energy/GridReadouts' &&
                n.name==='storage' && n.desc==='(Lcom/gtocore/api/wireless/energy/EnergyAccount;)Lnet/minecraft/network/chat/Component;') {anchor=n;count++;}
    }
    requireOne(count,'MonitorEU storage readout');
    anchor.owner='com/gtohjs/methods/InfiniteEnergyPresentation';anchor.name='monitorStorage';
    recordMarker(clazz,marker);return clazz;
}
function dev11SummaryStorageBridge(clazz) {
    var marker='gtohjs$dev11$summaryStorage';requireMarkerAbsent(clazz,marker);
    var method=uniqueMethod(clazz,'refresh','()V'),nodes=method.instructions.toArray();
    var stored=null,storedCount=0,early=null,earlyCount=0;
    for(var i=0;i<nodes.length;i++) { var n=nodes[i];
        if(n.getOpcode()===Opcodes.INVOKESTATIC && n.owner==='com/gtocore/common/wireless/energy/map/GridFormat' &&
                n.name==='storedDetail' && n.desc==='(DD)Lnet/minecraft/network/chat/Component;') {stored=n;storedCount++;}
        if(n.getOpcode()===Opcodes.IF_ICMPNE && nextOpcode(n).getOpcode()===Opcodes.RETURN) {early=n;earlyCount++;}
    }
    requireOne(storedCount,'GridSummaryPanel stored detail');
    requireOne(earlyCount,'GridSummaryPanel unchanged-summary return');
    var animate=new InsnList();animate.add(new VarInsnNode(Opcodes.ALOAD,1));
    animate.add(ASMAPI.buildMethodCall('com/gtohjs/methods/InfiniteEnergyPresentation','isInfiniteSummary',
        '(Lcom/gtocore/api/wireless/energy/GridView$Summary;)Z',ASMAPI.MethodType.STATIC));
    animate.add(new JumpInsnNode(Opcodes.IFNE,early.label));
    method.instructions.insertBefore(nextOpcode(early),animate);
    var format=new InsnList();format.add(new VarInsnNode(Opcodes.DLOAD,2));format.add(new VarInsnNode(Opcodes.DLOAD,4));
    format.add(ASMAPI.buildMethodCall('com/gtohjs/methods/InfiniteEnergyPresentation','summaryStorage',
        '(Lnet/minecraft/network/chat/Component;DD)Lnet/minecraft/network/chat/Component;',ASMAPI.MethodType.STATIC));
    method.instructions.insert(stored,format);method.maxStack+=4;
    recordMarker(clazz,marker);return clazz;
}
function dev11StationCapacityBridge(clazz) {
    var marker='gtohjs$dev11$stationCapacity';requireMarkerAbsent(clazz,marker);
    var method=uniqueMethod(clazz,'stationPanel','()Lcom/gregtechceu/gtceu/uipro/UIElement;');
    var nodes=method.instructions.toArray(),anchor=null,count=0;
    for(var i=0;i<nodes.length;i++) { var n=nodes[i];
        if(n.getOpcode()===Opcodes.INVOKESTATIC && n.owner==='com/gregtechceu/gtceu/uiwidgets/multiblock/MultiblockPage' &&
                n.name==='cachedRef' && n.desc==='(Ljava/util/function/Supplier;Ljava/util/function/Function;)Ljava/util/function/Supplier;') {anchor=n;count++;}
    }
    requireOne(count,'station capacity supplier');
    var register=uniqueMethod(clazz,'register','()V'),registerNodes=register.instructions.toArray();
    var tierCall=null,tierLoad=null,tierJump=null,tierCount=0;
    for(var j=0;j<registerNodes.length;j++) { var call=registerNodes[j];
        if(call.getOpcode()!==Opcodes.INVOKEVIRTUAL || call.owner!=='com/gtocore/common/block/WirelessEnergyUnitBlock' ||
                call.name!=='getTier' || call.desc!=='()I')continue;
        var load=nextOpcode(call),jump=load===null?null:nextOpcode(load);
        if(load!==null && load.getOpcode()===Opcodes.ILOAD && load.var===1 &&
                jump!==null && jump.getOpcode()===Opcodes.IF_ICMPGT) {tierCall=call;tierLoad=load;tierJump=jump;tierCount++;}
    }
    requireOne(tierCount,'station unit versus glass tier guard');
    var describe=uniqueMethod(clazz,'describeUnits','(I)V'),describeNodes=describe.instructions.toArray();
    var warningTier=null,warningJump=null,warningCount=0;
    for(var k=0;k<describeNodes.length;k++) { var first=describeNodes[k];
        if(first.getOpcode()!==Opcodes.ILOAD || first.var!==6)continue;
        var second=nextOpcode(first),branch=second===null?null:nextOpcode(second);
        if(second!==null && second.getOpcode()===Opcodes.ILOAD && second.var===1 &&
                branch!==null && branch.getOpcode()===Opcodes.IF_ICMPLE) {warningTier=first;warningJump=branch;warningCount++;}
    }
    requireOne(warningCount,'station over-tier warning guard');
    anchor.owner='com/gtohjs/methods/InfiniteEnergyPresentation';anchor.name='stationCapacitySupplier';
    register.instructions.insertBefore(tierCall,new VarInsnNode(Opcodes.ILOAD,1));
    tierCall.setOpcode(Opcodes.INVOKESTATIC);tierCall.owner='com/gtohjs/methods/InfiniteWirelessEnergyPredicate';
    tierCall.name='countsAtCasingTier';tierCall.desc='(Lcom/gtocore/common/block/WirelessEnergyUnitBlock;I)Z';
    register.instructions.remove(tierLoad);tierJump.setOpcode(Opcodes.IFEQ);
    var warningPrefix=new InsnList();warningPrefix.add(new VarInsnNode(Opcodes.ALOAD,0));
    warningPrefix.add(new InsnNode(Opcodes.DUP));
    warningPrefix.add(new FieldInsnNode(Opcodes.GETFIELD,clazz.name,'wirelessEnergyUnitPositions','Lcom/google/common/collect/Multimap;'));
    describe.instructions.insertBefore(warningTier,warningPrefix);
    var warningCall=ASMAPI.buildMethodCall('com/gtohjs/methods/InfiniteWirelessEnergyPredicate','allUnitsEligible',
        '(Lcom/gtocore/common/machine/multiblock/storage/WirelessEnergySubstationMachine;Lcom/google/common/collect/Multimap;II)Z',ASMAPI.MethodType.STATIC);
    describe.instructions.insertBefore(warningJump,warningCall);warningJump.setOpcode(Opcodes.IFNE);
    describe.maxStack+=3;
    recordMarker(clazz,marker);return clazz;
}
function dev11JadeBridge(clazz) {
    var marker='gtohjs$dev11$jade';requireMarkerAbsent(clazz,marker);
    var entries=[['register','(Lsnownee/jade/api/IWailaCommonRegistration;)V','registerCommon'],
        ['registerClient','(Lsnownee/jade/api/IWailaClientRegistration;)V','registerClientComponents']];
    for(var i=0;i<entries.length;i++) { var entry=entries[i],method=uniqueMethod(clazz,entry[0],entry[1]);
        var nodes=method.instructions.toArray(),tail=null,count=0;
        for(var j=0;j<nodes.length;j++)if(nodes[j].getOpcode()===Opcodes.RETURN){tail=nodes[j];count++;}
        requireOne(count,'GTOJadePlugin '+entry[0]+' return');
        var call=new InsnList();call.add(new VarInsnNode(Opcodes.ALOAD,1));
        call.add(ASMAPI.buildMethodCall('com/gtohjs/integration/jade/GTOHJSJadePlugin',entry[2],entry[1],ASMAPI.MethodType.STATIC));
        method.instructions.insertBefore(tail,call);method.maxStack=Math.max(method.maxStack,1);
    }
    recordMarker(clazz,marker);return clazz;
}
function initializeCoreMod() {
    ASMAPI.log('ERROR', '[GTOHJS/PROOF] CoreMod JS initialized');
    return {
        'gtohjs_adaptive_rate': {'target':{'type':'CLASS','name':'com.gtocore.api.wireless.energy.EnergyPort'},'transformer':adaptiveEnergyPortBridge},
        'gtohjs_adaptive_node_card': {'target':{'type':'CLASS','name':'com.gtocore.common.wireless.energy.map.GridNodeCard'},'transformer':adaptiveNodeCardBridge},
        'gtohjs_dev11_wireless_unit': {'target':{'type':'CLASS','name':'com.gtocore.common.data.machines.MultiBlockG'},'transformer':dev11WirelessUnitBridge},
        'gtohjs_dev11_monitor_storage': {'target':{'type':'CLASS','name':'com.gtocore.common.machine.monitor.MonitorEU'},'transformer':dev11MonitorStorageBridge},
        'gtohjs_dev11_summary_storage': {'target':{'type':'CLASS','name':'com.gtocore.common.wireless.energy.map.GridSummaryPanel'},'transformer':dev11SummaryStorageBridge},
        'gtohjs_dev11_station_capacity': {'target':{'type':'CLASS','name':'com.gtocore.common.machine.multiblock.storage.WirelessEnergySubstationMachine'},'transformer':dev11StationCapacityBridge},
        'gtohjs_dev11_jade': {'target':{'type':'CLASS','name':'com.gtocore.integration.jade.GTOJadePlugin'},'transformer':dev11JadeBridge},
        'gtohjs_fix3_cover': {'target':{'type':'CLASS','name':'com.gtocore.common.data.GTOCovers'},'transformer':coverBridge},
        'gtohjs_fix3_proxy': {'target':{'type':'CLASS','name':'com.gtocore.common.machine.multiblock.part.ae.MEPatternBufferProxyPartMachine'},'transformer':proxyBridge},
        'gtohjs_fix3_layout': {'target':{'type':'CLASS','name':'com.gtocore.common.machine.multiblock.part.ae.MEPatternPartUI'},'transformer':patternLayoutBridge},
        'gtohjs_fix3_vacuum': {'target':{'type':'CLASS','name':'com.gtocore.common.recipe.condition.VacuumCondition'},'transformer':vacuumBridge},
        'gtohjs_fix2_generator': {
            'target': {'type':'CLASS','name':'com.gtocore.common.machine.multiblock.generator.GeneratorArrayMachine'},
            'transformer': generatorBridge
        },
        'gtohjs_fix2_steam': {
            'target': {'type':'CLASS','name':'com.gtocore.common.machine.multiblock.steam.BaseSteamMultiblockMachine'},
            'transformer': methodGroup([FIX2_0,FIX2_1],'gtohjs$fix2$steam')
        },
        'gtohjs_fix2_steam_ui': {
            'target': {'type':'CLASS','name':'com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine'},
            'transformer': methodGroup([FIX2_2],'gtohjs$fix2$steamUi')
        },
        'gtohjs_fix2_material': {
            'target': {'type':'CLASS','name':'com.gtocore.data.recipe.generated.GTOMaterialRecipeHandler'},
            'transformer': methodGroup([FIX2_3],'gtohjs$fix2$material')
        },
        'gtohjs_dev9_machine_bridge': {
            'target': {'type': 'CLASS', 'name': 'com.gtocore.common.data.GTOMachines'},
            'transformer': staticBridge('com/gtocore/common/data/GTOMachines', 'MONITOR_MACHINE',
                'Lcom/gregtechceu/gtceu/api/machine/MachineDefinition;', 'machineBridge', 'gtohjs$dev9$machine')
        },
        'gtohjs_dev9_recipe_type_bridge': {
            'target': {'type': 'CLASS', 'name': 'com.gtocore.common.data.GTORecipeTypes'},
            'transformer': staticBridge('com/gtocore/common/data/GTORecipeTypes', 'F1A1B',
                'Lcom/gtolib/api/recipe/RecipeType;', 'recipeTypeBridge', 'gtohjs$dev9$recipeType')
        },
        'gtohjs_dev9_recipe_bridge': {
            'target': {'type': 'CLASS', 'name': 'com.gtocore.data.Data'},
            'transformer': recipeBridge
        },
        'gtohjs_dev9_ae_bridge': {
            'target': {'type': 'CLASS', 'name': 'com.gtocore.common.data.machines.GTAEMachines'},
            'transformer': staticBridge('com/gtocore/common/data/machines/GTAEMachines', 'ME_PATTERN_BUFFER_PROXY',
                'Lcom/gregtechceu/gtceu/api/machine/MachineDefinition;', 'aeBridge', 'gtohjs$dev9$ae')
        }
    };
}
