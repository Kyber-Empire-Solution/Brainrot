package lotr.common.itemreg;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.lang.reflect.Field;

public class SkibidiItems {




    public static void registerItem(Item item) {
        String prefixUnlocal = "item.lotr:";
        String textureName = item.getUnlocalizedName().substring(prefixUnlocal.length());
        item.setTextureName("lotr:" + textureName);
        GameRegistry.registerItem(item, "item." + textureName);
    }


    public static void registerBlock(Block block) {
        String prefixUnlocal = "tile:lotr.";
        String textureName = block.getUnlocalizedName().substring(prefixUnlocal.length());
        block.setBlockTextureName("lotr:" + textureName);
        GameRegistry.registerBlock(block, "tile." + textureName);
    }

    public static void autoRegisterItems() {

        for (Field field : SkibidiItems.class.getDeclaredFields()) {
            try {
                Object fieldValue = field.get(SkibidiItems.class);


                if (fieldValue instanceof Item) {
                    registerItem((Item) fieldValue);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        for (Field field : SkibidiItems.class.getDeclaredFields()) {
            try {
                Object fieldValue = field.get(SkibidiItems.class);

                if (fieldValue instanceof Block) {
                    registerBlock((Block) fieldValue);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
