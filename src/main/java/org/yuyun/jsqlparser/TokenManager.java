package org.yuyun.jsqlparser;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.min;

public class TokenManager {
    private final List<Token> tokens = new ArrayList<>();
    private int position = 0;

    public TokenManager(List<Token> tokens, int beginIndex, int endIndex) {
        for(int i=beginIndex; i<=endIndex; i++) {
            Token token = tokens.get(i);
            if(token.getType() != TokenType.COMMENT)
                this.tokens.add(token);
        }
    }

    public TokenManager subset(int beginIndex, int endIndex) {
        return new TokenManager(this.tokens, beginIndex, endIndex);
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void skip(int n) {
        this.position = min(this.position + n, this.tokens.size());
    }

    public Token peek() {
        return peek(0);
    }

    public Token peek(int skip) {
        int pos = this.position + skip;
        if(pos >= this.tokens.size())
            return null;
        return this.tokens.get(pos);
    }

    public Token read() {
        Token token;

        if(this.position >= this.tokens.size())
            return null;
        token = this.tokens.get(this.position);
        this.position += 1;

        return token;
    }

    public int findLeftBracket(String leftBracket) {
        for(int i=this.position; i< this.tokens.size(); i++) {
            Token token = this.tokens.get(i);
            if(token.getType() == TokenType.SYMBOL && token.getImage().equalsIgnoreCase(leftBracket))
                return i;
        }
        return -1;
    }

    public int findMatchedBracket(int index, String leftBracket, String rightBracket) {
        int usage = 1;
        for(int i=index; i< this.tokens.size(); i++) {
            Token token = this.tokens.get(i);
            if(token.getType() == TokenType.SYMBOL) {
                if(token.getImage().equalsIgnoreCase(leftBracket))
                    usage += 1;
                else if(token.getImage().equalsIgnoreCase(rightBracket)) {
                    usage -= 1;
                    if(usage == 0)
                        return i;
                }
            }
        }
        return -1;
    }

    public Token readComplexIdentifier() {
        Token token;

        while (true) {
            token = this.tryReadIdentifier();
            if(token == null)
                return null;

            if(this.tryReadSymbol(".") == null)
                return token;
        }
    }

    public Token tryReadIdentifier() {
        Token token = this.peek();
        if(token != null && token.getType() == TokenType.IDENTIFIER) {
            return this.read();
        }
        return null;
    }

    public Token tryReadKeyword(String keyword) {
        Token token = this.peek();
        if(token != null && token.getType() == TokenType.KEYWORD && token.getImage().equalsIgnoreCase(keyword)) {
            return this.read();
        }
        return null;
    }

    public Token tryReadKeyword() {
        Token token = this.peek();
        if(token != null && token.getType() == TokenType.KEYWORD) {
            return this.read();
        }
        return null;
    }

    public Token tryReadSymbol(String symbol) {
        Token token = this.peek();
        if(token != null && token.getType() == TokenType.SYMBOL && token.getImage().equalsIgnoreCase(symbol)) {
            return this.read();
        }
        return null;
    }
}
