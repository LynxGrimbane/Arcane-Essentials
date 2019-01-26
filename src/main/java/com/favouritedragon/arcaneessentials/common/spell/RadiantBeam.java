package com.favouritedragon.arcaneessentials.common.spell;

import com.favouritedragon.arcaneessentials.ArcaneEssentials;
import com.favouritedragon.arcaneessentials.common.util.ArcaneUtils;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.SpellType;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WizardryParticleType;
import electroblob.wizardry.util.WizardryUtilities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.List;

public class RadiantBeam extends Spell {

	public RadiantBeam() {
		super(Tier.ADVANCED, 40, Element.HEALING, "radiant_beam", SpellType.ATTACK, 80, EnumAction.BOW, false, ArcaneEssentials.MODID);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {

		Vec3d look = caster.getLookVec();
		float damage = 4.0f * modifiers.get(SpellModifiers.DAMAGE);
		float range = 60 + 2 * modifiers.get(WizardryItems.range_upgrade);

		if (!world.isRemote) {
			Vec3d dist = caster.getLookVec().scale(range);
			AxisAlignedBB hitBox = new AxisAlignedBB(caster.posX, WizardryUtilities.getPlayerEyesPos(caster) - 0.4F, caster.posZ, caster.posX + dist.x,
					WizardryUtilities.getPlayerEyesPos(caster) - 0.4 + dist.y, caster.posZ + dist.z);
			hitBox = hitBox.shrink(0.13);
			//TODO: Fix this ultra wack collision. Maybe raytrace chaining? I'll need a method for that though
			List<Entity> hit = world.getEntitiesWithinAABB(EntityLivingBase.class, hitBox);
			if (!hit.isEmpty()) {
				for (Entity e : hit) {
					if (e != caster) {
						if (!MagicDamage.isEntityImmune(MagicDamage.DamageType.RADIANT, e)) {
							e.setFire(10);
							e.motionX += look.x * 2 * modifiers.get(WizardryItems.blast_upgrade);
							e.motionY += look.y * 2 * modifiers.get(WizardryItems.blast_upgrade);
							e.motionZ += look.z * 2 * modifiers.get(WizardryItems.blast_upgrade);
							if (((EntityLivingBase) e).isEntityUndead()) {
								damage += 2;
							}
							e.attackEntityFrom(MagicDamage.causeDirectMagicDamage(caster, MagicDamage.DamageType.RADIANT), damage);
						} else {
							caster.sendMessage(new TextComponentTranslation("spell.resist",
									e.getName(), this.getNameForTranslationFormatted()));
						}
					}
				}
			}
		}
		RayTraceResult rayTrace = WizardryUtilities.standardEntityRayTrace(world, caster, range);

		if (rayTrace != null && rayTrace.typeOfHit == RayTraceResult.Type.ENTITY && rayTrace.entityHit instanceof EntityLivingBase) {

			EntityLivingBase target = (EntityLivingBase) rayTrace.entityHit;

			if (!MagicDamage.isEntityImmune(MagicDamage.DamageType.RADIANT, target)) {
				target.setFire(10);
				target.motionX += look.x * 2 * modifiers.get(WizardryItems.blast_upgrade);
				target.motionY += look.y * 2 * modifiers.get(WizardryItems.blast_upgrade);
				target.motionZ += look.z * 2 * modifiers.get(WizardryItems.blast_upgrade);
				if (target.isEntityUndead()) {
					damage += 2;
				}
				target.attackEntityFrom(MagicDamage.causeDirectMagicDamage(caster, MagicDamage.DamageType.RADIANT), damage);
			} else {
				if (!world.isRemote) caster.sendMessage(new TextComponentTranslation("spell.resist",
						target.getName(), this.getNameForTranslationFormatted()));
			}
		}
		if (world.isRemote) {
			for (int i = 0; i < 80; i++) {
				double x1 = caster.posX + look.x * i / 2 + world.rand.nextFloat() / 5 - 0.1f;
				double y1 = WizardryUtilities.getPlayerEyesPos(caster) - 0.4f + look.y * i / 2
						+ world.rand.nextFloat() / 5 - 0.1f;
				double z1 = caster.posZ + look.z * i / 2 + world.rand.nextFloat() / 5 - 0.1f;
				Wizardry.proxy.spawnParticle(WizardryParticleType.SPARKLE, world, x1, y1, z1,
						world.rand.nextDouble() / 60,
						world.rand.nextDouble() / 20,
						world.rand.nextDouble() / 60, 30, 1.0f, 1.0f, 0.3f);
				Wizardry.proxy.spawnParticle(WizardryParticleType.SPARKLE, world, x1, y1, z1,
						world.rand.nextDouble() / 60,
						world.rand.nextDouble() / 20,
						world.rand.nextDouble() / 60, 30, 1.0f, 1.0f, 0.3f);

			}
			ArcaneUtils.spawnDirectionHelix(world, caster, caster.getLookVec(), 180, range, 0.5, WizardryParticleType.SPARKLE,
					caster.posX, caster.posY + caster.getEyeHeight() - 0.4F, caster.posZ, world.rand.nextDouble() / 60, world.rand.nextDouble() / 20,
					world.rand.nextDouble() / 60, 30, 1.0F, 1.0F, 0.3F);

		}
		WizardryUtilities.playSoundAtPlayer(caster, WizardrySounds.SPELL_HEAL, 1.5F, 1);
		WizardryUtilities.playSoundAtPlayer(caster, WizardrySounds.SPELL_SHOCKWAVE, 0.5F, 1.0f);

		//TODO: Add shockwave on hit
		return true;
	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target,
						SpellModifiers modifiers) {

		float damage = 4.0f * modifiers.get(SpellModifiers.DAMAGE);
		if (target != null) {

			Vec3d vec = new Vec3d(target.posX - caster.posX, target.posY - caster.posY, target.posZ - caster.posZ).normalize();

			target.setFire(10);
			target.motionX += vec.x * 2 * modifiers.get(WizardryItems.blast_upgrade);
			target.motionY += vec.y * 2 * modifiers.get(WizardryItems.blast_upgrade);
			target.motionZ += vec.z * 2 * modifiers.get(WizardryItems.blast_upgrade);
			if (target.isEntityUndead()) {
				damage += 2;
			}
			target.attackEntityFrom(MagicDamage.causeDirectMagicDamage(caster, MagicDamage.DamageType.RADIANT), damage);

			if (world.isRemote) {
				for (int i = 0; i < 60; i++) {
					double x1 = caster.posX + vec.x * i / 2 + world.rand.nextFloat() / 5 - 0.1f;
					double y1 = caster.posY + caster.getEyeHeight() - 0.4f + vec.y * i / 2 + world.rand.nextFloat() / 5
							- 0.1f;
					double z1 = caster.posZ + vec.z * i / 2 + world.rand.nextFloat() / 5 - 0.1f;
					Wizardry.proxy.spawnParticle(WizardryParticleType.SPARKLE_ROTATING, world, x1, y1, z1,
							world.rand.nextDouble() / 40,
							world.rand.nextDouble() / 20,
							world.rand.nextDouble() / 40, 30, 1.0f, 1.0f, 0.3f);
					Wizardry.proxy.spawnParticle(WizardryParticleType.SPARKLE, world, x1, y1, z1,
							world.rand.nextDouble() / 40,
							world.rand.nextDouble() / 20,
							world.rand.nextDouble() / 40, 30, 1.0f, 1.0f, 0.3f);
				}
			}

			caster.playSound(WizardrySounds.SPELL_HEAL, 1.5F, 1);
			caster.playSound(WizardrySounds.SPELL_SHOCKWAVE, 0.5F, 1.0f);


			return true;
		}

		return false;
	}

	@Override
	public boolean canBeCastByNPCs() {
		return true;
	}
}
