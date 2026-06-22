package com.jaquadro.minecraft.storagedrawers.config;

import com.jaquadro.minecraft.storagedrawers.ModServices;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import com.texelsaurus.minecraft.chameleon.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ConversionRegistry
{
    public static final ConversionRegistry INSTANCE = new ConversionRegistry();

    private Set<TagKey<Item>> tagWhitelist = new HashSet<>();
    private Set<TagKey<Item>> tagBlacklist = new HashSet<>();
    private List<Set<ResourceLocation>> equivGroups = new ArrayList<>();
    private boolean initialized;

    public ConversionRegistry () { }

    public void initialize () {
        if (initialized)
            return;

        initialized = true;

        ModCommonConfig.INSTANCE.onLoad(() -> {
            ModCommonConfig.INSTANCE.UPGRADES.conversionUpgrade.tagWhitelist.get().forEach(this::addWhitelist);
            ModCommonConfig.INSTANCE.UPGRADES.conversionUpgrade.tagBlacklist.get().forEach(this::addBlacklist);

            List<? extends String> oreTypes = ModCommonConfig.INSTANCE.UPGRADES.conversionUpgrade.oreTypes.get();
            List<? extends String> oreMats = ModCommonConfig.INSTANCE.UPGRADES.conversionUpgrade.oreMaterials.get();

            if (!oreTypes.isEmpty() && !oreMats.isEmpty() && ModCommonConfig.INSTANCE.GENERAL.logStartupActivity.get()) {
                ModServices.log.info("Adding ore conversions for types: " + oreTypes.stream().collect(Collectors.joining(", ")));
                ModServices.log.info("Adding ore conversions for materials: " + oreMats.stream().collect(Collectors.joining(", ")));
            }

            for (String oreType : oreTypes) {
                for (String oreMat : oreMats)
                    addWhitelist(oreType + "/" + oreMat, false);
            }

            for (String entry : ModCommonConfig.INSTANCE.UPGRADES.conversionUpgrade.itemEquivGroups.get())
                addEquivGroup(entry);
        });
    }

    private void ensureInitialized () {
        if (!initialized)
            initialize();
    }

    public boolean addBlacklist (String entry) {
        ensureInitialized();

        String[] parts = entry.split(":");
        if (parts.length != 2)
            return false;

        return addBlacklist(parts[0], parts[1]);
    }

    public boolean addBlacklist (String namespace, String path) {
        ensureInitialized();

        return addBlacklist(ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

    public boolean addBlacklist (ResourceLocation entry) {
        ensureInitialized();

        if (entry == null)
            return false;

        tagBlacklist.add(TagKey.create(Registries.ITEM, entry.asIdentifier()));

        if (ModCommonConfig.INSTANCE.GENERAL.logStartupActivity.get())
            ModServices.log.info("New conversion denied tag " + entry);

        return true;
    }

    public boolean addWhitelist (String entry) {
        ensureInitialized();

        return addWhitelist(entry, true);
    }

    public boolean addWhitelist (String entry, boolean log) {
        ensureInitialized();

        String[] parts = entry.split(":");
        if (parts.length != 2)
            return false;

        return addWhitelist(parts[0], parts[1], log);
    }

    public boolean addWhitelist (String namespace, String path, boolean log) {
        ensureInitialized();

        return addWhitelist(ResourceLocation.fromNamespaceAndPath(namespace, path), log);
    }

    public boolean addWhitelist (ResourceLocation entry, boolean log) {
        ensureInitialized();

        if (entry == null)
            return false;

        tagWhitelist.add(TagKey.create(Registries.ITEM, entry.asIdentifier()));

        if (log && ModCommonConfig.INSTANCE.GENERAL.logStartupActivity.get())
            ModServices.log.info("New conversion allowed tag " + entry);

        return true;
    }

    public void addEquivGroup (String entry) {
        ensureInitialized();

        String[] items = entry.split(";\\s*");
        Set<ResourceLocation> group = new HashSet<>();

        for (String item : items) {
            String[] parts = item.split(":");
            if (parts.length != 2)
                continue;

            ResourceLocation key = ResourceLocation.fromNamespaceAndPath(parts[0], parts[1]);
            group.add(key);
        }

        if (group.size() > 1) {
            equivGroups.add(group);

            if (ModCommonConfig.INSTANCE.GENERAL.logStartupActivity.get())
                ModServices.log.info("New conversion equivalence rule " + entry);
        }
    }

    public boolean itemsShareEquivGroup (Item item1, Item item2) {
        ensureInitialized();

        ResourceLocation key1 = ResourceLocation.fromIdentifier(BuiltInRegistries.ITEM.getKey(item1));
        ResourceLocation key2 = ResourceLocation.fromIdentifier(BuiltInRegistries.ITEM.getKey(item2));

        for (Set<ResourceLocation> group : equivGroups) {
            if (!group.contains(key1))
                continue;

            if (group.contains(key2))
                return true;
        }

        return false;
    }

    public List<ItemStack> getEquivItems (Item item) {
        ensureInitialized();

        ResourceLocation key = ResourceLocation.fromIdentifier(BuiltInRegistries.ITEM.getKey(item));
        List<ItemStack> items = new ArrayList<>();

        for (Set<ResourceLocation> group : equivGroups) {
            if (!group.contains(key))
                continue;

            for (ResourceLocation entry : group) {
                Item other = BuiltInRegistries.ITEM.getValue(entry.asIdentifier());
                items.add(new ItemStack(other));
            }
        }

        return items;
    }

    public boolean isEntryValid (TagKey<Item> entry) {
        ensureInitialized();

        // Formerly utilized "graylist", which contained items not on the whitelist
        // but also not on the blacklist and not failing the valid equiv check
        if (tagBlacklist.contains(entry))
            return false;

        return tagWhitelist.contains(entry);
    }
}
