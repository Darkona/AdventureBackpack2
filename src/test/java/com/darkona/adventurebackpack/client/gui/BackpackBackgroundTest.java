package com.darkona.adventurebackpack.client.gui;

import com.darkona.adventurebackpack.client.gui.widget.BackgroundComposer;
import com.darkona.adventurebackpack.client.gui.widget.classic.ClassicSkins;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.darkona.adventurebackpack.inventory.BackpackLayout;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/** The background drawn by pieces: the classic window is the old fixed texture, pixel for pixel. */
class BackpackBackgroundTest
{
    private static int[] compose(BackpackLayout l)
    {
        ClassicSkins.install();
        return BackgroundComposer.compose(l.widgets());
    }

    private static final File TEXTURE = new File("src/main/resources/assets/adventurebackpack/textures/gui/guiBackpackNew.png");

    private static BufferedImage draw(BackpackLayout layout, BufferedImage texture)
    {
        BufferedImage img = new BufferedImage(layout.width(), layout.height(), BufferedImage.TYPE_INT_ARGB);
        int[] b = compose(layout);
        for (int i = 0; i < b.length; i += BackgroundComposer.STRIDE)
        {
            for (int y = 0; y < b[i + 5]; y++)
            {
                for (int x = 0; x < b[i + 4]; x++)
                {
                    img.setRGB(b[i] + x, b[i + 1] + y, texture.getRGB(b[i + 2] + x, b[i + 3] + y));
                }
            }
        }
        return img;
    }

    private static int argb(int pixel)
    {
        // Fully transparent pixels are equal whatever their colour bits
        return (pixel >>> 24) == 0 ? 0 : pixel;
    }

    @Test
    void theClassicWindowIsTheOldTexturePixelForPixel() throws IOException
    {
        BufferedImage texture = ImageIO.read(TEXTURE);
        BackpackLayout classic = BackpackLayout.of(6, 8);
        BufferedImage img = draw(classic, texture);
        int wrong = 0;
        StringBuilder first = new StringBuilder();
        for (int y = 0; y < classic.height(); y++)
        {
            for (int x = 0; x < classic.width(); x++)
            {
                boolean ropeSlot = x >= classic.ropeX() - 1 && x < classic.ropeX() + 17 && y >= classic.ropeY() - 1 && y < classic.ropeY() + 17;
                if (!ropeSlot && !storageCell(classic, x, y) && argb(img.getRGB(x, y)) != argb(texture.getRGB(x, y)))
                {
                    if (wrong++ < 400) first.append(" (").append(x).append(',').append(y).append(')');
                }
            }
        }
        assertEquals(0, wrong, "pixels that differ from the old texture:" + first);
    }

    private static boolean storageCell(BackpackLayout l, int x, int y)
    {
        for (int k = 0; k < l.storage(); k++)
        {
            if (x >= l.storageX(k) - 1 && x < l.storageX(k) + 17 && y >= l.storageY(k) - 1 && y < l.storageY(k) + 17) return true;
        }
        return false;
    }

    @Test
    void everyCopyStaysInsideTheWindowAndTheTexture()
    {
        int[][] shapes = {{4, 8}, {6, 8}, {7, 9}, {10, 12}, {4, 12}};
        for (int[] s : shapes)
        {
            BackpackLayout l = BackpackLayout.of(s[0], s[1]);
            int[] b = compose(l);
            assertEquals(0, b.length % BackgroundComposer.STRIDE);
            for (int i = 0; i < b.length; i += BackgroundComposer.STRIDE)
            {
                String where = s[0] + "x" + s[1] + " copy " + i / BackgroundComposer.STRIDE;
                assertTrue(b[i] >= 0 && b[i + 1] >= 0 && b[i] + b[i + 4] <= l.width() && b[i + 1] + b[i + 5] <= l.height(), where);
                assertTrue(b[i + 2] >= 0 && b[i + 3] >= 0 && b[i + 2] + b[i + 4] <= 256 && b[i + 3] + b[i + 5] <= 256, where);
            }
        }
    }
}
