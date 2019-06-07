package me.simplesoul.scalaL2.generic;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class TypeErasure4Java {

    public static void main(String[] args) {
        Interviewee4j interviewee = new Interviewee4j<Python4j>(new Python4j()) {};
        interviewee.getSkilledLanguage();
    }
}

class Interviewee4j<L> {

    L skilled;

    public Interviewee4j(L language) {
        this.skilled = language;
    }

    public L getSkilledLanguage() {
        assert(getClass().getGenericSuperclass().toString().equals("me.simplesoul.scalaL2.generic.Interviewee4j<me.simplesoul.scalaL2.generic.Python4j>"));
        assert(((ParameterizedType) (getClass().getGenericSuperclass())).getActualTypeArguments()[0].getTypeName() == "me.simplesoul.scalaL2.generic.Python4j");
        return skilled;
    }
}


abstract class Language4j {}

class Python4j extends Language4j {}

class Cpp4j extends Language4j {}
