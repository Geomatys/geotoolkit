/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2011, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wps.converters.inputs.references;

import java.awt.geom.AffineTransform;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.geotoolkit.mathml.xml.Mtable;
import org.geotoolkit.mathml.xml.Mtr;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.Reference;
import org.geotoolkit.wps.xml.WPSMarshallerPool;

/**
 *
 * @author Quentin Boileau (Geomatys).
 */
public class ReferenceToAffineTransformConverter extends AbstractReferenceInputConverter<AffineTransform> {

    private static ReferenceToAffineTransformConverter INSTANCE;

    private ReferenceToAffineTransformConverter() {
    }

    public static synchronized ReferenceToAffineTransformConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ReferenceToAffineTransformConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<AffineTransform> getTargetClass() {
        return AffineTransform.class;
    }

    @Override
    public AffineTransform convert(final Reference source, final Map<String, Object> params) throws UnconvertibleObjectException {

        final String mime = source.getMimeType() != null ? source.getMimeType() : WPSMimeType.TEXT_XML.val();
        final InputStream stream = getInputStreamFromReference(source);

        if (mime.equalsIgnoreCase(WPSMimeType.TEXT_XML.val()) || mime.equalsIgnoreCase(WPSMimeType.APP_GML.val())
                || mime.equalsIgnoreCase(WPSMimeType.TEXT_GML.val())) {

            try {
                final Unmarshaller unmarsh = WPSMarshallerPool.getInstance().acquireUnmarshaller();
                Object value = unmarsh.unmarshal(stream);
                WPSMarshallerPool.getInstance().recycle(unmarsh);

                return bindToAffineTransform(value);

            } catch (JAXBException ex) {
                throw new UnconvertibleObjectException("Reference geometry invalid input : Unmarshallable geometry", ex);
            }
        } else {
            throw new UnconvertibleObjectException("Reference data mime is not supported");
        }
    }

    private AffineTransform bindToAffineTransform(final Object object) throws UnconvertibleObjectException {

        ArgumentChecks.ensureNonNull("object", object);

        AffineTransform affineTransform = null;
        if (object instanceof org.geotoolkit.mathml.xml.Math) {
            final org.geotoolkit.mathml.xml.Math math = (org.geotoolkit.mathml.xml.Math) object;
            final List<Object> mathExp = math.getMathExpression();
            if (mathExp != null && !mathExp.isEmpty()) {
                final Mtable mtable = WPSConvertersUtils.findMtable(mathExp);

                if (mtable == null) {
                    throw new UnconvertibleObjectException("No mtable element found.");
                }

                final List<Mtr> rows = WPSConvertersUtils.getRows(mtable);

                final int nbRows = rows.size();
                final int nbCells = WPSConvertersUtils.getCells(rows.get(0)).length;
                if (nbRows != 2 || (nbCells != 2 && nbCells != 3)) {
                    throw new UnconvertibleObjectException("The matrix need to be a 2x2 or a 3x3 matrix .");
                }

                final double[][] matrix =  new double[nbRows][nbCells];
                for (int i = 0; i < nbRows; i++) {
                    final double[] cells = WPSConvertersUtils.getCells(rows.get(i));
                    if (cells.length != nbCells) {
                        throw new UnconvertibleObjectException("The matrix need to be a 2x2 or a 3x3 matrix .");
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
                affineTransform = new AffineTransform(flatMatrix);

            }
        }
        return affineTransform;
    }
}
