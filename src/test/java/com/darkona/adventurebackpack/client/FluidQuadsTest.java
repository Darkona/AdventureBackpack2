package com.darkona.adventurebackpack.client;

import codechicken.lib.render.FluidQuads;
import codechicken.lib.vec.Vector3;
import org.junit.jupiter.api.Test;

import java.lang.management.ManagementFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Performance contract, Tier 4: the fluid of a tank is drawn on every frame, so its geometry must not allocate.
 * {@link FluidQuads} must emit exactly the vertices of the old {@code RenderUtils.renderFluidCuboid} (kept here as
 * {@link Legacy}, 24 {@code Vector3} per tank and frame) and must create nothing per call.
 */
class FluidQuadsTest
{
    /** Records the vertices in a preallocated array: no allocation while it runs. */
    private static final class Recorder implements FluidQuads.Sink
    {
        final double[] data = new double[4096 * 5];
        int n;

        @Override
        public void vertex(double x, double y, double z, double u, double v)
        {
            if (n + 5 <= data.length)
            {
                data[n] = x;
                data[n + 1] = y;
                data[n + 2] = z;
                data[n + 3] = u;
                data[n + 4] = v;
            }
            n += 5;
        }
    }

    /** The old code, verbatim in what it computes and allocates (from codechicken.lib.render.RenderUtils). */
    private static final class Legacy
    {
        static final Vector3[] vectors = new Vector3[8];

        static
        {
            for (int i = 0; i < vectors.length; i++)
            {
                vectors[i] = new Vector3();
            }
        }

        static void cuboid(FluidQuads.Sink t, double minX, double minY, double minZ, double maxX, double maxY, double maxZ,
                           float minU, float maxU, float minV, float maxV, double res)
        {
            quad(t, new Vector3(minX, minY, minZ), new Vector3(maxX, minY, minZ), new Vector3(maxX, minY, maxZ), new Vector3(minX, minY, maxZ), minU, maxU, minV, maxV, res);
            quad(t, new Vector3(minX, maxY, minZ), new Vector3(minX, maxY, maxZ), new Vector3(maxX, maxY, maxZ), new Vector3(maxX, maxY, minZ), minU, maxU, minV, maxV, res);
            quad(t, new Vector3(minX, maxY, minZ), new Vector3(minX, minY, minZ), new Vector3(minX, minY, maxZ), new Vector3(minX, maxY, maxZ), minU, maxU, minV, maxV, res);
            quad(t, new Vector3(maxX, maxY, maxZ), new Vector3(maxX, minY, maxZ), new Vector3(maxX, minY, minZ), new Vector3(maxX, maxY, minZ), minU, maxU, minV, maxV, res);
            quad(t, new Vector3(maxX, maxY, minZ), new Vector3(maxX, minY, minZ), new Vector3(minX, minY, minZ), new Vector3(minX, maxY, minZ), minU, maxU, minV, maxV, res);
            quad(t, new Vector3(minX, maxY, maxZ), new Vector3(minX, minY, maxZ), new Vector3(maxX, minY, maxZ), new Vector3(maxX, maxY, maxZ), minU, maxU, minV, maxV, res);
        }

        static void quad(FluidQuads.Sink t, Vector3 p1, Vector3 p2, Vector3 p3, Vector3 p4, float minU, float maxU, float minV, float maxV, double res)
        {
            Vector3 base = p2;
            Vector3 wide = vectors[0].set(p4).subtract(p1);
            Vector3 high = vectors[1].set(p1).subtract(p2);

            double u1 = minU;
            double du = maxU - minU;
            double v2 = maxV;
            double dv = maxV - minV;
            double wlen = wide.mag();
            double hlen = high.mag();

            double x = 0;
            while (x < wlen)
            {
                double rx = wlen - x;
                if (rx > res) rx = res;
                double y = 0;
                while (y < hlen)
                {
                    double ry = hlen - y;
                    if (ry > res) ry = res;

                    Vector3 dx1 = vectors[2].set(wide).multiply(x / wlen);
                    Vector3 dx2 = vectors[3].set(wide).multiply((x + rx) / wlen);
                    Vector3 dy1 = vectors[4].set(high).multiply(y / hlen);
                    Vector3 dy2 = vectors[5].set(high).multiply((y + ry) / hlen);

                    t.vertex(base.x + dx1.x + dy2.x, base.y + dx1.y + dy2.y, base.z + dx1.z + dy2.z, u1, v2 - ry / res * dv);
                    t.vertex(base.x + dx1.x + dy1.x, base.y + dx1.y + dy1.y, base.z + dx1.z + dy1.z, u1, v2);
                    t.vertex(base.x + dx2.x + dy1.x, base.y + dx2.y + dy1.y, base.z + dx2.z + dy1.z, u1 + rx / res * du, v2);
                    t.vertex(base.x + dx2.x + dy2.x, base.y + dx2.y + dy2.y, base.z + dx2.z + dy2.z, u1 + rx / res * du, v2 - ry / res * dv);

                    y += ry;
                }
                x += rx;
            }
        }
    }

    /** The tanks of the mod: backpack worn and placed (Y down, so minY > maxY), jetpack, copter, and tiling cases. */
    private static final double[][] BOXES = {
            // minX, minY, minZ, maxX, maxY, maxZ, res
            {-0.22, 0.44, 0.15, -0.07, 0.1, 0.3, 0.8},
            {0.48, 0.44, 0.15, 0.63, 0.2, 0.3, 0.8},
            {0.0, 0.5, 0.0, 0.17, 0.0, 0.17, 0.8},
            {0.18, 0.5, -0.045, 0.335, 0.2, 0.09, 0.8},
            {0.0, 0.25, 0.0, 0.25, 0.1, 0.25, 0.8},
            {0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 0.2},
            {0.1, 0.9, -0.3, 0.7, 0.2, 0.55, 0.25},
    };

    private static final float[] ICON = {0.25f, 0.3125f, 0.5f, 0.5625f};

    private static void legacy(Recorder r, double[] b)
    {
        Legacy.cuboid(r, b[0], b[1], b[2], b[3], b[4], b[5], ICON[0], ICON[1], ICON[2], ICON[3], b[6]);
    }

    private static void fresh(Recorder r, double[] b)
    {
        FluidQuads.cuboid(r, b[0], b[1], b[2], b[3], b[4], b[5], ICON[0], ICON[1], ICON[2], ICON[3], b[6]);
    }

    @Test
    void sameVerticesAsTheOldCuboid()
    {
        for (double[] b : BOXES)
        {
            Recorder old = new Recorder();
            Recorder now = new Recorder();
            legacy(old, b);
            fresh(now, b);
            assertTrue(old.n >= 24 * 5, "the old code drew at least six quads");
            assertEquals(old.n, now.n);
            for (int i = 0; i < old.n; i++)
            {
                assertEquals(old.data[i], now.data[i], 0.0, "value " + i + " of box " + java.util.Arrays.toString(b));
            }
        }
    }

    @Test
    void aTankWiderThanTheTileDrawsOneQuadPerFace()
    {
        Recorder r = new Recorder();
        fresh(r, BOXES[0]);
        assertEquals(24 * 5, r.n);
    }

    private static long allocated()
    {
        return ((com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean()).getThreadAllocatedBytes(Thread.currentThread().getId());
    }

    @Test
    void drawingATankAllocatesNothing()
    {
        Recorder r = new Recorder();
        double[] b = BOXES[0];
        int calls = 20000;
        for (int i = 0; i < 5000; i++)
        {
            r.n = 0;
            fresh(r, b);
            r.n = 0;
            legacy(r, b);
        }
        long before = allocated();
        for (int i = 0; i < calls; i++)
        {
            r.n = 0;
            fresh(r, b);
        }
        long fresh = allocated() - before;

        before = allocated();
        for (int i = 0; i < calls; i++)
        {
            r.n = 0;
            legacy(r, b);
        }
        long old = allocated() - before;

        System.out.println("FLUID-CUBOID-ALLOC bytes per call: new=" + (fresh / (double) calls) + " old=" + (old / (double) calls));
        assertTrue(fresh < 2048, "FluidQuads allocated " + fresh + " bytes in " + calls + " calls");
        assertTrue(old > calls * 24L * 16, "the measurement must see the 24 vectors of the old code, saw " + old + " bytes");
    }
}
