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
 */
package org.geotoolkit.internal.feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import org.opengis.feature.Operation;
import org.opengis.feature.PropertyType;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class FeatureLoop {

    private FeatureLoop(){}

    public static void loop(Feature feature, Predicate<PropertyType> predicate, BiFunction<PropertyType,Object,Object> function){
        final FeatureType type = feature.getType();
        for(PropertyType pt : type.getProperties(true)){
            if(predicate==null || predicate.test(pt)){
                loop(feature, pt, function);
            }
        }
    }

    public static void loop(Feature feature, PropertyType pt, BiFunction<PropertyType,Object,Object> function){
        final String attName = pt.getName().toString();
        if(pt instanceof AttributeType){
            final AttributeType at = (AttributeType) pt;
            Object val = feature.getPropertyValue(attName);
            if(at.getMaximumOccurs()>1){
                //value is a collection
                if(val instanceof List){
                    //use list iterator to avoid creating a new collection
                    final ListIterator ite = ((List)val).listIterator();
                    while(ite.hasNext()){
                        Object v = ite.next();
                        Object r = function.apply(pt, v);
                        if(v!=r) ite.set(r);
                    }
                }else{
                    Collection c = (Collection) val;
                    //delay the creation of a new collection until it is really necessary
                    List cp = null;
                    int i=0;
                    for(Iterator ite = c.iterator();ite.hasNext();){
                        Object v = ite.next();
                        Object r = function.apply(pt, v);
                        if(v!=r){
                            cp = new ArrayList(c);
                            cp.set(i, r);
                        }
                        i++;
                    }
                    if(cp!=null){
                        feature.setPropertyValue(attName, val);
                    }
                }
            }else{
                //single value
                Object r = function.apply(pt, val);
                if(r!=val){
                    //we do this test, since we know setting a null may
                    //cause the creation of a property is sis feature implementation
                    feature.setPropertyValue(attName, val);
                }
            }
        }else if(pt instanceof FeatureAssociationRole){
            //TODO
        }else if(pt instanceof Operation){
            //NOTE : value is a Property, the bifunction can call the set value when needed
            Object val = feature.getPropertyValue(attName);
            function.apply(pt, val);
        }
    }

}
