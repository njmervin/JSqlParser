package org.yuyun.jsqlparser;

import java.util.*;

public class SQLParser {
    private final List<Token> tokens = new ArrayList<>();
    private CharReader charReader;
    private final List<SQLStmt> stmtList = new ArrayList<>();

    private SQLParser() {

    }

    public List<SQLStmt> getStmtList() {
        return this.stmtList;
    }

    private void parseStmtList() {
        int start = 0, count = 0;
        for(int i=0; i<tokens.size(); i++) {
            Token token = tokens.get(i);

            if(token.getType() == TokenType.SYMBOL && token.getImage().equals(";")) {
                if(count > 0) {
                    SQLStmt stmt = new SQLStmt();
                    stmt.setFirstTokenIndex(start);
                    stmt.setLastTokenIndex(i - 1);
                    stmtList.add(stmt);
                }
                start = i + 1;
                count = 0;
            }
            else {
                if(token.getType() != TokenType.COMMENT)
                    count += 1;
            }
        }

        if(count > 0) {
            SQLStmt stmt = new SQLStmt();
            stmt.setFirstTokenIndex(start);
            stmt.setLastTokenIndex(tokens.size() - 1);
            stmtList.add(stmt);
        }

        for(SQLStmt stmt : this.stmtList)
            parseStmt(stmt);
    }

    private void parseStmt(SQLStmt stmt) {
        stmt.setFirstLine(this.tokens.get(stmt.getFirstTokenIndex()).getBeginLine());
        stmt.setLastLine(this.tokens.get(stmt.getLastTokenIndex()).getEndLine());
        stmt.setSql(charReader.substring(this.tokens.get(stmt.getFirstTokenIndex()).getBeginPosition(),
                this.tokens.get(stmt.getLastTokenIndex()).getEndPosition())
        );
        buildCleanSQL(stmt);

        TokenManager tokenManager = new TokenManager(this.tokens, stmt.getFirstTokenIndex(), stmt.getLastTokenIndex());
        checkAlterOperation(stmt);

        tokenManager.setPosition(0);
        checkPrimaryOperation(stmt, tokenManager);

        tokenManager.setPosition(0);
        checkFromTables(stmt, tokenManager);
    }

    private void buildCleanSQL(SQLStmt stmt) {
        StringBuilder sb = new StringBuilder();
        sb.append(charReader.buffer(), 0, this.tokens.get(stmt.getLastTokenIndex()).getEndPosition() + 1);
        for(int i=0; i<this.tokens.get(stmt.getFirstTokenIndex()).getBeginPosition(); i++) {
            char ch = sb.charAt(i);
            if(ch != '\r' && ch != '\n')
                sb.setCharAt(i, ' ');
        }
        for(int i=stmt.getFirstTokenIndex(); i<=stmt.getLastTokenIndex(); i++) {
            Token token = this.tokens.get(i);
            if(token.getType() == TokenType.COMMENT) {
                for(int j=token.getBeginPosition(); j<=token.getEndPosition(); j++) {
                    char ch = sb.charAt(j);
                    if(ch != '\r' && ch != '\n')
                        sb.setCharAt(j, ' ');
                }
            }
        }
        stmt.setCleanSQL(sb.toString());
    }

    private void checkAlterOperation(SQLStmt stmt) {
        Set<String> alterOperation = new HashSet<>();
        for(int i=stmt.getFirstTokenIndex(); i<=stmt.getLastTokenIndex(); i++) {
            Token token = this.tokens.get(i);
            if(token.getType() == TokenType.KEYWORD) {
                if(Constants.ALTER_KEYWORDS.contains(token.getImage().toLowerCase()))
                    alterOperation.add(token.getImage());
            }
        }
        stmt.setAlterOperation(alterOperation);
    }

    private void checkPrimaryOperation(SQLStmt stmt, TokenManager tokenManager) {
        stmt.setPrimaryOperation("");

        Token token = tokenManager.tryReadKeyword();
        if(token == null)
            return;

        if(!token.getImage().equalsIgnoreCase("with")) {
            stmt.setPrimaryOperation(token.getImage().toLowerCase());
            return;
        }

        // with <identifier> [ ( <fields> ) ] as ( <subquery> ) [ , ...]
        while (true) {
            token = tokenManager.tryReadIdentifier();
            if(token == null)
                return;

            token = tokenManager.peek();
            if(token == null)
                return;

            // <fields>
            if(token.getType() == TokenType.SYMBOL && token.getImage().equals("(")) {
                int l = tokenManager.findLeftBracket("(");
                int r = tokenManager.findMatchedBracket(l + 1, "(", ")");
                if(r == -1)
                    return;
                tokenManager.setPosition(r + 1);
            }

            // <subquery>
            int l = tokenManager.findLeftBracket("(");
            if (l == -1)
                return;
            int r = tokenManager.findMatchedBracket(l + 1, "(", ")");
            if (r == -1)
                return;
            tokenManager.setPosition(r + 1);

            token = tokenManager.peek();
            if(token == null)
                return;

            if(token.getType() == TokenType.SYMBOL && token.getImage().equals(",")) {
                tokenManager.skip(1);
                continue;
            }

            if(token.getType() == TokenType.KEYWORD)
                stmt.setPrimaryOperation(token.getImage().toLowerCase());
            break;
        }
    }

    private void checkFromTables(SQLStmt stmt, TokenManager tokenManager) {
        List<String> tables = new ArrayList<>();
        Set<String> alias = new HashSet<>();
        Set<String> fromTables = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        checkFromTables(tokenManager, tables, alias);

        for(String table : tables) {
            if(!alias.contains(table.toLowerCase()))
                fromTables.add(table);
        }
        stmt.setFromTables(fromTables);
    }

    private void checkFromTables(TokenManager tokenManager, List<String> fromTables, Set<String> alias) {
        String name;

        while (true) {
            Token token = tokenManager.read();
            if(token == null)
                break;

            if(token.getType() == TokenType.KEYWORD) {
                if(token.getImage().equalsIgnoreCase("from")) {
                    while (true) {
                        token = tokenManager.peek();
                        if (token != null && token.getType() == TokenType.IDENTIFIER) { // <identifier> [as] <identifier>
                            token = tokenManager.readComplexIdentifier();
                            if (token != null) {
                                name = token.getImage();
                                fromTables.add(name);
                                tokenManager.tryReadKeyword("as");
                                token = tokenManager.tryReadIdentifier();
                                if(token != null && !token.getImage().equalsIgnoreCase(name))
                                    alias.add(token.getImage().toLowerCase());
                            }
                        }
                        else if(token != null && token.getType() == TokenType.SYMBOL && token.getImage().equals("(")) { // ( <subquery> ) [as] <identifier>
                            int l = tokenManager.findLeftBracket("(");
                            int r = tokenManager.findMatchedBracket(l + 1, "(", ")");
                            if(r == -1) {
                                tokenManager.skip(1);
                                continue;
                            }
                            TokenManager subQuery = tokenManager.subset(l + 1, r - 1);
                            checkFromTables(subQuery, fromTables, alias);
                            tokenManager.setPosition(r + 1);
                            tokenManager.tryReadKeyword("as");
                            token = tokenManager.tryReadIdentifier();
                            if(token != null)
                                alias.add(token.getImage().toLowerCase());
                        }

                        token = tokenManager.tryReadSymbol(",");
                        if(token == null)
                            break;
                    }
                }
                else if(token.getImage().equalsIgnoreCase("join")) {
                    token = tokenManager.peek();
                    if(token != null && token.getType() == TokenType.IDENTIFIER) { // join <identifier> [as] <identifier>
                        token = tokenManager.readComplexIdentifier();
                        if(token != null) {
                            name = token.getImage();
                            fromTables.add(name);
                            tokenManager.tryReadKeyword("as");
                            token = tokenManager.tryReadIdentifier();
                            if(token != null && !token.getImage().equalsIgnoreCase(name))
                                alias.add(token.getImage().toLowerCase());
                        }
                    }
                    else if(token != null && token.getType() == TokenType.SYMBOL && token.getImage().equals("(")) { // join ( <subquery> ) [as] <identifier>
                        int l = tokenManager.findLeftBracket("(");
                        int r = tokenManager.findMatchedBracket(l + 1, "(", ")");
                        if(r == -1) {
                            tokenManager.skip(1);
                            continue;
                        }
                        TokenManager subQuery = tokenManager.subset(l + 1, r - 1);
                        checkFromTables(subQuery, fromTables, alias);
                        tokenManager.setPosition(r + 1);
                        tokenManager.tryReadKeyword("as");
                        token = tokenManager.tryReadIdentifier();
                        if(token != null)
                            alias.add(token.getImage().toLowerCase());
                    }
                }
                else if(token.getImage().equalsIgnoreCase("with")) {
                    token = tokenManager.tryReadIdentifier();
                    if(token != null)
                        alias.add(token.getImage().toLowerCase());
                }
            }
        }
    }

    public static SQLParser parse(String sql) {
        SQLParser parser = new SQLParser();
        parser.charReader = new CharReader(sql, "<chunk>", 1);
        Lexer lexer = new Lexer(parser.tokens, parser.charReader);
        lexer.lex();
        parser.parseStmtList();
        return parser;
    }
}
