package codechicken.lib.render;

/**
 * The quads of a fluid cuboid, worked out with primitives only: it builds no vector, array or list, so a tank can be
 * drawn on every frame without garbage. It is the geometry of {@code RenderUtils.renderFluidQuad} (same faces, same
 * vertex order, same icon tiling every {@code res} units) written without {@code Vector3}, and it knows nothing of
 * OpenGL: the vertices go to a {@link Sink}, which the renderer points at the tessellator and the tests at a counter.
 * <p>
 * A face is {@code base} plus a {@code wide} side and a {@code high} side, both signed (the model space of the
 * backpacks has Y pointing down, so a box may have {@code minY > maxY}); the signs set the winding.
 */
public final class FluidQuads
{
    /** Receives the vertices in the order of the tessellator's {@code addVertexWithUV}. */
    public interface Sink
    {
        void vertex(double x, double y, double z, double u, double v);
    }

    private FluidQuads()
    {
    }

    /**
     * The six faces of the box {@code min..max} (any order of the corners), tiled with the icon every {@code res}
     * units. The icon is given as its {@code minU}, {@code maxU}, {@code minV} and {@code maxV}.
     */
    public static void cuboid(Sink out, double minX, double minY, double minZ, double maxX, double maxY, double maxZ,
                              float minU, float maxU, float minV, float maxV, double res)
    {
        double dx = maxX - minX;
        double dy = maxY - minY;
        double dz = maxZ - minZ;
        double u1 = minU;
        double du = maxU - minU;
        double v2 = maxV;
        double dv = maxV - minV;
        // bottom
        quad(out, maxX, minY, minZ, 0, 0, dz, -dx, 0, 0, u1, du, v2, dv, res);
        // top
        quad(out, minX, maxY, maxZ, dx, 0, 0, 0, 0, -dz, u1, du, v2, dv, res);
        // -x
        quad(out, minX, minY, minZ, 0, 0, dz, 0, dy, 0, u1, du, v2, dv, res);
        // +x
        quad(out, maxX, minY, maxZ, 0, 0, -dz, 0, dy, 0, u1, du, v2, dv, res);
        // -z
        quad(out, maxX, minY, minZ, -dx, 0, 0, 0, dy, 0, u1, du, v2, dv, res);
        // +z
        quad(out, minX, minY, maxZ, dx, 0, 0, 0, dy, 0, u1, du, v2, dv, res);
    }

    /** One face, bottom to top and left to right, in tiles of {@code res} units (the last one of each row and column is cut short). */
    public static void quad(Sink out, double bx, double by, double bz, double wx, double wy, double wz,
                            double hx, double hy, double hz, double u1, double du, double v2, double dv, double res)
    {
        double wlen = Math.sqrt(wx * wx + wy * wy + wz * wz);
        double hlen = Math.sqrt(hx * hx + hy * hy + hz * hz);

        double x = 0;
        while (x < wlen)
        {
            double rx = wlen - x;
            if (rx > res)
            {
                rx = res;
            }
            double f1 = x / wlen;
            double f2 = (x + rx) / wlen;
            double ux = rx / res * du;

            double y = 0;
            while (y < hlen)
            {
                double ry = hlen - y;
                if (ry > res)
                {
                    ry = res;
                }
                double g1 = y / hlen;
                double g2 = (y + ry) / hlen;
                double vy = ry / res * dv;

                out.vertex(bx + wx * f1 + hx * g2, by + wy * f1 + hy * g2, bz + wz * f1 + hz * g2, u1, v2 - vy);
                out.vertex(bx + wx * f1 + hx * g1, by + wy * f1 + hy * g1, bz + wz * f1 + hz * g1, u1, v2);
                out.vertex(bx + wx * f2 + hx * g1, by + wy * f2 + hy * g1, bz + wz * f2 + hz * g1, u1 + ux, v2);
                out.vertex(bx + wx * f2 + hx * g2, by + wy * f2 + hy * g2, bz + wz * f2 + hz * g2, u1 + ux, v2 - vy);

                y += ry;
            }

            x += rx;
        }
    }
}
