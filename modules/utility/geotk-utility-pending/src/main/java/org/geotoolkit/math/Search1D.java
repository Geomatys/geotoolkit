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
 * Table sans interpolation. L'appel de la méthode {@link #interpolate} de cette classe se contente
 * de retourner la valeur <var>y</var> correspondant au <var>x</var> le plus proche de celui qui a été
 * demandé (<var>xi</var>). Cette classe offre des outils de recherche qui seront utilisées par les
 * classes dérivées pour implémenter des interpolations.
 *
 * @version 1.0
 * @author Martin Desruisseaux
 * @module pending
 */
public class Search1D extends Table1D {

    /**
     * Position de la dernière donnée trouvée, de sorte que <var>xi</var>
     * est entre <code>x[klo]</code> et <code>x[khi]</code>.
     */
    protected int klo,  khi;

    @Override
    public void clear() {
        super.clear();
        klo = 0;
        khi = 0;
    }

    /**
     * Renvoie la valeur de <var>y</var> interpolée au <var>x</var> spécifié.
     * Les NaN apparaissant dans le vecteur des <var>x</var> seront ignorés.
     * Ceux qui apparaissent dans le vecteur des <var>y</var> seront aussi
     * ignorés si {@link Table1D#ignoreNaN} a été appellée avec l'argument
     * <code>true</code>.
     *
     * @param xi valeur de <var>x</var> pour laquelle on désire une valeur <var>y</var>.
     * @return valeur interpolée.
     * @throws ExtrapolationException si une extrapolation non-permise a eu lieu.
     */
    @Override
    public final double interpolate(final double xi) throws ExtrapolationException {
        if (locate(xi)) {
            return interpolate(xi, false);
        } else {
            throw new ExtrapolationException(xi);
        }
    }

    /**
     * Renvoie la valeur de <var>y</var> interpolée au <var>x</var> qui se trouve à l'index
     * spécifié. L'interpolation fera intervenir les valeurs autour de <code>y[index]</code>
     * sans utiliser <code>y[index]</code> lui-même. Les NaN apparaissant dans le vecteur des
     * <var>x</var> seront ignorés. Ceux qui apparaissent dans le vecteur des <var>y</var>
     * seront aussi ignorés si {@link Table1D#ignoreNaN} a été appellée avec l'argument
     * <code>true</code>.
     *
     * @param index de la donnée <var>x</var> pour laquelle on veut interpoller <var>y</var>.
     * @return valeur interpolée.
     * @throws ExtrapolationException si une extrapolation non-permise a eu lieu.
     */
    @Override
    public final double interpolateAt(final int index) throws ExtrapolationException {
        locateAt(index);
        return interpolate(x[index], false);
    }

    /**
     * Interpole les NaN trouvés dans le vecteur des <var>y</var>. Les arguments de cette méthode
     * contrôle la façon de décider si des NaN doivent être interpolés ou non. Trois conditions
     * s'appliquent pour qu'une interpolations puissent se faire:
     *
     *<ol><li>
     *  La donnée <var>x</var> correspondant au <var>y</var> à interpoler ne doit pas être NaN.
     *</li><li>
     *  S'il y a des NaN qui se suivent, ils ne doivent pas former un trou plus grand que
     *  <code>dxStop</code>. Par exemple si le vecteur des <var>x</var> représente le temps
     *  exprimé en heure et que le vecteur des <var>y</var> représente la température, alors
     *  la valeur 4.5 pour l'argument <code>dxStop</code> indiquera à cette méthode qu'elle
     *  ne doit pas interpoler les températures à l'intérieur des trous de plus de 4œ heures.
     *  Dans le cas de données échantillonées aux demi-heures, cela signifie que s'il y a plus
     *  de 9 NaN consécutifs, ceux-ci ne seront pas interpolés.
     *</li><li>
     *  De chaque côté des NaN, il doit y avoir des données valides représentant une plage
     *  d'au moins <code>dxStart</code>. Par exemple si le vecteur des <var>x</var> représente
     *  le temps exprimé en heure et que le vecteur des <var>y</var> représente la salinité, alors
     *  la valeur 3 pour l'argument <code>dxStart</code> indiquera à cette méthode qu'elle ne peut
     *  interpoler la salinité qu'à la condition qu'il y aie au moins 3 heures de données valides
     *  de part et d'autres de la donnée manquante (NaN).
     *</li></ol>
     *
     * @param dxStart	Plage minimal des <var>x</var> qu'il doit y avoir de chaque côté d'un NaN
     *					pour l'interpoler. Spécifiez 0 si vous ne voulez pas imposer de minimum.
     *
     * @param dxStop	Plage maximal des <var>x</var> couvert par les données manquantes pour qu'elles
     *					puissent être interpolées. Spécifiez <code>Float.POSITIVE_INFINITY</code> pour
     *					ne pas imposer de taille maximale (déconseillé!).
     *
     * @param yi		Tableau dans lequel copier tout les <var>y</var> en remplaçant les NaN par
     *					les valeurs interpolés. Ce tableau ne doit pas être plus long que les tableaux
     *					<var>x</var> et <var>y</var> spécifiés lors de la construction de la table. Il
     *					peut toutefois être plus court. Dans ce cas les interpolations ne se feront
     *					que sur les <code>yi.length</code> premières données. Si vous spécifiez
     *					<code>null</code>, un tableau sera créé automatiquement avec la longueur
     *					nécessaire pour contenir toutes les données.
     *
     * @return			Le tableau <var>yi</var>. Si <var>yi</var> était nul, alors le résultat
     *					sera soit un tableau nouvellement construit si des données ont été interpolées,
     *					ou soit le vecteur des <var>y</var> original si aucune interpolation n'a été faite.
     *
     * @see #interpolateNaN(double,double)
     */
    public final double[] interpolateNaN(final double dxStart, final double dxStop, double yi[]) {
        boolean toCreate;
        if (yi == null) {
            toCreate = true;
            yi = y;
        } else {
            toCreate = false;
            if (y != yi) {
                System.arraycopy(y, 0, yi, 0, yi.length);
            }
        }
        boolean oldStatus = ignoreYNaN;
        ignoreYNaN = true;
        try {
            loop:
            for (int index = 0; index < yi.length; index++) {
                if (!Double.isNaN(y[index]) && !Double.isNaN(x[index])) {
                    while (true) {
                        /*
                         *	Si on entre dans ce bloc, c'est qu'on a ignoré tous les NaN qui se trouvaient
                         *	au début du vecteur (ou d'un segment) et qu'on vient de trouver une donnée
                         *	valide. Il faut maintenant vérifier s'il y a suffisament de données valides
                         *	consécutives pour accepter de reprendre les interpolations.
                         */
                        int begin = index;
                        do {
                            if (++index >= yi.length) {
                                return yi;
                            }
                        } while (!Double.isNaN(y[index]) && !Double.isNaN(x[index]));
                        if (Math.abs(getInterval(begin, index - 1)) < dxStart) {
                            continue loop;
                        }
                        /*
                         *	Un nombre suffisant de données valides ayant été trouvées, on
                         *	comptera maintenant le nombre de NaN consécutifs qui apparaissent.
                         *	S'il n'y en a trop, l'instruction "break" fera recommencer les 4
                         *	lignes précédentes.
                         */
                        while (true) {
                            int beginNaN = index;
                            do {
                                if (++index >= yi.length) {
                                    return yi;
                                }
                            } while (Double.isNaN(y[index]) || Double.isNaN(x[index]));
                            if (Math.abs(getInterval(beginNaN, index - 1)) > dxStop) {
                                break;
                            }
                            int endNaN = index;
                            /*
                             *	Sachant qu'il n'y a pas trop de données manquantes, on vérifie
                             *	maintenant s'il y a suffisament de données après le trou.
                             */
                            begin = index;
                            while (++index < yi.length && !Double.isNaN(y[index]) && !Double.isNaN(x[index]));
                            if (Math.abs(getInterval(begin, index - 1)) < dxStart) {
                                continue loop;
                            }
                            /*
                             *	Maintenant que l'on est tout-à-fait rassuré quant au nombre de
                             *	données disponibles, on peut enfin procéder aux interpolations.
                             */
                            locateAt((beginNaN + endNaN) >> 1);
                            boolean reUseIndex = false;
                            while (beginNaN < endNaN) {
                                if (Double.isNaN(y[beginNaN])) {
                                    double xi = x[beginNaN];
                                    if (!Double.isNaN(xi)) {
                                        if (toCreate) {
                                            toCreate = false;
                                            yi = new double[y.length];
                                            System.arraycopy(y, 0, yi, 0, yi.length);
                                        }
                                        yi[beginNaN] = interpolate(xi, reUseIndex);
                                        reUseIndex = true;
                                    }
                                }
                                beginNaN++;
                            }
                        }
                    }
                }
            }
        } catch (ExtrapolationException exception) {
            throw new IllegalStateException("Unexpected extrapolation: " + exception.getMessage());
        } finally {
            ignoreYNaN = oldStatus;
        }
        return yi;
    }

    /**
     * Interpole les NaN trouvés dans le vecteur des <var>y</var>, en les remplaçant directement
     * dans le vecteur des <var>y</var> si possible. Le fait de ne pas avoir à créer de vecteur
     * temporaire peut rendre cette méthode plus rapide et plus économe en mémoire que l'autre
     * méthode {@link #interpolateNaN(double,double,double[])}.
     *
     * Si votre vecteur de données est si gros que vous ne pouvez vous permettre de
     * créer de vecteur temporaire, vous pouvez envisager d'utiliser la méthode
     * {@link #interpolateInPlaceNaN(double,double)}.<p>
     *
     * <strong>Note pour les développeurs de classes dérivées:</strong<br>
     * Les classes dérivées devrait redéfinir cette méthode avec l'une des deux lignes suivantes:
     * <code>return interpolateNaN(dxStart, dxStop, null)</code> s'il est nécessaire d'utiliser
     * un vecteur temporaire (c'est l'implémentation par défaut), ou <code>return interpolateNaN(dxStart,
     * dxStop, y)</code> si ce n'est pas nécessaire.
     *
     * @param dxStart	Plage minimal des <var>x</var> qu'il doit y avoir de chaque côté d'un NaN pour l'interpoler.
     * @param dxStop	Plage maximal des <var>x</var> couvert par les données manquantes pour qu'elles puissent être interpolées.
     * @return			Le tableau des <var>y</var>.
     *
     * @see #interpolateNaN(double, double, double[])
     * @see #interpolateInPlaceNaN(double, double)
     */
    public double[] interpolateNaN(final double dxStart, final double dxStop) {
        return interpolateNaN(dxStart, dxStop, null);
    }

    /**
     * Interpole les NaN trouvés dans le vecteur des <var>y</var>, en les remplaçant directement
     * dans le vecteur des <var>y</var> sans créer de vecteur temporaire. Cette méthode peut être plus
     * rapide et plus économe en mémoire que l'autre méthode {@link #interpolateNaN(double,double)}.
     * Elle peut toutefois donner des résultats légèrement différents. Supposons que vous vouliez
     * interpoler les NaN du vecteur suivant en utilisant une interpolation polynomiale d'ordre 4.
     *
     * <p><center><code>
     *        8.524 8.473 8.322 NaN NaN NaN 8.163 NaN 8.019 NaN 7.983 7.864 7.931 8.004
     * </code></center><p>
     *
     * Cette méthode interpole toujours les "grappes" de NaN en une étape. Dans cet exemple,
     * les trois premiers NaN seront correctement interpolés en utilisant les valeurs 8.473,
     * 8.322, 8.163 et 8.019. Le quatrième NaN forme une "grappe" à lui tout seul, parce qu'il
     * est entouré de deux données valides. Il sera donc interpolé en une seconde étape. Mais
     * comme la première "grappe" de trois NaN a été interpolée avant lui, l'interpolation du
     * quatrième NaN se fera intervenir le résultat de la dernière interpolation (au lieu de
     * 8.322), 8.163, 8.019 et 7.983.<p>
     *
     * Des exemples d'interpolations qui sont affectés par cet effet sont {@link Polynomial1D}
     * (avec un ordre supérieur à 2) et {@link Hermite1D}. Des exemples d'interpolations qui ne
     * sont pas affectés (et n'ont donc pas besoin d'utiliser un vecteur temporaire) sont
     * {@link Search1D}, {link Linear1D} et {link Spline1D}. Si la quantité de mémoire disponible
     * n'est pas trop critique, il vaut mieux utiliser la méthode {@link #interpolateNaN(double,double)},
     * qui décidera elle-même s'il vaux mieux créer un vecteur temporaire ou non.
     *
     * @param dxStart	Plage minimal des <var>x</var> qu'il doit y avoir de chaque côté d'un NaN pour l'interpoler.
     * @param dxStop	Plage maximal des <var>x</var> couvert par les données manquantes pour qu'elles puissent être interpolées.
     * @return			Le tableau des <var>y</var>.
     */
    public final double[] interpolateInPlaceNaN(final double dxStart, final double dxStop) {
        return interpolateNaN(dxStart, dxStop, y);
    }

    /**
     * Interpole <var>y</var> en utilisant les données qui apparaissent aux index {@link #klo}
     * et {@link #khi}. Cette méthode ne doit pas appeller {@link #locate} car c'est
     * déjà fait. Elle n'est pas non-plus synchronisée pour des raisons de performances.<p>
     *
     * L'implémentation par défaut de cette méthode retourne simplement la valeur <var>y</var>
     * qui correspond au <var>x</var> le plus proche de celui qui est spécifié. Cette méthode
     * doit être redéfinie par les classes dérivées qui implémenteront des interpolations
     * plus évoluées.<p>
     *
     * Au moment de l'appel de cette méthode, les NaN ont déjà été pris en compte dans le
     * vecteur des <var>x</var>. Cette méthode doit décider ce qu'elle fait des NaN dans
     * le vecteur des <var>y</var> en consultant le champs {@link Table1D#ignoreYNaN}.<p>
     *
     * Si l'argument <code>reUseIndex</code> est <code>true</code>, alors cette méthode
     * doit réutiliser exactement les mêmes index <var>klo</var> et <var>khi</var> que
     * lors de la dernière interpolation sans chercher à vérifier s'il y a des données
     * manquantes. Cela suppose donc qu'aucune implémentation de cette méthode ne modifie
     * les index dont elle a besoin. Cette aptitude est nécessaire pour éviter qu'une
     * interpolation ne se base sur des données interpolées lorsque l'on interpole
     * plusieurs données consécutives.
     *
     * @param xi			valeur de <var>x</var> pour laquelle on désire une
     *						valeur <var>y</var> interpolée.
     * @param reuseIndex	<var>true</var> s'il faut réutiliser les même
     *						index que ceux de la dernière interpolation.
     * @return				Valeur <var>y</var> interpolée.
     */
    protected double interpolate(final double xi, final boolean reuseIndex) throws ExtrapolationException {
        if (ignoreYNaN && !reuseIndex) {
            try {
                validateIndex(y);
            } catch (ExtrapolationException extrapole) {
                if (extrapole.index >= 0) {
                    return y[extrapole.index];
                }
                extrapole.xi = xi;
                throw extrapole;
            }
        }
        return y[(Math.abs(x[klo] - xi) <= Math.abs(x[khi] - xi)) ? klo : khi];
    }

    /**
     * Trouve l'index de la valeur <var>xi</var> dans le vecteur des <var>x</var> et renvoie dans le
     * tableu <code>index</code> les index qui y correspondent. Ce tableau peut avoir une longueur
     * quelconque. Cette méthode tentera de créer une suite d'index, mais en sautant les NaN qui
     * apparaissent dans le vecteur des <var>x</var> ou le vecteur des <var>y</var>. Par exemple
     * supposons que cet objet représente la table suivante:
     *
     * <blockquote><pre>&nbsp;index: 0 1 2 3 4  5   6  7
     *					&nbsp;  X = [2 4 5 7 8 NaN 12 14];
     *                  &nbsp;  Y = [4 7 2 1 6  1  NaN 5];</pre></blockquote>
     *
     * Alors, si <code>index</code> est un tableau de 4 éléments, <code>locate(10, index)</code>
     * écrira dans ce tableau [2 3 4 7]. Les valeurs 5 et 6 ont été sautées parce que
     * <code>X[5]==NaN</code> et <code>Y[6]==NaN</code>.
     *
     * @param   xi valeur <var>x</var> dont on désire les index.
     * @param   index tableau dans lequel écrire les index.
     * @return  <code>true</code> si l'opération à réussie, <code>false</code> si elle a échouée
     *          parce qu'il n'y a pas suffisament de données valides dans les vecteurs <var>X</var>
     *          et <var>Y</var>.
     */
    public final boolean locate(final double xi, final int index[]) {
        if (locate(xi)) {
            copyIndexInto(index);
            if (ignoreYNaN) {
                validateIndex(index, y);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Trouve les index des valeurs du vecteur des <var>x</var> qui englobent la valeur spécifiée.
     * Cette méthode ajuste la valeurs des champs {@link #klo} et {@link #khi} de façon à respecter
     * l'une des conditions suivantes:<p>
     *
     * <pre>x[klo]  < xi <  x[khi]</pre> si les données du vecteur des <var>x</var> sont en ordre croissant.<br>
     * <pre>x[klo]  > xi >  x[khi]</pre> si les données du vecteur des <var>x</var> sont en ordre décroissant.<br>
     * <pre>x[klo] == xi == x[khi]</pre> si une correspondance exacte fut trouvée.<p>
     *
     * Notez que le dernier cas implique que <code>klo==khi</code>. Si non (c'est-à-dire si le
     * vecteur des <var>x</var> ne contient pas une valeur identique à <var>xi</var>), alors
     * <code>klo</code> sera toujours inférieure à <code>khi</code>.<p>
     *
     * Mis à part les correspondances exactes, cette méthode produira dans la plupart des cas un
     * résultat tel que <code>khi==klo+1</code>. Si toutefois le vecteur des <var>x</var> contient
     * des NaN, alors l'écart entre <code>klo</code> et <code>khi</code> peut être plus grand. En
     * effet, cette méthode s'efforce de faire pointer les index <code>klo</code> et <code>khi</code>
     * vers des données valides.<p>
     *
     * <strong>Exemples:</strong> Supposons que le vecteur des x contient les données suivantes:
     *
     * <blockquote><code>
     *				[4 9 12 NaN NaN 34 56 76 89]
     * </code></blockquote>
     *
     *	Alors,
     *
     * <blockquote>
     * <pre>locate(9) </pre>   donnera  <code>klo=1</code>  et  <code>khi=1</code>.<br>
     * <pre>locate(60)</pre>   donnera  <code>klo=6</code>  et  <code>khi=7</code>.<br>
     * <pre>locate(20)</pre>   donnera  <code>klo=2</code>  et  <code>khi=5</code>.
     * </blockquote>
     *
     * Si les données du vecteur des <var>x</var> ne sont pas en ordre croissant
     * ou décroissant, alors cette méthode produira un résultat indéterminé. En
     * particulier, elle peut tomber dans une boucle infinie.
     *
     * @param xi valeur à rechercher dans le vecteur des x.
     * @return  <code>true</code> si la valeur spécifiée est comprise dans la plage du vecteur
     *          des <var>x</var>. <code>false</code> si elle se trouve en dehors ou si le vecteur
     *          n'a pas suffisamment de données autres que NaN.
     *
     * @see #klo
     * @see #khi
     * @see #copyIndexInto(int[]);
     */
    private final boolean locate(final double xi) {
        klo = 0;
        khi = x.length - 1;
        while (klo <= khi) {
            double x_klo = x[klo];
            double x_khi = x[khi];
            if (x_klo < xi && xi < x_khi) {
                /*
                 * Recherche l'index de la donnée xi dans
                 * un vecteur des x en ordre croissant.
                 */
                loop:
                while (khi - klo > 1) {
                    int k = (khi + klo) >> 1;
                    double xk = x[k];
                    if (xi < xk) {
                        khi = k;
                        continue loop;
                    }
                    if (xi > xk) {
                        klo = k;
                        continue loop;
                    }
                    if (xi == xk) {
                        klo = khi = k;
                        break loop;
                    }
                    /*
                     *	Le code suivant ne sera exécuté que si l'on vient de tomber sur un NaN.
                     *	Le "+1" de la ligne suivante n'existe que pour forcer un arrondissement
                     *	vers le haut dans la division par 2. Exemple: avec klo=1 et khi=5, on a
                     *	k=3 et kmax=2. Le code précédent avait examiné x[3], et le code suivant
                     *	examinera x[2] et x[4].
                     */
                    int kmax = (khi - klo + 1) >> 1;
                    for (int i = 1; i < kmax; i++) {
                        int ki = k + i;
                        xk = x[ki];
                        if (xi < xk && ki < khi) {
                            khi = ki;
                            continue loop;
                        }
                        if (xi > xk && ki > klo) {
                            klo = ki;
                            continue loop;
                        }
                        if (xi == xk) {
                            klo = khi = ki;
                            break loop;
                        }
                        ki = k - i;
                        xk = x[ki];
                        if (xi < xk && ki < khi) {
                            khi = ki;
                            continue loop;
                        }
                        if (xi > xk && ki > klo) {
                            klo = ki;
                            continue loop;
                        }
                        if (xi == xk) {
                            klo = khi = ki;
                            break loop;
                        }
                    }
                    break loop;
                }
                return true;
            }
            if (x_klo > xi && xi > x_khi) {
                /*
                 * Recherche l'index de la donnée xi dans
                 * un vecteur des x en ordre décroissant.
                 */
                loop:
                while (khi - klo > 1) {
                    int k = (khi + klo) >> 1;
                    double xk = x[k];
                    if (xi > xk) {
                        khi = k;
                        continue loop;
                    }
                    if (xi < xk) {
                        klo = k;
                        continue loop;
                    }
                    if (xi == xk) {
                        klo = khi = k;
                        break loop;
                    }
                    /*
                     *	Le code suivant ne sera exécuté que si l'on vient de tomber sur un NaN.
                     *	Le "+1" de la ligne suivante n'existe que pour forcer un arrondissement
                     *	vers le haut dans la division par 2. Exemple: avec klo=0 et khi=5, on a
                     *	k=2 et kmax=3. Le code précédent avait examiné x[2], et le code suivant
                     *	examinera x[0],x[1] ainsi que x[3],x[4].
                     */
                    int kmax = (khi - klo + 1) >> 1;
                    for (int i = 1; i < kmax; i++) {
                        int ki = k + i;
                        xk = x[ki];
                        if (xi > xk && ki < khi) {
                            khi = ki;
                            continue loop;
                        }
                        if (xi < xk && ki > klo) {
                            klo = ki;
                            continue loop;
                        }
                        if (xi == xk) {
                            klo = khi = ki;
                            break loop;
                        }
                        ki = k - i;
                        xk = x[ki];
                        if (xi > xk && ki < khi) {
                            khi = ki;
                            continue loop;
                        }
                        if (xi < xk && ki > klo) {
                            klo = ki;
                            continue loop;
                        }
                        if (xi == xk) {
                            klo = khi = ki;
                            break loop;
                        }
                    }
                    break loop;
                }
                return true;
            }
            /*
             * Cas où xi se trouve à la limite ou en dehors
             * du vecteur des x, ou que l'on a des NaN.
             */
            if (x_klo == xi) {
                khi = klo;
                return true;
            }
            if (x_khi == xi) {
                klo = khi;
                return true;
            }
            if (Double.isNaN(xi)) {
                break;
            }
            if (!Double.isNaN(x_klo) && !Double.isNaN(x_khi)) {
                break;
            }
            while (klo <= khi && Double.isNaN(x[klo])) {
                klo++;
            }
            while (khi >= klo && Double.isNaN(x[khi])) {
                khi--;
            }
        }
        return false;
    }

    /**
     * Positionne les index {@link #klo} et {@link #khi} autour de l'index
     * spécifié. Si le vecteur des <var>x</var> ne contient pas de NaN, alors
     * <code>klo</code> sera égal à <code>index-1</code> et <code>khi</code> sera
     * égal à <code>index+1</code>. Si le vecteur des <var>x</var> contient des NaN,
     * alors <code>klo</code> sera un peu plus bas et/ou <code>khi</code> un peu plus haut.
     *
     * @param index index autour duquel on veut se positionner.
     * @throws ExtrapolationException si les index tombent en dehors des limites du vecteur des <var>x</var>.
     * @see #validateIndex(float[])
     */
    private final void locateAt(final int index) throws ExtrapolationException {
        klo = khi = index;
        final int length = x.length;
        do {
            if (++khi >= length) {
                do {
                    if (--klo < 0) {
                        throw new ExtrapolationException();
                    }
                } while (Double.isNaN(x[klo]));
                throw new ExtrapolationException(+1, klo);
            }
        } while (Double.isNaN(x[khi]));
        do {
            if (--klo < 0) {
                throw new ExtrapolationException(-1, khi);
            }
        } while (Double.isNaN(x[klo]));
    }

    /**
     * Copie dans le tableau spécifié en argument la valeur des champs {@link #klo} et
     * {@link #khi}. Ce tableau peut avoir diverses longueurs. Un cas typique est lorsque
     * qu'il a une longueur de 2. Alors le champs <code>klo</code> sera simplement copié dans
     * <code>index[0]</code> et <code>khi</code> dans <code>index[1]</code>. Si le tableau à
     * une longueur de 1, alors seul <code>klo</code> sera copié dans <code>index[0]</code>.
     * Si le tableau à une longueur de 0, rien ne sera fait.<p>
     *
     * Les cas le plus intéressants se produisent lorsque le tableau <code>index</code> à une
     * longueur de 3 et plus. Cette méthode écrira <code>klo</code> et <code>khi</code> au milieu
     * de ce tableau, puis complètera les autres cellules avec la suite des index qui pointent
     * vers des valeurs autres que NaN. Par exemple si le vecteur des <var>x</var> contient:
     *
     * <p><center><code>
     *        [5 8 NaN 12 NaN 19 21 34]
     * </code></center><p>
     *
     * Alors l'appel de la méthode <code>locate(15)</code> donnera aux champs <code>klo</code> et
     * <code>khi</code> les valeurs 3 et 5 respectivement, de sorte que <code>x[3] < 15 < x[5]</code>.
     * Si vous souhaitez effectuer une interpolation polynomiale d'ordre 4 autour de ces données, vous
     * pouvez ensuite écrire:
     *
     * <blockquote><code>
     *			int index[]=new int[4];<br>
     *			copyIndexInto(index);
     * </code></blockquote>
     *
     * Le tableau <code>index</code> contiendra alors [1 3 5 6].<p>
     *
     * Cette méthode fonctionne correctement même si les index <code>klo</code> et <code>khi</code>
     * pointait vers le début au à la fin du vecteur des <var>x</var>. Notez toutefois qu'après
     * l'appel de cette méthode, les index <code>klo</code> et <code>khi</code> auront une valeur
     * indéterminée.
     *
     * @param index tableau dans lequel copier les champs <code>klo</code> et <code>khi</code>.
     * @throws ArrayIndexOutOfBoundsException s'il n'y a pas suffisament de données valides.
     *
     * @see #locate(double)
     * @see #validateIndex(int[], double[])
     */
    protected final void copyIndexInto(final int index[]) {
        final int xlength = x.length;
        int center = index.length;
        if (center >= 2) {
            center >>= 1;
            int i = center;
            /*
             *	Si khi et klo sont identiques, on n'écrira pas klo afin de ne
             *	pas répéter deux fois le même index. On écrira seulement khi.
             *	La boucle 'loop' copie au début du tableau 'index' les index
             *	qui précèdent klo.
             */
            if (khi != klo) {
                index[--i] = klo;
            }
            loop:
            while (i > 0) {
                do {
                    if (--klo < 0) {
                        center -= i;
                        System.arraycopy(index, i, index, 0, center);
                        break loop;
                    }
                } while (Double.isNaN(x[klo]));
                index[--i] = klo;
            }
            /*
             *	La boucle suivante copie khi et les index qui le suivent dans
             *	le tableau 'index'. Si on a atteint la fin des données sans
             *	avoir réussi à copier tous les index, on décalera vers la droite
             *	les index qui ont été copié et on tentera de combler le trou créé
             *	à gauche en copiant d'autres index qui précédaient klo.
             */
            i = center;
            index[i++] = khi;
            loop:
            while (i < index.length) {
                do {
                    if (++khi >= xlength) {
                        int remainder = index.length - i;
                        // center += remainder; // (not needed)
                        System.arraycopy(index, 0, index, remainder, i);
                        i = remainder;
                        do {
                            do {
                                if (--klo < 0) {
                                    throwArrayIndexOutOfBoundsException(index.length);
                                }
                            } while (Double.isNaN(x[klo]));
                            index[--i] = klo;
                        } while (i > 0);
                        break loop;
                    }
                } while (Double.isNaN(x[khi]));
                index[i++] = khi;
            }
        } else if (center > 0) {
            index[0] = klo;
        }
    }

    /**
     * Ajuste les index spécifiés de façon à ce qu'ils ne pointent vers aucune donnée NaN.
     * Ces index sont habituellement obtenus par la méthode {@link #copyIndexInto}.<p>
     *
     * Supposons que vous vous apprêtez à faire une interpolation polynomiale d'ordre 4
     * autour de la donnée <var>x</var>=84. Supposons qu'avec les méthodes {@link #locate}
     * et {@link #copyIndexInto}, vous ayiez obtenu les index [4 5 6 7]. La valeur 84 se
     * trouverait typiquement entre <code>x[5]</code> et <code>x[6]</code>. Maintenant
     * supposons que votre vecteur des <var>y</var> contienne les données suivantes:
     *
     * <p><center><code>
     *        y=[5 3 1 2 7 NaN 12 6 4 ...etc...]
     * </code></center><p>
     *
     * Vous voulez vous assurez que les index obtenus par <code>copyIndexInto</code> pointent
     * tous vers une donnée <var>y</var> valide. Après avoir appellé la méthode
     *
     * <p><center><code>
     *        validateIndex(index, y)
     * </code></center><p>
     *
     * vos index [4 5 6 7] deviendront [3 4 6 7], car <code>y[5]</code> avait pour valeur NaN.
     * Notez que vous n'avez pas à vous préocupper de savoir si les index pointent vers des
     * <var>x</var> valides. Ça avait déjà été assuré par <code>copyIndexInto</code> et continuera
     * à être assuré par <code>validateIndex</code>.<p>
     *
     * Voici un exemple d'utilisation. Supposons que trois vecteurs de données (<code>Y1</code>,
     * <code>Y2</code> et <code>Y3</code>) se partagent le même vecteur des <var>x</var>
     * (<code>X</code>). Supposons que vous souhaitez obtenir 4 index valides simultanément pour
     * tous les vecteurs autour de <var>x</var>=1045. Vous pourriez écrire:
     *
     * <blockquote><pre>
     * &nbsp;locate(1045);
     * &nbsp;int index[]=new int[4];
     * &nbsp;copyIndexIntoArray(index);
     * &nbsp;boolean hasChanged=false;
     * &nbsp;do
     * &nbsp;{
     * &nbsp;    hasChanged |= validateIndex(index, Y1);
     * &nbsp;    hasChanged |= validateIndex(index, Y2);
     * &nbsp;    hasChanged |= validateIndex(index, Y3);
     * &nbsp;}
     * &nbsp;while (hasChanged);
     * </pre></blockquote>
     *
     * S'il n'est pas nécessaire que les index soient valides pour tous les vecteurs simultanément,
     * vous pourriez copier les éléments de <code>index</code> dans un tableau temporaire après l'appel
     * de <code>copyIndexInto</code>. Il vous suffira alors de restituer cette copie avant chaque appel
     * de <code>validateIndex</code> pour chacun des vecteurs <code>Y</code>. En réutilisant cette copie,
     * vous évitez d'appeller trois fois <code>locate</code> et y gagnez ainsi un peu en vitesse d'éxecution.
     *
     * @param index A l'entrée, tableau d'index à vérifier. A la sortie, tableau d'index modifiés.
     *				Cette méthode s'efforce autant que possible de ne pas modifier les index se
     *				trouvant au centre de ce tableau.
     * @param y		Vecteur des données <var>y</var> servant à la vérification.
     * @return		<code>true</code> si des changements ont été fait, <code>false</code> sinon.
     * @throws ArrayIndexOutOfBoundsException s'il n'y a pas suffisament de données valides.
     *
     * @see #locate(double)
     * @see #copyIndexInto(int[])
     */
    protected final boolean validateIndex(final int index[], final double y[]) {
        boolean hasChanged = false;
        final int xlength = x.length;
        int center = index.length >> 1;
        loop:
        for (int i = center; --i >= 0;) {
            if (Double.isNaN(y[index[i]])) {
                /*
                 *	Ce bloc ne sera exécuté que si un NaN a été trouvé (sinon cette méthode sera
                 *	exécutée rapidement car elle n'aurait pratiquement rien à faire). La prochaine
                 *	boucle décale les index qui avaient déjà été trouvés (par 'copyIndexInto') de
                 *	façon à exclure les NaN. L'autre boucle va chercher d'autre index, de la même
                 *	façon que 'copyIndexInto' s'y prenait.
                 */
                hasChanged = true;
                for (int j = i; --j >= 0;) {
                    if (!Double.isNaN(y[index[j]])) {
                        index[i--] = index[j];
                    }
                }
                int klo = index[0];
                do {
                    do {
                        if (--klo < 0) {
                            center -= ++i;
                            System.arraycopy(index, i, index, 0, index.length - i);
                            break loop;
                        }
                    } while (Double.isNaN(x[klo]) || Double.isNaN(y[klo]));
                    index[i--] = klo;
                } while (i >= 0);
                break loop;
            }
        }
        /*
         *	Le code suivant fait la même opération que le code précédent,
         *	mais pour la deuxième moitié des index.
         */
        loop:
        for (int i = center; i < index.length; i++) {
            if (Double.isNaN(y[index[i]])) {
                hasChanged = true;
                for (int j = i; ++j < index.length;) {
                    if (!Double.isNaN(y[index[j]])) {
                        index[i++] = index[j];
                    }
                }
                int khi = index[index.length - 1];
                do {
                    do {
                        if (++khi >= xlength || khi >= y.length) {
                            int remainder = index.length - i;
                            // center += remainder; // (not needed)
                            System.arraycopy(index, 0, index, remainder, i);
                            i = remainder;
                            int klo = index[0];
                            do {
                                do {
                                    if (--klo < 0) {
                                        throwArrayIndexOutOfBoundsException(index.length);
                                    }
                                } while (Double.isNaN(x[klo]) || Double.isNaN(y[klo]));
                                index[--i] = klo;
                            } while (i > 0);
                            break loop;
                        }
                    } while (Double.isNaN(x[khi]) || Double.isNaN(y[khi]));
                    index[i++] = khi;
                } while (i < index.length);
                break loop;
            }
        }
        return hasChanged;
    }

    /**
     * Ajuste les index {@link #klo} et {@link #khi} de façon à ce qu'ils
     * pointent vers des données valides. Cette méthode est très similaire
     * à l'autre méthode {@link #validateIndex(int[], double[])}, excepté qu'elle agit directement
     * sur {@link #klo} et {@link #khi} plutôt que sur un tableau passé
     * en argument. On y gagne ainsi en rapidité d'exécution (on évite de
     * faire un appel à {@link #copyIndexInto(int[])}, mais ça ne gère toujours
     * que ces deux index.<p>
     *
     * Tout ce qui était entre <code>klo</code> et <code>khi</code> avant l'appel de
     * cette méthode le resteront après. Cette méthode ne fait que diminuer <code>klo</code>
     * et augmenter <code>khi</code>, si nécessaire. Si ce n'était pas possible, une exception
     * <code>ExtrapolationException</code> sera lancée.
     *
     * @param y Vecteur des données <var>y</var> servant à la vérification.
     * @return  <code>true</code> si des changements ont été fait, <code>false</code> sinon.
     * @throws  ExtrapolationException s'il n'y a pas suffisament de données valides.
     *
     * @see #validateIndex(int[], double[])
     * @see #locateAt(int)
     * @see #locate(double)
     */
    protected final boolean validateIndex(final double y[]) throws ExtrapolationException {
        boolean hasChanged = false;
        if (Double.isNaN(y[khi])) {
            hasChanged = true;
            do {
                if (++khi >= y.length) {
                    while (Double.isNaN(x[klo]) || Double.isNaN(y[klo])) {
                        if (--klo < 0) {
                            throw new ExtrapolationException();
                        }
                    }
                    throw new ExtrapolationException(+1, klo);
                }
            } while (Double.isNaN(x[khi]) || Double.isNaN(y[khi]));
        }
        if (Double.isNaN(y[klo])) {
            hasChanged = true;
            do {
                if (--klo < 0) {
                    throw new ExtrapolationException(-1, khi);
                }
            } while (Double.isNaN(x[klo]) || Double.isNaN(y[klo]));
        }
        return hasChanged;
    }

    /**
     * Renvoie la plage <var>dx</var> que couvrent les données entre les deux index spécifiés.
     * Cette plage est mesurée à partir de l'espace qui se trouve entre deux données, comme
     * dans le dessin ci-dessous.
     *
     * <blockquote><pre>
     * &nbsp;           lower       upper
     * &npsp;             |           |
     * &npsp;146   148   150   152   154   156   158
     * &npsp;          |                 |
     * &npsp;          ^------plage------^
     * </pre></blockquote>
     *
     *	Les exemples suivants supposent que vos données sont échantillonées aux heures:
     *
     * <blockquote>
     *		<code>getInterval(20,20)</code>		retournerait 1 heure.<br>
     *		<code>getInterval(17,18)</code>		retournerait 2 heures.<br>
     *		<code>getInterval(30,34)</code>		retournerait 5 heures.
     * </blockquote>
     *
     *	Il n'est pas obligatoire que l'intervalle d'échantillonnage soit constant.
     *	Cette méthode utilisera une interpolation linéaire lorsqu'il y a des NaN.
     *
     * @param lower index de la première donnée de la plage.
     * @param upper index de la dernière donnée de la plage.
     * @return intervalle <var>dx</var> entre ces deux index.
     */
    private double getInterval(final int lower, final int upper) {
        int k0, k1;
        /*
         *	Repère les index pointant vers les données à utiliser pour le calcul
         *	de l'intervalle. En l'absence de NaN on obtient:
         *
         *		klo0 = lower			khi0 = upper
         *		klo1 = klo0-1			khi1 = upper+1
         *
         *	Le schema ci-dessous donne un exemple de la façon dont se comporte
         *	le code en la présence de NaN pour des "lower" et "upper" donnés.
         *
         *		                   lower          upper
         *		                     |              |
         *		140  145  150  NaN  160  165  170  NaN  180  185  190
         *		           ^         ^         ^         ^
         *		          k1        k0         k0        k1
         */
        k0 = k1 = lower;
        final int xlength = x.length;
        while (Double.isNaN(x[k0])) {
            if (++k0 >= xlength) {
                return Double.NaN;
            }
        }
        do {
            if (--k1 < 0) {
                k1 = k0;
                do {
                    if (++k0 >= xlength) {
                        return Double.NaN;
                    }
                } while (Double.isNaN(x[k0]));
                break;
            }
        } while (Double.isNaN(x[k1]));
        double x0 = x[k0];
        double xlo = (x[k1] - x0) / (k1 - k0) * (lower - k0 - 0.5) + x0;

        k0 = k1 = upper;
        while (Double.isNaN(x[k0])) {
            if (--k0 < 0) {
                return Double.NaN;
            }
        }
        do {
            if (++k1 >= xlength) {
                k1 = k0;
                do {
                    if (--k0 < 0) {
                        return Double.NaN;
                    }
                } while (Double.isNaN(x[k0]));
                break;
            }
        } while (Double.isNaN(x[k1]));
        x0 = x[k0];
        return (x[k1] - x0) / (k1 - k0) * (upper - k0 + 0.5) + x0 - xlo;
    }
}
