package com.jaquadro.minecraft.storagedrawers.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import com.texelsaurus.minecraft.chameleon.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ConfigItemList
{
    private final List<String> listedNamespaces = new ArrayList<>();
    private final List<Item> listedItems = new ArrayList<>();
    private List<String> pendingRules = new ArrayList<>();
    private boolean initialized;

    public ConfigItemList () { }

    public void initialize () {
        if (initialized)
            return;

        initialized = true;

        innerInitialize();

        for (String rule : pendingRules) {
            register(rule);
        }

        pendingRules = null;
    }

    protected void ensureInitialized () {
        if (!initialized)
            initialize();
    }

    protected void innerInitialize () { }

    public boolean isListed (ItemStack stack) {
        ensureInitialized();

        Item item = stack.getItem();

        if (listedItems.contains(item))
            return true;

        if(!listedNamespaces.isEmpty()) {
            ResourceKey<Item> resourceKey = BuiltInRegistries.ITEM.getResourceKey(item).orElse(null);
            if (resourceKey != null) {
                String namespace = resourceKey.identifier().getNamespace();
                if (listedNamespaces.contains(namespace))
                    return true;
            }
        }

        return false;
    }

    public boolean registerNamespace (@NotNull String namespace) {
        ensureInitialized();

        if (namespace.isEmpty())
            return false;

        unregisterNamespace(namespace);
        listedNamespaces.add(namespace);

        logRegisterNamespace(namespace);

        return true;
    }

    protected void logRegisterNamespace (@NotNull String namespace) { }

    public boolean registerItem (@NotNull ItemStack item) {
        ensureInitialized();

        if (item.isEmpty())
            return false;

        unregisterItem(item);
        listedItems.add(item.getItem());

        logRegisterItem(item);

        return true;
    }

    protected void logRegisterItem (@NotNull ItemStack item) { }

    public void register (List<String> entries) {
        entries.forEach(this::register);
    }

    public boolean register (String entry) {
        if (!initialized) {
            pendingRules.add(entry);
            return true;
        }

        String[] parts = entry.split("\\s*:\\s*");
        if (parts.length == 1)
            return registerNamespace(parts[0]);

        ResourceLocation resource = ResourceLocation.parse(entry);
        Item item = BuiltInRegistries.ITEM.getValue(resource.asIdentifier());

        return registerItem(new ItemStack(item));
    }

    public boolean unregisterNamespace (@NotNull String namespace) {
        ensureInitialized();

        if (namespace.isEmpty())
            return false;

        return listedNamespaces.remove(namespace);
    }

    public boolean unregisterItem (@NotNull ItemStack stack) {
        ensureInitialized();

        if (stack.isEmpty())
            return false;

        return listedItems.remove(stack.getItem());
    }

}
