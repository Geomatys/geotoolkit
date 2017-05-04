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

package org.geotoolkit.db;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.sis.feature.AbstractAssociation;
import org.apache.sis.feature.AbstractOperation;
import org.apache.sis.feature.DefaultAssociationRole;
import org.apache.sis.feature.DefaultAttributeType;
import org.apache.sis.feature.FeatureExt;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.metadata.iso.citation.Citations;
import org.apache.sis.parameter.DefaultParameterDescriptorGroup;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.db.reverse.RelationMetaModel;
import org.geotoolkit.util.NamesExt;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import org.opengis.feature.IdentifiedType;
import org.opengis.feature.InvalidPropertyValueException;
import org.opengis.feature.MultiValuedPropertyException;
import org.opengis.feature.Property;
import org.opengis.metadata.Identifier;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DBRelationOperation extends AbstractOperation {

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

    private static final ParameterDescriptorGroup EMPTY_PARAMS = parameters("JDBCRelation", 1);

    private final JDBCFeatureStore store;
    private final RelationMetaModel relation;
    private String typeName;
    private FeatureAssociationRole type;

    public DBRelationOperation(GenericName name, JDBCFeatureStore store, RelationMetaModel template, String typeName){
        super(Collections.singletonMap(DefaultAttributeType.NAME_KEY, name));
        this.store = store;
        this.relation = template;
        this.typeName = typeName;
    }

    public RelationMetaModel getRelation() {
        return relation;
    }

    @Override
    public ParameterDescriptorGroup getParameters() {
        return EMPTY_PARAMS;
    }

    @Override
    public IdentifiedType getResult() {
        if (type==null) {
            final GenericName name = getName();
            FeatureType valueType = null;
            try{
                valueType = store.getDatabaseModel().getFeatureType(typeName);
            }catch (DataStoreException ex) {
                return new FeatureTypeBuilder().setName("unset").build();
            }

            if(relation.isImported()){
                type = new DefaultAssociationRole(Collections.singletonMap("name", name), valueType, 0, 1);
            }else{
                AttributeType att = (AttributeType) valueType.getProperty(relation.getForeignColumn());
                final boolean unique = FeatureExt.getCharacteristicValue(att, JDBCFeatureStore.JDBC_PROPERTY_UNIQUE.getName().toString(), Boolean.FALSE);
                type = new DefaultAssociationRole(Collections.singletonMap("name", name), valueType, 0, unique ? 1 : Integer.MAX_VALUE);
            }
        }

        return type;
    }

    @Override
    public Property apply(Feature ftr, ParameterValueGroup pvg) {

        final Object key = ftr.getPropertyValue(relation.getCurrentColumn());
        final QueryBuilder qb = new QueryBuilder();
        qb.setTypeName(NamesExt.create(store.getDefaultNamespace(), relation.getForeignTable()));
        qb.setFilter(relation.toFilter(key));
        final FeatureCollection res = store.createSession(false).getFeatureCollection(qb.buildQuery());
        final Object value;
        if(type.getMaximumOccurs()==1){
            try (FeatureIterator ite = res.iterator()) {
                if(ite.hasNext()){
                    value = ite.next();
                }else{
                    value = null;
                }
            }
        }else{
            value = res;
        }

        return new AbstractAssociation(type) {
            @Override
            public Feature getValue() throws MultiValuedPropertyException {
                if(value==null || value instanceof Feature){
                    return (Feature) value;
                }
                throw new MultiValuedPropertyException();
            }

            @Override
            public Collection<Feature> getValues() {
                if(value==null){
                    return Collections.EMPTY_LIST;
                } else if(value instanceof Feature){
                    return Arrays.asList((Feature)value);
                }else{
                    return res;
                }
            }

            @Override
            public void setValue(Feature value) throws InvalidPropertyValueException {
                throw new UnsupportedOperationException("Not supported.");
            }
        };
    }

}
