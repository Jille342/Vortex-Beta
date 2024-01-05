package client.utils;

import net.minecraft.entity.Entity;

@FunctionalInterface
public interface ICheck {
    boolean validate(Entity var1);
}
