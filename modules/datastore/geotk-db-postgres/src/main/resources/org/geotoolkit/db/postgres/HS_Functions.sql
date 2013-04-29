-- Function: "HS_Union"(timestamp with time zone, timestamp with time zone, timestamp with time zone, timestamp with time zone)

-- DROP FUNCTION "HS_Union"(timestamp with time zone, timestamp with time zone, timestamp with time zone, timestamp with time zone);

CREATE OR REPLACE FUNCTION "HS_Union"("HS_BeginTime1" timestamp with time zone, "HS_EndTime1" timestamp with time zone, "HS_BeginTime2" timestamp with time zone, "HS_EndTime2" timestamp with time zone)
  RETURNS timestamp with time zone[] AS
$BODY$declare 
	result timestamp[2];
begin
	
	--get min begin timestamp.
	if "HS_BeginTime1" < "HS_BeginTime2" then 
		result[0] = "HS_BeginTime1";
	else
		result[0] = "HS_BeginTime2";
	end if;
	
	--if end timestamp are null
	if "HS_EndTime1" is null or "HS_EndTime1" is null then 
		result[1] = null;
	else
		--test disjoin.
		if "HS_BeginTime2" > "HS_EndTime1" 
		or "HS_BeginTime1" > "HS_EndTime2" then
			raise exception 'the two periods do not meet or overlap';
		end if;

		if "HS_EndTime1" > "HS_EndTime2" then
			result[1] = "HS_EndTime1";
		else
			result[1] = "HS_EndTime2";
		end if;
	end if;
	return result;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_SucceedsOrMeets"(timestamp with time zone, timestamp with time zone, timestamp with time zone, timestamp with time zone)

-- DROP FUNCTION "HS_SucceedsOrMeets"(timestamp with time zone, timestamp with time zone, timestamp with time zone, timestamp with time zone);

CREATE OR REPLACE FUNCTION "HS_SucceedsOrMeets"("HS_Later_BeginTime" timestamp with time zone, "HS_Later_EndTime" timestamp with time zone, "HS_Previous_BeginTime" timestamp with time zone, "HS_Previous_EndTime" timestamp with time zone)
  RETURNS integer AS
$BODY$begin
	if "HS_Succeeds"("HS_Later_BeginTime","HS_Later_EndTime","HS_Previous_BeginTime","HS_Previous_EndTime") = 1
	or "HS_Previous_EndTime" = "HS_Later_BeginTime" then
		return 1;
	else
		return 0;
	end if;
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_Succeeds"(timestamp with time zone, timestamp with time zone, timestamp with time zone, timestamp with time zone)

-- DROP FUNCTION "HS_Succeeds"(timestamp with time zone, timestamp with time zone, timestamp with time zone, timestamp with time zone);

CREATE OR REPLACE FUNCTION "HS_Succeeds"("HS_Later_BeginTime" timestamp with time zone, "HS_Later_EndTime" timestamp with time zone, "HS_Previous_BeginTime" timestamp with time zone, "HS_Previous_EndTime" timestamp with time zone)
  RETURNS integer AS
$BODY$--
--return 1 if HS_Later attributs are later HS_previous attributs
--
begin
	if "HS_Later_BeginTime" > "HS_Previous_EndTime" then
		return 1;
	else 
		return 0;
	end if;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_PrecedesOrMeets"(timestamp with time zone, timestamp with time zone, timestamp with time zone, timestamp with time zone)

-- DROP FUNCTION "HS_PrecedesOrMeets"(timestamp with time zone, timestamp with time zone, timestamp with time zone, timestamp with time zone);

CREATE OR REPLACE FUNCTION "HS_PrecedesOrMeets"("HS_POM_BeginTime" timestamp with time zone, "HS_POM_EndTime" timestamp with time zone, "HS_Later_BeginTime" timestamp with time zone, "HS_Later_EndTime" timestamp with time zone)
  RETURNS integer AS
$BODY$--
--return 1 if HS_POM_BeginTime,HS_POM_EndTime attributs are earlier than HS_Later_BeginTime, HS_Later_EndTime parameters
-- or HS_POM_EndTime = HS_Later_BeginTime else 0
--
begin
	if "HS_Precedes"("HS_POM_BeginTime","HS_POM_EndTime","HS_Later_BeginTime","HS_Later_EndTime") = 1
	or "HS_POM_EndTime" = "HS_Later_BeginTime" then
		return 1;
	else
		return 0;
	end if;
end;
	$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_Precedes"(timestamp with time zone, timestamp with time zone, timestamp with time zone, timestamp with time zone)

-- DROP FUNCTION "HS_Precedes"(timestamp with time zone, timestamp with time zone, timestamp with time zone, timestamp with time zone);

CREATE OR REPLACE FUNCTION "HS_Precedes"("HS_Previous_BeginTime" timestamp with time zone, "HS_Previous_EndTime" timestamp with time zone, "HS_Later_BeginTime" timestamp with time zone, "HS_Later_EndTime" timestamp with time zone)
  RETURNS integer AS
$BODY$--
-- Return 1 if and only if previous attributs are before later attributs
--
begin
	if "HS_Previous_EndTime" < "HS_Later_BeginTime" then
		return 1;
	else
		return 0;
	end if;
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_Overlaps"(timestamp with time zone, timestamp with time zone, timestamp with time zone, timestamp with time zone)

-- DROP FUNCTION "HS_Overlaps"(timestamp with time zone, timestamp with time zone, timestamp with time zone, timestamp with time zone);

CREATE OR REPLACE FUNCTION "HS_Overlaps"("HS_BeginTime1" timestamp with time zone, "HS_EndTime1" timestamp with time zone, "HS_BeginTime2" timestamp with time zone, "HS_EndTime2" timestamp with time zone)
  RETURNS integer AS
$BODY$declare
 CurTS Timestamp;

Begin
	CurTS := "HS_GetTransactionTimestamp"();
	if "HS_EndTime1" is not null and "HS_EndTime2" is not null then
		if "HS_BeginTime2" <= "HS_BeginTime1"
		and "HS_BeginTime1" < "HS_EndTime2"
		or
		"HS_BeginTime1" <= "HS_BeginTime2"
		and "HS_BeginTime2" < "HS_EndTime1" then
			return 1;
		else
			return 0;
		end if;
	elsif "HS_EndTime1" is null and "HS_EndTime2" is null then
		return 1;
	elsif "HS_EndTime1" is null then
		--
		-- EndTime1 IS NULL
		--
		if "HS_BeginTime2" <= "HS_BeginTime1" AND "HS_BeginTime1" < "HS_EndTime2"
			OR
		"HS_BeginTime1" <= "HS_BeginTime2" AND "HS_BeginTime2" < CurTS
		THEN
			return 1;
		ELSE
			return 0;
		end if;
	else
		--
		-- EndTime2 IS NULL
		--
		if "HS_BeginTime2" <= "HS_BeginTime1" AND "HS_BeginTime1" < CurTS
					OR
		"HS_BeginTime1" <= "HS_BeginTime2" AND "HS_BeginTime2" < "HS_EndTime1"
		THEN
			return 1;
		ELSE
			return 0;
		end if;
	end if;
End;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_MonthInterval"(timestamp with time zone, timestamp with time zone)

-- DROP FUNCTION "HS_MonthInterval"(timestamp with time zone, timestamp with time zone);

CREATE OR REPLACE FUNCTION "HS_MonthInterval"("HS_BeginTime" timestamp with time zone, "HS_EndTime" timestamp with time zone)
  RETURNS interval AS
$BODY$--
--return interval which is difference between endTime and begintime.
--
begin
	if "HS_EndTime" is null then
		return null;
	else
		return age("HS_EndTime", "HS_BeginTime");
	end if;
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_Meets"(timestamp with time zone, timestamp with time zone, timestamp with time zone, timestamp with time zone)

-- DROP FUNCTION "HS_Meets"(timestamp with time zone, timestamp with time zone, timestamp with time zone, timestamp with time zone);

CREATE OR REPLACE FUNCTION "HS_Meets"("HS_BeginTime1" timestamp with time zone, "HS_EndTime1" timestamp with time zone, "HS_BeginTime2" timestamp with time zone, "HS_EndTime2" timestamp with time zone)
  RETURNS integer AS
$BODY$begin
	if "HS_EndTime1" = "HS_BeginTime2" or "HS_EndTime2" = "HS_BeginTime1" then
		return 1;
	else 
		return 0;
	end if;
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_Intersect"(timestamp with time zone, timestamp with time zone, timestamp with time zone, timestamp with time zone)

-- DROP FUNCTION "HS_Intersect"(timestamp with time zone, timestamp with time zone, timestamp with time zone, timestamp with time zone);

CREATE OR REPLACE FUNCTION "HS_Intersect"("HS_BeginTime1" timestamp with time zone, "HS_EndTime1" timestamp with time zone, "HS_BeginTime2" timestamp with time zone, "HS_EndTime2" timestamp with time zone)
  RETURNS timestamp with time zone[] AS
$BODY$declare
	result timestamp[2];
begin

	if ("HS_BeginTime1" > "HS_BeginTime2") then
		result[0] = "HS_BeginTime1";
	else 
		result[0] = "HS_BeginTime2";
	end if;

	if "HS_EndTime1" is null and "HS_EndTime2" is null then
		result[1] = null;
	else
		case 
			when ("HS_EndTime1" is null and "HS_EndTime2" is not null) then
				result[1] = "HS_EndTime2";
			when ("HS_EndTime1" is not null and "HS_EndTime2" is null) then
				result[1] = "HS_EndTime1";
			else
				if ("HS_EndTime1" > "HS_EndTime2") then
					result[1] = "HS_EndTime2";
				else 
					result[1] = "HS_EndTime1";
				end if;
		end case;
		--verify that intersection is valid (begin <= end).
		if result[1] < result[0] then 
			raise exception 'result of the intersect operation is empty period';
		end if;
	end if;
	return result;
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_GetTransactionTimestamp"()

-- DROP FUNCTION "HS_GetTransactionTimestamp"();

CREATE OR REPLACE FUNCTION "HS_GetTransactionTimestamp"()
  RETURNS timestamp with time zone AS
$BODY$BEGIN
  RETURN now();
END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_GetPrimaryKeys"(character varying)

-- DROP FUNCTION "HS_GetPrimaryKeys"(character varying);

CREATE OR REPLACE FUNCTION "HS_GetPrimaryKeys"(IN "tableName" character varying)
  RETURNS SETOF character varying AS
$BODY$declare 
	val character varying;
begin

	for val in SELECT KCU.COLUMN_NAME
	FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE AS KCU
	INNER JOIN
	INFORMATION_SCHEMA.TABLE_CONSTRAINTS AS TC
	ON KCU.CONSTRAINT_CATALOG = TC.CONSTRAINT_CATALOG
	AND KCU.CONSTRAINT_SCHEMA = TC.CONSTRAINT_SCHEMA
	AND KCU.CONSTRAINT_NAME = TC.CONSTRAINT_NAME
	AND KCU.TABLE_CATALOG = TC.TABLE_CATALOG
	AND KCU.TABLE_SCHEMA = TC.TABLE_SCHEMA
	AND KCU.TABLE_NAME = TC.TABLE_NAME
	WHERE KCU.TABLE_CATALOG = "HS_ExtractCatalogIdentifier"("tableName")
	AND KCU.TABLE_SCHEMA = "HS_ExtractSchemaIdentifier"("tableName")
	AND KCU.TABLE_NAME = "HS_ExtractTableIdentifier"("tableName")
	AND TC.CONSTRAINT_TYPE = 'PRIMARY KEY'
	ORDER BY ORDINAL_POSITION loop
		return next val;
	end loop;
	
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;

-- Function: "HS_GetHistoryRowSetIdentifierColumns"(character varying, character varying[])

-- DROP FUNCTION "HS_GetHistoryRowSetIdentifierColumns"(character varying, character varying[]);

CREATE OR REPLACE FUNCTION "HS_GetHistoryRowSetIdentifierColumns"("tableName" character varying, "trackedColumns" character varying[])
  RETURNS character varying[] AS
$BODY$DECLARE 
	AllPKeyColumns CHARACTER VARYING ARRAY;
	ConstNames CHARACTER VARYING ARRAY;
	ColumnNames CHARACTER VARYING ARRAY;
	IdentifierColumns CHARACTER VARYING ARRAY;
	i INTEGER;
	j integer;
	found integer;

	curCV character varying;

begin 
-- Get primary key columns of the specified table.
	i = 1;
	for curCV in select * from "HS_GetPrimaryKeys"("tableName") loop
		AllPKeyColumns[i] = curCV;
		i = i+1;
	end loop;
	i = 1;
	
	IF EXISTS(SELECT * FROM UNNEST(AllPKeyColumns))
	AND NOT EXISTS(SELECT *
	FROM UNNEST(AllPKeyColumns) AS UC(COL)
	WHERE COL NOT IN (
	SELECT COL FROM UNNEST("trackedColumns") AS TC(COL))) THEN
		IdentifierColumns = AllPKeyColumns;
	ELSE
		--
		-- Obtain the constraint names of the unique constraints
		-- of the specified tracked table.
		--
		i = 1;
		for curCV in SELECT CONSTRAINT_CATALOG ||
		'.' || CONSTRAINT_SCHEMA ||
		'.' || CONSTRAINT_NAME
		FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
		WHERE
		TABLE_CATALOG = "HS_ExtractCatalogIdentifier"("tableName")
		AND TABLE_SCHEMA = "HS_ExtractSchemaIdentifier"("tableName")
		AND TABLE_NAME = "HS_ExtractTableIdentifier"("tableName")
		AND CONSTRAINT_TYPE = 'UNIQUE' loop
			ConstNames[i] = curCV;
			i = i+1;
		end loop;
		
		-- Set control variable for WHILE loop to initial value 1.
		i = 1;
		-- Initially set ”found” flag to 0(not found).
		found = 0;
		-- Repeat while available unique constraint is not found and
		--
		--i is less than or equal to the number of unique constraints.
		WHILE found = 0 AND i <= array_Upper(ConstNames, 1) loop
			-- In case i-th unique constraint has no nullable columns,
			IF NOT EXISTS(SELECT *
			FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE AS KCU
			INNER JOIN INFORMATION_SCHEMA.COLUMNS AS C
			ON KCU.TABLE_CATALOG = C.TABLE_CATALOG
			AND KCU.TABLE_SCHEMA = C.TABLE_SCHEMA
			AND KCU.TABLE_NAME = C.TABLE_NAME
			AND KCU.COLUMN_NAME = C.COLUMN_NAME
			WHERE IS_NULLABLE = 'YES'
			AND CONSTRAINT_CATALOG ||
			'.' || CONSTRAINT_SCHEMA ||
			'.' || CONSTRAINT_NAME = ConstNames[i]) THEN
				
				-- Get column names of i-th unique constraint.
				--SET ColumnNames = ARRAY(
				--SELECT COLUMN_NAME
				--FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
				--WHERE CONSTRAINT_CATALOG ||
				--'.' || CONSTRAINT_SCHEMA ||
				--'.' || CONSTRAINT_NAME = ConstNames[i]);
				-- In case every column of i-th unique constraint is
				--
				--any of tracked columns.
				j = 1;
				for curCV in SELECT COLUMN_NAME
				FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
				WHERE CONSTRAINT_CATALOG ||
				'.' || CONSTRAINT_SCHEMA ||
				'.' || CONSTRAINT_NAME = ConstNames[i] loop
					ColumnNames[j] = curCV;
					j = j+1;
				end loop;
				
				IF NOT EXISTS(SELECT *
				FROM UNNEST(ColumnNames) AS UC(COL)
				WHERE COL NOT IN (
				SELECT COL FROM UNNEST("trackedColumns") AS TC(COL)))
				THEN
					-- Set output parameter to column names of
					--
					--i-th unique constraint;
					IdentifierColumns = ColumnNames;
					found = 1;
				END IF;
			END IF;
			-- Increment control variable for WHILE loop.
			i = i + 1;
		END loop;
	END IF;
	return IdentifierColumns;
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_ExtractTableIdentifier"(character varying)

-- DROP FUNCTION "HS_ExtractTableIdentifier"(character varying);

CREATE OR REPLACE FUNCTION "HS_ExtractTableIdentifier"("tableName" character varying)
  RETURNS character varying AS
$BODY$declare 
	tab text[];
	tLength integer;
begin
	tab = regexp_split_to_array("tableName", '\.');
	tLength = array_Upper(tab, 1);
	return tab[tLength];
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_ExtractSchemaIdentifier"(character varying)

-- DROP FUNCTION "HS_ExtractSchemaIdentifier"(character varying);

CREATE OR REPLACE FUNCTION "HS_ExtractSchemaIdentifier"("tableName" character varying)
  RETURNS character varying AS
$BODY$declare
	tab text[];
	tLength integer;
	result record;
begin 
	tab = regexp_split_to_array("tableName", '\.');
	tLength = array_Upper(tab, 1);
	if tLength > 1 then 
		return tab[tLength-1];
	else
		select table_schema into strict result from information_schema.tables where table_name = tab[tLength]; 
		return result."table_schema";
	end if;
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_ExtractColumnIdentifier"(character varying)

-- DROP FUNCTION "HS_ExtractColumnIdentifier"(character varying);

CREATE OR REPLACE FUNCTION "HS_ExtractColumnIdentifier"("columnName" character varying)
  RETURNS character varying AS
$BODY$begin
	return "columnName";
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;


-- Function: "HS_ExtractCatalogIdentifier"(character varying)

-- DROP FUNCTION "HS_ExtractCatalogIdentifier"(character varying);

CREATE OR REPLACE FUNCTION "HS_ExtractCatalogIdentifier"("tableName" character varying)
  RETURNS character varying AS
$BODY$declare
	tab text[];
	tLenght integer;
	result record;
begin 
	tab = regexp_split_to_array("tableName", '\.');
	tLenght = array_Upper(tab, 1);
	select table_catalog into strict result from information_schema.tables where table_name = tab[tLenght]; 
	return result."table_catalog";
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_Except"(timestamp with time zone, timestamp with time zone, timestamp with time zone, timestamp with time zone)

-- DROP FUNCTION "HS_Except"(timestamp with time zone, timestamp with time zone, timestamp with time zone, timestamp with time zone);

CREATE OR REPLACE FUNCTION "HS_Except"("HS_BeginTime1" timestamp with time zone, "HS_EndTime1" timestamp with time zone, "HS_BeginTime2" timestamp with time zone, "HS_EndTime2" timestamp with time zone)
  RETURNS timestamp with time zone[] AS
$BODY$declare
	result timestamp[2];

begin
	--test end timestamp are null.
	if "HS_EndTime1" is null and "HS_EndTime2" is null then
		if "HS_BeginTime1" < "HS_BeginTime2" then
			result[0] = "HS_BeginTime1";
			result[1] = "HS_BeginTime2";
		else
			result[0] = "HS_BeginTime2";
			result[1] = "HS_BeginTime1";
		end if;
	elsif "HS_BeginTime1" < "HS_BeginTime2" then
		result[0] = "HS_BeginTime1";
		--contains
		if "HS_EndTime2" < "HS_EndTime1"
		or "HS_EndTime1" is null then
			raise exception 'result of the except operation has disjoint periods';
		end if;
		--disjoins
		if "HS_EndTime1" < "HS_BeginTime2" then
			result[1] = "HS_EndTime1";
		else
		--overlaps
			result[1] = "HS_BeginTime2";
		end if;
	
	elsif "HS_BeginTime2" < "HS_BeginTime1" then
		--contains
		if "HS_EndTime2" > "HS_EndTime1"
		or "HS_EndTime2" is null then
			raise exception 'result of the except operation is empty period';
		end if;
		result[1] = "HS_EndTime1";
		--disjoin
		if "HS_EndTime2" < "HS_BeginTime1" then
			result[0] = "HS_BeginTime1";
		else 
		--overlaps
			result[0] = "HS_EndTime2";
		end if;
	end if;
	return result;
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_Equals"(timestamp with time zone, timestamp with time zone, timestamp with time zone, timestamp with time zone)

-- DROP FUNCTION "HS_Equals"(timestamp with time zone, timestamp with time zone, timestamp with time zone, timestamp with time zone);

CREATE OR REPLACE FUNCTION "HS_Equals"("HS_BeginTime1" timestamp with time zone, "HS_EndTime1" timestamp with time zone, "HS_BeginTime2" timestamp with time zone, "HS_EndTime2" timestamp with time zone)
  RETURNS integer AS
$BODY$begin
	if "HS_BeginTime1" = "HS_BeginTime2" and "HS_EndTime1"   = "HS_EndTime2" then
		return 1;
	else 
		return 0;
	end If;
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_DayInterval"(timestamp with time zone, timestamp with time zone)

-- DROP FUNCTION "HS_DayInterval"(timestamp with time zone, timestamp with time zone);

CREATE OR REPLACE FUNCTION "HS_DayInterval"("HS_BeginTime" timestamp with time zone, "HS_EndTime" timestamp with time zone)
  RETURNS interval AS
$BODY$begin
return "HS_MonthInterval"();
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_Contains"(timestamp with time zone, timestamp with time zone, timestamp with time zone, timestamp with time zone)

-- DROP FUNCTION "HS_Contains"(timestamp with time zone, timestamp with time zone, timestamp with time zone, timestamp with time zone);

CREATE OR REPLACE FUNCTION "HS_Contains"("HS_BeginTime1" timestamp with time zone, "HS_EndTime1" timestamp with time zone, "HS_BeginTime2" timestamp with time zone, "HS_EndTime2" timestamp with time zone)
  RETURNS integer AS
$BODY$--
-- return 1 if time1 parameters contains time2 parameters.
--
declare
	CurTS timestamp;
begin
	CurTS = "HS_GetTransactionTimestamp"();
	if "HS_EndTime1" is not null then
		if "HS_BeginTime1" <= "HS_BeginTime2" and "HS_EndTime2" <= "HS_EndTime1" then
			return 1;
		else
			return 0;
		end if;
	else
		if "HS_BeginTime1" <= "HS_BeginTime2" and "HS_EndTime2" <= CurTS then
			return 1;
		else
			return 0;
		end if;
	end if;
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_Contains"(timestamp with time zone, timestamp with time zone, timestamp with time zone)

-- DROP FUNCTION "HS_Contains"(timestamp with time zone, timestamp with time zone, timestamp with time zone);

CREATE OR REPLACE FUNCTION "HS_Contains"("HS_BeginTime" timestamp with time zone, "HS_EndTime" timestamp with time zone, "timePoint" timestamp with time zone)
  RETURNS integer AS
$BODY$declare
	CurTS timestamp;

begin
CurTS = "HS_GetTransactionTimestamp"();

if CurTS < "timePoint" then
	raise exception 'a given timestamp value expresses future time';
end if;

	if "timePoint" >= "HS_BeginTime" and "timePoint" < "HS_EndTime" then
		return 1; 
	else
		return 0;
	end if;
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_CreateCommaSeparatedIdentifierColumnList"(character varying, character varying[], character varying)

-- DROP FUNCTION "HS_CreateCommaSeparatedIdentifierColumnList"(character varying, character varying[], character varying);

CREATE OR REPLACE FUNCTION "HS_CreateCommaSeparatedIdentifierColumnList"("tableName" character varying, "trackedColumns" character varying[], "ColumnNamePostfix" character varying)
  RETURNS character varying AS
$BODY$DECLARE
	IdentifierColumns CHARACTER VARYING ARRAY;
	i INTEGER;
	result character varying;
begin
	IdentifierColumns = "HS_GetHistoryRowSetIdentifierColumns"("tableName", "trackedColumns");
	result = '';
	i = 1;
	while i <= array_Upper(IdentifierColumns, 1) loop
		if i > 1 then 
			result = result || ', ' ;
		END IF;
		result = result || "HS_ConstructColumnIdentifier"(IdentifierColumns[i]) ;
		IF "ColumnNamePostfix" IS NOT NULL THEN
			result = result || "ColumnNamePostfix" ;
		END IF;
		i = i + 1;
	END loop;
	return result;
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_CreateCommaSeparatedTrackedColumnAndTypeList"(character varying, character varying[])

-- DROP FUNCTION "HS_CreateCommaSeparatedTrackedColumnAndTypeList"(character varying, character varying[]);

CREATE OR REPLACE FUNCTION "HS_CreateCommaSeparatedTrackedColumnAndTypeList"("tableName" character varying, "trackedColumns" character varying[])
  RETURNS character varying AS
$BODY$declare 
	tcLenght integer;
	i integer;
	datatypecolumn record;
	result character varying;
begin
	tcLenght = array_Upper("trackedColumns", 1);
	result = '';
	i = 1;
	while i <= tcLenght loop
		-- recupe type de la column
		if i > 1 then 
			result = result || ', ';
		end if;
		select data_type into strict datatypecolumn from information_schema.columns where table_name = "HS_ExtractTableIdentifier"("tableName")  and column_name = "trackedColumns"[i];
		result = result || "HS_ConstructColumnIdentifier"("trackedColumns"[i]) || datatypecolumn."data_type";
		i = i+1;
	end loop;
	return result;
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_CreateCommaSeparatedTrackedColumnList"(character varying[], character varying)

-- DROP FUNCTION "HS_CreateCommaSeparatedTrackedColumnList"(character varying[], character varying);

CREATE OR REPLACE FUNCTION "HS_CreateCommaSeparatedTrackedColumnList"("trackedColumns" character varying[], "columnNamePrefix" character varying)
  RETURNS character varying AS
$BODY$declare 
	result character varying;
	i integer;
	tLenght integer;
begin 
	result = '';
	i = 1;
	tLenght = array_upper("trackedColumns", 1);
	while i <= tLenght loop
		-- In case i > 1, that is each time except first time of loop,
		IF i > 1 THEN
		-- Concatenate comma character into output parameter.
		result = result || ', ';
		END IF;
		-- In case the specified prefix is applicable,
		IF "columnNamePrefix" IS NOT NULL THEN
		-- Concatenate the specified prefix into output parameter.
		result = result || "columnNamePrefix";
		END IF;
		-- Concatenate the i-th tracked column name into output parameter.
		result = result || "HS_ConstructColumnIdentifier"("trackedColumns"[i]);
		
		i = i + 1;
	end loop;
	return result;
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_CreateCommaSeparatedTrackedColumnLiteralList"(character varying[])

-- DROP FUNCTION "HS_CreateCommaSeparatedTrackedColumnLiteralList"(character varying[]);

CREATE OR REPLACE FUNCTION "HS_CreateCommaSeparatedTrackedColumnLiteralList"("trackedColumns" character varying[])
  RETURNS character varying AS
$BODY$declare 
	result character varying;
	i integer;
	tcLenght integer;
begin
	tcLenght = array_Upper("trackedColumns", 1);

	i = 1;
	result = '';
	while i <= tcLenght loop
		if i > 1 then
			result = result||',';
		end if;

		result = result || "HS_ConstructColumnIdentifierLiteral"("trackedColumns"[i]);
		i = i + 1;
	end loop;
	return result;
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_CreateIdentifierColumnSelfJoinAndTestCondition"(character varying[], character varying, character varying, character varying, character varying)

-- DROP FUNCTION "HS_CreateIdentifierColumnSelfJoinAndTestCondition"(character varying[], character varying, character varying, character varying, character varying);

CREATE OR REPLACE FUNCTION "HS_CreateIdentifierColumnSelfJoinAndTestCondition"("trackedColumns" character varying[], prefix1 character varying, prefix2 character varying, "testCondition" character varying, "selfJoinCondition" character varying)
  RETURNS character varying AS
$BODY$declare 
	stmt character varying;
	tCLength integer;
	i integer;
begin
	tCLength = array_Upper("trackedColumns", 1);
	stmt = '';
	i = 1;
	WHILE i <= tCLength loop
		--add jointure condition. 
		if i > 1 then 
			stmt = stmt ||' '|| "selfJoinCondition"||' ';
		end if;
		--add first prefix and current trackedColumns value.
		stmt = stmt || "prefix1"||'"'||"trackedColumns"[i]||'"';
		--add test condition
		stmt = stmt || ' ' || "testCondition" || ' ';
		--add second prefix and current trackedColumns value.
		stmt = stmt || "prefix2"||'"'||"trackedColumns"[i]||'"';
		i = i + 1;
	end loop;
	return stmt;
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_ConstructIdentifier"(character varying, character varying, character varying)

-- DROP FUNCTION "HS_ConstructIdentifier"(character varying, character varying, character varying);

CREATE OR REPLACE FUNCTION "HS_ConstructIdentifier"("preFix" character varying, "sourceName" character varying, "postFix" character varying)
  RETURNS character varying AS
$BODY$declare 
	idb character varying;

begin
	if character_length("preFix") = 0 then
		idb = "HS_ExtractColumnIdentifier"("sourceName");
	else 
		idb = "HS_ExtractTableIdentifier"("sourceName");
	end if;

	return "preFix"||idb||"postFix";
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_ConstructColumnIdentifier"(character varying)

-- DROP FUNCTION "HS_ConstructColumnIdentifier"(character varying);

CREATE OR REPLACE FUNCTION "HS_ConstructColumnIdentifier"("columnName" character varying)
  RETURNS character varying AS
$BODY$begin
	return '"'||"HS_ConstructIdentifier"('',"columnName",'')||'"';
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_ConstructColumnIdentifierLiteral"(character varying)

-- DROP FUNCTION "HS_ConstructColumnIdentifierLiteral"(character varying);

CREATE OR REPLACE FUNCTION "HS_ConstructColumnIdentifierLiteral"("columnName" character varying)
  RETURNS character varying AS
$BODY$begin 
	return '"'||"HS_ExtractColumnIdentifier"("columnName")||'"';
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_CreateIdentifierColumnSelfJoinCondition"(character varying, character varying[], character varying, character varying)

-- DROP FUNCTION "HS_CreateIdentifierColumnSelfJoinCondition"(character varying, character varying[], character varying, character varying);

CREATE OR REPLACE FUNCTION "HS_CreateIdentifierColumnSelfJoinCondition"("tableName" character varying, "trackedColumns" character varying[], "columnNamePrefix1" character varying, "columnNamePrefix2" character varying)
  RETURNS character varying AS
$BODY$DECLARE 
	IdentifierColumns CHARACTER VARYING ARRAY;
	i INTEGER;
	result character varying;
	
begin
	IdentifierColumns = "HS_GetHistoryRowSetIdentifierColumns"("tableName", "trackedColumns");
	
	i = 1;
	result = '';
	WHILE i <= array_Upper(IdentifierColumns, 1) loop
	IF i > 1 THEN
		result = result || ' AND ' ;
	END IF;
	-- In case the prefix specified by ColumnNamePrefix1 is applicable,
	IF "columnNamePrefix1" IS NOT NULL THEN
		result = result || "columnNamePrefix1" ;
	END IF;
	result = result ||"HS_ConstructColumnIdentifier"(IdentifierColumns[i]);
	result = result || ' = ' ;
	-- In case the prefix specified by ColumnNamePrefix2 is applicable,
	IF "columnNamePrefix2" IS NOT NULL THEN
		result = result || "columnNamePrefix2" ;
	END IF;
	result = result || "HS_ConstructColumnIdentifier"(IdentifierColumns[i]);
	i = i + 1;
	END loop;
	return result;
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_ConstructDelTriggerIdentifier"(character varying)

-- DROP FUNCTION "HS_ConstructDelTriggerIdentifier"(character varying);

CREATE OR REPLACE FUNCTION "HS_ConstructDelTriggerIdentifier"("tableName" character varying)
  RETURNS character varying AS
$BODY$begin
	return "HS_ConstructIdentifier"('HS_TR_',"tableName",'_DEL');
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_ConstructInsTriggerIdentifier"(character varying)

-- DROP FUNCTION "HS_ConstructInsTriggerIdentifier"(character varying);

CREATE OR REPLACE FUNCTION "HS_ConstructInsTriggerIdentifier"("tableName" character varying)
  RETURNS character varying AS
$BODY$begin
	return "HS_ConstructIdentifier"('HS_TR_',"tableName",'_INS');
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_ConstructSequenceGeneratorIdentifier"(character varying)

-- DROP FUNCTION "HS_ConstructSequenceGeneratorIdentifier"(character varying);

CREATE OR REPLACE FUNCTION "HS_ConstructSequenceGeneratorIdentifier"("tableName" character varying)
  RETURNS character varying AS
$BODY$begin
	return '"'||"HS_ConstructIdentifier"('HS_SEQ_', "tableName", '')||'"'; 
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_ConstructTableIdentifier"(character varying)

-- DROP FUNCTION "HS_ConstructTableIdentifier"(character varying);

CREATE OR REPLACE FUNCTION "HS_ConstructTableIdentifier"("tableName" character varying)
  RETURNS character varying AS
$BODY$begin
	return '"'||"HS_ConstructIdentifier"('HS_TBL_',"tableName",'')||'"';
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_ConstructTableTypeIdentifier"(character varying)

-- DROP FUNCTION "HS_ConstructTableTypeIdentifier"(character varying);

CREATE OR REPLACE FUNCTION "HS_ConstructTableTypeIdentifier"("tableName" character varying)
  RETURNS character varying AS
$BODY$begin
	return "HS_ConstructIdentifier"('HS_TYPE_',"tableName",'');
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_ConstructUpdTriggerIdentifier"(character varying)

-- DROP FUNCTION "HS_ConstructUpdTriggerIdentifier"(character varying);

CREATE OR REPLACE FUNCTION "HS_ConstructUpdTriggerIdentifier"("tableName" character varying)
  RETURNS character varying AS
$BODY$begin
	return "HS_ConstructIdentifier"('HS_TR_', "tableName", '_UPD');
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_CreateHistoryTableSequenceNumberGenerator"(character varying)

-- DROP FUNCTION "HS_CreateHistoryTableSequenceNumberGenerator"(character varying);

CREATE OR REPLACE FUNCTION "HS_CreateHistoryTableSequenceNumberGenerator"("tableName" character varying)
  RETURNS character varying AS
$BODY$declare 
	stmt character varying;
	tmpSequenceGeneratorIdentifier character varying;

begin
	tmpSequenceGeneratorIdentifier = "HS_ConstructSequenceGeneratorIdentifier"("tableName");
	stmt = 'CREATE SEQUENCE ' || tmpSequenceGeneratorIdentifier ||
		' START WITH 1'||
		' INCREMENT BY 1'||
		' NO MAXVALUE'||
		' NO CYCLE';
	execute stmt;
	return stmt;
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
/*
-- Function: "HS_CreateHistoryErrorCheck"(character varying, integer[])

-- DROP FUNCTION "HS_CreateHistoryErrorCheck"(character varying, integer[]);

CREATE OR REPLACE FUNCTION "HS_CreateHistoryErrorCheck"("tableName" character varying, "trackedColumns" integer[])
  RETURNS void AS
$BODY$declare 
       IdentifierColumns text[];
	i integer;
	j integer;
	cn text;
	tn text;

begin
	if "tableName" is null then 
		raise exception 'table name is a null value';
	end if;

	if not exists (select * FROM information_schema.tables
	WHERE TABLE_CATALOG = "HS_ExtractCatalogIdentifier"("tableName")
	AND TABLE_SCHEMA    = "HS_ExtractSchemaIdentifier"("tableName")
	AND TABLE_NAME      = "HS_ExtractTableIdentifier"("tableName")) THEN
		raise exception 'table does not exist';
	end if;
	
	select "HS_GetHistoryRowSetIdentifierColumns"(TableName, TrackedColumns, IdentifierColumns);

	FOREACH tn in array IdentifierColumns loop
		IF NOT EXISTS(SELECT * from information_schema.table_constraints where
		table_name = tn) THEN
			raise exception	'tracked table has no unique constraint with NOT NULL';
		end if;
	end loop;

	if "trackedColumns" is null or array_Lenght("trackedColumns", 0) < 1  then
		raise exception 'no tracked column is specified';
	end if;

	i = 1;
	while i <= array_upper("trackedColumns", 1) loop
		IF TrackedColumns[i] IS NULL THEN
			raise exception 'column name is a null value';
		ELSIF NOT EXISTS(SELECT * FROM information_schema.columns
			WHERE TABLE_CATALOG = "HS_ExtractCatalogIdentifier"(TableName)
			AND TABLE_SCHEMA    = "HS_ExtractSchemaIdentifier"(TableName)
			AND TABLE_NAME      = "HS_ExtractTableIdentifier"(TableName)
			AND column_name     = "HS_ExtractColumnIdentifier"(TrackedColumns[i])) THEN
			raise exception 'column does not exist';
		end if;
		i = i + 1;
	end loop;

	foreach cn in array IdentifierColumns loop
		IF NOT EXISTS(SELECT * from information_schema.column_constraint where
		table_name = tableName and column_name = cn) THEN
			raise exception	'tracked table has no unique constraint with NOT NULL';
		end if;
	end loop;
end;
*/


$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_CreateHistoryTable"(character varying, character varying[])

-- DROP FUNCTION "HS_CreateHistoryTable"(character varying, character varying[]);

CREATE OR REPLACE FUNCTION "HS_CreateHistoryTable"("tableName" character varying, "trackedColumns" character varying[])
  RETURNS character varying AS
$BODY$declare 
 stmt character varying;
 stmtType character varying;
 tmpAllIdentifierColumnsType character varying;
 tmpAllIdentifierColumnsOpt character varying;

begin
	tmpAllIdentifierColumnsType = "HS_CreateCommaSeparatedTrackedColumnAndTypeList"("tableName", "trackedColumns");
	tmpAllIdentifierColumnsOpt  = "HS_CreateCommaSeparatedIdentifierColumnList"("tableName", "trackedColumns", ' WITH OPTIONS NOT NULL');
		
	stmt = 'CREATE TABLE ' || "HS_ConstructTableIdentifier"("tableName") ||'( "HS_SEQ" serial PRIMARY KEY, '||
	tmpAllIdentifierColumnsType || ', "HS_Begin" timestamp, "HS_End" timestamp )';
	EXECUTE stmt;
	return stmt;
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_InitializeHistoryTable"(character varying, character varying[])

-- DROP FUNCTION "HS_InitializeHistoryTable"(character varying, character varying[]);

CREATE OR REPLACE FUNCTION "HS_InitializeHistoryTable"("tableName" character varying, "trackedColumns" character varying[])
  RETURNS character varying AS
$BODY$declare 
	stmt character varying;
	tmpAllTrackedColumns character varying;
begin

	tmpAllTrackedColumns  = "HS_CreateCommaSeparatedTrackedColumnList"("trackedColumns", NULL);
	-- Construct an INSERT statement
	--in order to insert all rows in the tracked table into the history table.
	stmt ='INSERT INTO ' || "HS_ConstructTableIdentifier"("tableName")||
	' (' || tmpAllTrackedColumns || ', "HS_Begin", "HS_End")'||
	' SELECT '||tmpAllTrackedColumns || 
	' , "HS_GetTransactionTimestamp"()'||', null '||
	'FROM ' || '"'||"tableName"||'"';
	EXECUTE stmt;
	return stmt;
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: "HS_CreateInsertTrigger"(character varying, character varying[])

-- DROP FUNCTION "HS_CreateInsertTrigger"(character varying, character varying[]);

CREATE OR REPLACE FUNCTION "HS_CreateInsertTrigger"("tableName" character varying, "trackedColumns" character varying[])
  RETURNS character varying AS
$BODY$DECLARE 
	stmt character varying;
	stmt_fonc character varying;
	stmt_trigg character varying;
	trigger_name character varying;
	hs_table_name character varying;
	tmpAllTrackedColumns character varying;
	tmpAllTrackedColumns_new character varying;
begin
	tmpAllTrackedColumns = "HS_CreateCommaSeparatedTrackedColumnList"("trackedColumns", NULL);
	tmpAllTrackedColumns_new = "HS_CreateCommaSeparatedTrackedColumnList"("trackedColumns", 'NEW.');
	trigger_name = "HS_ConstructInsTriggerIdentifier"("tableName");
	hs_table_name = "HS_ConstructTableIdentifier"("tableName");

	stmt_fonc = 'CREATE OR REPLACE FUNCTION'|| ' "'||trigger_name||'"'||
		    '() RETURNS trigger AS $'||trigger_name||'$
	BEGIN
		INSERT INTO '||hs_table_name||'('||tmpAllTrackedColumns||
		', "HS_Begin", "HS_End") values('||tmpAllTrackedColumns_new||
		', "HS_GetTransactionTimestamp"(), null);
		return NEW;
	END;
	$'||trigger_name||'$ LANGUAGE plpgsql; ';
	
	stmt_trigg = 'CREATE TRIGGER' || ' "'||trigger_name||'"'||
		     ' AFTER INSERT ON "'||"tableName"||
		     '" FOR EACH ROW execute procedure "'||trigger_name||'"();';
	stmt = stmt_fonc || stmt_trigg;
	execute stmt;
	return stmt;
END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;


-- Function: "HS_CreateUpdateTrigger"(character varying, character varying[])

-- DROP FUNCTION "HS_CreateUpdateTrigger"(character varying, character varying[]);

CREATE OR REPLACE FUNCTION "HS_CreateUpdateTrigger"("tableName" character varying, "trackedColumns" character varying[])
  RETURNS character varying AS
$BODY$declare 
	selfJoin character varying;
	multiCondition character varying;
	tmpAllTrackedColumns character varying;
	tmpAllTrackedColumns_NEW character varying;
	stmt character varying;
	stmt_fonc character varying;
	stmt_trigg character varying;
	trigger_name character varying;
	hs_table_name character varying;
	

begin
	hs_table_name = "HS_ConstructTableIdentifier"("tableName");
	selfJoin = "HS_CreateIdentifierColumnSelfJoinCondition"("tableName", "trackedColumns", hs_table_name||'.', 'NEW.');
	multiCondition = "HS_CreateIdentifierColumnSelfJoinAndTestCondition"("trackedColumns", 'OLD.', 'NEW.', 'IS DISTINCT FROM', 'OR');
	tmpAllTrackedColumns = "HS_CreateCommaSeparatedTrackedColumnList"("trackedColumns", NULL);
	tmpAllTrackedColumns_NEW = "HS_CreateCommaSeparatedTrackedColumnList"("trackedColumns", 'NEW.');
	trigger_name = "HS_ConstructUpdTriggerIdentifier"("tableName");
	
	
	stmt_fonc = 'CREATE OR REPLACE FUNCTION'|| ' "'||trigger_name||'"'||
		    '() RETURNS trigger AS $'||trigger_name||'$
	BEGIN

		 UPDATE ' || hs_table_name ||' SET "HS_End" = "HS_GetTransactionTimestamp"() '||
		 'WHERE ' || selfJoin ||' AND '||hs_table_name ||'.'||'"HS_End" IS NULL;'

		
		'INSERT INTO '||hs_table_name||'('||tmpAllTrackedColumns||
		', "HS_Begin", "HS_End") values('||tmpAllTrackedColumns_NEW||
		', "HS_GetTransactionTimestamp"(), null);

		return NEW;
	END;
	$'||trigger_name||'$ LANGUAGE plpgsql; ';
	
	stmt_trigg = 'CREATE TRIGGER' || ' "'||trigger_name||'"'||
		     ' AFTER UPDATE OF '||tmpAllTrackedColumns||' ON "'||"tableName"||
		     '" FOR EACH ROW'||
		     ' WHEN ('||multiCondition||')'||
		     ' EXECUTE PROCEDURE "'||trigger_name||'"();';
	stmt = stmt_fonc || stmt_trigg;
	execute stmt;
	return stmt;
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;


-- Function: "HS_CreateHistory"(character varying, character varying[])

-- DROP FUNCTION "HS_CreateHistory"(character varying, character varying[]);

CREATE OR REPLACE FUNCTION "HS_CreateHistory"("tableName" character varying, "trackedColumns" character varying[])
  RETURNS character varying AS
$BODY$declare 
	stmt character varying;
	tablenam character varying;
begin
	tablenam = "HS_ExtractTableIdentifier"("tableName");
	stmt = '';
	-- verification
	--stmt = "HS_CreateHistoryErrorCheck"("tableName","trackedColumns");
	--creation de la table historique
	stmt = "HS_CreateHistoryTable"(tablenam,"trackedColumns");
	stmt = "HS_InitializeHistoryTable"(tablenam, "trackedColumns");

	--trigger creation
	stmt = "HS_CreateInsertTrigger"(tablenam, "trackedColumns");
	stmt = "HS_CreateUpdateTrigger"(tablenam, "trackedColumns");
	return stmt;
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;





