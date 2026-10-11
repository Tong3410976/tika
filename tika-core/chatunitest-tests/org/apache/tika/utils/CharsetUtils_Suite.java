package org.apache.tika.utils;

import org.junit.runner.RunWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;

@RunWith(value = JUnitPlatform.class)
@SelectClasses(value = { CharsetUtils_clean_2_1_Test.class, CharsetUtils_forName_3_0_Test.class, CharsetUtils_isSupported_1_0_Test.class })
public class CharsetUtils_Suite {
}
