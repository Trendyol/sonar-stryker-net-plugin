package com.trendyol.sonar.stryker.net;

public class SonarStrykerNetPlugin implements org.sonar.api.Plugin {
    @Override
    public void define(Context context) {
        context.addExtensions(
                SonarStrykerNetRulesDefinition.class,
                StrykerNetSensor.class);
    }
}
