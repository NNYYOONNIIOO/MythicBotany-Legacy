package mythicbotany.item;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import baubles.api.BaublesApi;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import vazkii.botania.api.item.ISequentialBreaker;
import vazkii.botania.api.mana.IManaGivingItem;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaTooltipDisplay;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.item.ItemTemperanceStone;
import vazkii.botania.common.item.equipment.tool.ToolCommons;
import vazkii.botania.common.item.equipment.tool.elementium.ItemElementiumPick;
import vazkii.botania.common.item.relic.ItemThorRing;

/** Alfsteel's TerraPick-like shatterer with stored mana and a Loki-compatible mode. */
public class ItemAlfsteelPick extends ItemPickaxe implements IManaItem, IManaTooltipDisplay, ISequentialBreaker {
    private static final String TAG_MANA = "mana";
    private static final String TAG_ENABLED = "enabled";
    private static final String TAG_TIPPED = "tipped";
    private static final String LOKI_CURSOR_LIST = "cursorList";
    private static final String LOKI_CURSOR_COUNT = "cursorCount";
    private static final String LOKI_CURSOR_PREFIX = "cursor";
    private static final String LOKI_X_OFFSET = "xOffset";
    private static final String LOKI_Y_OFFSET = "yOffset";
    private static final String LOKI_Z_OFFSET = "zOffset";
    private static final int MAX_MANA = 1000000000;
    private static final int MANA_PER_BLOCK = 200;
    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("7f0e7e20-0f64-4d5b-9f3a-2cc37b01a101");
    private static final UUID ATTACK_SPEED_UUID = UUID.fromString("7f0e7e20-0f64-4d5b-9f3a-2cc37b01a102");
    private static final List<Material> MATERIALS = Arrays.asList(
            Material.ROCK, Material.IRON, Material.ICE, Material.GLASS,
            Material.PISTON, Material.ANVIL, Material.GRASS, Material.GROUND,
            Material.SAND, Material.SNOW, Material.CRAFTED_SNOW, Material.CLAY);
    public static final int[] LEVELS = {0, 10000, 1000000, 10000000, 100000000, 1000000000};

    public ItemAlfsteelPick(Item.ToolMaterial material) {
        super(material);
        setMaxDamage(4600);
        setMaxStackSize(1);
        addPropertyOverride(new ResourceLocation("mythicbotany", "enabled"),
                (stack, world, entity) -> isEnabled(stack) ? 1.0F : 0.0F);
        addPropertyOverride(new ResourceLocation("mythicbotany", "tipped"),
                (stack, world, entity) -> isTipped(stack) ? 1.0F : 0.0F);
    }

    /** The shatterer is a 5-damage, 1.2-speed main-hand weapon. */
    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        if (slot == EntityEquipmentSlot.MAINHAND) {
            ImmutableMultimap.Builder<String, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                    new AttributeModifier(ATTACK_DAMAGE_UUID, "Weapon modifier", 4.0D, 0));
            builder.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
                    new AttributeModifier(ATTACK_SPEED_UUID, "Weapon modifier", -2.8D, 0));
            return builder.build();
        }
        return super.getAttributeModifiers(slot, stack);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        return !isEnabled(stack) || getMana_(stack) >= MANA_PER_BLOCK
                ? super.getDestroySpeed(stack, state) : 0.0F;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state,
                                    BlockPos pos, EntityLivingBase entityLiving) {
        if (worldIn.isRemote || entityLiving instanceof EntityPlayer
                && ((EntityPlayer) entityLiving).capabilities.isCreativeMode) {
            return true;
        }
        consumeMiningCost(stack, entityLiving);
        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (getLevel(stack) > 0) {
            setEnabled(stack, !isEnabled(stack));
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
                                      EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!player.isSneaking()) return EnumActionResult.PASS;
        if (!world.isRemote) setEnabled(player.getHeldItem(hand), !isEnabled(player.getHeldItem(hand)));
        return EnumActionResult.SUCCESS;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
        RayTraceResult ray = ToolCommons.raytraceFromEntity(player.world, player, true, 10.0D);
        if (!player.world.isRemote && ray != null && ray.sideHit != null) {
            if (isEnabled(stack)) {
                breakOtherBlock(player, stack, pos, pos, ray.sideHit);
            } else {
                breakLokiCursors(player, stack, pos);
            }
        }
        return false;
    }

    @Override
    public void breakOtherBlock(EntityPlayer player, ItemStack stack, BlockPos pos,
                                 BlockPos originPos, EnumFacing side) {
        IBlockState originState = player.world.getBlockState(pos);
        int level = getLevel(stack);
        if (level <= 0) return;
        if (!isEnabled(stack) || player.world.isAirBlock(pos)
                || !MATERIALS.contains(originState.getMaterial())
                || originState.getPlayerRelativeBlockHardness(player, player.world, pos) <= 0.0F
                || !originState.getBlock().canHarvestBlock(player.world, pos, player)) return;

        boolean thor = !ItemThorRing.getThorRing(player).isEmpty();
        int miningLevel = level + (thor ? 1 : 0);
        int rangeDepth = miningLevel / 2;
        if (ItemTemperanceStone.hasTemperanceActive(player) && miningLevel > 2) {
            miningLevel = 2;
            rangeDepth = 0;
        }
        int range = miningLevel - 1;
        int rangeY = Math.max(1, range);
        boolean doX = thor || side.getXOffset() == 0;
        boolean doY = thor || side.getYOffset() == 0;
        boolean doZ = thor || side.getZOffset() == 0;
        BlockPos begin = pos.add(doX ? -range : 0, doY ? -1 : 0, doZ ? -range : 0);
        BlockPos end = pos.add(doX ? range : rangeDepth * -side.getXOffset(),
                doY ? rangeY * 2 - 1 : rangeDepth * -side.getYOffset(),
                doZ ? range : rangeDepth * -side.getZOffset());
        for (BlockPos target : BlockPos.getAllInBox(begin, end)) {
            if (target.equals(pos) || getMana_(stack) < MANA_PER_BLOCK) continue;
            removeExtraBlock(player, stack, target, null);
        }
    }

    private void breakLokiCursors(EntityPlayer player, ItemStack stack, BlockPos origin) {
        if (player.world.isRemote) return;
        int slot = BaublesApi.isBaubleEquipped(player, vazkii.botania.common.item.ModItems.lokiRing);
        if (slot < 0) return;
        ItemStack loki = BaublesApi.getBaublesHandler(player).getStackInSlot(slot);
        IBlockState originState = player.world.getBlockState(origin);
        NBTTagCompound list = ItemNBTHelper.getCompound(loki, LOKI_CURSOR_LIST, false);
        int count = list.getInteger(LOKI_CURSOR_COUNT);
        for (int i = 0; i < count; i++) {
            NBTTagCompound cursor = list.getCompoundTag(LOKI_CURSOR_PREFIX + i);
            BlockPos target = origin.add(cursor.getInteger(LOKI_X_OFFSET),
                    cursor.getInteger(LOKI_Y_OFFSET), cursor.getInteger(LOKI_Z_OFFSET));
            if (!target.equals(origin)) removeExtraBlock(player, stack, target, originState);
        }
    }

    private void removeExtraBlock(EntityPlayer player, ItemStack stack, BlockPos pos,
                                  IBlockState requiredState) {
        World world = player.world;
        if (!(player instanceof EntityPlayerMP) || !world.isBlockLoaded(pos)) return;
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (!MATERIALS.contains(state.getMaterial()) || block.isAir(state, world, pos)
                || requiredState != null && (state.getBlock() != requiredState.getBlock()
                || state.getMaterial() != requiredState.getMaterial())
                || state.getPlayerRelativeBlockHardness(player, world, pos) <= 0.0F
                || !block.canHarvestBlock(world, pos, player)) return;

        EntityPlayerMP serverPlayer = (EntityPlayerMP) player;
        int exp = ForgeHooks.onBlockBreakEvent(world, serverPlayer.interactionManager.getGameType(), serverPlayer, pos);
        if (exp == -1) return;
        if (player.capabilities.isCreativeMode) {
            world.setBlockToAir(pos);
            return;
        }

        TileEntity tile = world.getTileEntity(pos);
        if (block.removedByPlayer(state, world, pos, player, true)) {
            block.onPlayerDestroy(world, pos, state);
            if (!isTipped(stack) || !ItemElementiumPick.isDisposable(block)) {
                block.harvestBlock(world, player, pos, state, tile, stack);
                block.dropXpOnBlockBreak(world, pos, exp);
            }
            consumeMiningCost(stack, player);
            world.playEvent(2001, pos, Block.getStateId(state));
        }
    }

    private void consumeMiningCost(ItemStack stack, EntityLivingBase entity) {
        if (isEnabled(stack)) {
            if (getMana_(stack) >= MANA_PER_BLOCK) {
                addMana(stack, -MANA_PER_BLOCK);
            } else {
                stack.damageItem(1, entity);
            }
        } else if (entity instanceof EntityPlayer && !hasLokiRing((EntityPlayer) entity)) {
            ToolCommons.damageItem(stack, 1, entity, MANA_PER_BLOCK);
        } else {
            stack.damageItem(1, entity);
        }
    }

    private static boolean hasLokiRing(EntityPlayer player) {
        return BaublesApi.isBaubleEquipped(player, vazkii.botania.common.item.ModItems.lokiRing) >= 0;
    }

    public static int getMana_(ItemStack stack) {
        return ItemNBTHelper.getInt(stack, TAG_MANA, 0);
    }

    public static int getLevel(ItemStack stack) {
        int mana = getMana_(stack);
        for (int i = LEVELS.length - 1; i > 0; i--) if (mana >= LEVELS[i]) return i;
        return 0;
    }

    public static void setMana(ItemStack stack, int mana) {
        ItemNBTHelper.setInt(stack, TAG_MANA, Math.max(0, Math.min(MAX_MANA, mana)));
    }

    public static boolean isEnabled(ItemStack stack) { return ItemNBTHelper.getBoolean(stack, TAG_ENABLED, false); }
    public static void setEnabled(ItemStack stack, boolean enabled) {
        ItemNBTHelper.setBoolean(stack, TAG_ENABLED, enabled && getLevel(stack) > 0);
    }
    public static boolean isTipped(ItemStack stack) { return ItemNBTHelper.getBoolean(stack, TAG_TIPPED, false); }
    public static void setTipped(ItemStack stack, boolean tipped) { ItemNBTHelper.setBoolean(stack, TAG_TIPPED, tipped); }

    @Override public int getMana(ItemStack stack) { return getMana_(stack); }
    @Override public int getMaxMana(ItemStack stack) { return MAX_MANA; }
    @Override public void addMana(ItemStack stack, int mana) {
        long value = (long) getMana_(stack) + mana;
        setMana(stack, (int) Math.max(0L, Math.min((long) MAX_MANA, value)));
    }
    @Override public boolean canReceiveManaFromPool(ItemStack stack, TileEntity pool) { return true; }
    @Override public boolean canReceiveManaFromItem(ItemStack stack, ItemStack otherStack) {
        return !(otherStack.getItem() instanceof IManaGivingItem);
    }
    @Override public boolean canExportManaToPool(ItemStack stack, TileEntity pool) { return false; }
    @Override public boolean canExportManaToItem(ItemStack stack, ItemStack otherStack) { return false; }
    @Override public boolean isNoExport(ItemStack stack) { return true; }
    @Override public boolean disposeOfTrashBlocks(ItemStack stack) { return isTipped(stack); }
    @Override public int getEntityLifespan(ItemStack stack, World world) { return Integer.MAX_VALUE; }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {
        if (entityItem.isBurning()) entityItem.extinguish();
        return false;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack before, ItemStack after, boolean slotChanged) {
        return after.getItem() != this || isEnabled(before) != isEnabled(after) || isTipped(before) != isTipped(after);
    }

    /** Botania's tooltip handler uses this for the separate, colored mana bar. */
    @Override
    public float getManaFractionForDisplay(ItemStack stack) {
        return (float) getMana(stack) / (float) getMaxMana(stack);
    }

    /** The vanilla inventory bar is durability, not stored mana. */
    @Override public boolean showDurabilityBar(ItemStack stack) { return stack.isItemDamaged(); }
    @Override public double getDurabilityForDisplay(ItemStack stack) { return super.getDurabilityForDisplay(stack); }
    @Override public int getRGBDurabilityForDisplay(ItemStack stack) { return super.getRGBDurabilityForDisplay(stack); }
}
