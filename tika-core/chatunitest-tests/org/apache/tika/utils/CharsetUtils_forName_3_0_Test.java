package org.apache.tika.utils;

import org.apache.tika.utils.CharsetUtils;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static java.util.Locale.ENGLISH;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharsetUtils_forName_3_0_Test {

    private static Method getCharsetICU;

    private static Method isSupportedICU;

    private static Map<String, Charset> COMMON_CHARSETS;

    @BeforeAll
    public static void setUp() throws Exception {
        COMMON_CHARSETS = new HashMap<>();
        COMMON_CHARSETS.put("ascii", Charset.forName("US-ASCII"));
        COMMON_CHARSETS.put("iso-8859-1", Charset.forName("ISO-8859-1"));
        COMMON_CHARSETS.put("cp1252", Charset.forName("windows-1252"));
        Method[] methods = CharsetUtils.class.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals("getCharsetICU")) {
                getCharsetICU = method;
                getCharsetICU.setAccessible(true);
            } else if (method.getName().equals("isSupportedICU")) {
                isSupportedICU = method;
                isSupportedICU.setAccessible(true);
            }
        }
    }

    @Test
    public void testForName_withNullName() {
        assertThrows(IllegalArgumentException.class, () -> CharsetUtils.forName(null));
    }
}
