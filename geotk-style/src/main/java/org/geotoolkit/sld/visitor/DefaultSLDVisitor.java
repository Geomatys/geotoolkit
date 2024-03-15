/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.sld.visitor;

import java.util.List;
import org.geotoolkit.style.visitor.DefaultStyleVisitor;
import org.opengis.filter.Filter;
import org.geotoolkit.sld.Constraints;
import org.geotoolkit.sld.CoverageConstraint;
import org.geotoolkit.sld.CoverageExtent;
import org.geotoolkit.sld.Extent;
import org.geotoolkit.sld.FeatureTypeConstraint;
import org.geotoolkit.sld.InlineFeature;
import org.geotoolkit.sld.Layer;
import org.geotoolkit.sld.LayerCoverageConstraints;
import org.geotoolkit.sld.LayerFeatureConstraints;
import org.geotoolkit.sld.LayerStyle;
import org.geotoolkit.sld.NamedLayer;
import org.geotoolkit.sld.NamedStyle;
import org.geotoolkit.sld.RangeAxis;
import org.geotoolkit.sld.RemoteOWS;
import org.geotoolkit.sld.SLDLibrary;
import org.geotoolkit.sld.SLDVisitor;
import org.geotoolkit.sld.Source;
import org.geotoolkit.sld.StyledLayerDescriptor;
import org.geotoolkit.sld.UserLayer;
import org.opengis.style.Style;

/**
 * Abstract implementation of SLDVisitor that simply walks the data structure.
 * <p>
 * This class implements the full SLDVisitor interface and will visit every SLD member of a
 * SLD object. This class performs no actions and is not intended to be used directly, instead
 * extend it and override the methods for the SLD type you are interested in. Remember to call the
 * super method if you want to ensure that the entire sld tree is still visited.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class DefaultSLDVisitor extends DefaultStyleVisitor implements SLDVisitor {

    @Override
    public Object visit(final StyledLayerDescriptor sld, Object data) {
        final List<? extends Layer> layers = sld.layers();
        if(layers != null){
            for(Layer l : layers){
                if(l instanceof NamedLayer){
                    data = ((NamedLayer)l).accept(this, data);
                }else if(l instanceof UserLayer){
                    data = ((UserLayer)l).accept(this, data);
                }
            }
        }
        final List<? extends SLDLibrary> libraries = sld.libraries();
        if(libraries != null){
            for(SLDLibrary lib : libraries){
                data = lib.accept(this, data);
            }
        }
        return data;
    }

    @Override
    public Object visit(final SLDLibrary library, Object data) {
        final StyledLayerDescriptor sld = library.getSLD();
        if(sld != null){
            data = sld.accept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(final NamedLayer layer, Object data) {
        final LayerFeatureConstraints lfc = layer.getConstraints();
        if(lfc != null){
            data = lfc.accept(this, data);
        }
        final List<? extends LayerStyle> styles = layer.styles();
        if(styles != null){
            for(LayerStyle ls : styles){
                if(ls instanceof NamedStyle){
                    data = ((NamedStyle)ls).accept(this, data);
                }else if(ls instanceof Style){
                    data = ((Style)ls).accept(this, data);
                }
            }
        }
        return data;
    }

    @Override
    public Object visit(final UserLayer layer, Object data) {
        final Constraints csts = layer.getConstraints();
        if(csts != null){
            if(csts instanceof LayerCoverageConstraints){
                data = ((LayerCoverageConstraints)csts).accept(this, data);
            }else if(csts instanceof LayerFeatureConstraints){
                data = ((LayerFeatureConstraints)csts).accept(this, data);
            }
        }
        final Source src = layer.getSource();
        if(src != null){
            if(src instanceof RemoteOWS){
                data = ((RemoteOWS)src).accept(this, data);
            }else if(src instanceof InlineFeature){
                data = ((InlineFeature)src).accept(this, data);
            }
        }
        final List<? extends Style> styles = layer.styles();
        if(styles != null){
            for(Style style : styles){
                data = style.accept(this, data);
            }
        }
        return data;
    }

    @Override
    public Object visit(final NamedStyle style, final Object data) {
        return data;
    }

    @Override
    public Object visit(final LayerCoverageConstraints constraints, Object data) {
        final List<? extends CoverageConstraint> lst = constraints.constraints();
        if(lst != null){
            for(CoverageConstraint gc : lst){
                data = gc.accept(this, data);
            }
        }
        return data;
    }

    @Override
    public Object visit(final LayerFeatureConstraints constraints, Object data) {
        final List<? extends FeatureTypeConstraint> lst = constraints.constraints();
        if(lst != null){
            for(FeatureTypeConstraint ftc : lst){
                data = ftc.accept(this, data);
            }
        }
        return data;
    }

    @Override
    public Object visit(final CoverageConstraint constraint, Object data) {
        final CoverageExtent extent = constraint.getCoverageExtent();
        if(extent != null){
            data = extent.accept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(final FeatureTypeConstraint constraint, Object data) {
        final List<Extent> extents = constraint.getExtent();
        if(extents != null){
            for(Extent ext : extents){
                data = ext.accept(this, data);
            }
        }
        final Filter filter = constraint.getFilter();
        if(filter != null){
            visit(filter, data);
        }
        return data;
    }

    @Override
    public Object visit(final CoverageExtent extent, Object data) {
        final List<RangeAxis> axis = extent.rangeAxis();
        if(axis != null){
            for(RangeAxis axi : axis){
                data = axi.accept(this, data);
            }
        }
        return data;
    }

    @Override
    public Object visit(final Extent extent, final Object data) {
        return data;
    }

    @Override
    public Object visit(final RangeAxis axi, final Object data) {
        return data;
    }

    @Override
    public Object visit(final RemoteOWS ows, final Object data) {
        return data;
    }

    @Override
    public Object visit(final InlineFeature inline, final Object data) {
        return data;
    }

}
