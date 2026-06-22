package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.ModConstants;
import com.jaquadro.minecraft.storagedrawers.core.recipe.*;
import com.texelsaurus.minecraft.chameleon.ChameleonServices;
import com.texelsaurus.minecraft.chameleon.api.ChameleonInit;
import com.texelsaurus.minecraft.chameleon.registry.ChameleonRegistry;
import com.texelsaurus.minecraft.chameleon.registry.RegistryEntry;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Supplier;

public class ModRecipes
{
    private static final ChameleonRegistry<RecipeSerializer<?>> RECIPES = ChameleonServices.REGISTRY.create(BuiltInRegistries.RECIPE_SERIALIZER, ModConstants.MOD_ID);

    public static final RegistryEntry<RecipeSerializer<AddUpgradeRecipe>> UPGRADE_RECIPE_SERIALIZER = RECIPES.register("add_upgrade", () -> simpleCustomRecipe(AddUpgradeRecipe::new));
    public static final RegistryEntry<RecipeSerializer<KeyringRecipe>> KEYRING_RECIPE_SERIALIZER = RECIPES.register("keyring", () -> simpleCustomRecipe(KeyringRecipe::new));
    public static final RegistryEntry<RecipeSerializer<RemoteGroupUpgradeRecipe>> REMOTE_GROUP_UPGRADE_SERIALIZER = RECIPES.register("remote_group_upgrade", () -> simpleCustomRecipe(RemoteGroupUpgradeRecipe::new));
    public static final RegistryEntry<RecipeSerializer<UpgradeDetachedDrawerRecipe>> DETACHED_UPGRADE_RECIPE_SERIALIZER = RECIPES.register("add_detached_upgrade", () -> simpleCustomRecipe(UpgradeDetachedDrawerRecipe::new));
    public static final RegistryEntry<RecipeSerializer<PersonalKeyRecipe>> PERSONAL_KEY_RECIPE_SERIALIZER = RECIPES.register("personal_key_cycle", () -> simpleCustomRecipe(PersonalKeyRecipe::new));

    private static <T extends CustomRecipe> RecipeSerializer<T> simpleCustomRecipe(Supplier<T> recipeFactory) {
        MapCodec<T> codec = MapCodec.unit(recipeFactory);
        StreamCodec<RegistryFriendlyByteBuf, T> streamCodec = StreamCodec.of(
            (buffer, recipe) -> { },
            buffer -> recipeFactory.get()
        );
        return new RecipeSerializer<>(codec, streamCodec);
    }

    public static void init (ChameleonInit.InitContext context) {
        RECIPES.init(context);
    }
}
