package com.example.renderer.service.serialization;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;

public class FXModule extends Module {

    @Override
    public String getModuleName() {
        return "JavaFX Module";
    }

    @Override
    public Version version() {
        return Version.unknownVersion();
    }

    @Override
    public void setupModule(SetupContext context) {
        context.addSerializers(new FXSerializers());
        context.addDeserializers(new FXDeserializers());
    }
}
