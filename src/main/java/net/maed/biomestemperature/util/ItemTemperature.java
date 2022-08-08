package net.maed.biomestemperature.util;

import net.minecraft.item.*;

public class ItemTemperature {
    public static float getTemperature(ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (item instanceof ArmorItem armorItem) {
            ArmorMaterial material = armorItem.getMaterial();
            if (material == ArmorMaterials.LEATHER) {
                return 5;
            }
            if (material == ArmorMaterials.IRON || material == ArmorMaterials.DIAMOND || material == ArmorMaterials.GOLD || material == ArmorMaterials.NETHERITE) {
                return -5;
            }
        }
        if (Items.ICE.equals(item)) {
            return -5f;
        }
        return 0.0f;
    }
}
