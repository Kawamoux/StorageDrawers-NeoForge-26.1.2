package com.jaquadro.minecraft.storagedrawers.util;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import net.minecraft.client.gui.Font;

public class CountFormatter
{
    public static String format (Font font, IDrawer drawer) {
        return formatApprox(font, drawer);
    }

    public static String format (Font font, int itemCount) {
        return formatApprox(font, itemCount);
    }

    public static String formatStackNotation (IDrawer drawer) {
        if (drawer == null || drawer.isEmpty())
            return "";

        return fromStackNotation(drawer.getStoredItemCount(), drawer.getStoredItemStackSize());
    }

    public static String fromStackNotation (int itemCount, int stackSize) {
        if (itemCount == 0 || stackSize == 0)
            return "";

        String text;
        int stacks = itemCount / stackSize;
        int remainder = itemCount - (stacks * stackSize);

        if (stacks > 0 && remainder > 0)
            text = stacks + "x" + stackSize + "+" + remainder;
        else if (stacks > 0)
            text = stacks + "x" + stackSize;
        else
            text = String.valueOf(remainder);

        return text;
    }

    public static String formatExact (IDrawer drawer) {
        if (drawer == null || drawer.isEmpty())
            return "";

        return formatExact(drawer.getStoredItemCount());
    }

    public static String formatExact (int itemCount) {
        if (itemCount == 0)
            return "";

        return String.valueOf(itemCount);
    }

    public static String formatApprox (Font font, IDrawer drawer) {
        if (drawer == null || drawer.isEmpty())
            return "";

        return formatApprox(font, drawer.getStoredItemCount());
    }

    public static String formatApprox (Font font, int count) {
        String text;

        if (count >= 1000000000)
            text = String.format("%.1fG", count / 1000000000f);
        else if (count >= 100000000)
            text = String.format("%.0fM", count / 1000000f);
        else if (count >= 1000000)
            text = String.format("%.1fM", count / 1000000f);
        else if (count >= 100000)
            text = String.format("%.0fK", count / 1000f);
        else if (count >= 10000)
            text = String.format("%.1fK", count / 1000f);
        else
            text = String.valueOf(count);

        return text;
    }
}
