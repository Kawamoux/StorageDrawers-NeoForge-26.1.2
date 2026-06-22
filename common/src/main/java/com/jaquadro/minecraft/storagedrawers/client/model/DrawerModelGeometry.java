package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.storagedrawers.ModConstants;
import com.jaquadro.minecraft.storagedrawers.block.BlockCompDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import com.texelsaurus.minecraft.chameleon.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.phys.AABB;
import org.apache.commons.io.IOUtils;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DrawerModelGeometry
{
    private static boolean geometryDataLoaded = false;

    public static void loadGeometryData () {
        if (geometryDataLoaded)
            return;

        geometryDataLoaded = true;

        populateGeometryData(ModConstants.loc("models/block/geometry/full_drawers_icon_area_1.json"),
            ModConstants.loc("models/block/geometry/full_drawers_count_area_1.json"),
            ModConstants.loc("models/block/geometry/full_drawers_ind_area_1.json"),
            ModConstants.loc("models/block/geometry/full_drawers_indbase_area_1.json"),
            ModBlocks.getDrawersOfTypeAndSizeAndDepth(BlockDrawers.class, 1, false).toArray(BlockDrawers[]::new));
        populateGeometryData(ModConstants.loc("models/block/geometry/full_drawers_icon_area_2.json"),
            ModConstants.loc("models/block/geometry/full_drawers_count_area_2.json"),
            ModConstants.loc("models/block/geometry/full_drawers_ind_area_2.json"),
            ModConstants.loc("models/block/geometry/full_drawers_indbase_area_2.json"),
            ModBlocks.getDrawersOfTypeAndSizeAndDepth(BlockDrawers.class, 2, false).toArray(BlockDrawers[]::new));
        populateGeometryData(ModConstants.loc("models/block/geometry/full_drawers_icon_area_4.json"),
            ModConstants.loc("models/block/geometry/full_drawers_count_area_4.json"),
            ModConstants.loc("models/block/geometry/full_drawers_ind_area_4.json"),
            ModConstants.loc("models/block/geometry/full_drawers_indbase_area_4.json"),
            ModBlocks.getDrawersOfTypeAndSizeAndDepth(BlockDrawers.class, 4, false).toArray(BlockDrawers[]::new));
        populateGeometryData(ModConstants.loc("models/block/geometry/half_drawers_icon_area_1.json"),
            ModConstants.loc("models/block/geometry/half_drawers_count_area_1.json"),
            ModConstants.loc("models/block/geometry/half_drawers_ind_area_1.json"),
            ModConstants.loc("models/block/geometry/half_drawers_indbase_area_1.json"),
            ModBlocks.getDrawersOfTypeAndSizeAndDepth(BlockDrawers.class, 1, true).toArray(BlockDrawers[]::new));
        populateGeometryData(ModConstants.loc("models/block/geometry/half_drawers_icon_area_2.json"),
            ModConstants.loc("models/block/geometry/half_drawers_count_area_2.json"),
            ModConstants.loc("models/block/geometry/half_drawers_ind_area_2.json"),
            ModConstants.loc("models/block/geometry/half_drawers_indbase_area_2.json"),
            ModBlocks.getDrawersOfTypeAndSizeAndDepth(BlockDrawers.class, 2, true).toArray(BlockDrawers[]::new));
        populateGeometryData(ModConstants.loc("models/block/geometry/half_drawers_icon_area_4.json"),
            ModConstants.loc("models/block/geometry/half_drawers_count_area_4.json"),
            ModConstants.loc("models/block/geometry/half_drawers_ind_area_4.json"),
            ModConstants.loc("models/block/geometry/half_drawers_indbase_area_4.json"),
            ModBlocks.getDrawersOfTypeAndSizeAndDepth(BlockDrawers.class, 4, true).toArray(BlockDrawers[]::new));

        populateGeometryData(ModConstants.loc("models/block/geometry/full_comp_drawers_icon_area_2.json"),
            ModConstants.loc("models/block/geometry/full_comp_drawers_count_area_2.json"),
            ModConstants.loc("models/block/geometry/full_comp_drawers_ind_area_2.json"),
            ModConstants.loc("models/block/geometry/full_comp_drawers_indbase_area_2.json"),
            ModBlocks.getDrawersOfTypeAndSizeAndDepth(BlockCompDrawers.class, 2, false).toArray(BlockDrawers[]::new));
        populateGeometryData(ModConstants.loc("models/block/geometry/full_comp_drawers_icon_area_3.json"),
            ModConstants.loc("models/block/geometry/full_comp_drawers_count_area_3.json"),
            ModConstants.loc("models/block/geometry/full_comp_drawers_ind_area_3.json"),
            ModConstants.loc("models/block/geometry/full_comp_drawers_indbase_area_3.json"),
            ModBlocks.getDrawersOfTypeAndSizeAndDepth(BlockCompDrawers.class, 3, false).toArray(BlockDrawers[]::new));
        populateGeometryData(ModConstants.loc("models/block/geometry/half_comp_drawers_icon_area_2.json"),
            ModConstants.loc("models/block/geometry/half_comp_drawers_count_area_2.json"),
            ModConstants.loc("models/block/geometry/half_comp_drawers_ind_area_2.json"),
            ModConstants.loc("models/block/geometry/half_comp_drawers_indbase_area_2.json"),
            ModBlocks.getDrawersOfTypeAndSizeAndDepth(BlockCompDrawers.class, 2, true).toArray(BlockDrawers[]::new));
        populateGeometryData(ModConstants.loc("models/block/geometry/half_comp_drawers_icon_area_3.json"),
            ModConstants.loc("models/block/geometry/half_comp_drawers_count_area_3.json"),
            ModConstants.loc("models/block/geometry/half_comp_drawers_ind_area_3.json"),
            ModConstants.loc("models/block/geometry/half_comp_drawers_indbase_area_3.json"),
            ModBlocks.getDrawersOfTypeAndSizeAndDepth(BlockCompDrawers.class, 3, true).toArray(BlockDrawers[]::new));
    }

    private static void populateGeometryData(ResourceLocation locationIcon,
                                             ResourceLocation locationCount,
                                             ResourceLocation locationInd,
                                             ResourceLocation locationIndBase,
                                             BlockDrawers... blocks) {
        List<GeometryElement> slotGeo = getElements(locationIcon);
        List<GeometryElement> countGeo = getElements(locationCount);
        List<GeometryElement> indicatorGeo = getElements(locationInd);
        List<GeometryElement> indicatorBaseGeo = getElements(locationIndBase);

        for (BlockDrawers block : blocks) {
            if (block == null)
                continue;

            populateGeometryData(block, slotGeo, BlockDrawers.GeometryType.Label);
            populateGeometryData(block, countGeo, BlockDrawers.GeometryType.Count);
            populateGeometryData(block, indicatorGeo, BlockDrawers.GeometryType.Indicator);
            populateGeometryData(block, indicatorBaseGeo, BlockDrawers.GeometryType.IndicatorBase);
        }
    }

    private static void populateGeometryData (BlockDrawers block, List<GeometryElement> info, BlockDrawers.GeometryType type) {
        if (block == null || info == null)
            return;

        int drawerCount = block.getDrawerCount();
        if (drawerCount > info.size())
            return;

        for (int i = 0; i < drawerCount; i++) {
            Vector3fc from = info.get(i).from();
            Vector3fc to = info.get(i).to();
            AABB bound = new AABB(from.x(), from.y(), from.z(), to.x(), to.y(), to.z());

            switch (type) {
                case Slot: block.slotGeometry[i] = bound; break;
                case Count: block.countGeometry[i] = bound; break;
                case Label: block.labelGeometry[i] = bound; break;
                case Indicator: block.indGeometry[i] = bound; break;
                case IndicatorBase: block.indBaseGeometry[i] = bound; break;
            }
        }
    }

    private static List<GeometryElement> getElements (ResourceLocation location) {
        Reader reader = null;
        try {
            Resource iresource = Minecraft.getInstance().getResourceManager().getResourceOrThrow(location.asIdentifier());
            reader = new InputStreamReader(iresource.open(), StandardCharsets.UTF_8);
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray elements = root.getAsJsonArray("elements");
            List<GeometryElement> result = new ArrayList<>();
            if (elements == null)
                return result;

            for (JsonElement element : elements) {
                JsonObject object = element.getAsJsonObject();
                result.add(new GeometryElement(readVector(object.getAsJsonArray("from")), readVector(object.getAsJsonArray("to"))));
            }

            return result;
        } catch (IOException e) {
            return new ArrayList<>();
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    private static Vector3f readVector (JsonArray array) {
        return new Vector3f(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat());
    }

    private record GeometryElement(Vector3fc from, Vector3fc to) { }
}
