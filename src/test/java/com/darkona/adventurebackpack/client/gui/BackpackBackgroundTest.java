package com.darkona.adventurebackpack.client.gui;

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
    private static final File TEXTURE = new File("src/main/resources/assets/adventurebackpack/textures/gui/guiBackpackNew.png");

    private static BufferedImage draw(BackpackLayout layout, BufferedImage texture)
    {
        BufferedImage img = new BufferedImage(layout.width(), layout.height(), BufferedImage.TYPE_INT_ARGB);
        int[] b = BackpackBackground.compose(layout);
        for (int i = 0; i < b.length; i += BackpackBackground.STRIDE)
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
                if (argb(img.getRGB(x, y)) != argb(texture.getRGB(x, y)))
                {
                    if (wrong++ < 400) first.append(" (").append(x).append(',').append(y).append(')');
                }
            }
        }
        assertEquals(0, wrong, "pixels that differ from the old texture:" + first);
    }

    @Test
    void everyCopyStaysInsideTheWindowAndTheTexture()
    {
        int[][] shapes = {{4, 8}, {6, 8}, {7, 9}, {10, 12}, {4, 12}};
        for (int[] s : shapes)
        {
            BackpackLayout l = BackpackLayout.of(s[0], s[1]);
            int[] b = BackpackBackground.compose(l);
            assertEquals(0, b.length % BackpackBackground.STRIDE);
            for (int i = 0; i < b.length; i += BackpackBackground.STRIDE)
            {
                String where = s[0] + "x" + s[1] + " copy " + i / BackpackBackground.STRIDE;
                assertTrue(b[i] >= 0 && b[i + 1] >= 0 && b[i] + b[i + 4] <= l.width() && b[i + 1] + b[i + 5] <= l.height(), where);
                assertTrue(b[i + 2] >= 0 && b[i + 3] >= 0 && b[i + 2] + b[i + 4] <= 256 && b[i + 3] + b[i + 5] <= 256, where);
            }
        }
    }
}
