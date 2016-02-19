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

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * FileVisitor implementation with Paths and an input {@link PathMatcher} that return all matcher Paths
 * into a list {@link #getMatchedPaths()}.
 * Input PathMatcher can be test against File only or also with directories using constructor
 * {@link #PathFilterVisitor(PathMatcher, boolean)}. By default PathMatcher is used only with Files type Path.
 *
 * @author Quentin Boileau (Geomatys)
 */
public class PathFilterVisitor extends SimpleFileVisitor<Path> {

    /**
     * PathMatcher used to tests visited Paths
     */
    private final PathMatcher matcher;

    /**
     * Store all matching Path visited.
     */
    private final List<Path> matchedPaths = new ArrayList<>();

    /**
     * Flag to disengage matching test against directory
     */
    private boolean fileOnly = true;

    public PathFilterVisitor(PathMatcher matcher) {
        this.matcher = matcher;
    }
    public PathFilterVisitor(PathMatcher matcher, boolean fileOnly) {
        this.matcher = matcher;
        this.fileOnly = fileOnly;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if (!fileOnly) {
            if (matcher.matches(dir)) {
                matchedPaths.add(dir);
            }
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (matcher.matches(file)) {
            matchedPaths.add(file);
        }
        return FileVisitResult.CONTINUE;
    }

    public List<Path> getMatchedPaths() {
        return matchedPaths;
    }
}
