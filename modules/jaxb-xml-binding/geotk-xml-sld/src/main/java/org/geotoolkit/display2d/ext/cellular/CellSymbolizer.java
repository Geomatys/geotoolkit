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
import java.util.ArrayList;
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
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.se.xml.v110.RuleType;
import org.geotoolkit.se.xml.v110.SymbolizerType;
import org.geotoolkit.sld.xml.StyleXmlIO;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.logging.Logging;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.expression.Expression;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.style.ExtensionSymbolizer;
import org.opengis.style.Rule;
import org.opengis.style.StyleVisitor;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CellularSymbolizerType")
@XmlRootElement(name="CellularSymbolizer",namespace="http://geotoolkit.org")
public class CellSymbolizer extends SymbolizerType implements ExtensionSymbolizer{

    private static final Logger LOGGER = Logging.getLogger(CellSymbolizer.class);
    public static final String NAME = "Cell";
    
    private int cellSize;
    @XmlTransient
    private List<Rule> rules;
    
    @XmlElement(name = "Rule",namespace="http://geotoolkit.org")
    private List<RuleType> jaxRules;


    public CellSymbolizer() {
    }

    public CellSymbolizer(int cellSize, List<? extends Rule> rules){
        this.cellSize = cellSize;
        this.rules = (List)rules;
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

    public int getCellSize() {
        return cellSize;
    }

    public List<Rule> getRules() {
        if(rules != null){
            return rules;
        }
        rules = new ArrayList<Rule>();
        
        if(jaxRules != null){
            final StyleXmlIO util = new StyleXmlIO();
            try {
                for(RuleType rt : jaxRules){
                    final Rule r = util.getTransformer110().visitRule(rt);
                    rules.add(r);
                };
            } catch (FactoryException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
        
        return rules;
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
        if(layer.getCoverageReference()!=null){
            return buildCellType(layer.getCoverageReference());
        }else{
            return buildCellType(layer.getCoverageReader(), 0);
        }
    }
    
    public static SimpleFeatureType buildCellType(CoverageReference ref) throws DataStoreException{
        final GridCoverageReader reader = ref.createReader();
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
            String name = ""+b;
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
    
}
