package org.yuyun.jsqlparser;

import lombok.Data;

@Data
public class Token {
    private String filename;
    private String image;
    private int beginLine;
    private int beginColumn;
    private int beginPosition;
    private int endLine;
    private int endColumn;
    private int endPosition;
    private TokenType type;
    private TokenSubType subType;

    public Token(TokenType type) {
        this.type = type;
        this.subType = TokenSubType.NONE;
    }

    public Token(TokenType type, TokenSubType subType) {
        this.type = type;
        this.subType = subType;
    }

    public void initStart(CharReader reader) {
        this.filename = reader.getChunkName();
        this.beginLine = reader.getLine();
        this.beginColumn = reader.getColumn();
        this.beginPosition = reader.getPosition();
    }

    public void initEnd(CharReader reader) {
        this.endLine = reader.getLine();
        this.endColumn = reader.getColumn();
        this.endPosition = reader.getPosition();
        this.image = reader.substring(this.beginPosition, this.endPosition);
    }
}
