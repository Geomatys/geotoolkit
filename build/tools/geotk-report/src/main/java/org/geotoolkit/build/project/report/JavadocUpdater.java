/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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
package org.geotoolkit.build.project.report;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.io.File;
import java.io.Writer;
import java.io.IOException;
import java.io.EOFException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


/**
 * Base classes of tools that automatically generate javadoc comments.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
public abstract class JavadocUpdater {
    /**
     * The encoding of source files.
     */
    private static final String ENCODING = "UTF-8";

    /**
     * The signature that indicates where to insert the comments.
     * Typically a HTML comment like {@code "<!-- GENERATED PARAMETERS -->"}.
     */
    private final String signatureBegin;

    /**
     * The signature that indicates where to stop the insertion of lines.
     */
    private final String signatureEnd;

    /**
     * The lines in HTML formats, without carriage returns. All {@code createFoo(...)} methods
     * defined in this class will append lines in HTML format to this list. After the list has
     * been completed, its content can be printed directly (for example by {@link #toString()},
     * or can be prefixed by the {@code " * "} characters of the lines are to be inserted in a
     * class Javadoc.
     */
    final List<String> lines;

    /**
     * The project root.
     */
    private final File root;

    /**
     * For subclass constructors only.
     *
     * @param signatureBegin The signature that indicates where to insert the comments.
     *        Typically a HTML comment like {@code "<!-- GENERATED PARAMETERS -->"}.
     * @param signatureEnd The signature that indicates where to stop the insertion of lines.
     */
    JavadocUpdater(final String signatureBegin, final String signatureEnd) throws IOException {
        this.signatureBegin = signatureBegin;
        this.signatureEnd   = signatureEnd;
        lines = new ArrayList<>();
        root = Reports.getProjectRootDirectory();
    }

    /**
     * Returns the outer class.
     */
    private static Class<?> getOuterClass(Class<?> classe) {
        while (classe != null) {
            final Class<?> enclosing = classe.getEnclosingClass();
            if (enclosing == null) break;
            classe = enclosing;
        }
        return classe;
    }

    /**
     * Updates the source code of the given class with the current content of {@link #lines}.
     * This method is used only for rewriting the comments of class description, since it
     * doesn't verify the class declaration before to search for the signature.
     *
     * <p>The begin and end signature must be specified explicitely since we can not rely on
     * class declaration. We expect different signature to be used for inner classes, if any.</p>
     *
     * @param module The module where are located the source files (e.g. {@code "referencing/geotk-referencing"}).
     * @param classe The class to rewrite.
     * @param begin  The signature that indicate the beginning of the comments to generate.
     * @param end    The signature that indicate the end of the comments to generate.
     */
    final void rewriteClassComment(final String module, final Class<?> classe,
            final String begin, final String end) throws IOException
    {
        rewriteSourceFile(module, classe, true, begin, end);
    }

    /**
     * Updates the source code of the given class with the current content of {@link #lines}.
     * This method is used only for rewriting the comments of a field or method, since it
     * searches for the class declaration before to search for the signature.
     *
     * @param module The module where are located the source files (e.g. {@code "referencing/geotk-referencing"}).
     * @param classe The class to rewrite.
     */
    final void rewriteMemberComment(final String module, final Class<?> classe) throws IOException {
        rewriteSourceFile(module, classe, false, signatureBegin, signatureEnd);
    }

    /**
     * Updates the source code of the given class with the current content of {@link #lines}.
     * This method is used only for rewriting the comments of class description, since it doesn't
     * verify the class declaration before to search for the signature.
     *
     * @param module The module where are located the source files (e.g. {@code "referencing/geotk-referencing"}).
     * @param classe The class to rewrite.
     * @param begin  The signature that indicate the beginning of the comments to generate.
     * @param end    The signature that indicate the end of the comments to generate.
     * @param foundClassSignature {@code true} for processing as if the class signature was
     *        already found, or {@code false} for performing the checks.
     */
    private void rewriteSourceFile(final String module, final Class<?> classe, boolean foundClassSignature,
            final String begin, final String end) throws IOException
    {
        // Where to put the updated code.
        final StringBuilder buffer = new StringBuilder();

        // What to search as an indication of the begining of the section to modify.
        final Pattern classSignature = foundClassSignature ? null :
                Pattern.compile(".*\\bclass\\s+" + classe.getSimpleName() + "\\b.*");
        boolean done = false;

        final File file = new File(root, "modules/" + module + "/src/main/java/" +
                getOuterClass(classe).getCanonicalName().replace('.', '/') + ".java");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), ENCODING))) {
            String line;
            while ((line = in.readLine()) != null) {
                buffer.append(line).append('\n');
                if (!done) {
                    if (!foundClassSignature) {
                        foundClassSignature = classSignature.matcher(line).matches();
                    } else if (line.contains(begin)) {
                        final String margin = line.substring(0, line.indexOf('*') + 2);
                        if (!margin.trim().equals("*")) {
                            throw new IOException("Expected a comment line, but found: " + line);
                        }
                        for (final String gen : lines) {
                            buffer.append(margin).append(gen).append('\n');
                        }
                        // Skip all remaining lines until the end of comments.
                        // Those lines were the previous generated table.
                        do {
                            line = in.readLine();
                            if (line == null) {
                                throw new EOFException();
                            }
                        } while (!line.trim().endsWith(end));
                        buffer.append(line).append('\n');
                        done = true;
                    }
                }
            }
        }
        // Write the result.
        try (Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODING)) {
            out.write(buffer.toString());
        }
    }

    /**
     * Returns the HTML code for debugging purpose
     */
    @Override
    public String toString() {
        final String lineSeparator = System.lineSeparator();
        final StringBuilder buffer = new StringBuilder();
        for (final String line : lines) {
            buffer.append(line).append(lineSeparator);
        }
        return buffer.toString();
    }
}
