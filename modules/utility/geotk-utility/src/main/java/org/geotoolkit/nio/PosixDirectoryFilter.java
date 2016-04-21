/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2016, Geomatys
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
package org.geotoolkit.nio;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 *  A {@link java.nio.file.DirectoryStream.Filter} implementation using Unix-style wildcards.
 * A pattern is given to the constructor, which can contains the
 * {@code "*"} and {@code "?"} wildcards.
 *
 * Example :
 * <pre>
 * <code>
 *
 * DirectoryStream.Filter<Path> filter = new PosixDirectoryFilter("*.png");
 *
 * try (DirectoryStream<Path> stream = Files.newDirectoryStream(root, filter)) {
 *      for (Path matching : stream) {
 *          //file matching filter in root directory
 *      }
 * }
 * </code>
 * </pre>
 *
 * @author Quentin Boileau (Geomatys)
 */
public class PosixDirectoryFilter implements DirectoryStream.Filter<Path> {

    /**
     * The pattern to matchs to filenames.
     */
    private final Pattern pattern;

    public PosixDirectoryFilter(final String pattern) {
        this(pattern, Boolean.FALSE);
    }
    /**
     * Constructs a file filter for the specified pattern.
     * The pattern can contains the {@code "*"} and {@code "?"} wildcards.
     *
     * @param pattern The pattern. Example: {@code "*.png"}
     * @param caseInsensitive use {@link Pattern#CASE_INSENSITIVE} flag
     */
    public PosixDirectoryFilter(final String pattern, boolean caseInsensitive) {
        final int length = pattern.length();
        final StringBuilder buffer = new StringBuilder(length + 8);
        for (int i=0; i<length; i++) {
            final char c = pattern.charAt(i);
            if (!Character.isLetterOrDigit(c)) {
                switch (c) {
                    case '?': buffer.append('.' ); continue;
                    case '*': buffer.append(".*"); continue;
                    default : buffer.append('\\'); break;
                }
            }
            buffer.append(c);
        }

        if (caseInsensitive) {
            this.pattern = Pattern.compile(buffer.toString(), Pattern.CASE_INSENSITIVE);
        } else {
            this.pattern = Pattern.compile(buffer.toString());
        }
    }

    @Override
    public boolean accept(Path entry) throws IOException {
        return (entry != null) && pattern.matcher(entry.getFileName().toString()).matches();
    }
}
