/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 * Copyright 2014 Geomatys.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.geotoolkit.processing.chain.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @since 0.8
 */
@XmlRootElement(name="StringList")
@XmlAccessorType(XmlAccessType.FIELD)
public class StringList {

    @XmlElement(name="Entry")
    private Collection<String> list;

    public StringList() {

    }

    public StringList(final Collection<String> list) {
        this.list = list;
    }

    public Collection<String> getList() {
        if(list == null){
            list = new ArrayList<>();
        }
        return list;
    }

    public void setList(final Collection<String> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[StringList]:\n");
        if (list != null) {
            for (String s : list) {
                sb.append(s).append(",");
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof StringList) {
            final StringList that = (StringList) obj;
            return Objects.equals(this.list, that.list);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.list != null ? this.list.hashCode() : 0);
        return hash;
    }
}
