package org.yuyun.jsqlparser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Constants {
    public static final Set<String> SYMBOLS = new HashSet<>(Arrays.asList(
            ";",
            "=",
            "||",
            "|",
            ".",
            "\"",
            "'",
            "+",
            "-",
            "*",
            "/",
            "%",
            "&",
            ",",
            ":",
            "?",
            "^",
            "<",
            ">",
            "{",
            "}",
            "[",
            "]",
            "(",
            ")"
    ));

    public static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
            "add",
            "all",
            "allocate",
            "alter",
            "and",
            "any",
            "are",
            "array",
            "as",
            "asensitive",
            "asymmetric",
            "at",
            "atomic",
            "authorization",
            "begin",
            "between",
            "bigint",
            "binary",
            "blob",
            "boolean",
            "both",
            "by",
            "call",
            "called",
            "cascaded",
            "case",
            "cast",
            "char",
            "character",
            "check",
            "clob",
            "close",
            "collate",
            "column",
            "commit",
            "connect",
            "constraint",
            "continue",
            "corresponding",
            "create",
            "cross",
            "cube",
            "current",
            "current_date",
            "current_default_transform_group",
            "current_path",
            "current_role",
            "current_time",
            "current_timestamp",
            "current_transform_group_for_type",
            "current_user",
            "cursor",
            "cycle",
            "date",
            "day",
            "deallocate",
            "dec",
            "decimal",
            "declare",
            "default",
            "delete",
            "deref",
            "describe",
            "deterministic",
            "disconnect",
            "distinct",
            "double",
            "drop",
            "dynamic",
            "each",
            "element",
            "else",
            "end",
            "end-exec",
            "escape",
            "except",
            "exec",
            "execute",
            "exists",
            "external",
            "false",
            "fetch",
            "filter",
            "float",
            "for",
            "foreign",
            "free",
            "from",
            "full",
            "function",
            "get",
            "global",
            "grant",
            "group",
            "grouping",
            "having",
            "hold",
            "hour",
            "identity",
            "immediate",
            "in",
            "indicator",
            "inner",
            "inout",
            "input",
            "insensitive",
            "insert",
            "int",
            "integer",
            "intersect",
            "interval",
            "into",
            "is",
            "isolation",
            "join",
            "language",
            "large",
            "lateral",
            "leading",
            "left",
            "like",
            "local",
            "localtime",
            "localtimestamp",
            "match",
            "member",
            "merge",
            "method",
            "minute",
            "modifies",
            "module",
            "month",
            "multiset",
            "national",
            "natural",
            "nchar",
            "nclob",
            "new",
            "no",
            "none",
            "not",
            "null",
            "numeric",
            "of",
            "old",
            "on",
            "only",
            "open",
            "or",
            "order",
            "out",
            "outer",
            "output",
            "over",
            "overlaps",
            "parameter",
            "partition",
            "precision",
            "prepare",
            "primary",
            "procedure",
            "range",
            "reads",
            "real",
            "recursive",
            "ref",
            "references",
            "referencing",
            "regr_avgx",
            "regr_avgy",
            "regr_count",
            "regr_intercept",
            "regr_r2",
            "regr_slope",
            "regr_sxx",
            "regr_sxy",
            "regr_syy",
            "release",
            "result",
            "return",
            "returns",
            "revoke",
            "right",
            "rollback",
            "rollup",
            "row",
            "rows",
            "savepoint",
            "scroll",
            "search",
            "second",
            "select",
            "sensitive",
            "session_user",
            "set",
            "similar",
            "smallint",
            "some",
            "specific",
            "specifictype",
            "sql",
            "sqlexception",
            "sqlstate",
            "sqlwarning",
            "start",
            "static",
            "submultiset",
            "symmetric",
            "system",
            "system_user",
            "table",
            "then",
            "time",
            "timestamp",
            "timezone_hour",
            "timezone_minute",
            "to",
            "trailing",
            "translation",
            "treat",
            "trigger",
            "true",
            "uescape",
            "union",
            "unique",
            "unknown",
            "unnest",
            "update",
            "upper",
            "user",
            "using",
            "value",
            "values",
            "var_pop",
            "var_samp",
            "varchar",
            "varying",
            "when",
            "whenever",
            "where",
            "width_bucket",
            "window",
            "with",
            "within",
            "without",
            "year"
    ));

    public static final Set<String> ALTER_KEYWORDS = new HashSet<>(Arrays.asList(
            "alter",
            "call",
            "comment",
            "commit",
            "create",
            "declare",
            "delete",
            "drop",
            "exec",
            "execute",
            "grant",
            "insert",
            "into",
            "revoke",
            "system",
            "update"
    ));
}
