package codeemoji.inlay.nameviolation;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@State(name = "ShortDescriptiveNameSettings", storages = @Storage("codeemoji-short-descriptive-name-settings.xml"))
public class ShortDescriptiveNameSettings implements PersistentStateComponent<ShortDescriptiveNameSettings> {

    private int numberOfLetters = 1;
    private int bigScope = 10;

    @Override
    public ShortDescriptiveNameSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ShortDescriptiveNameSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}