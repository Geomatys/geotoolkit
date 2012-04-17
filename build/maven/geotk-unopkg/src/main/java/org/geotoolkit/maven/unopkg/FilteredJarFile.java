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
package org.geotoolkit.maven.unopkg;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.util.zip.ZipEntry;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;


/**
 * A JAR file which can exclude some file entries and some attributes from the {@code MANIFEST.MF}
 * file. The main purpose of this class is to exclude the signature from the {@code vecmath.jar}
 * file before to compress it using the {@code pack200} tools, because {@code pack200} modifies
 * the binary stream, thus making the signature invalid. If we don't remove the signature, attempts
 * to use the JAR file may result in "SHA1 digest error for javax/vecmath" error message.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
final class FilteredJarFile extends JarFile {
    /**
     * The manifest encoding in JAR files.
     */
    private static final String MANIFEST_ENCODING = "UTF-8";

    /**
     * Open the file specified by the given name.
     */
    FilteredJarFile(final File filename) throws IOException {
        super(filename);
    }

    /**
     * Returns the list of entries, excluding Maven files (which are of no interest for the add-in)
     * and the signature.
     */
    @Override
    public Enumeration<JarEntry> entries() {
        final List<JarEntry> entries = Collections.list(super.entries());
        for (final Iterator<JarEntry> it=entries.iterator(); it.hasNext();) {
            final String name = it.next().getName();
            if (name.startsWith("META-INF/")) {
                if (name.startsWith("META-INF/maven/") || name.endsWith(".SF") || name.endsWith(".RSA")) {
                    it.remove();
                }
            }
        }
        return Collections.enumeration(entries);
    }

    /**
     * Returns the input stream for the given entry. If the given entry is a the manifest,
     * then this method will filter the manifest content in order to exclude the signature.
     */
    @Override
    public InputStream getInputStream(final ZipEntry ze) throws IOException {
        final InputStream in = super.getInputStream(ze);
        if (!ze.getName().equals(JarFile.MANIFEST_NAME)) {
            return in;
        }
        final List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, MANIFEST_ENCODING))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("SHA1-Digest:")) {
                    final int n = lines.size();
                    if (n == 0 || !lines.get(n-1).trim().startsWith("Name:")) {
                        throw new IOException("Can not process the following line from " +
                                JarFile.MANIFEST_NAME + ":\n" + line);
                    }
                    lines.remove(n-1);
                    continue;
                }
                lines.add(line);
            }
        }
        /*
         * 'in' has been closed at this point (indirectly, by closing the reader).
         * Now remove trailing empty lines, and returns the new MANIFEST.MF content.
         */
        for (int i=lines.size(); --i>=0;) {
            if (!lines.get(i).trim().isEmpty()) {
                break;
            }
            lines.remove(i);
        }
        final StringBuilder buffer = new StringBuilder(lines.size() * 60);
        for (final String line : lines) {
            buffer.append(line).append('\n');
        }
        return new ByteArrayInputStream(buffer.toString().getBytes(MANIFEST_ENCODING));
    }
}
