/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.storage.dggs;

import java.util.Objects;

/**
 * A zone unique identifier.
 *
 * @author Johann Sorel (Geomatys)
 * @todo not sure yet if we must have two differen types or just one with two acces methods
 */
public class ZonalIdentifier {

    public static class Text extends ZonalIdentifier {
        public final String id;

        public Text(String id) {
            this.id = id;
        }

        public String getValue() {
            return id;
        }

        @Override
        public String toString() {
            return id;
        }

        @Override
        public int hashCode() {
            return 23 * 7 + Objects.hashCode(this.id);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Text other = (Text) obj;
            return Objects.equals(this.id, other.id);
        }
    }

    public static class Long extends ZonalIdentifier {
        public final long id;

        public Long(long id) {
            this.id = id;
        }

        public long getValue() {
            return id;
        }

        @Override
        public String toString() {
            return java.lang.Long.toUnsignedString(id);
        }

        @Override
        public int hashCode() {
            return 67 * 5 + (int) (this.id ^ (this.id >>> 32));
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Long other = (Long) obj;
            return this.id == other.id;
        }

    }

}
