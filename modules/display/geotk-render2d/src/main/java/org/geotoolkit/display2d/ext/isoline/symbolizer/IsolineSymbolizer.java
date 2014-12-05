/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.display2d.ext.isoline.symbolizer;

import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.filter.function.other.OtherFunctionFactory;
import org.geotoolkit.process.coverage.isoline2.Isoline2;
import org.geotoolkit.se.xml.v110.*;
import org.geotoolkit.sld.xml.StyleXmlIO;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.style.visitor.ListingPropertyVisitor;
import org.opengis.feature.FeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.style.*;

import javax.measure.quantity.Length;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;
import javax.xml.bind.annotation.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Quentin Boileau (Geomatys)
 */
@XmlType(name = "IsolineSymbolizerType")
@XmlRootElement(name = "IsolineSymbolizer", namespace = "http://geotoolkit.org")
@XmlAccessorType(XmlAccessType.FIELD)
public class IsolineSymbolizer extends SymbolizerType implements ExtensionSymbolizer {

    @XmlTransient
    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    @XmlElement(name = "Raster", namespace = "http://geotoolkit.org")
    public RasterSymbolizerType rasterSymbolizerType;

    @XmlElement(name = "Stroke", namespace = "http://geotoolkit.org")
    public LineSymbolizerType lineSymbolizerType;

    @XmlElement(name = "Text", namespace = "http://geotoolkit.org")
    public TextSymbolizerType textSymbolizerType;

    @XmlElement(name = "IsolineOnly", namespace = "http://geotoolkit.org")
    public Boolean isolineOnly;

    @XmlTransient
    private RasterSymbolizer rasterSymbolizer;
    @XmlTransient
    private LineSymbolizer lineSymbolizer;
    @XmlTransient
    private TextSymbolizer textSymbolizer;

    public IsolineSymbolizer() {
    }

    public IsolineSymbolizer(RasterSymbolizer rasterSymbolizer, LineSymbolizer lineSymbolize) {
        this(rasterSymbolizer, lineSymbolize, null);
    }

    public IsolineSymbolizer(RasterSymbolizer rasterSymbolizer, LineSymbolizer lineSymbolize, TextSymbolizer ts) {
        this(rasterSymbolizer, lineSymbolize, ts, false);
    }

    public IsolineSymbolizer(RasterSymbolizer rasterSymbolizer, LineSymbolizer lineSymbolizer, TextSymbolizer ts, Boolean isolineOnly) {

        this.isolineOnly = isolineOnly;
        setRasterSymbolizer(rasterSymbolizer);
        setLineSymbolizer(lineSymbolizer);

        if (ts == null) {
            this.textSymbolizer = null;
        } else {
            setTextSymbolizer(GO2Utilities.STYLE_FACTORY.textSymbolizer(ts.getName(),
                    ts.getGeometryPropertyName(),
                    ts.getDescription(),
                    ts.getUnitOfMeasure(),
                    FF.function(OtherFunctionFactory.NUMBER_FORMAT, FF.literal("#.000"), FF.property("value")),
                    ts.getFont(),
                    StyleConstants.DEFAULT_POINTPLACEMENT,
                    ts.getHalo(),
                    ts.getFill()));
        }
    }

    public IsolineSymbolizer(IsolineSymbolizer source) {
        if (source != null) {
            setRasterSymbolizer(source.getRasterSymbolizer());
            setLineSymbolizer(source.getLineSymbolizer());
            setTextSymbolizer(source.getTextSymbolizer());
            setIsolineOnly(source.getIsolineOnly());
        }
    }

    @Override
    public String getExtensionName() {
        return "Isoline";
    }

    @Override
    public Map<String, Expression> getParameters() {

        final Map<String, Expression> config = new HashMap<String, Expression>();

        final Set<String> properties = new HashSet<String>();

        if (rasterSymbolizer != null) {
            rasterSymbolizer.accept(ListingPropertyVisitor.VISITOR, properties);
        }
        if (lineSymbolizer != null) {
            lineSymbolizer.accept(ListingPropertyVisitor.VISITOR, properties);
        }
        if (textSymbolizer != null) {
            textSymbolizer.accept(ListingPropertyVisitor.VISITOR, properties);
        }

        int i = 0;
        for (String str : properties) {
            config.put(String.valueOf(i++), FactoryFinder.getFilterFactory(null).property(str));
        }
        return config;
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
    public Expression getGeometry() {
        return null;
    }
    
    @Override
    public Object accept(StyleVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

    public RasterSymbolizerType getRasterSymbolizerType() {
        return rasterSymbolizerType;
    }

    public void setRasterSymbolizerType(RasterSymbolizerType rasterSymbolizerType) {
        this.rasterSymbolizerType = rasterSymbolizerType;
        this.rasterSymbolizer = null;
    }

    public LineSymbolizerType getLineSymbolizerType() {
        return lineSymbolizerType;
    }

    public void setLineSymbolizerType(LineSymbolizerType lineSymbolizerType) {
        this.lineSymbolizerType = lineSymbolizerType;
        this.lineSymbolizer = null;
    }

    public TextSymbolizerType getTextSymbolizerType() {
        return textSymbolizerType;
    }

    public void setTextSymbolizerType(TextSymbolizerType textSymbolizerType) {
        this.textSymbolizerType = textSymbolizerType;
        this.textSymbolizer = null;
    }

    public RasterSymbolizer getRasterSymbolizer() {
        if (rasterSymbolizer != null) {
            return rasterSymbolizer;
        } else if (rasterSymbolizerType != null) {
            final StyleXmlIO util = new StyleXmlIO();
            rasterSymbolizer = util.getTransformer110().visit(rasterSymbolizerType);
            return rasterSymbolizer;
        }
        return null;
    }

    public void setRasterSymbolizer(RasterSymbolizer rasterSymbolizer) {
        this.rasterSymbolizer = rasterSymbolizer;
        if (rasterSymbolizer != null) {
            final StyleXmlIO util = new StyleXmlIO();
            this.rasterSymbolizerType = util.getTransformerXMLv110().visit(rasterSymbolizer, null).getValue();
        }
    }

    public LineSymbolizer getLineSymbolizer() {
        if (lineSymbolizer != null) {
            return lineSymbolizer;
        } else if (lineSymbolizerType != null) {
            final StyleXmlIO util = new StyleXmlIO();
            lineSymbolizer = util.getTransformer110().visit(lineSymbolizerType);
            return lineSymbolizer;
        }
        return null;
    }

    public void setLineSymbolizer(LineSymbolizer lineSymbolizer) {
        this.lineSymbolizer = lineSymbolizer;
        if (lineSymbolizer != null) {
            final StyleXmlIO util = new StyleXmlIO();
            this.lineSymbolizerType = util.getTransformerXMLv110().visit(lineSymbolizer, null).getValue();
        }
    }

    public TextSymbolizer getTextSymbolizer() {
        if (textSymbolizer != null) {
            return textSymbolizer;
        } else if (textSymbolizerType != null) {
            final StyleXmlIO util = new StyleXmlIO();
            textSymbolizer = util.getTransformer110().visit(textSymbolizerType);
            return textSymbolizer;
        }
        return null;
    }

    public void setTextSymbolizer(TextSymbolizer textSymbolizer) {
        this.textSymbolizer = textSymbolizer;
        if (textSymbolizer != null) {
            final StyleXmlIO util = new StyleXmlIO();
            this.textSymbolizerType = util.getTransformerXMLv110().visit(textSymbolizer, null).getValue();
        }
    }

    public Boolean getIsolineOnly() {
        return isolineOnly;
    }

    public void setIsolineOnly(Boolean isolineOnly) {
        this.isolineOnly = isolineOnly;
    }

    /**
     * Get Isoline FeatureType
     * @return
     * @throws DataStoreException
     */
    public static FeatureType buildIsolineType() throws DataStoreException {
        return Isoline2.buildIsolineFeatureType(null);
    }
}
