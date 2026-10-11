package org.apache.tika.utils;

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

public class CharsetUtils_clean_2_1_Test {

    private static Method cleanMethod;

    private static Method forNameMethod;

    @BeforeAll
    public static void setUp() throws NoSuchMethodException {
        Class<?> clazz = CharsetUtils.class;
        cleanMethod = clazz.getDeclaredMethod("clean", String.class);
        cleanMethod.setAccessible(true);
        forNameMethod = clazz.getDeclaredMethod("forName", String.class);
        forNameMethod.setAccessible(true);
    }

    @Test
    public void testClean_validCharset() throws Exception {
        String charsetName = "UTF-8";
        String result = (String) cleanMethod.invoke(null, charsetName);
        assertEquals(charsetName, result);
    }

    @Test
    public void testClean_invalidCharset() throws Exception {
        String charsetName = "INVALID";
        String result = (String) cleanMethod.invoke(null, charsetName);
        assertNull(result);
    }

    @Test
    public void testClean_withLeadingTrailingWhitespace() throws Exception {
        String charsetName = "  UTF-8  ";
        String result = (String) cleanMethod.invoke(null, charsetName);
        assertEquals("UTF-8", result);
    }

    @Test
    public void testClean_withTrailingComma() throws Exception {
        String charsetName = "UTF-8,";
        String result = (String) cleanMethod.invoke(null, charsetName);
        assertEquals("UTF-8", result);
    }

    @Test
    public void testClean_withLeadingBracket() throws Exception {
        String charsetName = "[UTF-8]";
        String result = (String) cleanMethod.invoke(null, charsetName);
        assertEquals("UTF-8", result);
    }

    @Test
    public void testClean_withTrailingBracket() throws Exception {
        String charsetName = "UTF-8]";
        String result = (String) cleanMethod.invoke(null, charsetName);
        assertEquals("UTF-8", result);
    }

    @Test
    public void testClean_withQuotes() throws Exception {
        String charsetName = "\"UTF-8\"";
        String result = (String) cleanMethod.invoke(null, charsetName);
        assertEquals("UTF-8", result);
    }

    @Test
    public void testClean_withLeadingQuotes() throws Exception {
        String charsetName = "\"UTF-8\"";
        String result = (String) cleanMethod.invoke(null, charsetName);
        assertEquals("UTF-8", result);
    }

    @Test
    public void testClean_withTrailingQuotes() throws Exception {
        String charsetName = "UTF-8\"";
        String result = (String) cleanMethod.invoke(null, charsetName);
        assertEquals("UTF-8", result);
    }

    @Test
    public void testClean_withISOName() throws Exception {
        String charsetName = "ISO 8859-1";
        String result = (String) cleanMethod.invoke(null, charsetName);
        assertEquals("ISO-8859-1", result);
    }

    @Test
    public void testClean_withCPName() throws Exception {
        String charsetName = "CP1252";
        String result = (String) cleanMethod.invoke(null, charsetName);
        assertEquals("CP1252", result);
    }

    @Test
    public void testClean_withWinName() throws Exception {
        String charsetName = "Win1252";
        String result = (String) cleanMethod.invoke(null, charsetName);
        assertEquals("Windows-1252", result);
    }

    @Test
    public void testClean_withNone() throws Exception {
        String charsetName = "none";
        String result = (String) cleanMethod.invoke(null, charsetName);
        assertNull(result);
    }

    @Test
    public void testClean_withNo() throws Exception {
        String charsetName = "no";
        String result = (String) cleanMethod.invoke(null, charsetName);
        assertNull(result);
    }

    @Test
    public void testClean_withNull() throws Exception {
        String charsetName = null;
        String result = (String) cleanMethod.invoke(null, charsetName);
        assertNull(result);
    }

    @Test
    public void testClean_withEmptyString() throws Exception {
        String charsetName = "";
        String result = (String) cleanMethod.invoke(null, charsetName);
        assertNull(result);
    }

    @Test
    public void testClean_withWhitespace() throws Exception {
        String charsetName = "   ";
        String result = (String) cleanMethod.invoke(null, charsetName);
        assertNull(result);
    }

    @Test
    public void testClean_withNonStandardCharset() throws Exception {
        String charsetName = "NonStandardCharset";
        String result = (String) cleanMethod.invoke(null, charsetName);
    }
}
