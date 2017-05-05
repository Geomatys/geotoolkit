/*
 * Map and oceanographical data visualisation
 * Copyright (C) 1999 Pêches et Océans Canada
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Library General Public
 *    License as published by the Free Software Foundation; either
 *    version 2 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Library General Public License for more details (http://www.gnu.org/).
 *
 *
 * Contact: Observatoire du Saint-Laurent
 *          Institut Maurice Lamontagne
 *          850 de la Mer, C.P. 1000
 *          Mont-Joli (Québec)
 *          G5H 3Z4
 *          Canada
 *
 *          mailto:osl@osl.gc.ca
 */
package org.geotoolkit.processing.coverage.kriging;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import javax.vecmath.GMatrix;
import javax.vecmath.GVector;
import org.apache.sis.math.Plane;


/**
 *
 * @author rmarech
 *
 * @deprecated Seems a duplicated of {@link org.geotoolkit.math.ObjectiveAnalysis}?
 */
@Deprecated
public class IsolineGrid {
    /**
     * Valeur <var>x</var> minimale de la région
     * dans laquelle on veut interpoler des points.
     */
    private final double xmin;

    /**
     * Valeur <var>y</var> minimale de la région
     * dans laquelle on veut interpoler des points.
     */
    private final double ymin;

    /**
     * Pas selon <var>x</var> des positions pour
     * lesquelles on veut interpoler des points.
     */
    private final double dx;

    /**
     * Pas selon <var>y</var> des positions pour
     * lesquelles on veut interpoler des points.
     */
    private final double dy;

    /**
     * Nombre de colonnes
     * interpoller des points.
     */
    private final int nx;

    /**
     * Nombre de lignes
     * interpoller des points.
     */
    private final int ny;

    /**
     * An arbitrary scale factor applied in the distance computed by {@link #correlation(double)}.
     * This is a hack for allowing the code to work with different CRS. Do not relyslt alex tu on this hack,
     * it may be suppressed in future versions.
     */
    private double scale = 1;

    public IsolineGrid(final Rectangle2D region, final Dimension size) {
        if (!region.isEmpty()) {
            if (size.width > 1 && size.height > 1) {
                nx = size.width;
                ny = size.height;
                xmin = region.getX();
                ymin = region.getY();
                dx = region.getWidth() / (nx - 1);//le pas
                dy = region.getHeight() / (ny - 1);//le pas
            } else {
                throw new IllegalArgumentException("Illegal size");
            }
        } else {
            throw new IllegalArgumentException("Rectangle can't be empty");
        }
    }

    /**
     * Retourne le nombre de points qui seront interpollés. La méthode
     * {@link #interpole interpole(...)} retournera un tableau de cette
     * longueur.
     */
    public int getLength() {
        return nx * ny;
    }

    /**
     * Retourne la coordonnée <var>x</var> d'un point interpollé. Cette méthode reçoit
     * en paramètre l'index d'un élément du tableau retourné par {@link #interpole
     * interpole(...)}. Le résultat de cette méthode est indéterminé si l'index
     * n'est pas compris dans la plage <code>[0...{link #getLength})</code>.
     */
    public double getX(final int index) {
        return xmin + dx * (index % ny);
    }

    /**
     * Retourne la coordonnée <var>y</var> d'un point interpollé. Cette méthode reçoit
     * en paramétre l'index d'un élément du tableau retourné par {@link #interpole
     * interpole(...)}. Le résultat de cette méthode est indéterminé si l'index
     * n'est pas compris dans la plage <code>[0...{link #getLength})</code>.
     */
    public double getY(final int index) {
        return ymin + dy * (index / ny);
    }

    public double[] getXs(){
        double[] xs = new double[nx];

        for(int n=0; n<nx; n++){
            xs[n] = getX(n);
        }

        return xs;
    }

    public double[] getYs(){
        double[] ys = new double[ny];

        for(int n=0; n<nx; n++){
            ys[n] = getY(n*nx);
        }

        return ys;
    }

    /**
     * Utilise des points disparatres pour interpoller des valeurs à d'autres
     * positions. Cette méthode est utilisée le plus souvent pour interpoller
     * sur une grille régulière des valeurs qui proviennent de points distribués
     * aléatoirement.
     * <p>
     * La sortie de cette méthode est un tableau <code>double[]</code>. Pour
     * chaque élèment à l'index <var>i</var> de ce tableau, les coordonnées
     * peuvent être obtenues par <code>{@link #getX getX}(<var>i</var>)</code>
     * et <code>{@link #getY getY}(<var>i</var>)</code>. En d'autres mots, la
     * sortie de cette méthodes peut être utilisée comme suit:
     *
     * <pre>
     * final double[] vals=<strong>interpole</strong>(xVector, yVector, zVector);
     * for (int i=0; i<values.length; i++)
     * {
     *     final double x={@link #getX getX}(i);
     *     final double y={@link #getY getY}(i);
     *     final double z=vals[i];
     *     // ... put here code to process (x,y,z) ...
     * }
     * </pre>
     *
     * Par défaut, les méthodes {@link #getX} et {@link #getY} répartissent les
     * points sur une grille régulière dans laquelle les index des <var>x</var>
     * varient les plus vite, suivit des <var>y</var>. Les classes dérivées
     * pourraient toutefois redéfinir les méthodes {@link #getX}, {@link #getY}
     * et {@link #getLength} pour obtenir une autre distribution des points.
     *
     * @param xp Vecteur des coordonnées <var>x</var> des points.
     * @param yp Vecteur des coordonnées <var>y</var> des points.
     * @param zp Vecteur des valeurs <var>z</var> aux points (<var>x</var>,<var>y</var>).
     * @return Tableau de valeurs des points interpollés. Ce tableau aurait la longueur
     *         retournée par {@link #getLength}.
     */
    public double[] interpole(final double[] xp, final double[] yp, final double[] zp) {
        /*
         * Compute a regression plane P of Z(x,y). The object P
         * will contains internaly the plane's coefficients.
         */
        final Plane P = new Plane();
        P.fit(xp, yp, zp);
        /*
         * Create a matrix A(N,N) where N is the number of input data.
         * Note: the object 'GMatrix' is provided with Java3D.
         */
        final int N = xp.length;
        GMatrix A = new GMatrix(N, N);
        GVector X = new GVector(N);
        /*
         * Set the matrix elements. The square part A(i,j) is
         * the matrix of correlations among observations.
         */
        for (int i = 0; i < N; i++) {
            final double xi = xp[i];
            final double yi = yp[i];
            for (int j = 0; j < N; j++) {
                final double dx = xi - xp[j];
                final double dy = yi - yp[j];
                A.setElement(i, j, correlation(Math.sqrt(dx * dx + dy * dy)));
            }
            X.setElement(i, zp[i] - P.z(xi, yi));
        }
        /*
         * Invert the matrix, then multiply A by X.
         * This code compute in fact Y = A^-1 * X.
         * The result matrix is stored into A.
         */
        A.invert(); // A = A^-1
        X.mul(A, X); // X = A*X
        A = null;   // lets GC do his work
        /*
         * Now compute values.
         */
        final double values[] = new double[getLength()];
        for (int i = 0; i < values.length; i++) {
            final double xi = getX(i);
            final double yi = getY(i);
            double value = P.z(xi, yi);
            for (int k = 0; k < N; k++) {
                final double dx = xi - xp[k];
                final double dy = yi - yp[k];
                value += X.getElement(k) * correlation(Math.sqrt(dx * dx + dy * dy));
            }
            values[i] = value;
        }
        return values;
    }

    /**
     * Retourne la corrélation entre deux stations
     * espacées d'une certaine distance en mètres.
     * L'implémentation par défaut suppose que la
     * corrélation est gaussienne. Des classes
     * dérivées pourraient redéfinir cette méthode
     * pour utiliser une autre fonction de corrélation.<p>
     *
     * <strong>NOTE:</strong> THIS METHOD WILL CHANGE IN THE FUTURE.
     *
     * @param distance Distance en métres entre deux stations.
     * @return Un coéffcient de corrélation entre 0 et 1.
     */
    protected double correlation(double distance) {
        distance *= scale;
        distance = ((distance / 1000) - 1) / 150; // Similar to the basic program DISPWX
        if (distance < 0) {
            return 1 - 15 * distance;
        }
        return Math.exp(-distance * distance);
    }
}
