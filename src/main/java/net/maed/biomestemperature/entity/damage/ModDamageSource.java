package net.maed.biomestemperature.entity.damage;

import net.minecraft.entity.damage.DamageSource;

public class ModDamageSource extends DamageSource {

    public static final DamageSource HOT = new ModDamageSource("hot").setBypassesArmor();

    protected ModDamageSource(String name) {
        super(name);
    }
}
