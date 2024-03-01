package org.yuyun.jsqlparser;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Lexer {
    private final List<Token> tokens;
    private final CharReader charReader;

    private final Set<Character> symbolCharSet = new HashSet<>();

    public Lexer(List<Token> tokens, CharReader charReader) {
        this.tokens = tokens;
        this.charReader = charReader;
        for (String symbol : Constants.SYMBOLS) {
            for (int i = 0; i < symbol.length(); i++)
                symbolCharSet.add(symbol.charAt(i));
        }
    }

    private void skipBlank() {
        char ch = charReader.peek();
        while (ch != 0 && ch <= ' ') {
            charReader.read();
            ch = charReader.peek();
        }
    }

    private void readLineComment() {
        Token token = new Token(TokenType.COMMENT, TokenSubType.LINE_COMMENT);

        token.initStart(charReader);
        charReader.skip(1);
        token.initEnd(charReader);
        this.tokens.add(token);
        charReader.skip(1);

        for (; ; ) {
            char ch = charReader.peek();
            if (ch == '\r' || ch == '\n' || ch == 0)
                break;
            else {
                token.initEnd(charReader);
                charReader.skip(1);
            }
        }
    }

    private void readBlockComment() {
        Token token = new Token(TokenType.COMMENT, TokenSubType.BLOCK_COMMENT);

        token.initStart(charReader);
        charReader.skip(1);
        token.initEnd(charReader);
        this.tokens.add(token);
        charReader.skip(1);

        while (true) {
            char ch1 = charReader.peek();
            char ch2 = charReader.peek(1);
            if (ch1 == 0 || ch2 == 0) {
                throw new RuntimeException(String.format("%s(%s: %s): End of file found before end of comment.", token.getFilename(), token.getEndLine(), token.getEndColumn()));
            } else if (ch1 == '*' && ch2 == '/') {
                charReader.skip(1);
                token.initEnd(charReader);
                charReader.skip(1);
                break;
            } else {
                token.initEnd(charReader);
                charReader.skip(1);
            }
        }
    }

    private void readIdentifierOrKeyword() {
        Token token = new Token(TokenType.IDENTIFIER);

        token.initStart(charReader);
        token.initEnd(charReader);
        this.tokens.add(token);

        while (true) {
            char ch = charReader.peek();
            if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9') || ch == '_' || ch >= 0x100) {
                token.initEnd(charReader);
                charReader.skip(1);
            } else
                break;
        }

        if(Constants.KEYWORDS.contains(token.getImage().toLowerCase()))
            token.setType(TokenType.KEYWORD);
    }

    private void readString() {
        Token token = new Token(TokenType.LITERAL, TokenSubType.STRING);

        token.initStart(charReader);
        token.initEnd(charReader);
        this.tokens.add(token);

        char quote = charReader.read();

        while (true) {
            char ch = charReader.peek();
            if (ch == 0 || ch == '\n') {
                throw new RuntimeException(String.format("%s(%s: %s): Unterminated string.", token.getFilename(), token.getEndLine(), token.getEndColumn()));
            } else if (ch == '\'') {
                token.initEnd(charReader);
                charReader.skip(1);

                if (quote == '\'') {
                    ch = charReader.peek();
                    if (ch == '\'') {
                        token.initEnd(charReader);
                        charReader.skip(1);
                    } else
                        break;
                }
            } else if (ch == '"') {
                token.initEnd(charReader);
                charReader.skip(1);
                if (quote == '"')
                    break;
            } else {
                token.initEnd(charReader);
                charReader.skip(1);
            }
        }
    }

    private void readNumber() {
        Token token = new Token(TokenType.LITERAL, TokenSubType.NUMBER);

        token.initStart(charReader);
        token.initEnd(charReader);
        this.tokens.add(token);

        while (true) {
            char ch = charReader.peek();
            if ((ch >= '0' && ch <= '9') || ch == '.') {
                token.initEnd(charReader);
                charReader.skip(1);
            } else
                break;
        }
    }

    private void readSymbol() {
        Token token = new Token(TokenType.SYMBOL);

        token.initStart(charReader);
        token.initEnd(charReader);
        this.tokens.add(token);

        String symbol = "";
        while (true) {
            char ch = charReader.peek();
            if (ch == 0)
                break;

            symbol += ch;
            if (Constants.SYMBOLS.contains(symbol)) {
                token.initEnd(charReader);
                charReader.skip(1);
            } else
                break;
        }
    }

    public void lex() {
        char ch;
        while (true) {
            ch = charReader.peek();
            ;

            //结尾
            if (ch == 0)
                break;

            //跳过空白
            if (ch <= ' ') {
                skipBlank();
                continue;
            }

            //跳过行注释
            if (ch == '-' && charReader.peek(1) == '-') {
                readLineComment();
                continue;
            }

            //跳过块注释
            if (ch == '/' && charReader.peek(1) == '*') {
                readBlockComment();
                continue;
            }

            //标识符或关键字
            if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_' || ch > 0x100) {
                readIdentifierOrKeyword();
                continue;
            }

            //字符串
            if (ch == '"' || ch == '\'') {
                readString();
                continue;
            }

            //数字
            if (ch >= '0' && ch <= '9') {
                readNumber();
                continue;
            }

            //符号
            if (symbolCharSet.contains(ch)) {
                readSymbol();
                continue;
            }

            throw new RuntimeException(String.format("%s(%s: %s): Unknown character: %c.", charReader.getChunkName(), charReader.getLine(), charReader.getColumn(), ch));
        }
    }
}
