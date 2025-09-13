/*
 * This file is part of the Advanced Keybind Manager project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2025  Sakura-Ryoko and contributors
 *
 * Advanced Keybind Manager is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Advanced Keybind Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Advanced Keybind Manager.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.sakuraryoko.vkm;

import com.sakuraryoko.vkm.config.Configs;
import com.sakuraryoko.vkm.event.InputHandler;
import com.sakuraryoko.vkm.event.KeybindCallbacks;
import com.sakuraryoko.vkm.event.WorldLoadListener;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.event.WorldLoadHandler;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
//#if MC >= 12100
//$$ import fi.dy.masa.malilib.registry.Registry;
//$$ import fi.dy.masa.malilib.util.data.ModInfo;
//$$ import com.sakuraryoko.vkm.gui.GuiConfigs;
//#endif

public class InitHandler implements IInitializationHandler
{
    @Override
    public void registerModHandlers()
    {
        ConfigManager.getInstance().registerConfigHandler(Reference.MOD_ID, new Configs());
//#if MC >= 12100
//$$        Registry.CONFIG_SCREEN.registerConfigScreenFactory(
//$$                new ModInfo(Reference.MOD_ID, Reference.MOD_SHORT_NAME, GuiConfigs::new)
//$$        );
//#endif

        InputHandler handler = new InputHandler();
        InputEventHandler.getKeybindManager().registerKeybindProvider(handler);
        InputEventHandler.getInputManager().registerKeyboardInputHandler(handler);
        InputEventHandler.getInputManager().registerMouseInputHandler(handler);

        WorldLoadListener listener = new WorldLoadListener();
        WorldLoadHandler.getInstance().registerWorldLoadPreHandler(listener);
        WorldLoadHandler.getInstance().registerWorldLoadPostHandler(listener);

        KeybindCallbacks.getInstance().setCallbacks();
    }
}
