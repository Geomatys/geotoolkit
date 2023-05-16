/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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


package org.geotoolkit.dif.xml.v102;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 *
 *
 *             | DIF 9      | ECHO 10 | UMM                 | DIF 10                  | Notes                            |
 *             | ---------- | ------- | ------------------- | ----------------------- | -------------------------------- |
 *             | Parent_DIF |    -    | MetadataAssociation | MetadataAssociationType | use type=Parent                  |
 *             |                                                                                                         |
 *             | Parent_DIF |    -    | > Paret_DIF         | > Short_Name            | Now a subfield                   |
 *             |      -     |    -    | > Version           | > Version               | Adding Version as it was missing |
 *
 *
 *
 * <p>Classe Java pour _ParentMetadataType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="_ParentMetadataType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}EntryIDType">
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "_ParentMetadataType")
public class ParentMetadataType
    extends EntryIDType
{


}
