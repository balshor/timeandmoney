/**
 * Copyright (c) 2004 Domain Language, Inc. (http://domainlanguage.com) This
 * free software is distributed under the "MIT" licence. See file licence.txt.
 * For more information, see http://timeandmoney.sourceforge.net.
 */

package com.domainlanguage.util;

public abstract class TypeCheck {

    static public boolean is(Object instance, Class type) {
        return (instance != null) && type.isAssignableFrom(instance.getClass());
    }

    static public boolean is(Class subtype, Class type) {
        return type.isAssignableFrom(subtype);
    }

    static public boolean sameClassOrBothNull(Object first, Object second) {
        if ((first == null) || (second == null))
            return (first == second);

        if (first == second)
            return true;

        return is(first, second.getClass());
    }

}