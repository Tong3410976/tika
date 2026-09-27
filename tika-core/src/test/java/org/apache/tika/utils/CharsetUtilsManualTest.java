/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tika.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

public class CharsetUtilsManualTest {

    @Test
    public void testIsSupportedUsesIcuHook() throws Exception {

        Field field = CharsetUtils.class.getDeclaredField("isSupportedICU");
        field.setAccessible(true);

        Object previous = field.get(null);

        Method replacement = CharsetUtilsManualTest.class.getDeclaredMethod("icuSupports", String.class);
        replacement.setAccessible(true);

        try {
            field.set(null, replacement);

            assertTrue(CharsetUtils.isSupported("x-test-only-charset"));
        } finally {
            field.set(null, previous);
        }

    }

    private static boolean icuSupports(String name) {

        return true;
    }

    @Test
    public void testIsSupportedHandlesIcuInvocationFailure() throws Exception {
        Field field = CharsetUtils.class.getDeclaredField("isSupportedICU");
        field.setAccessible(true);

        Object previous = field.get(null);

        Method replacement = CharsetUtilsManualTest.class.getDeclaredMethod("icuThrows", String.class);
        replacement.setAccessible(true);

        try {
            field.set(null, replacement);

            assertFalse(CharsetUtils.isSupported("x-test-only-charset"));
        } finally {
            field.set(null, previous);
        }
    }

    private static boolean icuThrows(String name) {
        throw new IllegalStateException("simulated ICU failure");
    }

    @Test
    public void testForNameUsesIcuHookWhenAvailable() throws Exception {

        Field field = CharsetUtils.class.getDeclaredField("getCharsetICU");
        field.setAccessible(true);

        Object previous = field.get(null);

        Method replacement = CharsetUtilsManualTest.class.getDeclaredMethod("icuForName", String.class);
        replacement.setAccessible(true);

        try {
            field.set(null, replacement);

            assertSame(StandardCharsets.UTF_16BE, CharsetUtils.forName("x-test-only-charset"));
        } finally {
            field.set(null, previous);
        }

    }

    private static Charset icuForName(String name) {
        return StandardCharsets.UTF_16BE;
    }
}
