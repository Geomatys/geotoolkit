/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.wps.converters.inputs.complex;

import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.Map;
import org.geotoolkit.mathml.xml.Mtable;
import org.geotoolkit.mathml.xml.Mtr;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import org.geotoolkit.wps.xml.v100.ComplexDataType;

/**
 * 
 * @author Quentin Boileau (Geomatys).
 */
public class ComplexToAffineTransformConverter  extends AbstractComplexInputConverter<AffineTransform> {

    private static ComplexToAffineTransformConverter INSTANCE;

    private ComplexToAffineTransformConverter() {
    }

    public static synchronized ComplexToAffineTransformConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ComplexToAffineTransformConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<? extends AffineTransform> getTargetClass() {
        return AffineTransform.class;
    }

    @Override
    public AffineTransform convert(final ComplexDataType source, Map<String, Object> params) throws NonconvertibleObjectException {
        
        final List<Object> datas = source.getContent();
        
        if (datas != null && datas.size() > 1) {
            throw new NonconvertibleObjectException("Invalid data input : Only one element expected.");
        }
        
        AffineTransform at = null;
        
        final Object data = datas.get(0);
        
        if (data != null && data instanceof org.geotoolkit.mathml.xml.Math) {
            final org.geotoolkit.mathml.xml.Math math = (org.geotoolkit.mathml.xml.Math) data;
            final List<Object> mathExp = math.getMathExpression();
            if (mathExp != null && !mathExp.isEmpty()) {
                final Mtable mtable = WPSConvertersUtils.findMtable(mathExp);
                
                if (mtable == null) {
                    throw new NonconvertibleObjectException("No mtable element found.");
                }
                
                final List<Mtr> rows = WPSConvertersUtils.getRows(mtable);
                
                final int nbRows = rows.size();
                final int nbCells = WPSConvertersUtils.getCells(rows.get(0)).length;
                if (nbRows != 2 || (nbCells != 2 && nbCells != 3)) {
                    throw new NonconvertibleObjectException("The matrix need to be a 2x2 or a 3x3 matrix .");
                }
                
                final double[][] matrix =  new double[nbRows][nbCells];
                for (int i = 0; i < nbRows; i++) {
                    final double[] cells = WPSConvertersUtils.getCells(rows.get(i));
                    if (cells.length != nbCells) {
                        throw new NonconvertibleObjectException("The matrix need to be a 2x2 or a 3x3 matrix .");
                    }
                    
                    System.arraycopy(cells, 0, matrix[i], 0, nbCells);
                    
                }
                
                //TODO optimize 
                double[] flatMatrix = new double[nbCells*nbRows];
                int count = 0;
                for (int i = 0; i < nbCells; i++) {
                    for (int j = 0; j < nbRows; j++) {
                        flatMatrix[count++] = matrix[j][i];
                    }
                }
                at = new AffineTransform(flatMatrix);
               
            }
        }
        return at;
    }
}

