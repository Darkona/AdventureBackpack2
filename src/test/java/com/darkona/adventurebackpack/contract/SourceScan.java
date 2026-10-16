package com.darkona.adventurebackpack.contract;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A small Java source reader for the contract tests: it strips comments and literals (keeping line breaks, so line
 * numbers stay right), finds method headers and their bodies by brace matching, and drops the bodies of
 * {@code catch} blocks (a hot path's error branch is cold). No parser library: the mod's sources are plain Java 8.
 * Nested and anonymous classes' methods are found on their own too, and also stay inside the outer body.
 */
final class SourceScan
{
    private SourceScan() {}

    static final class SourceFile
    {
        final String rel;
        final String code;

        SourceFile(String rel, String code)
        {
            this.rel = rel;
            this.code = code;
        }

        int line(int offset)
        {
            int line = 1;
            for (int i = 0; i < offset && i < code.length(); i++)
            {
                if (code.charAt(i) == '\n') line++;
            }
            return line;
        }
    }

    static final class Method
    {
        final String name, params;
        /** The body without catch blocks, and its offset in the file's code. */
        final String body;
        final int bodyStart;
        /** The raw body (with catch blocks), for the GL balance rule. */
        final String rawBody;

        Method(String name, String params, String rawBody, int bodyStart)
        {
            this.name = name;
            this.params = params;
            this.rawBody = rawBody;
            this.body = blankCatchBlocks(rawBody);
            this.bodyStart = bodyStart;
        }
    }

    static List<SourceFile> read(Path root) throws IOException
    {
        List<Path> files;
        try (Stream<Path> walk = Files.walk(root))
        {
            files = walk.filter(p -> p.toString().endsWith(".java")).sorted().collect(Collectors.toList());
        }
        List<SourceFile> out = new ArrayList<SourceFile>();
        for (Path file : files)
        {
            String rel = root.relativize(file).toString().replace('\\', '/');
            out.add(new SourceFile(rel, strip(new String(Files.readAllBytes(file), StandardCharsets.UTF_8))));
        }
        return out;
    }

    private static final Pattern HEADER = Pattern.compile("(\\w+)\\s*\\(([^()]*(?:\\([^()]*\\)[^()]*)*)\\)\\s*(?:throws\\s+[\\w.,\\s]+)?\\{");
    private static final Pattern NOT_METHOD = Pattern.compile("if|for|while|switch|catch|synchronized|return|new|else|try|do");

    static List<Method> methods(String code)
    {
        List<Method> out = new ArrayList<Method>();
        Matcher h = HEADER.matcher(code);
        int from = 0;
        while (h.find(from))
        {
            String name = h.group(1);
            int open = h.end() - 1;
            if (NOT_METHOD.matcher(name).matches())
            {
                from = h.end();
                continue;
            }
            int end = matchBrace(code, open);
            out.add(new Method(name, h.group(2), code.substring(open, end + 1), open));
            from = h.end();
        }
        return out;
    }

    static int matchBrace(String code, int open)
    {
        int depth = 0;
        for (int i = open; i < code.length(); i++)
        {
            char c = code.charAt(i);
            if (c == '{') depth++;
            else if (c == '}' && --depth == 0) return i;
        }
        return code.length() - 1;
    }

    private static final Pattern CATCH = Pattern.compile("catch\\s*\\([^)]*\\)\\s*\\{");

    /** The body with every catch block's content replaced by blanks of the same length (offsets and lines kept). */
    static String blankCatchBlocks(String body)
    {
        StringBuilder sb = new StringBuilder(body);
        Matcher m = CATCH.matcher(body);
        int from = 0;
        while (m.find(from))
        {
            int open = m.end() - 1;
            int end = matchBrace(body, open);
            for (int i = open + 1; i < end; i++)
            {
                if (sb.charAt(i) != '\n') sb.setCharAt(i, ' ');
            }
            from = end;
        }
        return sb.toString();
    }

    /** Comments removed and literals emptied ({@code "abc"} becomes {@code ""}); every line break is kept. */
    static String strip(String src)
    {
        StringBuilder sb = new StringBuilder(src.length());
        int i = 0;
        while (i < src.length())
        {
            char c = src.charAt(i);
            if (c == '/' && i + 1 < src.length() && src.charAt(i + 1) == '/')
            {
                while (i < src.length() && src.charAt(i) != '\n') i++;
            } else if (c == '/' && i + 1 < src.length() && src.charAt(i + 1) == '*')
            {
                i += 2;
                while (i + 1 < src.length() && !(src.charAt(i) == '*' && src.charAt(i + 1) == '/'))
                {
                    if (src.charAt(i) == '\n') sb.append('\n');
                    i++;
                }
                i += 2;
            } else if (c == '"' || c == '\'')
            {
                sb.append(c).append(c);
                i++;
                while (i < src.length() && src.charAt(i) != c)
                {
                    if (src.charAt(i) == '\\') i++;
                    i++;
                }
                i++;
            } else
            {
                sb.append(c);
                i++;
            }
        }
        return sb.toString();
    }
}
