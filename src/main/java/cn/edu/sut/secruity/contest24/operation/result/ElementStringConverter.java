package cn.edu.sut.secruity.contest24.operation.result;

import cn.edu.sut.secruity.contest24.util.ElementOperation;
import it.unisa.dia.gas.jpbc.Element;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ElementStringConverter {

    /**
     * 用src元素的element属性填充dest的同名string属性
     * 要求src所有元素的element属性必须有值
     *
     * @param src
     * @param dest
     */
    public static void elementToString(Object src, Object dest) {
        try {
            //源需要field,目标需要fieldName
            Field[] srcFields = src.getClass().getDeclaredFields();
            List<String> destFieldNames = Arrays.stream(dest.getClass().getDeclaredFields()).map(Field::getName).collect(Collectors.toList());
            for (Field field : srcFields) {
                //若不同名结束
                if (!destFieldNames.contains(field.getName()))
                    continue;
                field.setAccessible(true);
                Object o = field.get(src);
                if (!Objects.isNull(o)) {
                    //得到属性的类型
                    Class<?> aClass = o.getClass();
                    //若继承自element
                    if (Element.class.isAssignableFrom(aClass)) {
                        Field destField = dest.getClass().getDeclaredField(field.getName());
                        destField.setAccessible(true);
                        destField.set(dest, ElementOperation.getElementString((Element) o));
                        destField.setAccessible(false);
                    }
                }
                field.setAccessible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void stringToElement(Object src, Object dest) {
        try {
            Field[] srcFields = src.getClass().getDeclaredFields();
            List<String> destFieldNames = Arrays.stream(dest.getClass().getDeclaredFields()).map(Field::getName).collect(Collectors.toList());
            for (Field field : srcFields) {
                //若不同名结束
                if (!destFieldNames.contains(field.getName()))
                    continue;
                field.setAccessible(true);
                Object o = field.get(src);
                if (!Objects.isNull(o)) {
                    Field destField = dest.getClass().getDeclaredField(field.getName());
                    Class<?> destFieldType = destField.getType();
                    //若继承自element
                    if (Element.class.isAssignableFrom(destFieldType)) {
                        destField.setAccessible(true);
                        destField.set(dest, ElementOperation.getElementFromString((String) o));
                        destField.setAccessible(false);
                    }
                }
                field.setAccessible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
