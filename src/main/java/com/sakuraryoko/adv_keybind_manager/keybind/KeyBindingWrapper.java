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

import com.sakuraryoko.adv_keybind_manager.util.KeyCodeWrapper;
import com.sakuraryoko.adv_keybind_manager.util.KeyType;
import com.sakuraryoko.adv_keybind_manager.util.KeyTypeWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;

import fi.dy.masa.malilib.util.StringUtils;

public class KeyBindingWrapper
{
	private final String id;
	private final String category;
	private final KeyCodeWrapper defaultKeyCode;
	private KeyCodeWrapper keyCode;
	private String translationKey;
	private boolean pressed;

	public KeyBindingWrapper(String id, int scanCode, String category)
	{
		this(id, new KeyTypeWrapper(KeyType.KEYBOARD), scanCode, category, id + "." + category);
	}

	public KeyBindingWrapper(String id, KeyTypeWrapper type, int scanCode, String category, String translationKey)
	{
		this.id = id;
		this.defaultKeyCode = new KeyCodeWrapper(id, scanCode, type);
		this.keyCode = this.defaultKeyCode;
		this.category = category;
		this.translationKey = translationKey;
		this.pressed = false;
	}

	public KeyBindingWrapper(KeyBinding keyBinding)
	{
		this.id = keyBinding.getId();
		this.category = keyBinding.getCategory();
		this.defaultKeyCode = new KeyCodeWrapper(keyBinding.getDefaultKeyCode());
		this.keyCode = new KeyCodeWrapper(keyBinding.keyCode);
		this.translationKey = this.buildVanillaTranslationKey(keyBinding.keyCode);
		this.pressed = false;
	}

	private String buildVanillaTranslationKey(InputUtil.KeyCode keyCode)
	{
		String keyCodeName = keyCode.getName();
		int code = keyCode.getKeyCode();
		String scanCodeName = null;

		switch (keyCode.getCategory())
		{
			case KEYSYM:
				scanCodeName = KeybindUtil.getKeycodeName(code);
				break;
			case SCANCODE:
				scanCodeName = KeybindUtil.getScancodeName(code);
				break;
			case MOUSE:
				scanCodeName = KeybindUtil.getTypeName(InputUtil.Type.MOUSE) +"."+ (code + 1);
		}

		return scanCodeName == null ? I18n.translate(keyCodeName) : scanCodeName;
	}

	public String getId()
	{
		return this.id;
	}

	public String getCategory()
	{
		return this.category;
	}

	public KeyCodeWrapper getDefaultKeyCode()
	{
		return this.defaultKeyCode;
	}

	public KeyCodeWrapper getKeyCode()
	{
		return this.keyCode;
	}

	public InputUtil.@Nullable KeyCode getDefaultKeyCodeVanilla()
	{
		return this.defaultKeyCode.getVanilla();
	}

	public InputUtil.@Nullable KeyCode getKeyCodeVanilla()
	{
		return this.keyCode.getVanilla();
	}

	public void setKeyCode(@NotNull KeyCodeWrapper keyCode)
	{
		this.keyCode = keyCode;
	}

	public void setKeyCode(@NotNull InputUtil.KeyCode keyCode)
	{
		this.keyCode = new KeyCodeWrapper(keyCode);
	}

	public void setPressed(boolean toggle)
	{
		this.pressed = toggle;
	}

	public boolean isPressed()
	{
		return this.pressed;
	}

	public boolean isBound()
	{
		return !(this.keyCode.getKeyCode() == -1);
	}

	public void setTranslationKey(String translationKey)
	{
		this.translationKey = translationKey;
	}

	public String getTranslationKey()
	{
		return this.translationKey;
	}

	public String getTranslated()
	{
		if (I18n.hasTranslation(this.translationKey))
		{
			return StringUtils.translate(this.translationKey);
		}

		return this.id + "." + this.category;
	}

	public boolean matchesKey(int keyCode, int scanCode)
	{
		if (keyCode == -1)
		{
			return this.keyCode.getType().isScancode() && this.keyCode.getKeyCode() == scanCode;
		}
		else
		{
			return this.keyCode.getType().isKeyboard() && this.keyCode.getKeyCode() == keyCode;
		}
	}

	public boolean matchesMouse(int keyCode)
	{
		return this.keyCode.getType().isMouse() && this.keyCode.getKeyCode() == keyCode;
	}

	public boolean isDefault()
	{
		return this.keyCode.equals(this.defaultKeyCode);
	}

	public static KeyBinding getVanillaById(String id)
	{
		return KeybindUtil.getByIdVanilla(id);
	}
}
