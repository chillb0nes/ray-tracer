package com.example.renderer;

import org.junit.Rule;

public class BaseJavaFXTest {
    @Rule
    public JavaFXThreadingRule jfxRule = new JavaFXThreadingRule();
}
