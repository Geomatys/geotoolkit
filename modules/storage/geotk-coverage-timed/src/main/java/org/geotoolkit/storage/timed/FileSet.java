/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.storage.timed;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.apache.sis.util.ArgumentChecks;

/**
 * Describes a set of files with following characteristics :
 * <ul>
 * <li>located in the same directory</li>
 * <li>base name (name without extension) is the same.</li>
 * <ul>
 *
 * The aim is to regroup files of a folder with the same extension. Note that
 * no scan is performed by this class, which just keep reference of given paths
 * via {@link #add(java.nio.file.Path) }.
 *
 * You can iterate over registered files using {@link #spliterator() }. Note that
 * it returns a snapshot of the file set at the moment of the call.
 *
 * @author Alexis Manin (Geomatys)
 */
class FileSet {

    /**
     * Name of the main file, without the extension.
     */
    protected final String baseName;

    /**
     * Path to the directory containing this dataset files.
     */
    protected final Path parent;

    /**
     * List extensions of the files composing this dataset. It's enough to
     * retrieve all files of the set, as we're designed to manage concurrent
     * files with same base name.
     */
    private final Set<String> extensions;

    FileSet(final Path mainFile) {
        ArgumentChecks.ensureNonNull("Main file", mainFile);
        parent = mainFile.getParent();
        final FileName name = new FileName(mainFile);
        baseName = name.baseName;
        extensions = new HashSet<>();
        extensions.add(name.ext);
    }

    public String getBaseName() {
        return baseName;
    }

    /**
     * Add the given file to this set if it fits. A path is considered valid if all
     * of the following conditions match :
     * <ul>
     * <li>Its parent is the directory described by {@link #parent},</li>
     * <li>Its name without extension equals {@link #baseName}.</li>
     * </ul>
     * @param p the file to add.
     * @return True if the given path described a valid element of this set, and has
     * been registered. False otherwise. Note that adding a file already present
     * in the set will return false.
     */
    public boolean add(final Path p) {
        return checkExecute(p, f -> extensions.add(f.ext));
    }

    public boolean remove(final Path p) {
        return checkExecute(p, f -> extensions.remove(f.ext));
    }

    private boolean checkExecute(final Path candidate, final Predicate<FileName> action) {
        if (candidate == null || !Objects.equals(parent, candidate.getParent())) {
            return false;
        }

        final FileName f = new FileName(candidate);
        return baseName.equals(f.baseName) && action.test(f);
    }

    /**
     * Create an immutable iterator over this set's paths.
     *
     * @return a snapshot of the file set. It means that no subsequent call to
     * {@link #add(java.nio.file.Path) } will modify the content of the spliterator.
     */
    public java.util.Spliterator<Path> spliterator() {
        return new Spliterator(parent, baseName, extensions);
    }

    /**
     * Extract file name from a path (using {@link Path#getFileName() }), then
     * separate its base name from the extension.
     */
    private static class FileName {
        public final String baseName;
        public final String ext;

        FileName(final Path p) {
            final String fileName = p.getFileName().toString();
            final int lastPoint = fileName.lastIndexOf('.');
            if (lastPoint < 1) {
                // no extension, as occurence at index zero would denote either an hidden file or a file without name.
                baseName = fileName;
                ext = "";
            } else {
                baseName = fileName.substring(0, lastPoint);
                ext = fileName.substring(lastPoint);
            }
        }
    }

    private static class Spliterator implements java.util.Spliterator<Path> {
        final Path parent;
        final String baseName;
        final String[] suffixes;

        private int idx = 0;

        Spliterator(final Path parent, final String baseName, final Set<String> suffixes) {
            this.parent = parent;
            this.baseName = baseName;
            this.suffixes = suffixes.toArray(new String[suffixes.size()]);
        }

        @Override
        public boolean tryAdvance(Consumer<? super Path> action) {
            if (idx < suffixes.length) {
                final String fileName = baseName.concat(suffixes[idx++]);
                final Path next = parent == null? Paths.get(fileName) : parent.resolve(fileName);
                action.accept(next);
                return true;
            }

            return false;
        }

        @Override
        public Spliterator trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return suffixes.length - idx;
        }

        @Override
        public int characteristics() {
            return DISTINCT | IMMUTABLE | NONNULL | SIZED;
        }
    }
}
