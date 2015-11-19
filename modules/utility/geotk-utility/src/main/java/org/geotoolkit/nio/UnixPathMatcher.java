/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2015, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2015, Geomatys
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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.regex.Pattern;

/**
 *  A {@link PathMatcher} implementation using Unix-style wildcards.
 * A pattern is given to the constructor, which can contains the
 * {@code "*"} and {@code "?"} wildcards.
 *
 * Example :
 * <pre>
 * <code>
 *
 * PathMatcher matcher = new UnixPathMatcher("*.png");
 *
 * matcher.matches(Paths.get("/tmp/image.png")); // true
 * matcher.matches(Paths.get("/tmp/image.jpeg")); // false
 * </code>
 * </pre>
 *
 * @author Quentin Boileau (Geomatys)
 */
public class UnixPathMatcher implements PathMatcher {

    /**
     * The pattern to matchs to filenames.
     */
    private final Pattern pattern;

    public UnixPathMatcher(final String pattern) {
        this(pattern, Boolean.FALSE);
    }
    /**
     * Constructs a file filter for the specified pattern.
     * The pattern can contains the {@code "*"} and {@code "?"} wildcards.
     *
     * @param pattern The pattern. Example: {@code "*.png"}
     * @param caseInsensitive use {@link Pattern#CASE_INSENSITIVE} flag
     */
    public UnixPathMatcher(final String pattern, boolean caseInsensitive) {
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
    public boolean matches(Path path) {
        return (path != null) && pattern.matcher(path.getFileName().toString()).matches();
    }
}
