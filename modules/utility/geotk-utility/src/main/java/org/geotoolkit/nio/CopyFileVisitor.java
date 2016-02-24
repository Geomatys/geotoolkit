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
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Java NIO FileVisitor that copy visited files and directory recursively into target {@link Path}
 *
 * Usage example :
 * <code>
 *     Path sourcePath = Paths.get("/some/path")
 *     Path targetPath = Paths.get("/output/path")
 *     Files.walkFileTree(sourcePath, new CopyFileVisitor(targetPath));
 * </code>
 *
 * @author Quentin Boileau (Geomatys)
 */
public class CopyFileVisitor extends SimpleFileVisitor<Path> {

    private final Path targetPath;
    private final CopyOption[] copyOption;
    private Path sourcePath = null;

    public CopyFileVisitor(Path targetPath, CopyOption... copyOption) {
        this.targetPath = targetPath;
        this.copyOption = copyOption;
    }

    @Override
    public FileVisitResult preVisitDirectory(final Path dir,
                                             final BasicFileAttributes attrs) throws IOException {
        if (sourcePath == null) {
            sourcePath = dir;
        } else {
            final Path relativize = sourcePath.relativize(dir);
            Files.createDirectories(targetPath.resolve(relativize.toString()));
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(final Path file,
                                     final BasicFileAttributes attrs) throws IOException {
        if (sourcePath == null) {
            sourcePath = file.getParent();
        }
        final Path relativize = sourcePath.relativize(file);
        Files.copy(file, targetPath.resolve(relativize.toString()), copyOption);
        return FileVisitResult.CONTINUE;
    }
}