/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013-2014, Geomatys
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
import com.vividsolutions.jts.geom.Polygon;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.se.xml.v110.RuleType;
import org.geotoolkit.se.xml.v110.SymbolizerType;
import org.geotoolkit.sld.xml.StyleXmlIO;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;
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
@XmlType(name = "CellSymbolizerType")
@XmlRootElement(name="CellSymbolizer",namespace="http://geotoolkit.org")
public class CellSymbolizer extends SymbolizerType implements ExtensionSymbolizer{

    public static final String PROPERY_GEOM_CENTER = "geom_center";
    public static final String PROPERY_GEOM_CONTOUR = "geom_contour";
    public static final String PROPERY_SUFFIX_COUNT = "_count";
    public static final String PROPERY_SUFFIX_MIN = "_min";
    public static final String PROPERY_SUFFIX_MEAN = "_mean";
    public static final String PROPERY_SUFFIX_MAX = "_max";
    public static final String PROPERY_SUFFIX_RANGE = "_range";
    public static final String PROPERY_SUFFIX_RMS = "_rms";
    public static final String PROPERY_SUFFIX_SUM = "_sum";
    
    private static final Logger LOGGER = Logging.getLogger(CellSymbolizer.class);
    public static final String NAME = "Cell";

    @XmlElement(name = "CellSize",namespace="http://geotoolkit.org")
    private int cellSize;
    
    @XmlElement(name = "Rule", type = RuleType.class)
    private RuleType ruleType;
    
    @XmlTransient
    private Rule rule;


    public CellSymbolizer() {
    }

    public CellSymbolizer(int cellSize, Rule rule){
        this.cellSize = cellSize;
        this.rule = rule;

        final StyleXmlIO util = new StyleXmlIO();
        if(rule!=null){
            this.ruleType = (RuleType) util.getTransformerXMLv110().visit(rule,null);
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

    public Rule getRule() {
        if(rule!=null){
            return rule;
        }

        if(ruleType!=null){
            final StyleXmlIO util = new StyleXmlIO();
            try {
                rule = util.getTransformer110().visitRule(ruleType);
            } catch (FactoryException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            }
        }

        return rule;
    }

    public RuleType getRuleType() {
        return ruleType;
    }

    public void setRuleType(RuleType jaxrule) {
        this.ruleType = jaxrule;
        this.rule = null;
    }

    @Override
    public Map<String, Expression> getParameters() {
        final Map<String,Expression> config = new HashMap<>();
        final Set<String> props = GO2Utilities.propertiesNames(Collections.singleton(getRule()));
        int i=0;
        for(String s : props){
            s = cellToBasePropertyName(s);
            if(s!=null){
                config.put(""+i, GO2Utilities.FILTER_FACTORY.property(s));
            }
            i++;
        }
        return config;
    }

    @Override
    public Object accept(StyleVisitor sv, Object o) {
        return sv.visit(this, o);
    }

    public static String cellToBasePropertyName(String s){
        if(s.endsWith(CellSymbolizer.PROPERY_SUFFIX_COUNT)){
            return s.substring(0, s.length()-CellSymbolizer.PROPERY_SUFFIX_COUNT.length());
        }else if(s.endsWith(CellSymbolizer.PROPERY_SUFFIX_MAX)){
            return s.substring(0, s.length()-CellSymbolizer.PROPERY_SUFFIX_MAX.length());
        }else if(s.endsWith(CellSymbolizer.PROPERY_SUFFIX_MEAN)){
            return s.substring(0, s.length()-CellSymbolizer.PROPERY_SUFFIX_MEAN.length());
        }else if(s.endsWith(CellSymbolizer.PROPERY_SUFFIX_MIN)){
            return s.substring(0, s.length()-CellSymbolizer.PROPERY_SUFFIX_MIN.length());
        }else if(s.endsWith(CellSymbolizer.PROPERY_SUFFIX_RANGE)){
            return s.substring(0, s.length()-CellSymbolizer.PROPERY_SUFFIX_RANGE.length());
        }else if(s.endsWith(CellSymbolizer.PROPERY_SUFFIX_RMS)){
            return s.substring(0, s.length()-CellSymbolizer.PROPERY_SUFFIX_RMS.length());
        }else if(s.endsWith(CellSymbolizer.PROPERY_SUFFIX_SUM)){
            return s.substring(0, s.length()-CellSymbolizer.PROPERY_SUFFIX_SUM.length());
        }else if(CellSymbolizer.PROPERY_GEOM_CENTER.equals(s) || CellSymbolizer.PROPERY_GEOM_CONTOUR.equals(s)){
            return null;
        }else{
            return s;
        }
    }
    
    public static SimpleFeatureType buildCellType(CoverageMapLayer layer) throws DataStoreException{
        return buildCellType(layer.getCoverageReference());
    }

    public static SimpleFeatureType buildCellType(CoverageReference ref) throws DataStoreException{
        final GridCoverageReader reader = ref.acquireReader();
        final SimpleFeatureType sft = buildCellType(reader, ref.getImageIndex());
        ref.recycle(reader);
        return sft;
    }

    public static SimpleFeatureType buildCellType(GridCoverageReader reader, int imageIndex) throws DataStoreException{
        final List<GridSampleDimension> lst = reader.getSampleDimensions(imageIndex);
        final GeneralGridGeometry gg = reader.getGridGeometry(imageIndex);
        final CoordinateReferenceSystem crs = gg.getCoordinateReferenceSystem();
        if(lst!=null){
            final String[] names = new String[lst.size()];
            for(int i=0;i<names.length;i++){
                names[i] = lst.get(i).getDescription().toString();
            }
            return buildCellType(lst.size(), names, crs);
        }else{
            //we need to find the number of bands by some other way
            final GridCoverageReadParam param = new GridCoverageReadParam();
            param.setResolution(gg.getEnvelope().getSpan(0),gg.getEnvelope().getSpan(1));
            final GridCoverage2D cov = (GridCoverage2D) reader.read(0, param);
            final int nbBands = cov.getRenderedImage().getSampleModel().getNumBands();
            return buildCellType(nbBands, null, crs);
        }
    }

    public static SimpleFeatureType buildCellType(GridCoverage2D coverage){
        final int nbBand = coverage.getNumSampleDimensions();
        final GridSampleDimension[] dims = coverage.getSampleDimensions();
        final String[] names = new String[dims.length];
        for(int i=0;i<names.length;i++){
            names[i] = dims[i].getDescription().toString();
        }
        return buildCellType(nbBand, names, coverage.getCoordinateReferenceSystem2D());
    }

    public static SimpleFeatureType buildCellType(int nbBand, String[] bandnames, CoordinateReferenceSystem crs){
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
        final AttributeTypeBuilder atb = new AttributeTypeBuilder();
        atb.setBinding(double.class);

        ftb.setName("cell");
        ftb.add(PROPERY_GEOM_CENTER, Point.class,crs);
        ftb.add(PROPERY_GEOM_CONTOUR, Polygon.class,crs);
        ftb.setDefaultGeometry(PROPERY_GEOM_CENTER);
        
        for(int b=0,n=nbBand;b<n;b++){
            final String name = "band_"+b;
            final String bandName = (bandnames!=null) ? bandnames[b] : "";
            atb.setDescription(bandName);

            adb.setName(name+PROPERY_SUFFIX_COUNT);
            adb.setType(atb.buildType());
            ftb.add(adb.buildDescriptor());

            adb.setName(name+PROPERY_SUFFIX_MIN);
            adb.setType(atb.buildType());
            ftb.add(adb.buildDescriptor());

            adb.setName(name+PROPERY_SUFFIX_MEAN);
            adb.setType(atb.buildType());
            ftb.add(adb.buildDescriptor());

            adb.setName(name+PROPERY_SUFFIX_MAX);
            adb.setType(atb.buildType());
            ftb.add(adb.buildDescriptor());

            adb.setName(name+PROPERY_SUFFIX_RANGE);
            adb.setType(atb.buildType());
            ftb.add(adb.buildDescriptor());

            adb.setName(name+PROPERY_SUFFIX_RMS);
            adb.setType(atb.buildType());
            ftb.add(adb.buildDescriptor());

            adb.setName(name+PROPERY_SUFFIX_SUM);
            adb.setType(atb.buildType());
            ftb.add(adb.buildDescriptor());
        }
        return ftb.buildSimpleFeatureType();
    }
    
    public static SimpleFeatureType buildCellType(final FeatureType basetype){
        final CoordinateReferenceSystem crs = basetype.getCoordinateReferenceSystem();
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
        final AttributeTypeBuilder atb = new AttributeTypeBuilder();
        atb.setBinding(double.class);

        ftb.setName("cell");
        ftb.add(PROPERY_GEOM_CENTER, Point.class,crs);
        ftb.add(PROPERY_GEOM_CONTOUR, Polygon.class,crs);
        ftb.setDefaultGeometry(PROPERY_GEOM_CENTER);
        
        //loop on all properties, extract numeric fields only
        for(PropertyDescriptor desc : basetype.getDescriptors()){
            if(desc instanceof AttributeDescriptor){
                final AttributeDescriptor att = (AttributeDescriptor) desc;
                final Class binding = att.getType().getBinding();
                if(Number.class.isAssignableFrom(binding) || String.class.isAssignableFrom(binding)){
                    final String name = att.getLocalName();
                    atb.setDescription(name);

                    adb.setName(name+PROPERY_SUFFIX_COUNT);
                    adb.setType(atb.buildType());
                    ftb.add(adb.buildDescriptor());

                    adb.setName(name+PROPERY_SUFFIX_MIN);
                    adb.setType(atb.buildType());
                    ftb.add(adb.buildDescriptor());

                    adb.setName(name+PROPERY_SUFFIX_MEAN);
                    adb.setType(atb.buildType());
                    ftb.add(adb.buildDescriptor());

                    adb.setName(name+PROPERY_SUFFIX_MAX);
                    adb.setType(atb.buildType());
                    ftb.add(adb.buildDescriptor());

                    adb.setName(name+PROPERY_SUFFIX_RANGE);
                    adb.setType(atb.buildType());
                    ftb.add(adb.buildDescriptor());

                    adb.setName(name+PROPERY_SUFFIX_RMS);
                    adb.setType(atb.buildType());
                    ftb.add(adb.buildDescriptor());

                    adb.setName(name+PROPERY_SUFFIX_SUM);
                    adb.setType(atb.buildType());
                    ftb.add(adb.buildDescriptor());
                }
            }
        }
        
        return ftb.buildSimpleFeatureType();
    }

    @Override
    public String toString() {
        return "CellSymbolizer";
    }

}
