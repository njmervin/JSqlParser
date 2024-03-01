package org.yuyun.jsqlparser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            if(token.getType() != TokenType.COMMENT)
                count += 1;

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
        stmt.setSql(charReader.substring(this.tokens.get(stmt.getFirstTokenIndex()).getBeginPosition(),
                this.tokens.get(stmt.getLastTokenIndex()).getEndPosition())
        );
        buildCleanSQL(stmt);
        checkAlterOperation(stmt);
        checkPrimaryOperation(stmt);
        checkFromTables(stmt);
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

    private void checkPrimaryOperation(SQLStmt stmt) {
        TokenManager tokenManager = new TokenManager(this.tokens, stmt.getFirstTokenIndex(), stmt.getLastTokenIndex());
        stmt.setPrimaryOperation("");
        tokenManager.skipComment();
        Token token = tokenManager.peek();
        if(token == null || token.getType() != TokenType.KEYWORD)
            return;

        //判断with
        if(token.getType() == TokenType.KEYWORD && token.getImage().equalsIgnoreCase("with")) {
            while (true) {
                int l = tokenManager.findLeftBracket("(");
                if (l == -1)
                    return;
                int r = tokenManager.findMatchedBracket(l + 1, "(", ")");
                if (r == -1)
                    return;

                tokenManager.setPosition(r + 1);
                tokenManager.skipComment();

                token = tokenManager.peek();
                if(token == null)
                    return;
                if(token.getType() == TokenType.KEYWORD) {
                    stmt.setPrimaryOperation(token.getImage().toLowerCase());
                    return;
                }
                else if(token.getType() == TokenType.SYMBOL && token.getImage().equals(","))
                    continue;
                else
                    return;
            }
        }
        else
            stmt.setPrimaryOperation(token.getImage().toLowerCase());
    }

    private void checkFromTables(SQLStmt stmt) {
        Set<String> fromTables = new HashSet<>();
        Set<String> ts = new HashSet<>();

        TokenManager tokenManager = new TokenManager(this.tokens, stmt.getFirstTokenIndex(), stmt.getLastTokenIndex());
        while (true) {
            Token token = tokenManager.read();
            if(token == null)
                break;

            if(token.getType() == TokenType.KEYWORD) {
                if(token.getImage().equalsIgnoreCase("from")) {
                    token = tokenManager.peek();
                    if(token != null && token.getType() == TokenType.IDENTIFIER) {
                        token = tokenManager.readComplexIdentifier();
                        if(token != null && !ts.contains(token.getImage().toLowerCase())) {
                            ts.add(token.getImage().toLowerCase());
                            fromTables.add(token.getImage());
                        }
                    }
                }
                else if(token.getImage().equalsIgnoreCase("join")) {
                    token = tokenManager.peek();
                    if(token != null && token.getType() == TokenType.IDENTIFIER) {
                        token = tokenManager.readComplexIdentifier();
                        if(token != null && !ts.contains(token.getImage().toLowerCase())) {
                            ts.add(token.getImage().toLowerCase());
                            fromTables.add(token.getImage());
                        }
                    }
                }
            }
        }
        stmt.setFromTables(fromTables);
    }

    public static SQLParser parse(String sql) {
        SQLParser parser = new SQLParser();
        parser.charReader = new CharReader(sql, "<chunk>", 1);
        Lexer lexer = new Lexer(parser.tokens, parser.charReader);
        lexer.lex();
        parser.parseStmtList();
        return parser;
    }

    public static void main(String[] args) throws IOException {
//        SQLParser parser = SQLParser.parse("select; 1 from dual; -- abc\n select <''''''> from dual;/*qq*/123 date'2024-10-23'; Delete from abc");
        Reader reader = new InputStreamReader(Files.newInputStream(Paths.get("z:/a.txt")), StandardCharsets.UTF_8);
        char[] cbuf = new char[64*1024];
        int n = reader.read(cbuf);
        SQLParser parser = SQLParser.parse(new String(cbuf, 0, n));
        for(SQLStmt stmt : parser.getStmtList()) {
            System.out.println("===>");
            System.out.println(stmt.getPrimaryOperation());
            System.out.println(stmt.getFromTables());
            System.out.println(stmt.getAlterOperation());
            System.out.println(stmt.getSql());
        }
    }
}
