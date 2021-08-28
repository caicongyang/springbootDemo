package com.caicongyang.utils;

import com.google.common.collect.Lists;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * @author WuBo
 * @version 1.0.1
 * @CreateDate 2010-03-18 15:27:02
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class ReflectUtils {

    /**
     * 调用字段名对象的setter方法，也有可能只是以set*的普通方法
     *
     * @param instance 调用实例
     * @param fieldName 字段名，也可能只是一个普通的名字
     * @param type setter方法参数类型
     * @param args setter方法参数
     * @return setter方法调用后的返回值，通常为null
     */
    public static Object callSetMethod(Object instance, String fieldName, Class<?> type,
        Object... args)
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object result = null;

        Method setter = setter(instance.getClass(), fieldName, type);
        if (setter != null) {
            result = setter.invoke(instance, args);
        }

        return result;
    }

    /**
     * 根据字段名调用对象的getter方法，如果字段类型为boolean，则方法名可能为is开头，也有可能只是以setFieleName的普通方法
     *
     * @return getter方法调用后的返回值
     */
    public static Object callGetMethod(Object instance, String fieldName)
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object result = null;

        Method getter = getter(instance.getClass(), fieldName);
        if (getter != null) {
            result = getter.invoke(instance);
        }

        return result;
    }

    /**
     * 得到给写的类或其父类中声明的可访问公共字段
     *
     * @param entityClass 类
     * @param fieldname 字段名
     * @param caseSensitive 是否大小写敏感
     */
    public static Field getField(Class<?> entityClass, String fieldname, boolean caseSensitive)
        throws NoSuchFieldException {
        if (caseSensitive) {
            try {
                Field f = entityClass.getField(fieldname);
                if (f != null) {
                    return f;
                }
            } catch (NoSuchFieldException e) {
                if (entityClass.getSuperclass() != null
                    && entityClass.getSuperclass() != Object.class) {
                    return getField(entityClass.getSuperclass(), fieldname, caseSensitive);
                } else {
                    throw e;
                }
            }
        } else {
            Field[] fs = entityClass.getFields();
            for (int i = 0; i < fs.length; i++) {
                Field f = fs[i];
                if (fieldname.toLowerCase().equals(f.getName().toLowerCase())) {
                    return f;
                }
            }
        }
        return null;
    }

    /**
     * 得到给写的类或其父类中声明的公共方法
     */
    public static Method getMethod(Class<?> entityClass, String methodName, Class<?>... type)
        throws NoSuchMethodException {
        try {
            Method m = entityClass.getMethod(methodName, type);
            if (m != null) {
                return m;
            }
        } catch (NoSuchMethodException ex) {
            if (entityClass.getSuperclass() != null
                && entityClass.getSuperclass() != Object.class) {
                return getMethod(entityClass.getSuperclass(), methodName, type);
            } else {
                throw ex;
            }
        }
        return null;
    }

    /**
     * 得到给定类或其父类的所有可访问公共字段
     */
    public static Field[] getFields(Class<?> entityClass, boolean containsStatic) {
        return getFields(entityClass, containsStatic ? 0 : Modifier.STATIC);
    }

    public static Field[] getFields(Class<?> entityClass, int filterModifierSet) {
        List<Field> fields = new ArrayList<Field>();

        Field[] temp = entityClass.getFields();
        for (int i = 0; i < temp.length; i++) {
            Field f = temp[i];
            if (hasModifiers(f, filterModifierSet)) {
                continue;
            } else {
                fields.add(f);
            }
        }
        if (entityClass.getSuperclass() != null && entityClass.getSuperclass() != Object.class) {
            Field[] fs = getFields(entityClass.getSuperclass(), filterModifierSet);
            for (int i = 0; i < fs.length; i++) {
                fields.add(fs[i]);
            }
        }
        return fields.toArray(new Field[fields.size()]);
    }

    public static Class getSuperClassGenericType(Class clazz, int index) {
        Type type = getParameterizedTypeBySuperClass(clazz, index);
        return getClassFromType(type, Object.class);
    }

    public static Type getParameterizedTypeBySuperClass(Class clazz, int index) {
        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            Class superClass = clazz.getSuperclass();
            if (superClass != Object.class) {
                return getSuperClassGenericType(superClass, index);
            }
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            return Object.class;
        }

        return params[index];
    }

    public static Class getInterfaceGenericType(Class clazz, Class interfaceClazz, int index) {
        Type type = getParameterizedTypeByInterface(clazz, interfaceClazz, index);
        return getClassFromType(type, Object.class);
    }

    public static Type getParameterizedTypeByInterface(Class clazz, Class interfaceClazz,
        int index) {
        if (ClassUtils.isCglibProxyClass(clazz)) {
            clazz = ClassUtils.getUserClass(clazz);
        }
        Type[] genTypes = clazz.getGenericInterfaces();

        for (Type genType : genTypes) {
            if (!(genType instanceof ParameterizedType)) {
                continue;
            }

            ParameterizedType paramType = (ParameterizedType) genType;
            if (interfaceClazz != null) {
                Class rawType = (Class) paramType.getRawType();
                if (!interfaceClazz.isAssignableFrom(rawType)) {
                    continue;
                }
            }

            Type[] params = paramType.getActualTypeArguments();
            if (index >= params.length || index < 0) {
                return Object.class;
            }
            return params[index];
        }

        for (Class<?> itf : clazz.getInterfaces()) {
            Type result = getInterfaceGenericType(itf, interfaceClazz, index);
            if (result != Object.class) {
                return result;
            }
        }

        return Object.class;
    }

    public static Class getClassFromType(Type type, Class defaultClass) {
        if (type instanceof Class) {
            return (Class) type;
        }
        if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            if (rawType instanceof Class) {
                return (Class) rawType;
            }
        }
        return defaultClass;
    }

    /**
     * @author WuBo
     * @CreateDate 2012-7-27 下午04:41:49
     */
    public static Method setter(Class clazz, String fieldName, Class<?>... type)
        throws NoSuchMethodException {
        PropertyDescriptor pd = getPropertyDescriptor(clazz, fieldName);

        if (pd != null) {
            if (pd.getWriteMethod() != null && Modifier
                .isPublic(pd.getWriteMethod().getModifiers())) {
                return pd.getWriteMethod();
            }
        }

        return getMethod(clazz, "set" + StringUtils.capitalize(fieldName), type);
    }

    public static Method setter4Safe(Class clazz, String fieldName) {
        try {
            return setter(clazz, fieldName);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * 得到类的所有只有一个参数且方法名以set为前缀的方法
     */
    public static Method[] setters(Class clazz) {
        return setters(clazz, null);
    }

    public static Method[] setters(Class clazz, Predicate<PropertyDescriptor> predicate) {
        PropertyDescriptor[] pds = getPropertyDescriptors(clazz);

        List<Method> rslt = new ArrayList<Method>(pds.length);
        for (PropertyDescriptor pd : pds) {
            Method setter = pd.getWriteMethod();
            if (setter != null) {
                if (predicate == null || predicate.test(pd)) {
                    rslt.add(setter);
                }
            }
        }
        return rslt.toArray(new Method[rslt.size()]);
    }

    /**
     * 得到类的所有空参且方法名以get或is为前缀的方法
     */
    public static Method[] getters(Class clazz) {
        List<Method> rslt = doGetters(clazz, null);
        return rslt.toArray(new Method[rslt.size()]);
    }

    public static Method[] getters(Class clazz, Predicate<PropertyDescriptor> predicate) {
        List<Method> rslt = doGetters(clazz, predicate);
        return rslt.toArray(new Method[rslt.size()]);
    }

    public static Method[] getters4IncludedAnnotation(Class clazz,
        Class<? extends Annotation> includedAnnotation) {
        List<Method> getters = doGetters(clazz, null);
        List<Method> rslt = new ArrayList<Method>(getters.size());
        for (Method getter : getters) {
            if (getter.getAnnotation(includedAnnotation) != null) {
                rslt.add(getter);
            }
        }
        return rslt.toArray(new Method[rslt.size()]);
    }

    public static Method[] getters4ExcludedAnnotation(Class clazz,
        Class<? extends Annotation> excludedAnnotation) {
        List<Method> getters = doGetters(clazz, null);
        List<Method> rslt = new ArrayList<Method>(getters.size());
        for (Method getter : getters) {
            if (getter.getAnnotation(excludedAnnotation) != null) {
                continue;
            }

            rslt.add(getter);
        }
        return rslt.toArray(new Method[rslt.size()]);
    }

    public static Method[] getters4IncludedNames(Class clazz, String[] includedNames)
        throws NoSuchMethodException {
        List<Method> rslt = new ArrayList<Method>(includedNames.length);

        for (String name : includedNames) {
            rslt.add(ReflectUtils.getter(clazz, name));
        }

        return rslt.toArray(new Method[rslt.size()]);
    }

    public static Method[] getters4ExcludedNames(Class clazz, String[] excludedNames) {
        Set<String> ignoredSet = Collections.EMPTY_SET;

        if (excludedNames != null) {
            ignoredSet = new HashSet<String>(excludedNames.length * 2, 1F);

            for (String exludedName : excludedNames) {
                PropertyDescriptor pd = getPropertyDescriptor(clazz, exludedName);
                if (pd != null) {
                    ignoredSet.add(pd.getReadMethod().getName());
                }
            }
        }

        List<Method> getters = doGetters(clazz, null);
        List<Method> rslt = new ArrayList<Method>(getters.size());
        for (Method getter : getters) {
            if (ignoredSet.contains(getter.getName())) {
                continue;
            }

            rslt.add(getter);
        }
        return rslt.toArray(new Method[rslt.size()]);
    }

    private static List<Method> doGetters(Class clazz, Predicate<PropertyDescriptor> predicate) {
        PropertyDescriptor[] pds = getPropertyDescriptors(clazz);

        List<Method> rslt = new ArrayList<Method>();
        for (PropertyDescriptor pd : pds) {
            Method getter = pd.getReadMethod();
            if (getter != null && Modifier.isPublic(getter.getModifiers())) {
                if (predicate == null || predicate.test(pd)) {
                    rslt.add(getter);
                }
            }
        }
        return rslt;
    }

    /**
     * 得到类的所有getter方法对应的字段名
     */
    public static String[] getterNames(Class clazz) {
        PropertyDescriptor[] pds = getPropertyDescriptors(clazz);
        List<String> names = Lists.newArrayListWithExpectedSize(pds.length);
        for (PropertyDescriptor pd : pds) {
            if (pd.getReadMethod() != null) {
                names.add(pd.getName());
            }
        }
        return names.toArray(new String[names.size()]);
    }

    /**
     * 得到类的所有setter方法对应的字段名
     */
    public static String[] setterNames(Class clazz) {
        PropertyDescriptor[] pds = getPropertyDescriptors(clazz);
        List<String> names = Lists.newArrayListWithExpectedSize(pds.length);
        for (PropertyDescriptor pd : pds) {
            if (pd.getWriteMethod() != null) {
                names.add(pd.getName());
            }
        }
        return names.toArray(new String[names.size()]);
    }

    /**
     * 得到指定类的指定字段名的getter方法
     *
     * @author WuBo
     * @CreateDate 2012-7-27 下午04:34:31
     */
    public static Method getter(Class clazz, String fieldName) throws NoSuchMethodException {
        PropertyDescriptor pd = getPropertyDescriptor(clazz, fieldName);
        if (pd != null) {
            return pd.getReadMethod();
        }

        throw new NoSuchMethodException(clazz.getName() + "." + fieldName + "()");
    }

    public static Method getter4Safe(Class clazz, String fieldName) {
        try {
            return getter(clazz, fieldName);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String propertyName) {
        return BeanUtils.getPropertyDescriptor(clazz, propertyName);
    }

    public static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) {
        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(clazz);

        int idx = 0;
        int hasClass = 0;
        for (PropertyDescriptor pd : pds) {
            if ("class".equals(pd.getName())) {
                hasClass = 1;
                break;
            }
            idx++;
        }

        PropertyDescriptor[] rslt = new PropertyDescriptor[pds.length - hasClass];

        if (hasClass == 0) {
            System.arraycopy(pds, 0, rslt, 0, rslt.length);
        } else if (idx == 0) {
            System.arraycopy(pds, 1, rslt, 0, rslt.length);
        } else {
            System.arraycopy(pds, 0, rslt, 0, idx);

            System.arraycopy(pds, idx + 1, rslt, idx, rslt.length - idx);
        }

        return rslt;
    }

    public static PropertyDescriptor[] getPropertyDescriptors4IncludedNames(Class<?> clazz,
        String[] includedNames) {
        List<PropertyDescriptor> rslt = Lists.newArrayList();
        for (String name : includedNames) {
            rslt.add(getPropertyDescriptor(clazz, name));
        }
        return rslt.toArray(new PropertyDescriptor[rslt.size()]);
    }

    public static PropertyDescriptor[] getPropertyDescriptors4ExcludedNames(Class<?> clazz,
        String[] excludedNames) {
        PropertyDescriptor[] pds = getPropertyDescriptors(clazz);
        if (excludedNames == null || excludedNames.length == 0) {
            return pds;
        }

        List<PropertyDescriptor> rslt = Lists.newArrayList();

        for (PropertyDescriptor pd : pds) {
            boolean exclude = false;

            for (String excludedName : excludedNames) {
                if (pd.getName().equals(excludedName)) {
                    exclude = true;
                    break;
                }
            }

            if (!exclude) {
                rslt.add(pd);
            }
        }

        return rslt.toArray(new PropertyDescriptor[rslt.size()]);
    }

    /**
     * 得到类中声明的方法
     */
    public static Method getDeclaredMethod(Class<?> clazz, String methodName,
        boolean containsParents, Class<?>... type) throws NoSuchMethodException {
        try {
            return clazz.getDeclaredMethod(methodName, type);
        } catch (NoSuchMethodException ex) {
            if (containsParents && clazz.getSuperclass() != null
                && clazz.getSuperclass() != Object.class) {
                return getDeclaredMethod(clazz.getSuperclass(), methodName, containsParents, type);
            } else {
                throw ex;
            }
        }
    }

    /**
     * 得到类中声明的字段
     */
    public static Field getDeclaredField(Class<?> clazz, String fieldName, boolean containsParents)
        throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException ex) {
            if (containsParents && clazz.getSuperclass() != null
                && clazz.getSuperclass() != Object.class) {
                return getDeclaredField(clazz.getSuperclass(), fieldName, containsParents);
            } else {
                throw ex;
            }
        }
    }

    /**
     * <h3>得到指定类的所有声明字段</h3>
     * <p>当containsParents值为true时，返回的字段列表中将包含所有父类的声明字段（Object除外）</p>
     * <p>当containsStatic值为false时，不包含静态字段</p>
     *
     * @param ignoreProperties 忽略的字段
     * @param containsParents 是否包含父类中声明的字段
     * @param containsStatic 是否包含静态字段
     */
    public static Field[] getDeclaredFields(Class<?> entityClass, String[] ignoreProperties,
        boolean containsParents, boolean containsStatic) {
        return getDeclaredFields(entityClass, ignoreProperties, containsParents,
            containsStatic ? 0 : Modifier.STATIC);
    }

    public static Field[] getDeclaredFields(Class<?> entityClass, String[] ignoreProperties,
        boolean containsParents, int filterModifierSet) {
        List<Field> fields = new LinkedList<Field>();
        List<String> excludeProps = Collections.EMPTY_LIST;
        if (ignoreProperties != null) {
            excludeProps = Arrays.asList(ignoreProperties);
        }

        while (entityClass != null) {
            Field[] temp = entityClass.getDeclaredFields();
            for (int i = 0; i < temp.length; i++) {
                Field f = temp[i];
                if (hasModifiers(f, filterModifierSet)) {
                    continue;
                } else {
                    if (excludeProps.contains(f.getName())) {
                        continue;
                    }
                    fields.add(f);
                }
            }
            if (containsParents && entityClass.getSuperclass() != null
                && entityClass.getSuperclass() != Object.class) {
                entityClass = entityClass.getSuperclass();
            } else {
                entityClass = null;
            }
        }

        return fields.toArray(new Field[fields.size()]);
    }

    /**
     * 判断此Field是否有包含指定修饰符集的其中之一
     *
     * @param modifierSet 修饰符集
     * @return 是否包含指定修饰符集的其中之一，返回true
     */
    public static boolean hasModifiers(Field field, int modifierSet) {
        int mdfr = field.getModifiers();
        return (mdfr & modifierSet) != 0;
    }

    public static boolean isInheritFromObject(Method m) {
        try {
            Object.class.getMethod(m.getName(), m.getParameterTypes());
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        } catch (SecurityException e) {
            return false;
        }
    }

    public static boolean isSuper(Type superType, Type subType) {
        if (superType instanceof Class) {
            return ((Class) superType).isAssignableFrom(getClassFromType(subType));
        }
        if (superType instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) superType).getRawType();
            return ((Class) rawType).isAssignableFrom(getClassFromType(subType));
        }
        return false;
    }

    public static boolean isSub(Type subType, Type superType) {
        if (subType instanceof Class) {
            return getClassFromType(superType).isAssignableFrom((Class) subType);
        }
        if (subType instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) subType).getRawType();
            return getClassFromType(superType).isAssignableFrom((Class) rawType);
        }
        return false;
    }

    private static Class getClassFromType(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        }
        if (type instanceof ParameterizedType) {
            return getClassFromType(((ParameterizedType) type).getRawType());
        }
        return null;
    }

    public static Type getArgumentType(Type type, int index) {
        if (type instanceof ParameterizedType) {
            Type[] args = ((ParameterizedType) type).getActualTypeArguments();
            if (args.length > index) {
                return args[index];
            }
        }
        return null;
    }

    public static Type getCollectionArgumentType(Collection<?> coll) {
        if (coll.size() > 0) {
            Object obj = coll.iterator().next();
            return obj != null ? obj.getClass() : null;
        }
        return null;
    }

}
