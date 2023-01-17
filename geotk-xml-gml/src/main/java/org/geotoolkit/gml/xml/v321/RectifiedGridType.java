/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2012, Geomatys
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


package org.geotoolkit.gml.xml.v321;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.opengis.coverage.grid.RectifiedGrid;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.TransformException;


/**
 * <p>Java class for RectifiedGridType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="RectifiedGridType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}GridType">
 *       &lt;sequence>
 *         &lt;element name="origin" type="{http://www.opengis.net/gml/3.2}PointPropertyType"/>
 *         &lt;element name="offsetVector" type="{http://www.opengis.net/gml/3.2}VectorType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RectifiedGridType", propOrder = {
    "origin",
    "offsetVector"
})
@XmlRootElement(name = "RectifiedGrid")
public class RectifiedGridType extends GridType {

    @XmlElement(required = true)
    private PointPropertyType origin;
    @XmlElement(required = true)
    private List<VectorType> offsetVector;

    public RectifiedGridType() {

    }

    public RectifiedGridType(final RectifiedGrid grid) {
        this(grid, null);
    }

    public RectifiedGridType(final RectifiedGrid grid, final CoordinateReferenceSystem crs) {
       super(grid, crs);
       if (grid != null) {
           PointType oriPt = new PointType(grid.getOrigin(), false);
           oriPt.setId("pt-" + new Random().nextInt()); // for xml validation
           origin          = new PointPropertyType(oriPt);
           offsetVector    = new ArrayList<>();

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
           PointType oriPt = new PointType(ori, false);
           oriPt.setId("pt-" + new Random().nextInt()); // for xml validation
           origin          = new PointPropertyType(oriPt);
           offsetVector    = new ArrayList<>();

           Matrix m = MathTransforms.getMatrix(gridToCRS);
           if (m == null) {
               m = MathTransforms.getMatrix(gg.reduce(0,1).getGridToCRS(PixelInCell.CELL_CORNER));
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
    public PointPropertyType getOrigin() {
        return origin;
    }

    public void setOrigin(final PointPropertyType origin) {
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
