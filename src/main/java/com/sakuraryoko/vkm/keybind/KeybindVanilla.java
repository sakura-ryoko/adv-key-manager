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

package com.sakuraryoko.vkm.keybind;

//#if MC >= 12105
//$$ import com.mojang.serialization.Codec;
//#else
//#endif
import fi.dy.masa.malilib.hotkeys.*;
import fi.dy.masa.malilib.util.KeyCodes;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.sakuraryoko.vkm.util.KeyCodeWrapper;
import com.sakuraryoko.vkm.util.KeyType;

public class KeybindVanilla implements IKeybind
{
    private final KeybindSettings settings = KeybindSettings.EXCLUSIVE;
    private final KeybindWrapper keybind;
    @Nullable
    private IHotkeyCallback callback;

    public KeybindVanilla(KeybindWrapper keybind)
    {
        this.keybind = keybind;
        this.callback = null;
    }

    public int getDefaultKeyCode()
    {
		KeyCodeWrapper keyCode = this.keybind.getDefaultKeyCode();
		return keyCode.getType().isMouse() ? keyCode.getKeyCode() - 100 : keyCode.getKeyCode();
    }

    public int getKeyCode()
    {
		KeyCodeWrapper keyCode = this.keybind.getKeyCode();
		return keyCode.getType().isMouse() ? keyCode.getKeyCode() - 100 : keyCode.getKeyCode();
    }

    @Override
    public boolean isValid()
    {
        return this.keybind != null;
    }

//#if MC >= 12105
    //$$ @Override
    //$$ public Codec<? extends IKeybind> codec()
    //$$ {
        //$$ return null;
    //$$ }
//#else
//#endif

    @Override
    public boolean isPressed()
    {
        return this.keybind.isPressed();
    }

    @Override
    public boolean isKeybindHeld()
    {
        return this.keybind.isPressed();
    }

    @Override
    public boolean updateIsPressed()
    {
        boolean cancel = false;

        this.keybind.setPressed(!this.isPressed());

        if (this.callback != null)
        {
            cancel = this.callback.onKeyAction(KeyAction.PRESS, this);
        }

        return cancel;
    }

    @Override
    public KeybindSettings getSettings()
    {
        return this.settings;
    }

    @Override
    public void setSettings(KeybindSettings settings)
    {
        // Cannot change Settings of Vanilla Keybinds
    }

    @Override
    public void clearKeys()
    {
        this.keybind.clearKey();
    }

    @Override
    public void addKey(int keyCode)
    {
		KeyType type = KeyType.KEYBOARD;

		if (keyCode < -1)
		{
			type = KeyType.MOUSE;
			keyCode += 100;
		}

		this.keybind.update(keyCode, -1, type);
    }

    @Override
    public void removeKey(int keyCode)
    {
        this.keybind.clearKey();
    }

    @Override
    public boolean matches(int keyCode)
    {
        return this.keybind.matchesKey(keyCode, -1) || this.keybind.matchesMouse(keyCode);
    }

    @Override
    public boolean overlaps(IKeybind other)
    {
        if (other == this || this.getKeyCode() == -1 ||
            other.getKeys().isEmpty() || other.getKeys().getFirst() == -1)
        {
            return false;
        }

        if (other instanceof KeybindVanilla)
        {
            KeybindVanilla kbv = (KeybindVanilla) other;

            if (Objects.equals(kbv.keybind.getId(), this.keybind.getId()))
            {
                return false;
            }

            return kbv.keybind.matchesKey(this.getKeyCode(), -1) || kbv.keybind.matchesMouse(this.getKeyCode());
        }

		if (other.getSettings().getContext() != KeybindSettings.Context.INGAME)
		{
			return false;
		}

		if (this.contextOverlaps(other))
		{
			return other.getKeys().getFirst() == this.getKeyCode();
		}

		return false;
    }

	// Cloned from KeybindMulti
	public boolean contextOverlaps(IKeybind other)
	{
		KeybindSettings settingsOther = other.getSettings();
		KeybindSettings.Context c1 = this.settings.getContext();
		KeybindSettings.Context c2 = settingsOther.getContext();

		if (c1 == KeybindSettings.Context.ANY || c2 == KeybindSettings.Context.ANY || c1 == c2)
		{
			KeyAction a1 = this.settings.getActivateOn();
			KeyAction a2 = settingsOther.getActivateOn();

			return a1 == KeyAction.BOTH || a2 == KeyAction.BOTH || a1 == a2;
		}

		return false;
	}

	@Override
    public void tick()
    {
        // NO-OP
    }

    @Override
    public String getKeysDisplayString()
    {
        return this.getStringValue();
    }

    @Override
    public List<Integer> getKeys()
    {
        List<Integer> list = new ArrayList<>();

        list.add(this.getKeyCode());

        return list;
    }

    @Override
    public void setCallback(@Nullable IHotkeyCallback callback)
    {
        this.callback = callback;
    }

    @Override
    public boolean areSettingsModified()
    {
        return false;
    }

    @Override
    public void resetSettingsToDefaults()
    {
        // NO-OP
    }

    @Override
    public boolean isModified()
    {
        return !this.keybind.isDefault();
    }

    @Override
    public void resetToDefault()
    {
        this.keybind.reset();
    }

    @Override
    public String getDefaultStringValue()
    {
        return KeyCodes.getNameForKey(this.getDefaultKeyCode());
    }

    @Override
    public void setValueFromString(String value)
    {
        String[] split = value.split(",");

		int keyCode = KeyCodes.getKeyCodeFromName(split[0]);

        this.addKey(keyCode);
    }

    @Override
    public boolean isModified(String newValue)
    {
        return !this.getDefaultStringValue().equals(newValue);
    }

    @Override
    public String getStringValue()
    {
        String name = KeyCodes.getNameForKey(this.getKeyCode());

        // For empty Keybinds (-1 keyCode)
        if (name == null)
        {
            name = "";
        }

        return name;
    }
}
