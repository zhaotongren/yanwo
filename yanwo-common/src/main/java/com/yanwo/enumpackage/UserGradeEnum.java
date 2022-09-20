package com.yanwo.enumpackage;

public enum UserGradeEnum {

    common(0) {
        public String toString() {
            return "普通会员";
        }
    },
    senior(1) {
        public String toString() {
            return "VIP会员";
        }
    },
    supern(2) {
        public String toString() {
            return "知己会员";
        }
    };

    private final int val;

    UserGradeEnum(int n) {
        val = n;
    }

    public int getIntValue() {
        return val;
    }

    public static UserGradeEnum getByIntValue(int n) {
        switch (n) {
            case 0:
                return common;
            case 1:
                return senior;
            case 2:
                return supern;
            default:
                return null;
        }
    }
}
