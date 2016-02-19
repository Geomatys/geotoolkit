/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.image.io.large;

import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.nio.IOUtilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.util.logging.Logging;

/**
 * Create tree directory made, starting from tree root directory, define by user.
 *
 * @author Remi Marechal (Geomatys).
 */
public class QuadTreeDirectory {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.image.io.large");

    private static final String D00 = "00";
    private static final String D01 = "01";
    private static final String D10 = "10";
    private static final String D11 = "11";

    private final int nbrElementX;
    private final int nbrElementY;
    private final Path treeRootPath;
    private final String extension;
    private final boolean isDeleteOnExit;

    /**
     * Create tree directory made, starting from tree root directory, define by user.
     *
     * @param treeRootPath tree root directory path.
     * @param nbrElementX global elements number in X direction.
     * @param nbrElementY global elements number in Y direction.
     * @param extension extension format of element will be stocked. May be null.
     * @param isDeleteOnExit true if user want delete all tree directory at end of JVM else false.
     */
    public QuadTreeDirectory(final Path treeRootPath, final int nbrElementX, final int nbrElementY, final String extension, final boolean isDeleteOnExit) throws IOException {
        ArgumentChecks.ensureNonNull("Quad-tree root file.", treeRootPath);
        ArgumentChecks.ensureStrictlyPositive("Quad-tree X axis dimension", nbrElementX);
        ArgumentChecks.ensureStrictlyPositive("Quad-tree Y axis dimension", nbrElementY);

        this.treeRootPath       = treeRootPath;
        this.nbrElementX        = nbrElementX;
        this.nbrElementY        = nbrElementY;
        this.extension = (extension == null) ? "" : (extension.substring(0, 1).equalsIgnoreCase(".")) ? extension : "." + extension;
        this.isDeleteOnExit = isDeleteOnExit;
        // Check if given path is valid, because if it's not, the all quad-tree is compromised.
        createDirectory(treeRootPath);
        create4rchitecture(treeRootPath, nbrElementX, nbrElementY);
        LOGGER.log(Level.FINE, "Quad-tree have been successfully initialized for path" + treeRootPath);
    }

    /**
     * Root directory where tiles are stored.
     *
     * @return root directory
     */
    public Path getTreeRootPath() {
        return treeRootPath;
    }

    private void create4rchitecture(final Path path, int numXTiles, int numYTiles) throws IOException {
        LOGGER.log(Level.FINE, "Begin creation of an entire quadTree level");
        createDirectory(path);
        if (numXTiles <= 2 && numYTiles <= 2) return;

        final int nxt = (numXTiles + 1) / 2;
        final int nyt = (numYTiles + 1) / 2;

        if (numXTiles <= 2) {
            //cut in height
            //create 2 directories
            create4rchitecture(path.resolve(D00), numXTiles, nyt); //00
            create4rchitecture(path.resolve(D01), numXTiles, numYTiles - nyt); //01

        } else if (numYTiles <= 2) {
            //cut in width
            //create 2 directories
            create4rchitecture(path.resolve(D00), nxt, numYTiles); //00
            create4rchitecture(path.resolve(D10), numXTiles - nxt, numYTiles); //10

        } else {
            //create 4 directories
            create4rchitecture(path.resolve(D00), nxt, nyt); //00
            create4rchitecture(path.resolve(D10), numXTiles-nxt, nyt); //10
            create4rchitecture(path.resolve(D01), nxt, numYTiles-nyt); //01
            create4rchitecture(path.resolve(D11), numXTiles-nxt, numYTiles-nyt); //11
            LOGGER.log(Level.FINE, "QuadTree level finely created.");
        }
    }

    /**
     * Create directory at place define by StringBuilder path.A verification is done to be sure that folder has been
     */
    private void createDirectory(final Path path) throws IOException {
        if (Files.isRegularFile(path)) {
            throw new IOException("Current path represents a file, but a directory is needed here : "+path);
        }
        // If not exists, we try to create directory.
        if (Files.notExists(path)) {
            Files.createDirectories(path);
            if (isDeleteOnExit) {
                IOUtilities.deleteOnExit(path);
            }
        }
        checkDirectory(path);
    }

    /**
     * Return appropriate path of element at X, Y position.
     *
     * @param x x direction element position.
     * @param y y direction element position.
     * @return appropriate path of element at X, Y position.
     */
    public String getPath(int x, int y) {
        return getPath(new StringBuilder(treeRootPath.toString()), 0, 0, nbrElementX-1, nbrElementY-1, x, y);
    }

    private String getPath(StringBuilder path, int mintx, int minty, int maxtx, int maxty, int tileX, int tileY) {
        final int dx = maxtx-mintx;
        final int dy = maxty-minty;
        if (dx <= 1 && dy <= 1) {
            path.append("/");
            path.append(tileX);
            path.append("_");
            path.append(tileY);
            path.append(extension);
            return (path.toString());
        }
        final int demx = mintx + dx / 2 + 1;
        final int demy = minty + dy / 2 + 1;

        path.append("/");
        if (dx <= 1) {
            //2 sub-directories in height.
            if (intersect(mintx, minty, maxtx, demy-1, tileX, tileY)) {
                path.append(D00);
                return getPath(path, mintx, minty, maxtx, demy-1, tileX, tileY);
            } else if (intersect(mintx, demy, maxtx, maxty, tileX, tileY)) {
                path.append(D01);
                return getPath(path, mintx, demy, maxtx, maxty, tileX, tileY);
            }
        } else if (dy <= 1) {
            //2 sub-directories in width.
            if (intersect(mintx, minty, demx-1, maxty, tileX, tileY)) {
                path.append(D00);
                return getPath(path, mintx, minty, demx-1, maxty, tileX, tileY);
            } else if (intersect( demx, minty, maxtx, maxty, tileX, tileY)) {
                path.append(D10);
                return getPath(path, demx, minty, maxtx, maxty, tileX, tileY);
            }
        } else {
            //4 cases.
            if (intersect(mintx, minty, demx-1, demy-1, tileX, tileY)) {
                path.append(D00);
                return getPath(path, mintx, minty, demx-1, demy-1, tileX, tileY);
            } else if (intersect(demx, minty, maxtx, demy-1, tileX, tileY)) {
                path.append(D10);
                return getPath(path, demx, minty, maxtx, demy-1, tileX, tileY);
            } else if (intersect(mintx, demy, demx-1, maxty, tileX, tileY)) {
                path.append(D01);
                return getPath(path, mintx, demy, demx-1, maxty, tileX, tileY);
            } else if (intersect(demx, demy, maxtx, maxty, tileX, tileY)) {
                path.append(D11);
                return getPath(path, demx, demy, maxtx, maxty, tileX, tileY);
            }
        }
        throw new IllegalStateException("Undefined path. Asked tile ("+tileX+", "+tileY+") is out of bounds.");
    }

    private boolean intersect(int minx, int miny, int maxx, int maxy, int tx, int ty){
        return (tx >= minx) && (tx <= maxx) && (ty >= miny) && (ty <= maxy);
    }

    private final void checkDirectory(final Path toCheck) throws IOException {
        final Path path = toCheck.toAbsolutePath();
        if (Files.isRegularFile(toCheck)) {
            throw new IOException("Current path represents a file, but a directory is needed here : "+path);
        }
        if (Files.notExists(toCheck)) {
            final Path parent = getExistingParent(toCheck);
            if (parent != null && Files.isWritable(parent)) {
                throw new IOException("Cannot create folder : "+path+", because application does not possess writing authorisation on parent folder : "+ parent.toString());
            } else {
                throw new IOException("Cannot create folder : "+path+" for some unknown reason");
            }
        }
        if (!Files.isWritable(toCheck) || !Files.isReadable(toCheck)) {
            throw new IOException("Given directory ("+ path +") for Quad-tree does not possess sufficient rights.");
        }
    }

    /**
     * Search a parent folder for input file, one which actually exists on the file system.
     *
     * @param child The file to search parent for.
     * @return the first exising parent folder of the given file, or null if we cannot find any.
     */
    private Path getExistingParent(Path child) {
        final Path parent = child.getParent();
        if (parent != null && Files.notExists(parent)) {
            return getExistingParent(parent);
        }
        return parent;
    }

    @Override
    protected void finalize() throws Throwable {
        if (isDeleteOnExit) {
            cleanDirectory();
        }
        super.finalize();
    }

    /**
     * Delete root directory recursively.
     */
    void cleanDirectory() throws IOException {
        IOUtilities.deleteRecursively(treeRootPath);
    }
}
