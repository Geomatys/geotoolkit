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

import java.io.File;

/**
 * Create tree directory made, starting from tree root directory, define by user.
 *
 * @author Remi Marechal (Geomatys).
 */
public class QuadTreeDirectory {

    private static final String D00 = "/00";
    private static final String D01 = "/01";
    private static final String D10 = "/10";
    private static final String D11 = "/11";

    private final StringBuilder strBuilder;
    private final int nbrElementX;
    private final int nbrElementY;
    private final String treeRootPath;
    private final int treeRootPathLength;
    private final String extension;
    private final boolean isDeleteOnExit;

    /**
     * Recursive necessary attributes.
     */
    int dx, dy, demx, demy;

    /**
     * Create tree directory made, starting from tree root directory, define by user.
     *
     * @param treeRootPath tree root directory path.
     * @param nbrElementX global elements number in X direction.
     * @param nbrElementY global elements number in Y direction.
     * @param extension extension format of element will be stocked. May be null.
     * @param isDeleteOnExit true if user want delete all tree directory at end of JVM else false.
     */
    public QuadTreeDirectory(String treeRootPath, int nbrElementX, int nbrElementY, String extension, boolean isDeleteOnExit) {
        this.treeRootPath       = treeRootPath;
        this.treeRootPathLength = treeRootPath.length();
        this.strBuilder         = new StringBuilder(treeRootPath);
        this.nbrElementX        = nbrElementX;
        this.nbrElementY        = nbrElementY;
        this.extension = (extension == null) ? "" : (extension.substring(0, 1).equalsIgnoreCase(".")) ? extension : "." + extension;
        this.isDeleteOnExit = isDeleteOnExit;
    }

    /**
     * Create tree directory.
     */
    public void create4rchitecture() {
        new File(strBuilder.toString()).mkdir();
        create4rchitecture(nbrElementX, nbrElementY);
        strBuilder.setLength(treeRootPathLength);
    }

    private void create4rchitecture( int numXTiles, int numYTiles) {
        if (numXTiles <= 2 && numYTiles <= 2) {
            return;
        }
        final int pathLength = strBuilder.length();
        final int nxt = (numXTiles + 1) / 2;
        final int nyt = (numYTiles + 1) / 2;

        if (numXTiles <= 2) {
            //cut in height
            //create 2 directories
            strBuilder.append(D00);
            createDirectory();
            create4rchitecture(numXTiles, nyt);

            strBuilder.setLength(pathLength);

            strBuilder.append(D01);
            createDirectory();
            create4rchitecture(numXTiles, numYTiles-nyt);
            strBuilder.setLength(pathLength);

        } else if (numYTiles <= 2) {

            //cut in width
            //create 2 directories
            strBuilder.append(D00);
            createDirectory();
            create4rchitecture(nxt, numYTiles);

            strBuilder.setLength(pathLength);

            strBuilder.append(D10);
            createDirectory();
            create4rchitecture(numXTiles-nxt, numYTiles);

            strBuilder.setLength(pathLength);

        } else {

            //create 4 directories
            strBuilder.append(D00);
            createDirectory();
            create4rchitecture(nxt, nyt);

            strBuilder.setLength(pathLength);

            strBuilder.append(D10);
            createDirectory();
            create4rchitecture(numXTiles-nxt, nyt);

            strBuilder.setLength(pathLength);

            strBuilder.append(D01);
            createDirectory();
            create4rchitecture(nxt, numYTiles-nyt);

            strBuilder.setLength(pathLength);

            strBuilder.append(D11);
            createDirectory();
            create4rchitecture(numXTiles-nxt, numYTiles-nyt);

            strBuilder.setLength(pathLength);
        }
    }

    /**
     * Create directory at place define by StringBuilder path.
     */
    private void createDirectory() {
        final File file = new File(strBuilder.toString());
        file.mkdir();
        if (isDeleteOnExit) file.deleteOnExit();
    }

    /**
     * Return appropriate path of element at X, Y position.
     *
     * @param x x direction element position.
     * @param y y direction element position.
     * @return appropriate path of element at X, Y position.
     */
    public String getPath(int x, int y) {
        final String path = getPath(strBuilder, 0, 0, nbrElementX-1, nbrElementY-1, x, y);
        strBuilder.setLength(treeRootPathLength);
        return path;
    }

    private String getPath(StringBuilder path, int mintx, int minty, int maxtx, int maxty, int tileX, int tileY) {
        dx = maxtx-mintx;
        dy = maxty-minty;
        if (dx <= 1 && dy <= 1) {
            path.append("/");
            path.append(tileX);
            path.append("_");
            path.append(tileY);
            path.append(extension);
            return (path.toString());
        }
        demx = mintx + dx / 2 + 1;
        demy = minty + dy / 2 + 1;

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
        throw new IllegalStateException("undefine path");
    }

    private boolean intersect(int minx, int miny, int maxx, int maxy, int tx, int ty){
        final boolean x = ((tx >= minx) && (tx <= maxx));
        final boolean y = ((ty >= miny) && (ty <= maxy));
        return x && y;
    }
}
