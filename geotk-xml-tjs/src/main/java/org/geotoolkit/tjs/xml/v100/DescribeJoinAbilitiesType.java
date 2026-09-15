/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.tjs.xml.v100;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Request to describe to which spatial frameworks this server can join attribute data.
 * The XSD binds the {@code DescribeJoinAbilities} root element directly to
 * {@code tjs:RequestBaseType} with no extension; this subclass exists purely so JAXB has a
 * distinct root-annotated type per the uniform per-operation convention.
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DescribeJoinAbilitiesType")
@XmlRootElement(name = "DescribeJoinAbilities", namespace = "http://www.opengis.net/tjs/1.0")
public class DescribeJoinAbilitiesType extends RequestBaseType {
}
