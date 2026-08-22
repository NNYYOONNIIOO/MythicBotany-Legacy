package mythicbotany.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;

/** Dragonstone ore drops Botania's metadata-9 mana resource like diamond ore. */
public class BlockDragonstoneOre extends Block {
    public BlockDragonstoneOre() {
        super(Material.ROCK);
        setHardness(5.0F);
        setResistance(8.0F);
        setSoundType(SoundType.STONE);
        setHarvestLevel("pickaxe", 2);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random random, int fortune) {
        return vazkii.botania.common.item.ModItems.manaResource;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return 9;
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random random) {
        return 1 + (fortune > 0 ? random.nextInt(fortune + 1) : 0);
    }
}
