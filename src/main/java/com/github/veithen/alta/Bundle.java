package com.github.veithen.alta;

final class Bundle {
    private final String symbolicName;

    Bundle(String symbolicName) {
        this.symbolicName = symbolicName;
    }

    String getSymbolicName() {
        return symbolicName;
    }
}
