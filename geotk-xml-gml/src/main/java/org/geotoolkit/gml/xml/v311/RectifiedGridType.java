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
package org.geotoolkit.gml.xml.v311;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.opengis.coverage.grid.RectifiedGrid;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.TransformException;


/**
 * A rectified grid has an origin and vectors that define its post locations.
 *
 * <p>Java class for RectifiedGridType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="RectifiedGridType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}GridType">
 *       &lt;sequence>
 *         &lt;element name="origin" type="{http://www.opengis.net/gml}PointType"/>
 *         &lt;element name="offsetVector" type="{http://www.opengis.net/gml}VectorType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RectifiedGridType", propOrder = {
    "origin",
    "offsetVector"
})
public class RectifiedGridType extends GridType {

    @XmlElement(required = true)
    private PointType origin;
    @XmlElement(required = true)
    private List<VectorType> offsetVector;

    public RectifiedGridType() {

    }

    public RectifiedGridType(final RectifiedGrid grid) {
       super(grid);
       if (grid != null) {
           origin       = new PointType(grid.getOrigin(), false);
           offsetVector = new ArrayList<>();

           final List<double[]> vectors = grid.getOffsetVectors();
           for (double[] vector : vectors) {
                offsetVector.add(new VectorType(vector));
           }
       }
    }

    public RectifiedGridType(final GridGeometry gg) throws TransformException {
       super(gg);
       if (gg != null) {
           MathTransform gridToCRS = gg.getGridToCRS(PixelInCell.CELL_CORNER);
           DirectPosition ori = gridToCRS.transform(new GeneralDirectPosition(gridToCRS.getSourceDimensions()), null);
           origin = new PointType(ori, false);
           origin.setId("pt-" + new Random().nextInt()); // for xml validation
           offsetVector    = new ArrayList<>();

           Matrix m = MathTransforms.getMatrix(gridToCRS);
           if (m == null) {
               m = MathTransforms.getMatrix(gg.selectDimensions(0,1).getGridToCRS(PixelInCell.CELL_CORNER));
           }
           if (m != null) {
               for (int j = 0; j < m.getNumRow() -1; j++) {
                   double[] row = new double[gridToCRS.getSourceDimensions()];
                   for (int i = 0; i < m.getNumCol() - 1; i++) {
                       row[i] = m.getElement(j, i);
                   }
                   offsetVector.add(new VectorType(row));
               }
           }
       }
    }

    /**
     * Gets the value of the origin property.
     */
    public PointType getOrigin() {
        return origin;
    }

    public void setOrigin(final PointType origin) {
        this.origin = origin;
    }

    /**
     * Gets the value of the offsetVector property.
     */
    public List<VectorType> getOffsetVector() {
        return offsetVector;
    }

    /**
     * Sets the value of the offsetVector property
     */
    public void setOffsetVector(final List<VectorType> offsetVector) {
        this.offsetVector = offsetVector;
    }

}
