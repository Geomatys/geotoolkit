/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Geomatys
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

package org.geotoolkit.jdbc;

/**
 * Constants defined by JDBC to retrieve a database meta-model.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class MetaDataConstants {

    public static final class Schema{
        /** schema name */
        public static final String TABLE_SCHEM = "TABLE_SCHEM";
        /** catalog name (may be null) */
        public static final String TABLE_CATALOG = "TABLE_CATALOG";
        
        private Schema(){}
    }

    public static final class Table{
        /** table catalog (may be null) */
        public static final String TABLE_CAT = "TABLE_CAT";
        /** table schema (may be null) */
        public static final String TABLE_SCHEM = "TABLE_SCHEM";
        /** table name */
        public static final String TABLE_NAME = "TABLE_NAME";
        /** table type. Typical types are : <br/>
         * "TABLE", "VIEW", "SYSTEM TABLE",
         * "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM". */
        public static final String TABLE_TYPE = "TABLE_TYPE";
        /** explanatory comment on the table */
        public static final String REMARKS = "REMARKS";
        /** the types catalog (may be null) */
        public static final String TYPE_CAT = "TYPE_CAT";
        /** the types schema (may be null) */
        public static final String TYPE_SCHEM = "TYPE_SCHEM";
        /** type name (may be null) */
        public static final String TYPE_NAME = "TYPE_NAME";
        /** name of the designated "identifier" column of a typed table (may be null) */
        public static final String SELF_REFERENCING_COL_NAME = "SELF_REFERENCING_COL_NAME";
        /** specifies how values in SELF_REFERENCING_COL_NAME are created.<br/>
         * Values are "SYSTEM", "USER", "DERIVED". (may be null) */
        public static final String REF_GENERATION =  "REF_GENERATION";

        public static final String VALUE_TYPE_TABLE             = "TABLE";
        public static final String VALUE_TYPE_VIEW              = "VIEW";
        public static final String VALUE_TYPE_SYSTEMTABLE       = "SYSTEM TABLE";
        public static final String VALUE_TYPE_GLOBALTEMPORARY   = "GLOBAL TEMPORARY";
        public static final String VALUE_TYPE_LOCALTEMPORARY    = "LOCAL TEMPORARY";
        public static final String VALUE_TYPE_ALIAS             = "ALIAS";
        public static final String VALUE_TYPE_SYNONYM           = "SYNONYM";

        public static final String VALUE_REFGEN_SYSTEM          = "SYSTEM";
        public static final String VALUE_REFGEN_USER            = "USER";
        public static final String VALUE_REFGEN_DERIVED         = "DERIVED";

        private Table(){}
    }

    public static final class Column{

        /** table catalog (may be null) */
        public static final String TABLE_CAT = "TABLE_CAT";
        /** table schema (may be null) */
        public static final String TABLE_SCHEM = "TABLE_SCHEM";
        /** table name */
        public static final String TABLE_NAME = "TABLE_NAME";
        /** column name */
        public static final String COLUMN_NAME = "COLUMN_NAME";
        /** int : SQL type from java.sql.Types */
        public static final String DATA_TYPE = "DATA_TYPE";
        /** Data source dependent type name, for a UDT the type name is fully qualified */
        public static final String TYPE_NAME = "TYPE_NAME";
        /** int : column size. */
        public static final String COLUMN_SIZE = "COLUMN_SIZE";
        /** not used. */
        public static final String BUFFER_LENGTH = "BUFFER_LENGTH";
        /** int : the number of fractional digits. Null is returned for
         * data types where DECIMAL_DIGITS is not applicable. */
        public static final String DECIMAL_DIGITS = "DECIMAL_DIGITS";
        /** int : Radix (typically either 10 or 2) */
        public static final String NUM_PREC_RADIX = "NUM_PREC_RADIX";
        /** int : is NULL allowed. <br/>
         * columnNoNulls - might not allow NULL values <br/>
         * columnNullable - definitely allows NULL values<br/>
         * columnNullableUnknown - nullability unknown */
        public static final String NULLABLE = "NULLABLE";
        /** comment describing column (may be null) */
        public static final String REMARKS = "REMARKS";
        /** default value for the column, which should be interpreted as a
         * string when the value is enclosed in single quotes (may be null) */
        public static final String COLUMN_DEF = "COLUMN_DEF";
        /** int : unused */
        public static final String SQL_DATA_TYPE = "SQL_DATA_TYPE";
        /** int : unused */
        public static final String SQL_DATETIME_SUB = "SQL_DATETIME_SUB";
        /** int : for char types the maximum number of bytes in the column */
        public static final String CHAR_OCTET_LENGTH = "CHAR_OCTET_LENGTH";
        /** int : index of column in table (starting at 1) */
        public static final String ORDINAL_POSITION = "ORDINAL_POSITION";
        /** ISO rules are used to determine the nullability for a column.<br/>
         * YES --- if the parameter can include NULLs<br/>
         * NO --- if the parameter cannot include NULLs<br/>
         * empty string --- if the nullability for the parameter is unknown */
        public static final String IS_NULLABLE = "IS_NULLABLE";
        /** catalog of table that is the scope of a reference attribute
         * (null if DATA_TYPE isn't REF) */
        public static final String SCOPE_CATLOG = "SCOPE_CATLOG";
        /** schema of table that is the scope of a reference attribute
         * (null if the DATA_TYPE isn't REF) */
        public static final String SCOPE_SCHEMA = "SCOPE_SCHEMA";
        /** table name that this the scope of a reference attribute
         * (null if the DATA_TYPE isn't REF) */
        public static final String SCOPE_TABLE = "SCOPE_TABLE";
        /** short : source type of a distinct type or user-generated Ref type, SQL type
         * from java.sql.Types (null if DATA_TYPE isn't DISTINCT or user-generated REF) */
        public static final String SOURCE_DATA_TYPE = "SOURCE_DATA_TYPE";
        /** Indicates whether this column is auto incremented<br/>
         * YES --- if the column is auto incremented<br/>
         * NO --- if the column is not auto incremented<br/>
         * empty string --- if it cannot be determined whether<br/>
         * the column is auto incremented parameter is unknown
         */
        public static final String IS_AUTOINCREMENT = "IS_AUTOINCREMENT";

        public static final String VALUE_YES = "YES";
        public static final String VALUE_NO = "NO";

        private Column(){}
    }

    private MetaDataConstants(){}

}
