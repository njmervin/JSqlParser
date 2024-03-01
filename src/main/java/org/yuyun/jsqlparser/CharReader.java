package org.yuyun.jsqlparser;

import java.io.IOException;

import static java.lang.Math.max;

public class CharReader {
    private final char[] cbuf;
    private final String chunkName;
    private int line = 1;
    private int column = 1;
    private int position = 0;

    public CharReader(String text) {
        this.cbuf = text.toCharArray();
        this.chunkName = "<main>";
    }

    public CharReader(String text, String chunkName, int line) {
        this.cbuf = text.toCharArray();
        this.chunkName = chunkName;
        this.line = max(line, 1);
    }

    public String getChunkName() {
        return chunkName;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public int getPosition() {
        return position;
    }

    public String substring(int beginIndex, int endIndex) {
        return new String(this.cbuf, beginIndex, endIndex - beginIndex + 1);
    }

    public char[] buffer() {
        return this.cbuf;
    }

    public char peek() {
        return this.peek(0);
    }

    public char peek(int skip) {
        int pos = this.position + skip;
        if(pos < this.cbuf.length)
            return this.cbuf[pos];
        else
            return 0;
    }

    public void skip(int n) {
        for(int i=0; i<n; i++)
            this.read();
    }

    public char read() {
        char ch;

        if(this.position >= this.cbuf.length)
            return 0;

        ch = this.cbuf[this.position++];
        if(ch == '\n') {
            this.line += 1;
            this.column = 1;
            return ch;
        }
        else if(ch == '\r') {
            ch = this.peek();
            if(ch == '\n') {
                this.position += 1;
            }
            this.line += 1;
            this.column = 1;
            return '\n';
        }
        else {
            this.column += 1;
            return ch;
        }
    }
}
