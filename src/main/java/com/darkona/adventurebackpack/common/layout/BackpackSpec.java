package com.darkona.adventurebackpack.common.layout;

import java.util.Arrays;

public final class BackpackSpec
{
    private final int rows;
    private final int columns;
    private final int[] kinds;
    private final int[] params;

    public BackpackSpec(int rows, int columns, int[] kinds, int[] params)
    {
        if (kinds.length != params.length) throw new IllegalArgumentException("kinds and params differ in length");
        this.rows = rows;
        this.columns = columns;
        this.kinds = kinds.clone();
        this.params = params.clone();
    }

    public int rows()
    {
        return rows;
    }

    public int columns()
    {
        return columns;
    }

    public int widgets()
    {
        return kinds.length;
    }

    public int kind(int i)
    {
        return kinds[i];
    }

    public int param(int i)
    {
        return params[i];
    }

    public int paramOf(int kind, int fallback)
    {
        for (int i = 0; i < kinds.length; i++)
        {
            if (kinds[i] == kind) return params[i];
        }
        return fallback;
    }

    public boolean has(int kind, int param)
    {
        for (int i = 0; i < kinds.length; i++)
        {
            if (kinds[i] == kind && params[i] == param) return true;
        }
        return false;
    }

    public BackpackSpec resized(int newRows, int newColumns)
    {
        return newRows == rows && newColumns == columns ? this : new BackpackSpec(newRows, newColumns, kinds, params);
    }

    public int[] encode()
    {
        int[] out = new int[3 + 2 * kinds.length];
        out[0] = rows;
        out[1] = columns;
        out[2] = kinds.length;
        for (int i = 0; i < kinds.length; i++)
        {
            out[3 + 2 * i] = kinds[i];
            out[4 + 2 * i] = params[i];
        }
        return out;
    }

    public static BackpackSpec decode(int[] in)
    {
        if (in.length < 3 || in[2] < 0 || in.length != 3 + 2 * in[2]) throw new IllegalArgumentException("bad spec of " + in.length + " ints");
        int n = in[2];
        int[] kinds = new int[n];
        int[] params = new int[n];
        for (int i = 0; i < n; i++)
        {
            kinds[i] = in[3 + 2 * i];
            params[i] = in[4 + 2 * i];
        }
        return new BackpackSpec(in[0], in[1], kinds, params);
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof BackpackSpec)) return false;
        BackpackSpec s = (BackpackSpec) o;
        return rows == s.rows && columns == s.columns && Arrays.equals(kinds, s.kinds) && Arrays.equals(params, s.params);
    }

    @Override
    public int hashCode()
    {
        return (rows * 31 + columns) * 31 + Arrays.hashCode(kinds) * 17 + Arrays.hashCode(params);
    }

    @Override
    public String toString()
    {
        return rows + "x" + columns + " " + Arrays.toString(kinds) + Arrays.toString(params);
    }
}
