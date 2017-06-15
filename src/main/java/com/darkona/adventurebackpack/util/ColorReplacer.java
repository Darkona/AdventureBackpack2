package com.darkona.adventurebackpack.util;

/**
 * Created on 14/10/2014
 *
 * @author Darkona
 */

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class ColorReplacer
{
    public static BufferedImage colorImage(int colour, BufferedImage image)
    {
        int width = image.getWidth();
        int height = image.getHeight();
        WritableRaster raster = image.getRaster();

        for (int xx = 0; xx < width; xx++)
        {
            for (int yy = 0; yy < height; yy++)
            {
                int[] pixels = raster.getPixel(xx, yy, (int[]) null);
                pixels[0] = colour;
                pixels[1] = colour;
                pixels[2] = colour;
                raster.setPixel(xx, yy, pixels);
            }
        }
        return image;
    }
}