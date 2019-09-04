package com.example.renderer;

import org.junit.Rule;

public class BaseJavaFXTest extends BaseTest {
    @Rule
    public JavaFXThreadingRule jfxRule = new JavaFXThreadingRule();
}
