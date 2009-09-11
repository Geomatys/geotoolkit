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

/**
 * Exception lancée lorsqu'une extrapolation a
 * été effectuée alors que ce n'était pas permis.
 *
 * @author Martin Desruisseaux
 * @version 1.0
 */
public class ExtrapolationException extends Exception {

    /**
     * Valeur <var>x</var> pour laquelle une interpolation avait été demandée.
     */
    public double xi = Double.NaN;
    /**
     * Indique la raison pour laquelle il y a eu une extrapolation. La donnée
     * demandée peut être en dessous ou au dessus de la plage des <var>x</var>.
     * Ce champs contiendra typiquement une des valeurs suivantes:<p>
     *
     *		-1	Indique que le <var>xi</var> demandé précède les données du vecteur des.
     *			<var>x</var>. Ce serait le cas par exemple si on demandait <var>xi</var>=8
     *			alors que le vecteur des <var>x</var> contient [9 12 13 16] ou [6 4 2 1].
     *
     *		 0	Cette information n'est pas disponible.
     *
     *		+1	Indique que le <var>xi</var> demandé suit les données du vecteur des
     *			<var>x</var>. Ce serait le cas par exemple si on demandait <var>xi</var>=8
     *			alors que le vecteur des <var>x</var> contient [1 2 4 6] ou [16 13 12 9].
     */
    public byte raison = 0;
    /**
     * Index d'une donnée valide. Si <code>raison</code> est négatif, alors cet index sera celui
     * de la première donnée valide du vecteur des <var>x</var>.  Si <code>raison</code> est positif,
     * alors cet index sera celui de la dernière donnée valide du vecteur des <var>x</var>. Si cette
     * information ne s'applique pas, cet index sera -1.
     */
    public int index = -1;

    /**
     * Construit une exception déclarant que le vecteur
     * des <var>x</var> ne contient pas suffisament de données.
     */
    public ExtrapolationException() {
        super("Le vecteur des X ne contient pas suffisament de données valides."); // TODO: localize
    }

    /**
     * Construit une exception déclarant que le vecteur des <var>x</var> ne contient pas
     * suffisament de données ou que la donnée <var>xi</var> demandée n'est pas valide,
     * si celle-ci est un NaN.
     *
     * @param xi valeur de <var>x</var> pour laquelle on voulait interpoler un <var>y</var>.
     */
    public ExtrapolationException(double xi) {
        super(Double.isNaN(xi) ? "Je ne peux pas interpoler à x=NaN." : "Le vecteur des X ne contient pas suffisament de données valides."); // TODO: Localize
        this.xi = xi;
    }

    /**
     *	Construit un objet déclarant qu'une extrapolation a eu lieu.
     *
     * @param raison Raison de l'extrapolation (-1, 0 ou +1).
     *        Voyez la description de {@link #raison}.
     */
    public ExtrapolationException(int raison) {
        super("La donnée demandée est en dehors de la plage de valeurs du vecteur des X."); // TODO: Localize
        this.raison = (byte) raison;
    }

    /**
     *	Construit une exception déclarant qu'une extrapolation a eu lieu.
     *
     * @param raison	Raison de l'extrapolation (-1, 0 ou +1).
     *					Voyez la description de {@link #raison}.
     * @param index		index d'une donnée valide.
     */
    public ExtrapolationException(int raison, int index) {
        this(raison);
        this.index = index;
    }

    /**
     *	Renvoie un message décrivant l'erreur.
     */
    @Override
    public String getMessage() {
        String message = super.getMessage();
        return message + " (" + xi + ')'; // TODO: Localize
    }
}
