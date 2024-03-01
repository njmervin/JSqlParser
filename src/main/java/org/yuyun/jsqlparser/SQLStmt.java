package org.yuyun.jsqlparser;

import lombok.Data;

import java.util.Set;

@Data
public class SQLStmt {
    private int firstTokenIndex;
    private int firstLine;
    private int lastTokenIndex;
    private int lastLine;
    private Set<String> alterOperation;
    private String primaryOperation;
    private Set<String> fromTables;
    private String sql;
    private String cleanSQL;
}
