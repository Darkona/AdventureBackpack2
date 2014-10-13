package entity.ai;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;

public class AvoidEntitySelector implements IEntitySelector {


    final EntityAIAvoidPlayerWithBackpack entityAvoiderAI;

    AvoidEntitySelector(EntityAIAvoidPlayerWithBackpack par1EntityAIAvoidEntity) {
        this.entityAvoiderAI = par1EntityAIAvoidEntity;
    }


    /**
     * Return whether the specified entity is applicable to this filter.
     */
    public boolean isEntityApplicable(Entity par1Entity) {
        return par1Entity.isEntityAlive() && EntityAIAvoidPlayerWithBackpack.func_98217_a(this.entityAvoiderAI).getEntitySenses().canSee(par1Entity);
    }

}
