package co.kr.sothis.dbanalyzer.vo;

import org.springframework.stereotype.Component;

@Component
public final class PostgreSqlQuery {

    public String columnInfoQuery() {
        return """
                 SELECT\s
                    COALESCE(b.relname, c.table_name) AS table_name,
                    c.column_name,
                    a.description,
                    c.data_type,
                    c.character_maximum_length ,
                    c.is_nullable,
                    c.column_default, -- 기본값
                    CASE\s
                        WHEN COALESCE(d.constraint_name, '') = '' THEN NULL\s
                        ELSE 'PK'\s
                    END AS is_pk,
                    fk.ref_table AS referenced_table, -- 외래키 참조 테이블
                    fk.ref_column AS referenced_column -- 외래키 참조 컬럼
                FROM pg_catalog.pg_description a
                RIGHT JOIN (
                    SELECT oid, relname
                    FROM pg_class
                    WHERE relname IN (
                        SELECT table_name
                        FROM information_schema.tables
                        WHERE table_schema = ? AND table_type = 'BASE TABLE'
                    )
                    AND relnamespace = (
                        SELECT oid\s
                        FROM pg_catalog.pg_namespace\s
                        WHERE nspname = ?
                    )
                ) b ON a.objoid = b.oid
                FULL OUTER JOIN (
                    SELECT\s
                        tab.table_name,
                        col.column_name,
                        col.ordinal_position,
                        col.data_type,
                        col.character_maximum_length,
                        col.is_nullable,
                        col.column_default
                    FROM information_schema.tables tab
                    JOIN information_schema.columns col\s
                    ON tab.table_name = col.table_name\s
                       AND tab.table_type = 'BASE TABLE'\s
                       AND tab.table_schema = ? \s
                       AND col.table_schema = ?
                ) c ON b.relname = c.table_name AND a.objsubid = c.ordinal_position
                LEFT OUTER JOIN (
                    SELECT\s
                        u.table_name,
                        u.column_name,
                        u.constraint_name,
                        u.ordinal_position
                    FROM information_schema.key_column_usage AS u
                    INNER JOIN information_schema.table_constraints AS t\s
                    ON t.constraint_name = u.constraint_name
                    WHERE u.table_schema = ?\s
                          AND u.table_schema = t.table_schema\s
                          AND t.constraint_type = 'PRIMARY KEY'
                ) d ON b.relname = d.table_name AND c.column_name = d.column_name
                LEFT JOIN (
                    SELECT\s
                        fk.table_name AS fk_table,
                        fk.column_name AS fk_column,
                        pk.table_name AS ref_table,
                        pk.column_name AS ref_column
                    FROM information_schema.referential_constraints rc
                    JOIN information_schema.key_column_usage fk\s
                    ON fk.constraint_name = rc.constraint_name
                    AND fk.table_schema = rc.constraint_schema
                    JOIN information_schema.constraint_column_usage pk
                    ON pk.constraint_name = rc.unique_constraint_name
                    AND pk.table_schema = rc.constraint_schema
                    WHERE fk.table_schema = ?
                ) fk ON c.table_name = fk.fk_table AND c.column_name = fk.fk_column
                WHERE c.column_name IS NOT NULL
                ORDER BY table_name, c.ordinal_position NULLS FIRST;
                """;
    }

    public String schemaTableListQuery() {
        return """
                SELECT table_schema,\s
                       array_agg(table_name ORDER BY table_name ASC) AS tables
                FROM information_schema.tables
                WHERE table_type = 'BASE TABLE'
                GROUP BY table_schema
                ORDER BY table_schema;
                """;
    }

    public String tableInfoQuery() {
        return """
                WITH pk_columns AS (
                    SELECT\s
                        kcu.table_schema,
                        kcu.table_name,
                        kcu.column_name
                    FROM\s
                        information_schema.table_constraints tc
                    JOIN\s
                        information_schema.key_column_usage kcu
                    ON\s
                        tc.constraint_name = kcu.constraint_name
                        AND tc.table_schema = kcu.table_schema
                    WHERE\s
                        tc.constraint_type = 'PRIMARY KEY'
                )
                SELECT\s
                    c.column_name AS column_name,
                    c.data_type AS data_type,
                    COALESCE(c.character_maximum_length, c.numeric_precision) AS length,
                    c.column_default AS default_value,
                    c.is_nullable AS nullable,
                    CASE\s
                        WHEN pk.column_name IS NOT NULL THEN 'YES'
                        ELSE 'NO'
                    END AS is_primary_key
                FROM\s
                    information_schema.columns c
                LEFT JOIN\s
                    pk_columns pk
                ON\s
                    c.table_schema = pk.table_schema
                    AND c.table_name = pk.table_name
                    AND c.column_name = pk.column_name
                WHERE\s
                    c.table_schema = ?\s
                    AND c.table_name = ?
                ORDER BY\s
                    c.ordinal_position;
                
                """;
    }

    public String tableInfoWithReferQuery() {
        return """
                WITH pk_columns AS (
                    SELECT
                        kcu.table_schema,
                        kcu.table_name,
                        kcu.column_name
                    FROM
                        information_schema.table_constraints tc
                    JOIN
                        information_schema.key_column_usage kcu
                    ON
                        tc.constraint_name = kcu.constraint_name
                        AND tc.table_schema = kcu.table_schema
                    WHERE
                        tc.constraint_type = 'PRIMARY KEY'
                ),
                fk_columns AS (
                    SELECT
                        kcu.table_schema,
                        kcu.table_name,
                        kcu.column_name,
                        ccu.table_name AS refer_table,
                        ccu.column_name AS refer_column
                    FROM
                        information_schema.table_constraints tc
                    JOIN
                        information_schema.key_column_usage kcu
                    ON
                        tc.constraint_name = kcu.constraint_name
                        AND tc.table_schema = kcu.table_schema
                    JOIN
                        information_schema.constraint_column_usage ccu
                    ON
                        tc.constraint_name = ccu.constraint_name
                        AND tc.table_schema = ccu.table_schema
                    WHERE
                        tc.constraint_type = 'FOREIGN KEY'
                )
                SELECT
                    c.column_name AS column_name,
                    c.data_type AS data_type,
                    COALESCE(c.character_maximum_length, c.numeric_precision) AS length,
                    c.column_default AS default_value,
                    c.is_nullable AS nullable,
                    CASE
                        WHEN pk.column_name IS NOT NULL THEN 'YES'
                        ELSE 'NO'
                    END AS is_primary_key,
                    fk.refer_table AS refer_table,
                    fk.refer_column AS refer_column
                FROM
                    information_schema.columns c
                LEFT JOIN
                    pk_columns pk
                ON
                    c.table_schema = pk.table_schema
                    AND c.table_name = pk.table_name
                    AND c.column_name = pk.column_name
                LEFT JOIN
                    fk_columns fk
                ON
                    c.table_schema = fk.table_schema
                    AND c.table_name = fk.table_name
                    AND c.column_name = fk.column_name
                WHERE
                    c.table_schema = ?
                    AND c.table_name = ?
                ORDER BY
                    c.ordinal_position;
                
                """;
    }
}
