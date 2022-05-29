package dzwdz.toomanybinds.autocompletion;

import dzwdz.toomanybinds.mixinterface.KeyBindingMixinterface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.text.Text;

public class BindSuggestion {
    public Text name;
    public Text category;
    public KeyBinding bind;
    private final String searchable;
    public boolean favorite = false;

    public BindSuggestion(KeyBinding bind) {
        this.bind = bind;
        name = Text.translatable(bind.getTranslationKey());
        category = Text.translatable(bind.getCategory());
        searchable = (name.getString() + " " + category.getString()).toLowerCase();
    }

    public boolean matches(String[] searchTerms) {
        for (String term : searchTerms) {
            if (!searchable.contains(term)) return false;
        }
        return true;
    }

    public void execute() {
        MinecraftClient mc = MinecraftClient.getInstance();
        GameOptions options = mc.options;

        LauncherCompletion.addToHistory(getId());

        // workarounds for keybinds that are handled in dumb, incompatible ways
        if (bind == options.fullscreenKey) {
            mc.getWindow().toggleFullscreen();
            mc.options.getFullscreen().setValue(mc.getWindow().isFullscreen());
            mc.options.write();
        } else if (bind == options.screenshotKey) {
            ScreenshotRecorder.saveScreenshot(mc.runDirectory, mc.getFramebuffer(), (text) -> {
                mc.execute(() -> {
                    mc.inGameHud.getChatHud().addMessage(text);
                });
            });
        } else {
            ((KeyBindingMixinterface) bind).toomanybinds$press();
            // amecs compat
            bind.setPressed(true);
            bind.setPressed(false);
        }
    }

    public String getId() {
        return bind.getTranslationKey();
    }
}
