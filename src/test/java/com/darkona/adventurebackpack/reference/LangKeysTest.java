package com.darkona.adventurebackpack.reference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

/** The lang files the mod ships: every key the code translates exists, and Spanish covers the same keys as English. */
class LangKeysTest
{
    /** A whole key of the mod given to a translation call (a prefix ending in "." is completed at run time), or a key binding name. */
    private static final Pattern CODE_KEY = Pattern.compile(
            "(?:ChatComponentTranslation|translateToLocal\\w*|I18n\\.format)\\(\\s*\"(adventurebackpack:[A-Za-z.]*[A-Za-z])\"|\"(keys\\.adventureBackpack\\.[A-Za-z.]+)\"");

    private static Set<String> keys(String locale) throws IOException
    {
        Set<String> keys = new TreeSet<String>();
        InputStream in = LangKeysTest.class.getResourceAsStream("/assets/adventurebackpack/lang/" + locale + ".lang");
        assertNotNull(in, locale + ".lang is missing");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        try
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                int eq = line.indexOf('=');
                if (eq > 0) keys.add(line.substring(0, eq));
            }
        } finally
        {
            reader.close();
        }
        return keys;
    }

    @Test
    void spanishHasTheEnglishKeys() throws IOException
    {
        assertEquals(keys("en_US"), keys("es_ES"));
    }

    @Test
    void everyMessageKeyInTheCodeExists() throws IOException
    {
        Set<String> english = keys("en_US");
        Set<String> missing = new TreeSet<String>();
        Path sources = Paths.get("src/main/java");
        assertTrue(Files.isDirectory(sources), "run from the project directory");
        Stream<Path> files = Files.walk(sources);
        try
        {
            for (Path file : (Iterable<Path>) files.filter(p -> p.toString().endsWith(".java"))::iterator)
            {
                Matcher m = CODE_KEY.matcher(new String(Files.readAllBytes(file), StandardCharsets.UTF_8));
                while (m.find())
                {
                    String key = m.group(1) != null ? m.group(1) : m.group(2);
                    if (!english.contains(key)) missing.add(key + " (" + file.getFileName() + ")");
                }
            }
        } finally
        {
            files.close();
        }
        assertTrue(missing.isEmpty(), "keys used in the code but missing in en_US.lang: " + missing);
    }
}
