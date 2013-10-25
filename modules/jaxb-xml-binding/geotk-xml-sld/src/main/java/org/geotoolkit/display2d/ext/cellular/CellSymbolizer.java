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
package org.geotoolkit.display2d.ext.cellular;

import com.vividsolutions.jts.geom.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.quantity.Length;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.ogc.xml.v110.FilterType;
import org.geotoolkit.se.xml.v110.PointSymbolizerType;
import org.geotoolkit.se.xml.v110.SymbolizerType;
import org.geotoolkit.se.xml.v110.TextSymbolizerType;
import org.geotoolkit.sld.xml.StyleXmlIO;
import org.apache.sis.util.logging.Logging;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.Expression;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.style.ExtensionSymbolizer;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.StyleVisitor;
import org.opengis.style.TextSymbolizer;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CellSymbolizerType")
@XmlRootElement(name="CellSymbolizer",namespace="http://geotoolkit.org")
public class CellSymbolizer extends SymbolizerType implements ExtensionSymbolizer{

    private static final Logger LOGGER = Logging.getLogger(CellSymbolizer.class);
    public static final String NAME = "Cell";

    @XmlElement(name = "CellSize",namespace="http://geotoolkit.org")
    private int cellSize;
    @XmlTransient
    private Filter filter;
    @XmlTransient
    private PointSymbolizer pointSymbolizer;
    @XmlTransient
    private TextSymbolizer textSymbolizer;

    @XmlElement(name = "Filter", namespace = "http://www.opengis.net/ogc")
    private FilterType filterType;

    @XmlElement(name = "PointSymbolizer",namespace="http://geotoolkit.org")
    private PointSymbolizerType pointSymbolizerType;
    @XmlElement(name = "TextSymbolizer",namespace="http://geotoolkit.org")
    private TextSymbolizerType textSymbolizerType;


    public CellSymbolizer() {
    }

    public CellSymbolizer(int cellSize, Filter filter, PointSymbolizer ps, TextSymbolizer ts){
        this.cellSize = cellSize;
        this.filter = filter;
        this.pointSymbolizer = ps;
        this.textSymbolizer = ts;

        final StyleXmlIO util = new StyleXmlIO();
        if(filter!=null){
            this.filterType = util.getTransformerXMLv110().visit(filter);
        }
        if(ps!=null){
            this.pointSymbolizerType = util.getTransformerXMLv110().visit(ps,null).getValue();
        }
        if(ts!=null){
            this.textSymbolizerType = util.getTransformerXMLv110().visit(ts,null).getValue();
        }

    }

    @Override
    public Unit<Length> getUnitOfMeasure() {
        return NonSI.PIXEL;
    }

    @Override
    public String getGeometryPropertyName() {
        return null;
    }

    @Override
    public String getExtensionName() {
        return NAME;
    }

    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
    }

    public int getCellSize() {
        return cellSize;
    }

    public Filter getFilter() {
        if(filter!=null){
            return filter;
        }

        if(filterType!=null){
            final StyleXmlIO util = new StyleXmlIO();
            try {
                filter = util.getTransformer110().visitFilter(filterType);
            } catch (FactoryException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            }
        }

        return filter;
    }

    public PointSymbolizer getPointSymbolizer() {
        if(pointSymbolizer!=null){
            return pointSymbolizer;
        }

        if(pointSymbolizerType!=null){
            final StyleXmlIO util = new StyleXmlIO();
            pointSymbolizer = util.getTransformer110().visit(pointSymbolizerType);
        }

        return pointSymbolizer;
    }

    public TextSymbolizer getTextSymbolizer() {
        if(textSymbolizer!=null){
            return textSymbolizer;
        }

        if(textSymbolizerType!=null){
            final StyleXmlIO util = new StyleXmlIO();
            textSymbolizer = util.getTransformer110().visit(textSymbolizerType);
        }

        return textSymbolizer;
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public void setfilterType(FilterType jaxfilter) {
        this.filterType = jaxfilter;
        this.filter = null;
    }

    public PointSymbolizerType getPointSymbolizerType() {
        return pointSymbolizerType;
    }

    public void setPointSymbolizerType(PointSymbolizerType jaxpointSymbolizer) {
        this.pointSymbolizerType = jaxpointSymbolizer;
        this.pointSymbolizer = null;
    }

    public TextSymbolizerType getTextSymbolizerType() {
        return textSymbolizerType;
    }

    public void setTextSymbolizerType(TextSymbolizerType jaxtextSymbolizer) {
        this.textSymbolizerType = jaxtextSymbolizer;
        this.textSymbolizer = null;
    }

    @Override
    public Map<String, Expression> getParameters() {
        final Map<String,Expression> config = new HashMap<String, Expression>();
        return config;
    }

    @Override
    public Object accept(StyleVisitor sv, Object o) {
        return sv.visit(this, o);
    }

    public static SimpleFeatureType buildCellType(CoverageMapLayer layer) throws DataStoreException{
        return buildCellType(layer.getCoverageReference());
    }

    public static SimpleFeatureType buildCellType(CoverageReference ref) throws DataStoreException{
        final GridCoverageReader reader = ref.acquireReader();
        try{
            return buildCellType(reader, ref.getImageIndex());
        }finally{
            //reader.dispose();
        }
    }

    public static SimpleFeatureType buildCellType(GridCoverageReader reader, int imageIndex) throws DataStoreException{
        final List<GridSampleDimension> lst = reader.getSampleDimensions(imageIndex);
        final GeneralGridGeometry gg = reader.getGridGeometry(imageIndex);
        final CoordinateReferenceSystem crs = gg.getCoordinateReferenceSystem();
        if(lst!=null){
            return buildCellType(lst.size(), crs);
        }else{
            //we need to find the number of bands by some other way
            final GridCoverageReadParam param = new GridCoverageReadParam();
            param.setResolution(gg.getEnvelope().getSpan(0),gg.getEnvelope().getSpan(1));
            final GridCoverage2D cov = (GridCoverage2D) reader.read(0, param);
            final int nbBands = cov.getRenderedImage().getSampleModel().getNumBands();
            return buildCellType(nbBands, crs);
        }
    }

    public static SimpleFeatureType buildCellType(GridCoverage2D coverage){
        final int nbBand = coverage.getNumSampleDimensions();
        return buildCellType(nbBand, coverage.getCoordinateReferenceSystem2D());
    }

    public static SimpleFeatureType buildCellType(int nbBand, CoordinateReferenceSystem crs){
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("cell");
        ftb.add("geom", Point.class,crs);
        for(int b=0,n=nbBand;b<n;b++){
            String name = "band_"+b;
            ftb.add(name+"_count",double.class);
            ftb.add(name+"_min",double.class);
            ftb.add(name+"_mean",double.class);
            ftb.add(name+"_max",double.class);
            ftb.add(name+"_range",double.class);
            ftb.add(name+"_rms",double.class);
            ftb.add(name+"_sum",double.class);
        }
        return ftb.buildSimpleFeatureType();
    }

    @Override
    public String toString() {
        return "CellSymbolizer";
    }

}
