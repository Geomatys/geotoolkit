/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.math;

import javax.vecmath.MismatchedSizeException;

/**
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Johann Sorel, portage geotoolkit (Geomatys)
 */
public interface Extrapolation {

    /**
     * Définies les vecteurs <var>x</var>
     * et <var>y</var> de cette table.
     * @throws MismatchedSizeException si
     *  les deux vecteurs n'ont pas la
     *  même longueur.
     */
    void setData(double[] x, double[] y) throws MismatchedSizeException;

    /**
     * Oublie toutes références vers les données. Vous pouvez appeller cette méthode lorsque
     * vous en avez terminé avec les interpolations et que vous voulez laisser le nettoyeur
     * faire son travail.
     */
    void clear();

    /**
     * Signale à la table que des données du vecteurs des <var>x</var> ou du vecteur des
     * <var>y</var> ont été changées. Certains types d'interpolations telle que l'interpolation
     * cubique B-Spline ont besoin de savoir s'ils doivent recalculer leurs champs internes.
     * L'implémentation par défaut ne fait rien.
     */
    void recompute();

    /**
     * Indique si les interpolations devront ignorer les NaN dans le vecteur des <var>y</var>.
     * Par défaut, les NaN ne seront pas ignorés de sorte qu'ils pourront être retournés par la
     * méthode {@link #interpole(double)} s'ils apparaissent dans le vecteur des <var>y</var>.
     * Si vous demandez à ignorer les NaN, alors la méthode <code>interpole</code> tentera de
     * toujours utiliser des données valides pour ses calculs. Cette méthode n'a aucun effet
     * sur les vecteur des <var>x</var>. Pour ce dernier, les NaN seront toujours ignorés.
     *
     * @param <code>true</code> si la méthode {@link #interpolate} doit agir comme si elle
     *        utilisait des copies des données dans lesquelles on avait retiré tout les NaN.
     *
     * @return l'ancien état.
     */
    boolean ignoreNaN(boolean ignore);

    /**
     * Renvoie la valeur <var>yi</var> interpolé au <var>xi</var> spécifié. Le type d'interpolation
     * effectué dépendra de la classe de cet objet. Par exemple la classe <code>Spline1D</code>
     * effectuera une interpolation cubique B-Spline.
     *
     * @param xi valeur de <var>x</var> pour laquelle on veut interpoler un <var>y</var>.
     * @throws ExtrapolationException si une extrapolation non-permise a eu lieu.
     */
    double interpolate(double xi) throws ExtrapolationException;

    /**
     * Renvoie la valeur <var>y</var> interpolé à <code>x[index]</code> mais sans utiliser
     * la valeur de <code>y[index]</code>. Cette méthode est utile pour boucher les trous
     * causé par les données manquantes (NaN). Cette méthode est plus rapide que
     * <code>interpole</code> car elle ne nécessite pas que l'on recherche la position
     * de <var>xi</var> dans le vecteur des <var>x</var>.<p>
     *
     * Vous pouvez aussi utiliser cette méthode pour interpoler des pics isolés qui vous
     * semble suspects. Toutefois s'il y a une possibilité que deux pics soient collés,
     * il est préférable de remplacer tous les pics par des NaN et ensuite d'utiliser
     * cette méthode pour combler les trous.
     *
     * @param index index du <var>x</var> pour lequel on veut interpoler un <var>y</var>.
     * @throws ExtrapolationException si une extrapolation non-permise a eu lieu.
     */
    double interpolateAt(int index) throws ExtrapolationException;

}
