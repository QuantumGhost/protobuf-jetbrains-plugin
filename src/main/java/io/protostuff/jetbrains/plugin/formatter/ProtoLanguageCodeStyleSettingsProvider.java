package io.protostuff.jetbrains.plugin.formatter;

import static com.intellij.psi.codeStyle.CommonCodeStyleSettings.END_OF_LINE;

import com.google.common.base.Joiner;
import com.intellij.application.options.IndentOptionsEditor;
import com.intellij.application.options.SmartIndentOptionsEditor;
import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;
import io.protostuff.jetbrains.plugin.ProtoLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Code style setting provider.
 *
 * @author Kostiantyn Shchepanovskyi
 */
public class ProtoLanguageCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider {

    @NotNull
    @Override
    public Language getLanguage() {
        return ProtoLanguage.INSTANCE;
    }


    @Override
    public void customizeSettings(@NotNull CodeStyleSettingsCustomizable consumer, @NotNull SettingsType settingsType) {
        switch (settingsType) {
            default:
                break;
        }
    }

    @Nullable
    @Override
    public IndentOptionsEditor getIndentOptionsEditor() {
        return new SmartIndentOptionsEditor();
    }


    @Nullable
    @Override
    public CommonCodeStyleSettings getDefaultCommonSettings() {
        CommonCodeStyleSettings settings = new CommonCodeStyleSettings(ProtoLanguage.INSTANCE);
        settings.initIndentOptions();
        // TODO: we should define our own settings
        settings.SPACE_AROUND_ASSIGNMENT_OPERATORS = true;
        settings.SPACE_BEFORE_SEMICOLON = false;
        settings.BRACE_STYLE = END_OF_LINE;
        settings.KEEP_BLANK_LINES_IN_CODE = 2;
        settings.KEEP_LINE_BREAKS = false;
        return settings;
    }

    @Override
    public String getCodeSample(@NotNull SettingsType settingsType) {
        return Joiner.on('\n').join(
                "message Test {",
                "    optional int32 foo = 1;",
                "    optional string bar = 2;",
                "}"
        );
    }

}
