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

package com.sakuraryoko.adv_keybind_manager.keybind;

import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import net.minecraft.client.options.KeyBinding;

public class ConfigKeybindWrapper extends ConfigHotkey
{
    private KeyBinding vanilla;

    private ConfigKeybindWrapper(String name, String defaultStorageString, String comment)
    {
        super(name, defaultStorageString, comment);
    }

    private ConfigKeybindWrapper(String name, String defaultStorageString,
                                 KeybindSettings settings, String comment)
    {
        super(name, defaultStorageString, settings, comment);
    }

    private ConfigKeybindWrapper(String name, String defaultStorageString, String comment, String prettyName)
    {
        super(name, defaultStorageString, comment, prettyName);
    }

    private ConfigKeybindWrapper(String name, String defaultStorageString, KeybindSettings settings, String comment,
                                 String prettyName)
    {
        super(name, defaultStorageString, settings, comment, prettyName);
    }
}
