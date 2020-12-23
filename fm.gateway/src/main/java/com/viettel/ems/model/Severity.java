package com.viettel.ems.model;

public enum Severity {
    NORMAL,
    WARNING,
    MINOR,
    MAJOR,
    CRITICAL;

    public static Severity fromCode(int code) {
        switch (code) {
            case 4: return CRITICAL;
            case 3: return MAJOR;
            case 2: return MINOR;
            case 1: return WARNING;
            default: return NORMAL;
        }
    }
}
