/*
 * Copyright (C) 2005-2017 Qihoo 360 Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.zero.support.compat.util;

import android.content.Context;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 和反射操作有关的Utils
 */
public final class ReflectUtils {

    // ----------------
    // Class & Constructor
    // ----------------

    public static Class<?> getClass(final String className) throws ClassNotFoundException {
        return Class.forName(className);
    }

    public static <T> T invokeConstructor(Class<T> cls, Class[] parameterTypes, Object... args) throws
            NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<T> c = cls.getConstructor(parameterTypes);
        if (c != null) {
            c.setAccessible(true);
            return c.newInstance(args);
        }
        return null;
    }

    // ----------------
    // Field
    // ----------------

    public static Field getField(Class<?> cls, String fieldName) {
        // From Apache: FieldUtils.getField()

        // check up the superclass hierarchy
        for (Class<?> acls = cls; acls != null; acls = acls.getSuperclass()) {
            try {
                final Field field = acls.getDeclaredField(fieldName);
                // getDeclaredField checks for non-public scopes as well
                // and it returns accurate results
                setAccessible(field, true);

                return field;
            } catch (final NoSuchFieldException ex) { // NOPMD
                // ignore
            }
        }
        // check the public interface case. This must be manually searched for
        // incase there is a public supersuperclass field hidden by a private/package
        // superclass field.
        Field match = null;
        for (final Class<?> class1 : cls.getInterfaces()) {
            try {
                final Field test = class1.getField(fieldName);
                Validate.isTrue(match == null, "Reference to field %s is ambiguous relative to %s"
                        + "; a matching field exists on two or more implemented interfaces.", fieldName, cls);
                match = test;
            } catch (final NoSuchFieldException ex) { // NOPMD
                // ignore
            }
        }
        return match;
    }

    public static Object readStaticField(Class<?> c, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        return readField(c, null, fieldName);
    }

    public static Object readField(Object target, String fieldName) throws IllegalAccessException, NoSuchFieldException {
        return readField(target.getClass(), target, fieldName);
    }

    public static Object readField(Class<?> c, Object target, String fieldName) throws IllegalAccessException, NoSuchFieldException {
        Field f = getField(c, fieldName);

        return readField(f, target);
    }

    public static Object readField(final Field field, final Object target) throws IllegalAccessException {
        return field.get(target);
    }

    public static void writeField(Object target, String fName, Object value) throws NoSuchFieldException, IllegalAccessException {
        writeField(target.getClass(), target, fName, value);
    }

    public static void writeField(Class<?> c, Object object, String fName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field f = getField(c, fName);
        writeField(f, object, value);
    }

    public static void writeField(final Field field, final Object target, final Object value) throws IllegalAccessException {
        field.set(target, value);
    }

    public static List<Field> getAllFieldsList(final Class<?> cls) {
        // From Apache: FieldUtils.getAllFieldsList()

        Validate.isTrue(cls != null, "The class must not be null");
        final List<Field> allFields = new ArrayList<>();
        Class<?> currentClass = cls;
        while (currentClass != null) {
            final Field[] declaredFields = currentClass.getDeclaredFields();
            for (final Field field : declaredFields) {
                allFields.add(field);
            }
            currentClass = currentClass.getSuperclass();
        }
        return allFields;
    }

    public static void removeFieldFinalModifier(final Field field) {
        // From Apache: FieldUtils.removeFinalModifier()
        Validate.isTrue(field != null, "The field must not be null");

        try {
            if (Modifier.isFinal(field.getModifiers())) {
                // Do all JREs implement Field with a private ivar called "modifiers"?
                final Field modifiersField = Field.class.getDeclaredField("modifiers");
                final boolean doForceAccess = !modifiersField.isAccessible();
                if (doForceAccess) {
                    modifiersField.setAccessible(true);
                }
                try {
                    modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                } finally {
                    if (doForceAccess) {
                        modifiersField.setAccessible(false);
                    }
                }
            }
        } catch (final NoSuchFieldException ignored) {
            // The field class contains always a modifiers field
        } catch (final IllegalAccessException ignored) {
            // The modifiers field is made accessible
        }
    }

    // ----------------
    // Method
    // ----------------

    public static Method getMethod(Class<?> cls, String methodName, Class<?>... parameterTypes) {
        // check up the superclass hierarchy
        for (Class<?> acls = cls; acls != null; acls = acls.getSuperclass()) {
            try {
                final Method method = acls.getDeclaredMethod(methodName, parameterTypes);
                // getDeclaredField checks for non-public scopes as well
                // and it returns accurate results
                setAccessible(method, true);

                return method;
            } catch (final NoSuchMethodException ex) { // NOPMD
                // ignore
            }
        }
        // check the public interface case. This must be manually searched for
        // incase there is a public supersuperclass field hidden by a private/package
        // superclass field.
        Method match = null;
        for (final Class<?> class1 : cls.getInterfaces()) {
            try {
                final Method test = class1.getMethod(methodName, parameterTypes);
                Validate.isTrue(match == null, "Reference to field %s is ambiguous relative to %s"
                        + "; a matching field exists on two or more implemented interfaces.", methodName, cls);
                match = test;
            } catch (final NoSuchMethodException ex) { // NOPMD
                // ignore
            }
        }
        return match;
    }


    public static Object invokeMethod(final Object object, final String methodName, Class<?>[] methodParamTypes, Object... args)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class clz = object.getClass();
        Method m = getMethod(clz, methodName, methodParamTypes);
        return m.invoke(args);
    }

    public static Object invokeMethod(ClassLoader loader, String clzName,
                                      String methodName, Object methodReceiver,
                                      Class<?>[] methodParamTypes, Object... methodParamValues) throws
            ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (methodReceiver == null) {
            return null;
        }

        Class clz = Class.forName(clzName, false, loader);
        if (clz != null) {
            Method med = clz.getMethod(methodName, methodParamTypes);
            if (med != null) {
                med.setAccessible(true);
                return med.invoke(methodReceiver, methodParamValues);
            }
        }
        return null;
    }

    public static <T> T invokeMethodQuietly(final Object object, final String methodName, Class<?>[] methodParamTypes, Object... args) {
        Class clz = object.getClass();
        Method m = getMethod(clz, methodName, methodParamTypes);
        T result = null;
        try {
            result = (T) m.invoke(object, args);
        } catch (Throwable e) {
            //ignore
        }
        return result;
    }

    public static void setAccessible(AccessibleObject ao, boolean value) {
        if (ao.isAccessible() != value) {
            ao.setAccessible(value);
        }
    }

    // ----------------
    // Other
    // ----------------

    public static final void dumpObject(Object object, FileDescriptor fd, PrintWriter writer, String[] args) {
        try {
            Class<?> c = object.getClass();
            do {
                writer.println("c=" + c.getName());
                Field fields[] = c.getDeclaredFields();
                for (Field f : fields) {
                    boolean acc = f.isAccessible();
                    if (!acc) {
                        f.setAccessible(true);
                    }
                    Object o = f.get(object);
                    writer.print(f.getName());
                    writer.print("=");
                    if (o != null) {
                        writer.println(o.toString());
                    } else {
                        writer.println("null");
                    }
                    if (!acc) {
                        f.setAccessible(acc);
                    }
                }
                c = c.getSuperclass();
            } while (c != null && !c.equals(Object.class) && !c.equals(Context.class));
        } catch (Throwable e) {
//            if (LOGR) {
//                LebianLog.e(MISC_TAG, e.getMessage(), e);
//            }
        }
    }


    /**
     * Locates a given field anywhere in the class inheritance hierarchy.
     *
     * @param instance an object to search the field into.
     * @param name     field name
     * @return a field object
     * @throws NoSuchFieldException if the field cannot be located
     */
    public static Field findField(Object instance, String name) throws NoSuchFieldException {
        for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Field field = clazz.getDeclaredField(name);

                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }

                return field;
            } catch (NoSuchFieldException e) {
                // ignore and search next
            }
        }

        throw new NoSuchFieldException("Field " + name + " not found in " + instance.getClass());
    }

    public static Field findField(Class<?> originClazz, String name) throws NoSuchFieldException {
        for (Class<?> clazz = originClazz; clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Field field = clazz.getDeclaredField(name);

                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }

                return field;
            } catch (NoSuchFieldException e) {
                // ignore and search next
            }
        }

        throw new NoSuchFieldException("Field " + name + " not found in " + originClazz);
    }

    /**
     * Locates a given method anywhere in the class inheritance hierarchy.
     *
     * @param instance       an object to search the method into.
     * @param name           method name
     * @param parameterTypes method parameter types
     * @return a method object
     * @throws NoSuchMethodException if the method cannot be located
     */
    public static Method findMethod(Object instance, String name, Class<?>... parameterTypes)
            throws NoSuchMethodException {
        for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Method method = clazz.getDeclaredMethod(name, parameterTypes);

                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }

                return method;
            } catch (NoSuchMethodException e) {
                // ignore and search next
            }
        }

        throw new NoSuchMethodException("Method "
                + name
                + " with parameters "
                + Arrays.asList(parameterTypes)
                + " not found in " + instance.getClass());
    }

    /**
     * Locates a given method anywhere in the class inheritance hierarchy.
     *
     * @param clazz          a class to search the method into.
     * @param name           method name
     * @param parameterTypes method parameter types
     * @return a method object
     * @throws NoSuchMethodException if the method cannot be located
     */
    public static Method findMethod(Class<?> clazz, String name, Class<?>... parameterTypes)
            throws NoSuchMethodException {
        for (; clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Method method = clazz.getDeclaredMethod(name, parameterTypes);

                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }

                return method;
            } catch (NoSuchMethodException e) {
                // ignore and search next
            }
        }

        throw new NoSuchMethodException("Method "
                + name
                + " with parameters "
                + Arrays.asList(parameterTypes)
                + " not found in " + clazz);
    }

    /**
     * Locates a given constructor anywhere in the class inheritance hierarchy.
     *
     * @param instance       an object to search the constructor into.
     * @param parameterTypes constructor parameter types
     * @return a constructor object
     * @throws NoSuchMethodException if the constructor cannot be located
     */
    public static Constructor<?> findConstructor(Object instance, Class<?>... parameterTypes)
            throws NoSuchMethodException {
        for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Constructor<?> ctor = clazz.getDeclaredConstructor(parameterTypes);

                if (!ctor.isAccessible()) {
                    ctor.setAccessible(true);
                }

                return ctor;
            } catch (NoSuchMethodException e) {
                // ignore and search next
            }
        }

        throw new NoSuchMethodException("Constructor"
                + " with parameters "
                + Arrays.asList(parameterTypes)
                + " not found in " + instance.getClass());
    }

    /**
     * Replace the value of a field containing a non null array, by a new array containing the
     * elements of the original array plus the elements of extraElements.
     *
     * @param instance      the instance whose field is to be modified.
     * @param fieldName     the field to modify.
     * @param extraElements elements to append at the end of the array.
     */
    public static void expandFieldArray(Object instance, String fieldName, Object[] extraElements)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field jlrField = findField(instance, fieldName);

        Object[] original = (Object[]) jlrField.get(instance);
        Object[] combined = (Object[]) Array.newInstance(original.getClass().getComponentType(), original.length + extraElements.length);

        // NOTE: changed to copy extraElements first, for patch load first

        System.arraycopy(extraElements, 0, combined, 0, extraElements.length);
        System.arraycopy(original, 0, combined, extraElements.length, original.length);

        jlrField.set(instance, combined);
    }

    /**
     * Replace the value of a field containing a non null array, by a new array containing the
     * elements of the original array plus the elements of extraElements.
     *
     * @param instance  the instance whose field is to be modified.
     * @param fieldName the field to modify.
     */
    public static void reduceFieldArray(Object instance, String fieldName, int reduceSize)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        if (reduceSize <= 0) {
            return;
        }

        Field jlrField = findField(instance, fieldName);

        Object[] original = (Object[]) jlrField.get(instance);
        int finalLength = original.length - reduceSize;

        if (finalLength <= 0) {
            return;
        }

        Object[] combined = (Object[]) Array.newInstance(original.getClass().getComponentType(), finalLength);

        System.arraycopy(original, reduceSize, combined, 0, finalLength);

        jlrField.set(instance, combined);
    }

    public static Object getActivityThread(Context context,
                                           Class<?> activityThread) {
        try {
            if (activityThread == null) {
                activityThread = Class.forName("android.app.ActivityThread");
            }
            Method m = activityThread.getMethod("currentActivityThread");
            m.setAccessible(true);
            Object currentActivityThread = m.invoke(null);
            if (currentActivityThread == null && context != null) {
                // In older versions of Android (prior to frameworks/base 66a017b63461a22842)
                // the currentActivityThread was built on thread locals, so we'll need to try
                // even harder
                Field mLoadedApk = context.getClass().getField("mLoadedApk");
                mLoadedApk.setAccessible(true);
                Object apk = mLoadedApk.get(context);
                Field mActivityThreadField = apk.getClass().getDeclaredField("mActivityThread");
                mActivityThreadField.setAccessible(true);
                currentActivityThread = mActivityThreadField.get(apk);
            }
            return currentActivityThread;
        } catch (Throwable ignore) {
            return null;
        }
    }

    /**
     * Handy method for fetching hidden integer constant value in system classes.
     *
     * @param clazz
     * @param fieldName
     * @return
     */
    public static int getValueOfStaticIntField(Class<?> clazz, String fieldName, int defVal) {
        try {
            final Field field = findField(clazz, fieldName);
            return field.getInt(null);
        } catch (Throwable thr) {
            return defVal;
        }
    }
}
