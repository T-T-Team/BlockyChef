package tnt.tntlib.api;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public final class AnnotationHelper {

    public static <A extends Annotation> List<Class<?>> findAnnotatedTypes(Class<A> annotationType) {
        Type annotation = Type.getType(annotationType);
        List<ModFileScanData> data = ModList.get().getAllScanData();
        List<Class<?>> typeList = new ArrayList<>();
        for (ModFileScanData scanData : data) {
            for (ModFileScanData.AnnotationData annotationData : scanData.getAnnotations()) {
                if (annotationData.annotationType().equals(annotation)) {
                    Class<?> classType = getClassFromType(annotationData.clazz());
                    typeList.add(classType);
                }
            }
        }
        return typeList;
    }

    @Nullable
    public static <A extends Annotation> Field getField(Class<?> type, Class<A> annotation, BiPredicate<Field, A> filter) {
        for (Field field : type.getDeclaredFields()) {
            A a = field.getAnnotation(annotation);
            if (a != null && filter.test(field, a)) {
                return field;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T, A extends Annotation> T getFieldValue(Class<?> type, Class<A> annotation, Object instance, BiPredicate<Field, A> filter) {
        for (Field field : type.getDeclaredFields()) {
            A a = field.getAnnotation(annotation);
            if (a != null && filter.test(field, a)) {
                try {
                    field.setAccessible(true);
                    return (T) field.get(instance);
                } catch (IllegalAccessException e) {
                    return null;
                }
            }
        }
        return null;
    }

    private static Class<?> getClassFromType(Type type) {
        try {
            return Class.forName(type.getClassName(), false, TNTUtils.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
