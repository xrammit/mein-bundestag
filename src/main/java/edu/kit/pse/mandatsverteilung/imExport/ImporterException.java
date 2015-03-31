package edu.kit.pse.mandatsverteilung.imExport;

public class ImporterException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = -6165671765861900528L;
    private int column;
    private int line;
    private KindOfException kind;

    /**
     * 
     * @param message
     * @param line
     * @param column
     * @param throwable
     */
    ImporterException(String message, int line, int column,
            Throwable throwable, KindOfException kind) {
        super("ImportException in line: " + line + " and column: " + column
                + ": " + message, throwable);
        this.line = line;
        this.column = column;
        this.kind = kind;
    }

    ImporterException(String message, int line, Throwable throwable,
            KindOfException kind) {
        super("ImportException in line: " + line + " : " + message, throwable);
        this.line = line;
        this.kind = kind;
    }

    /**
     * 
     * @param message
     * @param line
     * @param column
     */
    ImporterException(String message, int line, int column, KindOfException kind) {
        super("ImportException in line: " + line + " and column: " + column
                + ": " + message);
        this.line = line;
        this.column = column;
        this.kind = kind;
    }

    ImporterException(String message, int line, KindOfException kind) {
        super("ImportException in line: " + line + " : " + message);
        this.line = line;
        this.kind = kind;
    }

    ImporterException(String message, KindOfException kind) {
        super("ImportException: " + message);
        this.line = -1;
        this.column = -1;
        this.kind = kind;
    }

    public ImporterException(String message, Throwable n, KindOfException kind) {
        super("ImportException: " + message);
        this.kind = kind;
    }

    public ImporterException(Throwable throwable, KindOfException kind) {
        super(throwable);
        this.kind = kind;
    }

    public KindOfException getKind() {
        return kind;
    }

    public int getSourceLine() {
        return this.line;
    }

    public int getSourceColumn() {
        return this.column;
    }

}
