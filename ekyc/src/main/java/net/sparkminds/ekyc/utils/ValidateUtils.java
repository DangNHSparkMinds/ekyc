package net.sparkminds.ekyc.utils;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class ValidateUtils {

    public static boolean isNullOrEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String) {
            return ((String) obj).isEmpty();
        }
        if (obj instanceof Collection) {
            return ((Collection<?>) obj).isEmpty();
        }
        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).isEmpty();
        }
        if (obj instanceof Optional) {
            return ((Optional<?>) obj).isEmpty();
        }
        if (obj.getClass().isArray()) {
            return ((Object[]) obj).length == 0;
        }
        return false;
    }
}
