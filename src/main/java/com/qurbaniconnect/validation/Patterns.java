package com.qurbaniconnect.validation;

/** Shared regexes for Pakistani phone numbers, CNICs and passwords. */
public final class Patterns {
    private Patterns() {}
    public static final String PK_PHONE = "^(03\\d{9}|\\+923\\d{9})$";
    public static final String CNIC = "^\\d{5}-?\\d{7}-?\\d$";
    public static final String PASSWORD = "^(?=.*[A-Za-z])(?=.*\\d).{8,64}$";
    public static final String PASSWORD_MSG = "Password must be 8-64 characters and contain a letter and a digit";
    public static final String PHONE_MSG = "Phone must be a valid Pakistani mobile number (e.g. 03001234567)";
}
