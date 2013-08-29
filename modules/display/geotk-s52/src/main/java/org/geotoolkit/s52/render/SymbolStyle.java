/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.s52.render;

import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.s52.dai.SymbolBitmap;
import org.geotoolkit.s52.dai.SymbolColorReference;
import org.geotoolkit.s52.dai.SymbolDefinition;
import org.geotoolkit.s52.dai.SymbolExposition;
import org.geotoolkit.s52.dai.SymbolIdentifier;
import org.geotoolkit.s52.dai.SymbolVector;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class SymbolStyle {

    /** identifier */
    public SymbolIdentifier ident;
    /** holds the color map */
    public SymbolColorReference colors;
    /** style definition */
    public SymbolDefinition definition;
    /** explication of the style */
    public SymbolExposition explication;
    /** drawing commands */
    public final List<SymbolVector> vectors = new ArrayList<>();
    /** bitmap style */
    public SymbolBitmap bitmap;

}
