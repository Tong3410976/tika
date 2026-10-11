package org.apache.tika.utils;

import org.apache.tika.utils.CharsetUtils;
import java.lang.reflect.Method;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static java.util.Locale.ENGLISH;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharsetUtils_isSupported_1_0_Test {

    private static Method isSupportedMethod;

    @BeforeAll
    public static void setUp() throws NoSuchMethodException {
        isSupportedMethod = CharsetUtils.class.getDeclaredMethod("isSupported", String.class);
        isSupportedMethod.setAccessible(true);
    }

    @Test
    public void testIsSupportedWithSupportedCharset() throws Exception {
        String charsetName = "UTF-8";
        boolean result = (boolean) isSupportedMethod.invoke(null, charsetName);
        assertTrue(result);
    }

    @Test
    public void testIsSupportedWithUnsupportedCharset() throws Exception {
        String charsetName = "INVALID_CHARSET";
        boolean result = (boolean) isSupportedMethod.invoke(null, charsetName);
        assertFalse(result);
    }

    @Test
    public void testIsSupportedWithIllegalArgumentException() throws Exception {
        String charsetName = null;
        boolean result = (boolean) isSupportedMethod.invoke(null, charsetName);
        assertFalse(result);
    }
}
