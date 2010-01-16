/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.legacy;

import java.io.*;
import java.util.*;


/**
 * Performs a shallow migration of Java code from GeoTools to Geotk. This helper tools
 * performs some class and package renaming. It will not solve every dependency issues, but
 * just some common ones. The last changes in method names, signatures, <i>etc.</i>
 * will need to be done by hand.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public class MigrateFromGeoTools {
    /**
     * The prefix to prepend to every keys read from the properties file.
     * This prefix was omitted from the properties file for brievety.
     */
    static final String GEOTOOLS_PREFIX = "org.geotools.";

    /**
     * The prefix to prepend to every values read from the properties file.
     * This prefix was omitted from the properties file for brievety.
     */
    static final String GEOTOOLKIT_PREFIX = "org.geotoolkit.";

    /**
     * The table of class name changes. Key are old names and values are new names.
     * This is the content of the {@code ClassChanges.properties} resource file.
     */
    final Map<String,String> table;

    /**
     * {@code true} if this class is running in "pretend" mode.
     */
    private final boolean pretend;

    /**
     * Initializes a new upgrader.
     *
     * @param pretend {@code true} if this class is running in "pretend" mode.
     */
    MigrateFromGeoTools(final boolean pretend) throws IOException {
        final BufferedReader r = new BufferedReader(new InputStreamReader(open("PortedClasses.txt"), "UTF-8"));
        final Properties tmp = new Properties();
        String line;
        while ((line = r.readLine()) != null) {
            if ((line = line.trim()).length() != 0) {
                tmp.put(line, line);
            }
        }
        r.close();
        final InputStream in = open("ClassChanges.properties");
        tmp.load(in);
        in.close();

        @SuppressWarnings({"unchecked","rawtypes"})
        final Map<String,String> copy = new LinkedHashMap<String,String>((Map) tmp);
        table = Collections.unmodifiableMap(copy);
        this.pretend = pretend;
    }

    /**
     * Opens the given resources, which should be bundled in the JAR.
     */
    private static final InputStream open(final String resources) throws IOException {
        final InputStream in = MigrateFromGeoTools.class.getResourceAsStream(resources);
        if (in == null) {
            throw new FileNotFoundException(resources);
        }
        return in;
    }

    /**
     * Migrates the given file. If the file is a directory, migrate all files in
     * the given directory and sub-directories.
     */
    private void migrate(final File file) throws IOException {
        if (file.isDirectory()) {
            if (!file.isHidden() || file.getName().equals(".")) {
                for (final File f : file.listFiles()) {
                    migrate(f);
                }
            }
            return;
        }
        if (!file.getName().endsWith(".java")) {
            return;
        }
        /*
         * At this point we have a source file to process.
         */
        boolean hasChanges = false;
        final String lineSeparator = System.getProperty("line.separator", "\n");
        final BufferedReader in = new BufferedReader(new FileReader(file));
        final StringBuilder buffer = new StringBuilder();
        final Map<String,String> imports = new LinkedHashMap<String,String>();
        String line;
        while ((line = in.readLine()) != null) {
            final String modified = migrate(line, imports);
            if (modified == null) {
                hasChanges = true;
                continue;
            }
            buffer.append(modified).append(lineSeparator);
            if (!hasChanges && !line.equals(modified)) {
                hasChanges = true;
            }
        }
        in.close();
        if (hasChanges) {
            System.out.println(file);
            if (!pretend) {
                final Writer out = new FileWriter(file);
                out.write(buffer.toString());
                out.close();
            }
        }
    }

    /**
     * Upgrades a single line. This method is non-private for testing purpose only.
     *
     * @param  line The line to migrate.
     * @return The upgraded line, or {@code null} if it is an import statement to skip.
     */
    final String migrate(String line, final Map<String,String> imports) {
        for (final Map.Entry<String,String> entry : table.entrySet()) {
            String search  =   GEOTOOLS_PREFIX + entry.getKey();
            String replace = GEOTOOLKIT_PREFIX + entry.getValue();
            if (line != (line = replace(line, search, replace))) {
                if (line.trim().startsWith("import")) {
                    search = search.substring(search.lastIndexOf('.') + 1).replace('$', '.');
                    replace = replace.substring(replace.lastIndexOf('.') + 1);
                    final boolean isInnerClass = (replace != (replace = replace.replace('$', '.')));
                    imports.put(search, replace);
                    if (isInnerClass) {
                        return null; // Do not insert an import statement for inner classes.
                    }
                }
            }
        }
        /*
         * Now process the imported classes.
         */
        for (final Map.Entry<String,String> entry : imports.entrySet()) {
            final String search = entry.getKey();
            final String update = entry.getValue();
            line = replace(line, search, update);
        }
        return line;
    }

    /**
     * Replaces the search string by the update, provided that is begin and end by a
     * non-Java identifier.
     */
    private static String replace(final String line, final String search, final String update) {
        int lo = line.indexOf(search);
        if (lo < 0) {
            return line;
        }
        int last = 0;
        final int e = line.length();
        final StringBuilder buffer = new StringBuilder();
        do {
            final int hi = lo + search.length();
            if ((lo == 0 || !Character.isJavaIdentifierStart(line.charAt(lo-1))) &&
                (hi == e || !Character.isJavaIdentifierPart(line.charAt(hi))))
            {
                buffer.append(line.substring(last, lo)).append(update);
                last = hi;
            }
            lo = line.indexOf(search, hi);
        } while (lo >= 0);
        return buffer.append(line.substring(last)).toString();
    }

    /**
     * Runs from the command line. This method contains a few undocumented commands.
     * Those commands are mostly for maintaining this module and may change in any
     * future version.
     *
     * @param  args The command line arguments.
     * @throws IOException If an error occured while reading or writing the source files.
     */
    public static void main(String[] args) throws IOException {
        switch (args.length) {
            case 1: {
                final String arg = args[0];
                if (arg.equals("--migrate")) {
                    new MigrateFromGeoTools(false).migrate(new File("."));
                    return;
                } else if (arg.equals("--pretend")) {
                    new MigrateFromGeoTools(true).migrate(new File("."));
                    return;
                } else if (arg.equals("--check-gt")) {
                    new ClassChangesChecker().check(false);
                    return;
                } else if (arg.equals("--check-gto")) {
                    new ClassChangesChecker().check(true);
                    return;
                }
                break;
            }
            case 2: {
                final String arg = args[0];
                if (arg.equals("--ported")) {
                    new ClassChangesChecker().listPortedClasses(args[1]);
                    return;
                }
                break;
            }
            case 3: {
                final String arg = args[0];
                if (arg.equals("--intersect")) {
                    new ClassChangesChecker().intersect(args[1], args[2]);
                }
                return;
            }
        }
        final PrintStream out = System.out;
        out.println("Usage: java -jar geotk-migrate.jar [OPTION]");
        out.println("Migrate Java source code from GeoTools to Geotoolkit in current directory");
        out.println("and all sub-directories. Only simple changes like class renaming are performed.");
        out.println();
        out.println("OPTION:");
        out.println("  --pretend  Lists the files that would be changed but do not touch them.");
        out.println("  --migrate  Migrates GeoTools source code in place. The migrated files");
        out.println("             will overwrite the old ones.");
        // The other commands are undocumented - we use them for maintaining this module only.
    }
}
