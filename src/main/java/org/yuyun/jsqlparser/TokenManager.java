package org.yuyun.jsqlparser;

import java.util.List;

import static java.lang.Math.min;

public class TokenManager {
    private final List<Token> tokens;
    private int beginIndex;
    private int endIndex;
    private int position;

    public TokenManager(List<Token> tokens, int beginIndex, int endIndex) {
        this.tokens = tokens;
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
        this.position = beginIndex;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void skip(int n) {
        this.position = min(this.position + n, this.endIndex + 1);
    }

    public void skipComment() {
        int start = this.position;
        for(int i=start; i<=this.endIndex; i++) {
            Token token = this.tokens.get(i);
            if(token.getType() != TokenType.COMMENT) {
                this.position = i;
                return;
            }
        }
        this.position = this.endIndex + 1;
    }

    public Token peek() {
        return peek(0);
    }

    public Token peek(int skip) {
        int pos = this.position + skip;
        if(pos < this.beginIndex || pos > this.endIndex)
            return null;
        return this.tokens.get(pos);
    }

    public Token read() {
        Token token;

        if(this.position > this.endIndex)
            return null;
        token = this.tokens.get(this.position);
        this.position += 1;

        return token;
    }

    public int findLeftBracket(String leftBracket) {
        for(int i=this.position; i<= this.endIndex; i++) {
            Token token = this.tokens.get(i);
            if(token.getType() == TokenType.SYMBOL && token.getImage().equalsIgnoreCase(leftBracket))
                return i;
        }
        return -1;
    }

    public int findMatchedBracket(int index, String leftBracket, String rightBracket) {
        int usage = 1;
        for(int i=index; i<= this.endIndex; i++) {
            Token token = this.tokens.get(i);
            if(token.getType() == TokenType.SYMBOL) {
                if(token.getImage().equalsIgnoreCase("("))
                    usage += 1;
                else if(token.getImage().equalsIgnoreCase(")")) {
                    usage -= 1;
                    if(usage == 0)
                        return i;
                }
            }
        }
        return -1;
    }

    public Token readComplexIdentifier() {
        Token token = this.peek();
        if(token == null)
            return null;

        while (true) {
            if(token.getType() != TokenType.IDENTIFIER)
                return null;
            skip(1);

            Token sym = this.peek();
            if(sym != null && sym.getType() == TokenType.SYMBOL && sym.getImage().equals("."))
                skip(1);
            else
                return token;
        }
    }
}
