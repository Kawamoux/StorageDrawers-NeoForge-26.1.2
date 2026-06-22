package com.texelsaurus.minecraft.chameleon.resources;

import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public final class ResourceLocation implements Comparable<ResourceLocation> {
    public static final Codec<ResourceLocation> CODEC = Identifier.CODEC.xmap(ResourceLocation::new, ResourceLocation::asIdentifier);

    private final Identifier delegate;

    private ResourceLocation(Identifier delegate) {
        this.delegate = delegate;
    }

    public static ResourceLocation fromNamespaceAndPath(String namespace, String path) {
        return new ResourceLocation(Identifier.fromNamespaceAndPath(namespace, path));
    }

    public static ResourceLocation fromIdentifier(Identifier id) {
        return new ResourceLocation(id);
    }

    public static ResourceLocation parse(String id) {
        return new ResourceLocation(Identifier.parse(id));
    }

    public static ResourceLocation withDefaultNamespace(String path) {
        return new ResourceLocation(Identifier.withDefaultNamespace(path));
    }

    public static @Nullable ResourceLocation tryParse(String id) {
        Identifier parsed = Identifier.tryParse(id);
        return parsed == null ? null : new ResourceLocation(parsed);
    }

    public Identifier asIdentifier() {
        return delegate;
    }

    public String getNamespace() {
        return delegate.getNamespace();
    }

    public String getPath() {
        return delegate.getPath();
    }

    public ResourceLocation withPath(String path) {
        return new ResourceLocation(delegate.withPath(path));
    }

    public ResourceLocation withPrefix(String prefix) {
        return new ResourceLocation(delegate.withPrefix(prefix));
    }

    public ResourceLocation withSuffix(String suffix) {
        return new ResourceLocation(delegate.withSuffix(suffix));
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ResourceLocation other && delegate.equals(other.delegate);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public int compareTo(ResourceLocation other) {
        return delegate.compareTo(other.delegate);
    }
}
