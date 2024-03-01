package org.yuyun.jsqlparser;

import lombok.Data;

@Data
public class Token {
    private CharReader charReader;
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

    public Token(CharReader charReader, TokenType type) {
        this.charReader = charReader;
        this.type = type;
        this.subType = TokenSubType.NONE;
    }

    public Token(CharReader charReader, TokenType type, TokenSubType subType) {
        this.charReader = charReader;
        this.type = type;
        this.subType = subType;
    }

    public void initStart() {
        this.filename = this.charReader.getChunkName();
        this.beginLine = this.charReader.getLine();
        this.beginColumn = this.charReader.getColumn();
        this.beginPosition = this.charReader.getPosition();
        this.endLine = this.charReader.getLine();
        this.endColumn = this.charReader.getColumn();
        this.endPosition = this.charReader.getPosition();
    }

    public void initEnd() {
        this.endLine = this.charReader.getLine();
        this.endColumn = this.charReader.getColumn();
        this.endPosition = this.charReader.getPosition();

    }

    public String getImage() {
        if(this.image == null)
            this.image = this.charReader.substring(this.beginPosition, this.endPosition);
        return this.image;
    }
}
