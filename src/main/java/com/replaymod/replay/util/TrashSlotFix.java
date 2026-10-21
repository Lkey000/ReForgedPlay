package com.replaymod.replay.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

/**
 * TrashSlotとの互換対策。
 *
 * ReplayModのGUIライブラリ(de.johni0702.*)が全画面に作るラッパー画面の
 * 初期化イベントが、インベントリの直後に発火する。TrashSlotはこれを
 * 「コンテナ画面ではない画面」と判断し、作成済みのスロットをnullで上書きする。
 * (TrashSlot側のReplayMod除外は "com.replaymod" 始まりしか見ていないため効かない)
 *
 * TrashSlotの処理の前後にフックして、nullにされた状態を元に戻す。
 * TrashSlotが未導入の環境では何もしない。
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public final class TrashSlotFix {

    private static final Logger LOGGER = LogManager.getLogger("TrashSlotFix");
    private static final String GUI_HANDLER_CLASS = "net.blay09.mods.trashslot.client.TrashSlotGuiHandler";

    private static Field componentField;
    private static Field settingsField;
    private static boolean resolved = false;
    private static Object savedComponent;
    private static Object savedSettings;

    private TrashSlotFix() {}

    private static boolean isReplayWrapper(Screen screen) {
        return screen.getClass().getName().startsWith("de.johni0702.");
    }

    private static void resolveFields() {
        if (resolved) return;
        resolved = true;
        try {
            Class<?> c = Class.forName(GUI_HANDLER_CLASS);
            componentField = c.getDeclaredField("trashSlotComponent");
            componentField.setAccessible(true);
            try {
                settingsField = c.getDeclaredField("currentContainerSettings");
                settingsField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                LOGGER.warn("currentContainerSettings not found, restoring component only");
            }
        } catch (ClassNotFoundException e) {
            // TrashSlot未導入。何もしない。
        } catch (Exception e) {
            LOGGER.warn("could not access TrashSlot fields", e);
        }
    }

    // TrashSlotより先に実行: 状態を保存
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void beforeTrashSlot(ScreenEvent.Init.Post event) {
        if (!isReplayWrapper(event.getScreen())) return;
        resolveFields();
        if (componentField == null) return;
        try {
            savedComponent = componentField.get(null);
            savedSettings = settingsField != null ? settingsField.get(null) : null;
        } catch (Exception e) {
            LOGGER.warn("could not read TrashSlot state", e);
        }
    }

    // TrashSlotより後に実行: nullにされていたら元に戻す
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void afterTrashSlot(ScreenEvent.Init.Post event) {
        if (!isReplayWrapper(event.getScreen())) return;
        if (componentField == null) return;
        try {
            if (savedComponent != null
                    && componentField.get(null) == null
                    && MinecraftClient.getInstance().currentScreen instanceof HandledScreen<?>) {
                componentField.set(null, savedComponent);
                if (settingsField != null) {
                    settingsField.set(null, savedSettings);
                }
                LOGGER.debug("restored trashSlotComponent after ReplayMod wrapper screen init");
            }
        } catch (Exception e) {
            LOGGER.warn("could not restore TrashSlot state", e);
        } finally {
            savedComponent = null;
            savedSettings = null;
        }
    }
}