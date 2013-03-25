/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2013, Geomatys
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
package org.geotoolkit.process.chain.model.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.ObjectConverters;
import org.geotoolkit.process.chain.model.ClassFull;
import org.geotoolkit.util.Converters;
import org.geotoolkit.util.converter.ObjectConverter;

/**
 * Used to find convertion possibilities from two objects.
 *
 * @author Johann Sorel (Geomatys)
 */
public class ConverterMatcher {

    public static ConverterMatcher DEFAULT;
    
    static {
        DEFAULT = new ConverterMatcher(
                    new HashMap<ClassFull, List<ClassFull>>(),
                    new ArrayList<ObjectConverter>());
    }
    
    private final Map<ClassFull, List<ClassFull>> distantConverters;
    private final Collection<ObjectConverter> localConverters;

    public ConverterMatcher(Map<ClassFull, List<ClassFull>> distantConverters,
            Collection<ObjectConverter> localConverters) {
        ArgumentChecks.ensureNonNull("disntantConverters", distantConverters);
        ArgumentChecks.ensureNonNull("localConverters", localConverters);
        this.distantConverters = distantConverters;
        this.localConverters = localConverters;
    }

    /**
     *
     * @param source : expect Class or ClassFullDto
     * @param target : expect Class or ClassFullDto
     * @return true is convertion is possible
     * @throws IllegalArgumentException if source or targer is not an expected type
     */
    public boolean canBeConverted(Object source, Object target) throws IllegalArgumentException{

        if(source == null || target == null){
            return false;
        }

        if(source instanceof Class && target instanceof Class){
            return canBeConverted((Class)source, (Class)target);
        }else if(source instanceof Class && target instanceof ClassFull){
            return canBeConverted(new ClassFull((Class)source), (ClassFull)target);
        }else if(source instanceof ClassFull && target instanceof Class){
            return canBeConverted((ClassFull)source, new ClassFull((Class)target) );
        }else if(source instanceof ClassFull && target instanceof ClassFull){
            return canBeConverted((ClassFull)source, (ClassFull)target);
        }

        throw new IllegalArgumentException("Unexpected types : " + source +" ⇒ "+target);
    }

    private boolean canBeConverted(final Class source, final Class target){

        //check natural possibilities
        if(target.isAssignableFrom(source)){
            //source is already the same or a subclass of target.
            return true;
        }

        for(ObjectConverter oc : localConverters){
            if(oc.getSourceClass().isAssignableFrom(source)
               && oc.getTargetClass().isAssignableFrom(target)){
                return true;
            }
        }
        return false;
    }

    private boolean canBeConverted(final ClassFull source, final ClassFull target){

        //check natural possibilities
        if(isAssignableFrom(source,target)){
            //source is already the same or a subclass of target.
            return true;
        }

        for(Entry<ClassFull, List<ClassFull>> entry : distantConverters.entrySet()){
            if(isAssignableFrom(source,entry.getKey())){
                for(ClassFull cfd : entry.getValue()){
                    if(isAssignableFrom(cfd, target)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @return true if candidate is the same or a child class of reference.
     */
    public static boolean isAssignableFrom(ClassFull candidate, ClassFull reference){
        if(candidate.getName().equals(reference.getName())){
            return true;
        }
        if(candidate.getClasses().contains(reference.getName())){
            return true;
        }
        return false;
    }

}
