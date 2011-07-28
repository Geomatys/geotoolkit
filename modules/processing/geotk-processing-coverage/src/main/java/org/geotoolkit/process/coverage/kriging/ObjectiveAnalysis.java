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
package org.geotoolkit.process.coverage.kriging;

import com.vividsolutions.jts.geom.Coordinate;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.GVector;
import javax.vecmath.GMatrix;
import javax.vecmath.Point3d;
import org.geotoolkit.math.Plane;

/**
 * <p align=justify>Classe ayant la charge d'interpoller sur une grille régulière des
 * points disparatres. La méthode utilisée pour cette interpolation est appellée (en
 * anglais) <em>Objective Analysis</em>.
 *
 * @version 1.1
 * @author Martin Desruisseaux
 * @author Howard Freeland
 * @author Johann Sorel (adaptation isoligne et mise a jour sur geotoolkit)
 * @module pending
 */
public class ObjectiveAnalysis {

    private static final double EPS = 1E-8;

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
     * Construit un objet qui interpollera des points
     * sur une grille réguliére dans une certaine région.
     *
     * @param region Coordonnées de la région dans laquelle
     *        on voudra interpoller des points.
     * @param size Nombre de points à interpoller
     *        horizontalement et verticalement.
     */
    public ObjectiveAnalysis(final Rectangle2D region, final Dimension size) {
        if (!region.isEmpty()) {
            if (size.width > 1 && size.height > 1) {
                nx = size.width;
                ny = size.height;
                xmin = region.getX();
                ymin = region.getY();
                dx = region.getWidth() / (nx - 1);
                dy = region.getHeight() / (ny - 1);
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
        distance = ((distance / 1000) - 1) / 150; // Similar to the basic program DISPWX
        if (distance < 0) {
            return 1 - 15 * distance;
        }
        return Math.exp(-distance * distance);
    }

    /**
     * Create a contour plot of this grid. This method work only for regular
     * gridded data set. Randomly distributed data set are transformed into
     * regular gridded data set by the {@link #computeGrid} method, which is
     * automatically called when needed.<p>
     *
     * The contouring result will be stored in the {@link #contour} field. If
     * this field was null, a new {@link ca.dfo.map.Bathymetry} object will be
     * created. Subclass may override this method to perform the contouring in an
     * other way, or to create a different instance of <code>Bathymetry</code> if
     * the <code>bathymetry</code> field was null.
     *
     * @param xp The projected <var>x</var> coordinates.
     * @param yp The projected <var>y</var> coordinates.
     * @param zp The <var>z</var> values of a regular grid <var>z(x,y)</var>.
     * @throws IllegalStateException If the grid is not regular. This exception
     *         may be throws if someone override {@link #computeGrid} and failed
     *         to provided a regular grid.
     */
    public Map<Point3d,List<Coordinate>> doContouring(final double[] xp, final double[] yp, final double[] zp, final double[] lls) throws IllegalStateException {
        final Map<Point3d,List<Coordinate>> cellMap = new HashMap<Point3d,List<Coordinate>>();

        if (xp == null || yp == null || zp == null) return cellMap;

        final int xlength = xp.length;
        final int ylength = yp.length;
        final double x[] = new double[4];
        final double y[] = new double[4];
        final double z[] = new double[4];
        final Coordinate toMerge[] = new Coordinate[2];

        for (int i=1; i<xlength; i++) {
            /*
             * Obtient les coordonnées des quatre coins    [0]...[3]
             * de la cellule à tracer. Les coordonnées      :     :
             * seront mémorisés aux index suivants:        [1]...[2]
             */
            x[0] = x[1] = xp[i - 1];
            x[2] = x[3] = xp[i];

            for (int j=1; j<ylength; j++) {
                y[0] = y[3] = yp[j - 1];
                y[1] = y[2] = yp[j];

                z[0] = zp[(i - 1) + (j - 1) * xlength];
                z[1] = zp[(i - 1) + j * xlength];
                z[3] = zp[i + (j - 1) * xlength];
                z[2] = zp[i + j * xlength];

                /*
                 * Obtient les valeurs extrémes de z: zmin et zmax.
                 * Par la suite, on balayera les valeurs zl de toutes
                 * les courbes de niveaux qui passent entre ces deux
                 * bornes. Par exemple si zmin=8.76 et zmax=11.35,
                 * alors on balayera (par défaut) zl=9, 10 et 11.
                 */
                double zmin = Double.POSITIVE_INFINITY;
                double zmax = Double.NEGATIVE_INFINITY;
                for (int k=0; k<z.length; k++) {
                    if (z[k] < zmin) zmin = z[k];
                    if (z[k] > zmax) zmax = z[k];
                }

                for(double zl : lls){
                    if(zl > zmax || zl < zmin){
                        // la cellule ne contient pas ce niveau
                        continue;
                    }

                    final boolean isDone[] = new boolean[4];
                    for (int k=0; k<isDone.length; k++) isDone[k] = false;
                    
                    while (true) {
                        int kmin = -1;
                        int mmin = -1;
                        double px0 = Double.NaN;
                        double py0 = Double.NaN;
                        double px1 = Double.NaN;
                        double py1 = Double.NaN;
                        double d2min = Double.POSITIVE_INFINITY;
                        /*
                         * Pour chacun des côtés de la cellule, trouve le point
                         * d'intersection entre la courbe de niveau et le côté
                         * de la cellule. Seuls les côtés qui n'ont pas déjà été
                         * traités lors d'un passage précèdent (!isDone) seront
                         * examinés.
                         */
                        for (int k0=0; k0<z.length; k0++) {
                            if (!isDone[k0]) {
                                final int k1 = (k0+1) % z.length;
                                if ((zl >= z[k0] && zl <= z[k1]) || (zl >= z[k1] && zl <= z[k0])) {
                                    //Ce coté contient la valeur de la courbe de niveau
                                    double tmp;
                                    if (z[k1] == z[k0]) {
                                        tmp = Math.pow(x[k0]-x[k1], 2) + Math.pow(y[k0]-y[k1], 2);
                                        if (tmp < d2min) {
                                            d2min = tmp;
                                            kmin = k0;
                                            mmin = k0;
                                            px0 = x[k0];
                                            px1 = x[k1];
                                            py0 = y[k0];
                                            py1 = y[k1];
                                        }
                                    } else {
                                        tmp = (zl-z[k0]) / (z[k1]-z[k0]);
                                        final double tx0 = x[k0] + tmp * (x[k1] - x[k0]);
                                        final double ty0 = y[k0] + tmp * (y[k1] - y[k0]);
                                        /*
                                         * (tx0,ty0) contient le point d'intersection de la courbe zl
                                         * avec le côté k0. On recherche maintenant un point
                                         * d'intersection avec un autre côté. La paire de points
                                         * (tx0,ty0) - (tx1,ty1) qui donnent la distance la plus courte
                                         * sera retenue comme la paire à tracer.
                                         */
                                        for (int m0 = 0; m0 < z.length; m0++) {
                                            if (m0 != k0 && !isDone[m0]) {
                                                final int m1 = (m0 + 1) % z.length;
                                                if ((zl >= z[m0] && zl <= z[m1]) || (zl >= z[m1] && zl <= z[m0])) {
                                                    tmp = (zl - z[m0]) / (z[m1] - z[m0]);
                                                    final double tx1 = x[m0] + tmp * (x[m1] - x[m0]);
                                                    final double ty1 = y[m0] + tmp * (y[m1] - y[m0]);
                                                    tmp = Math.pow(tx0 - tx1, 2) + Math.pow(ty0 - ty1, 2);
                                                    if (tmp < d2min) {
                                                        d2min = tmp;
                                                        kmin = k0;
                                                        mmin = m0;
                                                        px0 = tx0;
                                                        py0 = ty0;
                                                        px1 = tx1;
                                                        py1 = ty1;
                                                    }
                                                } else {
                                                    isDone[m0] = true;
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    isDone[k0] = true;
                                }
                            }
                        }
                        /*
                         * Maintenant que l'on dispose des coordonnées d'une ligne
                         * droite, ajoute ces coordonnées à la "bathymetrie" que
                         * l'on est en train de construire. Si aucune coordonnées
                         * n'a été trouvé, c'est qu'on en a fini avec cette cellule
                         * pour ce niveau. On passera alors au niveau suivant.
                         */
                        if (kmin >= 0 && mmin >= 0) {
                            isDone[kmin] = true;
                            isDone[mmin] = true;
                            toMerge[0] = new Coordinate(px0, py0);
                            toMerge[1] = new Coordinate(px1, py1);

                            /*
                             * Vérifie si les points d'intersections (px0,py0) et/ou (px1,py1) ont
                             * déjà été trouvés avant. Si c'est le cas, le segment de ligne (px0,py0)
                             * (px1,py1) devra venir se rattacher aux segments déjà existants. Sinon,
                             * on créera une nouvelle ligne à laquelle viendra peut-être s'en rattacher
                             * d'autres plus tard. Note: La présence du (float) est une façon paresseuse
                             * d'arrondir les nombre de façon à éviter certaines erreurs d'arrondissements.
                             */
                            final Point3d P0 = new Point3d((float) px0, (float) py0, zl);
                            final Point3d P1 = new Point3d((float) px1, (float) py1, zl);
                            final List<Coordinate> I0 = cellMap.remove(P0);
                            final List<Coordinate> I1 = cellMap.remove(P1);
                            /*
                             * Si aucun des points d'intersections n'avaient été trouvés avant,
                             * créé un nouvel {@link Polygon}. Sinon, procéde à des fusions...
                             */
                            if (I0 == null && I1 == null) {
                                final List<Coordinate> polylines = new ArrayList<Coordinate>();
                                polylines.add(toMerge[0]);
                                polylines.add(toMerge[1]);
                                cellMap.put(P0, polylines);
                                cellMap.put(P1, polylines);

                            }else if (I0 == null && I1 != null) {
                                merge(toMerge, I1);
                                cellMap.put(P0, I1);

                            }else if (I0 != null && I1 == null) {
                                merge(toMerge, I0);
                                cellMap.put(P1, I0);

                            }else if (I0 != null && I1 != null) {

                                if(!merge(toMerge, I1)){
                                    merge(toMerge,I0);
                                }

                                if (I0 != I1) {
                                    merge(I1, I0);

                                    /*
                                     * Puisque I1 a été fusionné à I0 et qu'on ne gardera plus
                                     * que I0, il faut maintenant changer les références vers
                                     * I1 en référence vers I0. Il ne reste normalement qu'une
                                     * référence vers I1 et une référence vers I0. On le vérifiera
                                     * pour s'assurer que l'algorithme n'a pas de bug.
                                     */
                                    int checkRefCountI0 = 0;
                                    int checkRefCountI1 = 0;
                                    final Iterator<Entry<Point3d,List<Coordinate>>> it = cellMap.entrySet().iterator();
                                    if (it != null) {
                                        while (it.hasNext()) {
                                            final Entry<Point3d,List<Coordinate>> entrie = it.next();
                                            final Object I = entrie.getValue();
                                            if (I == I0) {
                                                checkRefCountI0++;
                                            }
                                            if (I == I1) {
                                                entrie.setValue(I0);
                                                checkRefCountI1++;
                                            }
                                        }
                                    }
                                    /*
                                     *@TODO maybe this piece of code should be rewrited
                                     * See issue GEOTK-196 on http://dev.geomatys.com for more informations
                                     */
//                                    if (checkRefCountI0 != 1) {
//                                        throw new IllegalStateException("should not happen");
//                                    }
//                                    if (checkRefCountI1 != 1) {
//                                        throw new IllegalStateException("should not happen");
//                                    }
                                } else {
                                    /*
                                     * Si on vient de refermer une cle (I0==I1), alors il ne
                                     * reste plus de référence vers celle-ci. On en créera une
                                     * avec un point bidon, choisie de façon à être innacessible
                                     * par le reste de cette méthode.
                                     */
                                    P0.add(P1);
                                    P0.scale(0.5);
                                    P0.z = zl;
                                    cellMap.put(P0, I0);
                                }
                            }
                        } else {
                            break;
                        }
                    }

                }
            }
        }
        

        return cellMap;

    }

    private boolean merge(final Coordinate[] toMerge,final List<Coordinate> coords){
        Coordinate coord0 = toMerge[0];
        Coordinate coord1 = toMerge[1];

        Coordinate startCoord = coords.get(0);
        Coordinate endCoord = coords.get(coords.size()-1);

        if(equalCoordinates(startCoord, coord0)){
            //add at the begining
            coords.add(0,coord1);
        }else if(equalCoordinates(startCoord, coord1)){
            //add at the begining
            coords.add(0,coord0);
        }else if(equalCoordinates(endCoord, coord0)){
            //add at the end
            coords.add(coord1);
        }else if(equalCoordinates(endCoord, coord1)){
            //add at the end   
            coords.add(coord0);
        }else{
            return false;
        }

        return true;
    }

    private boolean merge(final List<Coordinate> toMerge,final List<Coordinate> coords){
        Coordinate coord0 = toMerge.get(0);
        Coordinate coord1 = toMerge.get(toMerge.size()-1);

        Coordinate startCoord = coords.get(0);
        Coordinate endCoord = coords.get(coords.size()-1);

        if(equalCoordinates(startCoord, coord0)){
            //add at the begining
            toMerge.remove(0);
            Collections.reverse(toMerge);
            coords.addAll(0,toMerge);
        }else if(equalCoordinates(startCoord, coord1)){
            //add at the begining
            toMerge.remove(toMerge.size()-1);
            coords.addAll(0,toMerge);
        }else if(equalCoordinates(endCoord, coord0)){
            //add at the end
            toMerge.remove(0);
            coords.addAll(toMerge);
        }else if(equalCoordinates(endCoord, coord1)){
            //add at the end
            toMerge.remove(toMerge.size()-1);
            Collections.reverse(toMerge);
            coords.addAll(toMerge);
        }else{
            return false;
        }

        return true;
    }

    private boolean equalCoordinates(final Coordinate coord0, final Coordinate coord1){

        //test x values
        if (Math.abs(coord0.x - coord1.x) > EPS * Math.max(Math.abs(coord0.x), Math.abs(coord1.x))) {
            return false;
        }
        if (Math.abs(coord0.y - coord1.y) > EPS * Math.max(Math.abs(coord0.y), Math.abs(coord1.y))) {
            return false;
        }

        return true;
    }

    public static void main(final String[] args) {

        if (true) {
            final int s = 5;
            final double[] x = new double[5];
            final double[] y = new double[5];
            final double[] z = new double[5];

            x[0] = 0;
            x[1] = 0;
            x[2] = 10;
            x[3] = 20;
            x[4] = 20;

            y[0] = 0;
            y[1] = 20;
            y[2] = 10;
            y[3] = 0;
            y[4] = 20;

            z[0] = 1;
            z[1] = 2;
            z[2] = 50;
            z[3] = 3;
            z[4] = 4;

            final ObjectiveAnalysis ob = new ObjectiveAnalysis(new Rectangle(0, 0, 20, 20), new Dimension(s, s));
            Logger.getLogger(ObjectiveAnalysis.class.getName()).log(Level.INFO, "dx=" + ob.dx + "   dy=" + ob.dy);
            final double[] computed = ob.interpole(x, y, z);
            final double[] cx = ob.getXs();
            final double[] cy = ob.getYs();

            final Map<Point3d,List<Coordinate>> steps = ob.doContouring(cx, cy, computed, new double[]{-10,10,20,30,40,50});
            final List<Shape> shapes = new ArrayList<Shape>();
            for(final Point3d p : steps.keySet()){
                
                final List<Coordinate> coords = steps.get(p);

                GeneralPath isoline = null;
                for(final Coordinate coord : coords){
                    if(isoline == null){
                        isoline = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
                        isoline.moveTo(coord.x*10, coord.y*10);
                    }else{
                        isoline.lineTo(coord.x*10, coord.y*10);
                    }
                }

                shapes.add(isoline);
            }

            JFrame frm = new JFrame();
            frm.setContentPane(new JPanel(){

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);

                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(Color.BLACK);
                    for(Shape shape : shapes){
                        g2.draw(shape);
                    }

                }

            });

            frm.setSize(800, 600);
            frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frm.setLocationRelativeTo(null);
            frm.setVisible(true);

        }
    }
}
