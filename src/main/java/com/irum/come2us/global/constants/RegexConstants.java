package com.irum.come2us.global.constants;

public final class RegexConstants {

    private RegexConstants() {}
    ;

    public static final String EMAIL = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    public static final String PHONE_NUMBER = "^010-?(\\d{4})-?(\\d{4})$";
}
