package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * The crossbow icon in an inventory slot. Forge's inventory render helper draws the item at the slot centre, 10 pixels
 * per unit, seen from the classic block angle: on screen (x right, y up, z towards the viewer) that is Rx(30)·Ry(-45).
 * The renderer then applies {@link CrossbowAnimation#ICON_ROTATIONS}, the scale and the offset. In model space the
 * barrel points to -Z, the top of the crossbow to +Y and the gear side to +X.
 */
class CrossbowIconTest
{
    private static final double PX_PER_MODEL_UNIT = 10.0 * 0.0625;

    private static double[][] rot(double deg, double x, double y, double z)
    {
        double a = Math.toRadians(deg), c = Math.cos(a), s = Math.sin(a), t = 1 - c;
        return new double[][]{
                {t * x * x + c, t * x * y - s * z, t * x * z + s * y},
                {t * x * y + s * z, t * y * y + c, t * y * z - s * x},
                {t * x * z - s * y, t * y * z + s * x, t * z * z + c}};
    }

    private static double[][] mul(double[][] a, double[][] b)
    {
        double[][] m = new double[3][3];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                for (int k = 0; k < 3; k++) m[i][j] += a[i][k] * b[k][j];
        return m;
    }

    private static double[] apply(double[][] m, double x, double y, double z)
    {
        return new double[]{m[0][0] * x + m[0][1] * y + m[0][2] * z, m[1][0] * x + m[1][1] * y + m[1][2] * z, m[2][0] * x + m[2][1] * y + m[2][2] * z};
    }

    private static double[][] screen()
    {
        double[][] m = mul(rot(30, 1, 0, 0), rot(-45, 0, 1, 0));
        float[] r = CrossbowAnimation.ICON_ROTATIONS;
        for (int i = 0; i < r.length; i += 4) m = mul(m, rot(r[i], r[i + 1], r[i + 2], r[i + 3]));
        return m;
    }

    private static double[] pixel(double[][] m, double x, double y, double z)
    {
        double k = PX_PER_MODEL_UNIT * CrossbowAnimation.ICON_SCALE;
        return apply(m, (x + CrossbowAnimation.ICON_OFFSET_X) * k, (y + CrossbowAnimation.ICON_OFFSET_Y) * k, (z + CrossbowAnimation.ICON_OFFSET_Z) * k);
    }

    @Test
    void liesDiagonallyWithTheBarrelToTheTopRight()
    {
        double[][] m = screen();
        double[] front = apply(m, 0, 0, -1);
        assertTrue(front[0] > 0.65 && front[1] > 0.65, "barrel towards the top right corner");
        assertTrue(Math.abs(front[2]) < 0.1, "barrel flat on the screen, not pointing at the viewer");
        double[] top = apply(m, 0, 1, 0);
        assertTrue(top[2] > 0.85, "seen from above: the top of the crossbow faces the viewer");
        double[] side = apply(m, 1, 0, 0);
        assertTrue(side[2] > 0.2 && side[2] < 0.6, "a little of the gear side shows, for depth");
    }

    @Test
    void stockGoesToTheBottomLeft()
    {
        double[] butt = pixel(screen(), 0, 0, 20);
        assertTrue(butt[0] < -4 && butt[1] < -4, "stock end in the bottom left quarter");
    }

    @Test
    void wholeModelFitsTheSixteenPixelSlot()
    {
        double[][] m = screen();
        double[][] extremes = {
                {-2, 1.66, 20.28}, {2, -3.23, 19.24},
                {12.46, 0.5, -8.9}, {-12.46, 2.5, -8.9}, {14.6, 1.5, -5}, {-14.6, 1.5, -5},
                {3, 4, -15}, {-3, 0, -15}, {0, 13, -10}, {0, -7.1, 7}};
        for (double[] p : extremes)
        {
            double[] px = pixel(m, p[0], p[1], p[2]);
            assertTrue(Math.abs(px[0]) <= 8 && Math.abs(px[1]) <= 8, "inside the slot: " + p[0] + "," + p[1] + "," + p[2] + " -> " + px[0] + "," + px[1]);
        }
    }
}
