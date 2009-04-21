--
-- Additional indexes for the EPSG database. Those indexes are not declared
-- in the SQL scripts distributed by EPSG. They are not required for proper
-- working of the EPSG factory, but can significantly improve performances.
--

--------------------------------------------------------------------------------
-- Index for queries used by DirectEpsgFactory.createFoo(epsgcode) methods.   --
-- Indexed fields are numeric values used mainly in equality comparisons.     --
--------------------------------------------------------------------------------
CREATE INDEX ix_alias_object_code                ON epsg_alias                     (object_code);
CREATE INDEX ix_crs_datum_code                   ON epsg_coordinatereferencesystem (datum_code);
CREATE INDEX ix_crs_projection_code              ON epsg_coordinatereferencesystem (projection_conv_code);
CREATE INDEX ix_coordinate_axis_code             ON epsg_coordinateaxis            (coord_axis_code);
CREATE INDEX ix_coordinate_axis_sys_code         ON epsg_coordinateaxis            (coord_sys_code);
CREATE INDEX ix_coordinate_operation_crs         ON epsg_coordoperation            (source_crs_code, target_crs_code);
CREATE INDEX ix_coordinate_operation_method_code ON epsg_coordoperation            (coord_op_method_code);
CREATE INDEX ix_parameter_usage_method_code      ON epsg_coordoperationparamusage  (coord_op_method_code);
CREATE INDEX ix_parameter_values                 ON epsg_coordoperationparamvalue  (coord_op_code, coord_op_method_code);
CREATE INDEX ix_parameter_value_code             ON epsg_coordoperationparamvalue  (parameter_code);
CREATE INDEX ix_path_concat_operation_code       ON epsg_coordoperationpath        (concat_operation_code);
CREATE INDEX ix_supersession_object_code         ON epsg_supersession              (object_code);


--------------------------------------------------------------------------------
-- Index for queries used by DirectEpsgFactory.createFoo(epsgcode) methods.   --
-- Indexed fields are numeric values used in ORDER BY clauses.                --
--------------------------------------------------------------------------------
CREATE INDEX ix_coordinate_axis_order            ON epsg_coordinateaxis           (coord_axis_order);
CREATE INDEX ix_coordinate_operation_accuracy    ON epsg_coordoperation           (coord_op_accuracy);
CREATE INDEX ix_parameter_order                  ON epsg_coordoperationparamusage (sort_order);
CREATE INDEX ix_path_concat_operation_step       ON epsg_coordoperationpath       (op_path_step);
CREATE INDEX ix_supersession_object_year         ON epsg_supersession             (supersession_year);
CREATE INDEX ix_version_history_date             ON epsg_versionhistory           (version_date);


--------------------------------------------------------------------------------
-- Index on the object names, used in order to find an EPSG code from a name. --
--------------------------------------------------------------------------------
CREATE INDEX ix_name_crs            ON epsg_coordinatereferencesystem (coord_ref_sys_name);
CREATE INDEX ix_name_cs             ON epsg_coordinatesystem          (coord_sys_name);
CREATE INDEX ix_name_axis           ON epsg_coordinateaxisname        (coord_axis_name);
CREATE INDEX ix_name_datum          ON epsg_datum                     (datum_name);
CREATE INDEX ix_name_ellipsoid      ON epsg_ellipsoid                 (ellipsoid_name);
CREATE INDEX ix_name_prime_meridian ON epsg_primemeridian             (prime_meridian_name);
CREATE INDEX ix_name_coord_op       ON epsg_coordoperation            (coord_op_name);
CREATE INDEX ix_name_method         ON epsg_coordoperationmethod      (coord_op_method_name);
CREATE INDEX ix_name_parameter      ON epsg_coordoperationparam       (parameter_name);
CREATE INDEX ix_name_unit           ON epsg_unitofmeasure             (unit_of_meas_name);
