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
package org.geotoolkit.gui.javafx.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class NextPreviousList<T> {

    private final List<T> list = new ArrayList<>();
    private final int cacheSize;
    /** index points on the current map transform */
    private int index = -1;

    private final ObjectProperty<T> previousProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<T> nextProperty = new SimpleObjectProperty<>();

    public NextPreviousList(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    private T get(int index){
        if(index<0 || index>=list.size()) return null;
        return list.get(index);
    }

    public synchronized void put(T object){
        //check if it's the next or previous value
        if(Objects.equals(get(index+1),object)){
             index = index+1;
        }else if(Objects.equals(get(index-1),object)){
            index = index-1;
        }else{
            while(list.size()>index+1){
                list.remove(index+1);
            }
            list.add(object);
            index = list.size()-1;
        }

        //do not store too much elements
        while(list.size()>cacheSize){
            list.remove(0);
            index = Math.max(0,index-1);
        }

        update();
    }

    private void update(){
        previousProperty.set(get(index-1));
        nextProperty.set(get(index+1));
    }

    public void next(){
        if(index<list.size()){
            index++;
        }
        update();
    }

    public void previous(){
        if(index>=0){
            index--;
        }
        update();
    }

    public ObjectProperty<T> nextProperty() {
        return nextProperty;
    }

    public ObjectProperty<T> previousProperty() {
        return previousProperty;
    }

}
