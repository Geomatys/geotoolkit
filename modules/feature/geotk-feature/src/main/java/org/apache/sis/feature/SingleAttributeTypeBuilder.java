/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
 *
 */
package org.apache.sis.feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.apache.sis.feature.AbstractIdentifiedType.*;
import org.geotoolkit.util.NamesExt;
import org.opengis.feature.AttributeType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;
import org.apache.sis.internal.feature.AttributeConvention;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class SingleAttributeTypeBuilder {

    private final Map parameters = new HashMap();
    private final List<AttributeType> atts = new ArrayList<>();
    private Class valueClass;
    private int minimumOccurs;
    private int maximumOccurs;
    private Object defaultValue;

    public SingleAttributeTypeBuilder() {
        minimumOccurs = 1;
        maximumOccurs = 1;
    }

    public SingleAttributeTypeBuilder reset(){
        parameters.clear();
        atts.clear();
        valueClass = null;
        minimumOccurs = 1;
        maximumOccurs = 1;
        defaultValue = null;
        return this;
    }

    public SingleAttributeTypeBuilder copy(AttributeType base){
        setName(base.getName());
        setDefinition(base.getDefinition());
        setDescription(base.getDescription());
        setDesignation(base.getDesignation());
        atts.addAll(base.characteristics().values());
        valueClass = base.getValueClass();
        minimumOccurs = base.getMinimumOccurs();
        maximumOccurs = base.getMaximumOccurs();
        defaultValue = base.getDefaultValue();
        return this;
    }

    public SingleAttributeTypeBuilder setName(String localPart){
        this.setName(null,localPart);
        return this;
    }

    public SingleAttributeTypeBuilder setName(String namespace, String localPart){
        setName(NamesExt.create(namespace, localPart));
        return this;
    }

    public SingleAttributeTypeBuilder setName(GenericName name) {
        parameters.put(DefaultAttributeType.NAME_KEY, name);
        return this;
    }

    public GenericName getName(){
        return GenericName.class.cast(parameters.get(DefaultAttributeType.NAME_KEY));
    }

    public SingleAttributeTypeBuilder setDescription(CharSequence description){
        parameters.put(DESCRIPTION_KEY, description);
        return this;
    }

    public CharSequence getDescription(){
        return CharSequence.class.cast(parameters.get(DESCRIPTION_KEY));
    }

    public SingleAttributeTypeBuilder setDesignation(CharSequence designation){
        parameters.put(DESIGNATION_KEY, designation);
        return this;
    }

    public CharSequence getDesignation(){
        return CharSequence.class.cast(parameters.get(DESIGNATION_KEY));
    }

    public SingleAttributeTypeBuilder setDefinition(CharSequence definition){
        parameters.put(DEFINITION_KEY, definition);
        return this;
    }

    public CharSequence getDefinition(){
        return CharSequence.class.cast(parameters.get(DEFINITION_KEY));
    }

    public SingleAttributeTypeBuilder setValueClass(Class valueClass) {
        this.valueClass = valueClass;
        return this;
    }

    public Class getValueClass(){
        return valueClass;
    }

    public SingleAttributeTypeBuilder setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public SingleAttributeTypeBuilder setMinimumOccurs(int minimumOccurs) {
        this.minimumOccurs = minimumOccurs;
        return this;
    }

    public int getMinimumOccurs() {
        return minimumOccurs;
    }

    public SingleAttributeTypeBuilder setMaximumOccurs(int maximumOccurs) {
        this.maximumOccurs = maximumOccurs;
        return this;
    }

    public int getMaximumOccurs() {
        return maximumOccurs;
    }

    /**
     * Set maximum string length
     * @param length
     * @return created attribute
     */
    public SingleAttributeTypeBuilder setLength(int length){
        return addCharacteristic(AttributeConvention.MAXIMAL_LENGTH_CHARACTERISTIC, Integer.class, 0, 1, length);
    }

    public SingleAttributeTypeBuilder setCRS(CoordinateReferenceSystem crs){
        return addCharacteristic(AttributeConvention.CRS_CHARACTERISTIC, CoordinateReferenceSystem.class, 0, 1, crs);
    }

    public SingleAttributeTypeBuilder setPossibleValues(Collection values){
        return addCharacteristic(AttributeConvention.VALID_VALUES_CHARACTERISTIC, Object.class, 0, 1, values);
    }

    public SingleAttributeTypeBuilder addCharacteristic(String localPart, Class valueClass, int minimumOccurs, int maximumOccurs, Object defaultValue){
        final GenericName name = NamesExt.create(localPart);
        return addCharacteristic(name,valueClass,minimumOccurs,maximumOccurs,defaultValue);
    }

    public SingleAttributeTypeBuilder addCharacteristic(GenericName name, Class valueClass, int minimumOccurs, int maximumOccurs, Object defaultValue){
        return addCharacteristic(new DefaultAttributeType(
                    Collections.singletonMap(NAME_KEY, name),
                    valueClass,minimumOccurs,maximumOccurs,defaultValue));
    }

    public SingleAttributeTypeBuilder addCharacteristic(AttributeType characteristic){
        //search and remove previous characteristic with the same id if it exist
        for(AttributeType at : atts){
            if(at.getName().equals(characteristic.getName())){
                atts.remove(at);
                break;
            }
        }
        atts.add(characteristic);
        return this;
    }

    public AttributeType build(){
        return new DefaultAttributeType(parameters, valueClass,
                minimumOccurs, maximumOccurs,
                defaultValue, atts.toArray(new AttributeType[atts.size()]));
    }

    public static AttributeType create(GenericName name, Class valueClass) {
        return new DefaultAttributeType(Collections.singletonMap("name", name), valueClass, 1, 1, null);
    }

}
