package com.github.veithen.alta.pattern;

public class EvaluationException extends Exception {
    private static final long serialVersionUID = 1L;

    public EvaluationException(String msg) {
        super(msg);
    }

    public EvaluationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
