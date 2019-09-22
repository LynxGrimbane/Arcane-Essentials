package com.favouritedragon.arcaneessentials.common.spell.earth;

import com.favouritedragon.arcaneessentials.ArcaneEssentials;
import com.favouritedragon.arcaneessentials.common.entity.EntityFallingBlockSpawner;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Quake extends Spell {

	public Quake() {
		super(ArcaneEssentials.MODID, "quake", EnumAction.BOW, false);
		addProperties(DAMAGE, DURATION, RANGE, BLAST_RADIUS);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		if (world.getBlockState(caster.getPosition().offset(EnumFacing.DOWN)).getBlock() != Blocks.AIR) {
			Vec3d look = caster.getLookVec();
			float damage = getProperty(DAMAGE).floatValue() * modifiers.get(SpellModifiers.POTENCY);
			int lifetime = getProperty(DURATION).intValue() * 20 * (int) modifiers.get(WizardryItems.duration_upgrade);
			EntityFallingBlockSpawner spawner = new EntityFallingBlockSpawner(world, caster.posX + look.x * 0.25, caster.getEntityBoundingBox().minY, caster.posZ + look.z * 0.25, caster,
					lifetime, damage);
			look.scale(getProperty(RANGE).doubleValue());
			spawner.setOwner(caster);
			spawner.motionX = look.x;
			spawner.motionY = 0;
			spawner.motionZ = look.z;
			caster.swingArm(hand);
			if (!world.isRemote)
				return world.spawnEntity(spawner);
		}
		return false;
	}


	@Override
	public boolean canBeCastByNPCs() {
		return true;
	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target, SpellModifiers modifiers) {
		if (world.getBlockState(caster.getPosition().offset(EnumFacing.DOWN)).getBlock() != Blocks.AIR) {
			Vec3d look = caster.getLookVec();
			float damage = getProperty(DAMAGE).floatValue() * modifiers.get(SpellModifiers.POTENCY);
			int lifetime = getProperty(DURATION).intValue() * 20 * (int) modifiers.get(WizardryItems.duration_upgrade);
			EntityFallingBlockSpawner spawner = new EntityFallingBlockSpawner(world, caster.posX + look.x * 0.25, caster.getEntityBoundingBox().minY, caster.posZ + look.z * 0.25, caster,
					lifetime, damage);
			look.scale(getProperty(RANGE).doubleValue());
			spawner.setOwner(caster);
			spawner.motionX = look.x;
			spawner.motionY = 0;
			spawner.motionZ = look.z;
			caster.swingArm(hand);
			if (!world.isRemote)
				return world.spawnEntity(spawner);
		}
		return false;
	}

}
