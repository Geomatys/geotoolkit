/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.apache.sis.feature;

import com.vividsolutions.jts.geom.Geometry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.metadata.iso.citation.Citations;
import org.apache.sis.parameter.DefaultParameterDescriptorGroup;
import org.apache.sis.referencing.CRS;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.geometry.jts.transform.CoordinateSequenceMathTransformer;
import org.geotoolkit.geometry.jts.transform.GeometryCSTransformer;
import org.opengis.feature.Attribute;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureInstantiationException;
import org.opengis.feature.FeatureOperationException;
import org.opengis.feature.FeatureType;
import org.opengis.feature.IdentifiedType;
import org.opengis.feature.Operation;
import org.opengis.feature.Property;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;
import org.opengis.metadata.Identifier;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;
import org.opengis.util.InternationalString;
import org.opengis.util.ScopedName;

/**
 * FeatureType implementation which define a reprojected view of a reference
 * feature type.
 * 
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class ReprojectFeatureType implements DecoratedFeatureType {
/**
     * @todo this is a copy of {@code LinkOperation} package private method.
     */
    static ParameterDescriptorGroup parameters(final String name, final int minimumOccurs,
            final ParameterDescriptor<?>... parameters)
    {
        final Map<String,Object> properties = new HashMap<>(4);
        properties.put(ParameterDescriptorGroup.NAME_KEY, name);
        properties.put(Identifier.AUTHORITY_KEY, Citations.SIS);
        return new DefaultParameterDescriptorGroup(properties, minimumOccurs, 1);
    }
    private static final ParameterDescriptorGroup EMPTY_PARAMS = parameters("Reproject", 1);
    private static final PropertyType AMBIGUOUS = new DefaultAttributeType(Collections.singletonMap("name", "ambiguous"),Object.class,0,0,null);

    private final FeatureType base;
    private final Set<GenericName> fullNames = new HashSet<>();
    private final Map<String,PropertyType> names = new HashMap<>();
    private final CoordinateReferenceSystem targetCRS;
    private boolean isSimple;
    
    private final Map<CoordinateReferenceSystem,GeometryCSTransformer> cache = new HashMap<>();

    /**
     * Filter feature type properties.
     *
     * @param base reference feature type
     * @param targetCRS wanted geometries crs
     */
    public ReprojectFeatureType(FeatureType base, final CoordinateReferenceSystem targetCRS) {
        ArgumentChecks.ensureNonNull("type", base);
        this.base = base;
        this.targetCRS = targetCRS;

        isSimple = true;
        for (PropertyType property : base.getProperties(true)) {

            final int minimumOccurs, maximumOccurs;
            if (property instanceof AttributeType<?>) {
                minimumOccurs = ((AttributeType<?>) property).getMinimumOccurs();
                maximumOccurs = ((AttributeType<?>) property).getMaximumOccurs();
                isSimple &= (minimumOccurs == maximumOccurs);
            } else if (property instanceof FeatureAssociationRole) {
                minimumOccurs = ((FeatureAssociationRole) property).getMinimumOccurs();
                maximumOccurs = ((FeatureAssociationRole) property).getMaximumOccurs();
                isSimple = false;
            } else {
                minimumOccurs = 0;
                maximumOccurs = 0;
            }
            if (maximumOccurs != 0) {
                isSimple &= (maximumOccurs == 1);
            }

            //replace operations by ViewOperation
            if (AttributeConvention.isGeometryAttribute(property)) {
                property = new ReprojectOperation(property);
            } else if(property instanceof Operation) {
                property = new DecoratedOperation((Operation) property);
            }
            

            final GenericName fullName = property.getName();
            fullNames.add(fullName);
            GenericName name = fullName;
            names.put(name.toString(), property);
            while (name instanceof ScopedName) {
                name = ((ScopedName)name).tail();
                if(names.containsKey(name.toString())){
                    //name is ambigus
                    names.put(name.toString(), AMBIGUOUS);
                    break;
                }else{
                    names.put(name.toString(), property);
                }
            }
            //name tip part
            if(!names.containsKey(name.toString())){
                names.put(name.toString(), property);
            }
        }
    }

    @Override
    public FeatureType getDecoratedType() {
        return base;
    }

    /**
     * Redirect to wrapped feature type.
     *
     * @return name
     */
    @Override
    public GenericName getName() {
        return base.getName();
    }

    /**
     * Redirect to wrapped feature type.
     *
     * @return abstract
     */
    @Override
    public boolean isAbstract() {
        return base.isAbstract();
    }

    /**
     *
     * @return true if type is simple
     */
    @Override
    public boolean isSimple() {
        return isSimple;
    }

    /**
     * Redirect to wrapped feature type.
     *
     * @return definition
     */
    @Override
    public InternationalString getDefinition() {
        return base.getDefinition();
    }

    /**
     * Redirect to wrapped feature type.
     *
     * @return designation
     */
    @Override
    public InternationalString getDesignation() {
        return base.getDesignation();
    }

    /**
     * Redirect to wrapped feature type.
     *
     * @return description
     */
    @Override
    public InternationalString getDescription() {
        return base.getDescription();
    }

    @Override
    public PropertyType getProperty(String name) throws PropertyNotFoundException {
        final PropertyType type = names.get(name);
        if (type==null) {
            throw new PropertyNotFoundException("No property for name "+name);
        }else if (type==AMBIGUOUS) {
            throw new PropertyNotFoundException("Ambiguous name "+name);
        }
        return type;
    }

    @Override
    public Collection<? extends PropertyType> getProperties(boolean includeSuperTypes) {
        final Collection<PropertyType> properties = new ArrayList<>();
        final Collection<? extends PropertyType> basePropertiers = base.getProperties(includeSuperTypes);
        for (PropertyType pt : basePropertiers) {
            if(fullNames.contains(pt.getName())){
                properties.add(getProperty(pt.getName().toString()));
            }
        }
        return properties;
    }

    @Override
    public Set<? extends FeatureType> getSuperTypes() {
        return Collections.EMPTY_SET;
    }

    /**
     * NOTE : copied and modified from DefaultFeatureType.isAssignableFrom
     *
     * Returns {@code true} if this type is same or a super-type of the given type.
     * The check is based mainly on the feature type {@linkplain #getName() name}, which should be unique.
     * 
     * <div class="note"><b>Analogy:</b>
     * if we compare {@code FeatureType} to {@link Class} in the Java language, then this method is equivalent
     * to {@link Class#isAssignableFrom(Class)}.</div>
     *
     * @param  type  the type to be checked.
     * @return {@code true} if instances of the given type can be assigned to association of this type.
     */
    @Override
    public boolean isAssignableFrom(final FeatureType type) {
        if (type == this) {
            return true; // Optimization for a common case.
        }
        ArgumentChecks.ensureNonNull("type", type);
        return maybeAssignableFrom(this, type);
    }

    /**
     * NOTE : copied and modified from DefaultFeatureType.maybeAssignableFrom
     *
     * Returns {@code true} if the given base type may be the same or a super-type of the given type, using only
     * the name as a criterion. This is a faster check than {@link #isAssignableFrom(FeatureType)}.
     *
     * <p>Performance note: callers should verify that {@code base != type} before to invoke this method.</p>
     */
    static boolean maybeAssignableFrom(final FeatureType base, final FeatureType type) {
        // Slower path for non-SIS implementations.
        if (Objects.equals(base.getName(), type.getName())) {
            return true;
        }
        for (final FeatureType superType : type.getSuperTypes()) {
            if (base == superType || maybeAssignableFrom(base, superType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Feature newInstance() throws FeatureInstantiationException, UnsupportedOperationException {
        return newInstance(base.newInstance());
    }

    public Feature newInstance(Feature base) throws FeatureInstantiationException, UnsupportedOperationException {
        return new ReprojectFeature(base);
    }

    private static GeometryCSTransformer getTransform(CoordinateReferenceSystem baseCrs, CoordinateReferenceSystem targetCrs) throws FactoryException{
        final CoordinateSequenceMathTransformer trs =
                new CoordinateSequenceMathTransformer(CRS.findOperation(baseCrs, targetCrs, null).getMathTransform());
        final GeometryCSTransformer transformer = new GeometryCSTransformer(trs);
        return transformer;
    }

    private Geometry reproject(Geometry val, CoordinateReferenceSystem declaredCrs) throws FactoryException, TransformException {
        if (val == null) return val;

        if(declaredCrs==null){
            //extract the crs from the geometry
            try {
                declaredCrs = JTS.findCoordinateReferenceSystem((Geometry)val);
                if(declaredCrs==targetCRS) return val;
            } catch (FactoryException ex) {
                //we don't know the original crs, we can't transform
                return val;
            }
        }

        if(declaredCrs==null){
            Logging.getLogger("org.geotoolkit.feature").log(Level.WARNING,
                    "A feature geometry property in type "+base.getName() +" has no crs.");
            return val;
        }

        GeometryCSTransformer trs = cache.get(declaredCrs);
        if(trs==null){
            trs = getTransform(declaredCrs, targetCRS);
            cache.put(declaredCrs, trs);
        }

        Geometry geom = trs.transform(val);
        JTS.setCRS(geom, targetCRS);
        return geom;
        
    }

    @Override
    public String toString() {
        return FeatureFormat.sharedFormat(this);
    }

    private class ReprojectOperation implements Operation {

        private final PropertyType base;
        private final AttributeType result;
        private final CoordinateReferenceSystem declatedCrs;

        public ReprojectOperation(PropertyType base) {
            this.base = base;

            IdentifiedType result = base;
            while (result instanceof Operation) {
                result = ((Operation)result).getResult();
            }
            final AttributeType res = (AttributeType) result;
            declatedCrs = FeatureExt.getCRS(res);
            final SingleAttributeTypeBuilder atb = new SingleAttributeTypeBuilder();
            atb.copy(res);
            atb.addCharacteristic(AttributeConvention.CRS_CHARACTERISTIC, CoordinateReferenceSystem.class, 0, 1, targetCRS);
            this.result = atb.build();
        }

        @Override
        public GenericName getName() {
            return base.getName();
        }

        @Override
        public InternationalString getDefinition() {
            return base.getDefinition();
        }

        @Override
        public InternationalString getDesignation() {
            return base.getDesignation();
        }

        @Override
        public InternationalString getDescription() {
            return base.getDescription();
        }

        @Override
        public ParameterDescriptorGroup getParameters() {
            if (base instanceof Operation) {
                return ((Operation)base).getParameters();
            } else {
                return EMPTY_PARAMS;
            }
        }

        @Override
        public IdentifiedType getResult() {
            return result;
        }

        @Override
        public Property apply(Feature feature, ParameterValueGroup parameters) {
            if (feature instanceof ReprojectFeature) {
                final Geometry value = (Geometry) ((ReprojectFeature)feature).base.getPropertyValue(base.getName().toString());
                final Attribute att = result.newInstance();
                try {
                    att.setValue(reproject(value, declatedCrs));
                } catch (FactoryException | TransformException ex) {
                    //TODO replace with geoapi OperationException when available
                    throw new RuntimeException(ex.getMessage(),ex);
                }
                return att;
            }else{
                throw new IllegalArgumentException("Invalid input feature, was expecting a feature of type "+ReprojectFeatureType.this.getName());
            }
        }
    }

    private class ReprojectFeature extends AbstractFeature implements DecoratedFeature {

        private final Feature base;

        private ReprojectFeature(Feature base) {
            super(ReprojectFeatureType.this);
            this.base = base;
        }

        @Override
        public DecoratedFeatureType getType() {
            return ReprojectFeatureType.this;
        }

        @Override
        public Feature getDecoratedFeature() {
            return base;
        }

        @Override
        public Object getPropertyValue(String name) throws PropertyNotFoundException {
            final PropertyType prop = names.get(name);
            if(prop instanceof Operation){
                return getOperationValue(name);
            }
            return base.getPropertyValue(name);
        }

        @Override
        public void setPropertyValue(String name, Object value) throws IllegalArgumentException {
            final PropertyType prop = names.get(name);
            if(prop instanceof Operation){
                setOperationValue(name,value);
            }
            base.setPropertyValue(name, value);
        }

    }

}
