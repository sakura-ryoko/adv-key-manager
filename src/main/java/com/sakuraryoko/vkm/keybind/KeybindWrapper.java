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

import java.util.concurrent.atomic.AtomicReference;

import com.sakuraryoko.vkm.VanKeyMngr;
import com.sakuraryoko.vkm.util.KeyCodeWrapper;
import com.sakuraryoko.vkm.util.KeyType;
import com.sakuraryoko.vkm.util.KeyTypeWrapper;
//#if MC >= 12001
//#else
import net.minecraft.text.LiteralText;
//#endif
import net.minecraft.text.Text;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;

import fi.dy.masa.malilib.util.JsonUtils;

public class KeybindWrapper
{
	private final String id;
	private final String category;
	private final KeyCodeWrapper defaultKeyCode;
	private KeyCodeWrapper keyCode;
	private String translationKey;
	private boolean pressed;

	public KeybindWrapper(String id, int keyCode, int scanCode, String category)
	{
		this(id, keyCode, scanCode, category, id);
	}

	public KeybindWrapper(String id, int keyCode, int scanCode, String category, String translationKey)
	{
		KeyTypeWrapper type = KeyTypeWrapper.fromKeyCode(keyCode, scanCode);

		if (type.getType() == KeyType.SCANCODE && keyCode == -1)
		{
			keyCode = scanCode;
		}

		this.id = id;
		this.defaultKeyCode = new KeyCodeWrapper(id, keyCode, type);
		this.keyCode = this.defaultKeyCode;
		this.category = category;
		this.translationKey = translationKey;
		this.pressed = false;
	}

	public KeybindWrapper(KeyBinding keyBinding)
	{
		this.id = keyBinding.getId();
		this.category = keyBinding.getCategory();
		this.defaultKeyCode = new KeyCodeWrapper(keyBinding.getDefaultKeyCode());
		this.keyCode = new KeyCodeWrapper(keyBinding.keyCode);
        //#if MC >= 11605
        //$$ this.translationKey = keyBinding.boundKey.getTranslationKey();
        //#else
        this.translationKey = this.buildVanillaTranslationKey(keyBinding.keyCode);
        //#endif
		this.pressed = false;
	}

	private KeybindWrapper(String id, String category, String translationKey, KeyCodeWrapper def, KeyCodeWrapper key)
	{
		this.id = id;
		this.category = category;
		this.translationKey = translationKey;
		this.defaultKeyCode = def;
		this.keyCode = key;
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
			return I18n.translate(this.translationKey);
		}

        if (I18n.hasTranslation(this.id))
        {
            return I18n.translate(this.id);
        }

		return this.id;
	}

    public String getTranslatedCategory()
    {
        if (I18n.hasTranslation(this.category))
        {
            return I18n.translate(this.category);
        }

        return this.category;
    }

    public String getTranslatedId()
    {
        if (I18n.hasTranslation(this.id))
        {
            return I18n.translate(this.id);
        }

        return this.id;
    }

    public String getBoundTranslationKey()
    {
        if (this.keyCode != null)
        {
            return this.keyCode.getTranslationKey();
        }

        return this.translationKey;
    }

    public Text getBoundTranslated()
    {
        if (this.keyCode != null)
        {
            return this.keyCode.getTranslated();
        }

//#if MC >= 11904
        //$$ return Text.of(this.getTranslated());
//#else
        return new LiteralText(this.getTranslated());
//#endif
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

	public KeyBinding getVanillaById()
	{
		return KeybindUtil.getByIdVanilla(this.id);
	}

    public KeyBinding getVanillaByKeyCode()
    {
        return KeybindUtil.getByCodeVanilla(this.keyCode.getVanilla());
    }

    public void reset()
    {
		if (!this.isDefault())
		{
			VanKeyMngr.debugLog("KeybindWrapper#reset(): id: [{}]", this.id);
			this.keyCode = this.defaultKeyCode;
			KeybindUtil.updateByID(this.id, this.keyCode.getVanilla());
		}

        this.pressed = false;
    }

    public void update(int key, int scanCode, KeyType type)
    {
		VanKeyMngr.debugLog("KeybindWrapper#update():IN: key: [{}], scanCode: [{}]", key, scanCode);
		InputUtil.KeyCode keyCode;

		if (type == KeyType.KEYBOARD)
		{
			keyCode = InputUtil.getKeyCode(key, scanCode);
		}
		else
		{
			keyCode = this.matchMouseKeyCode(key);
		}

        this.reset();
        KeybindUtil.updateByID(this.id, keyCode);
        this.keyCode = new KeyCodeWrapper(keyCode);
		VanKeyMngr.debugLog("KeybindWrapper#update():OUT: name: [{}], keyCode: [{}]", this.keyCode.getName(), this.keyCode.getKeyCode());
    }

	public InputUtil.KeyCode matchMouseKeyCode(int keyCode)
	{
		AtomicReference<InputUtil.KeyCode> result = new AtomicReference<>(InputUtil.UNKNOWN_KEYCODE);

		KeybindUtil.MAP_BY_NAME.forEach(
				(str, key) ->
				{
					if (key.getKeyCode() == keyCode)
					{
						result.set(key);
					}
				}
		);

		return result.get();
	}

    public void clearKey()
    {
		VanKeyMngr.debugLog("KeybindWrapper#clearKey(): id: [{}]", this.id);
        InputUtil.KeyCode keyCode = InputUtil.UNKNOWN_KEYCODE;
        this.reset();
        KeybindUtil.updateByID(this.id, keyCode);
        this.keyCode = new KeyCodeWrapper(keyCode);
    }

	public JsonElement toJson()
	{
		JsonObject obj = new JsonObject();

		obj.addProperty("id", this.id);
		obj.addProperty("category", this.category);
		obj.addProperty("translationKey", this.translationKey);
		obj.add("defaultKeyCode", this.defaultKeyCode.toJson());
		obj.add("keyCode", this.keyCode.toJson());

		return obj;
	}

	@Nullable
	public static KeybindWrapper fromJson(JsonElement element)
	{
		try
		{
			if (element.isJsonObject())
			{
				JsonObject obj = element.getAsJsonObject();

				String id = "";
				String cat = "";
				String transKey = "";
				KeyCodeWrapper def = null;
				KeyCodeWrapper key = null;

				if (JsonUtils.hasString(obj, "id"))
				{
					id = obj.get("id").getAsString();
				}
				if (JsonUtils.hasString(obj, "category"))
				{
					cat = obj.get("category").getAsString();
				}
				if (JsonUtils.hasString(obj, "translationKey"))
				{
					transKey = obj.get("translationKey").getAsString();
				}
				if (JsonUtils.hasObject(obj, "defaultKeyCode"))
				{
					def = KeyCodeWrapper.fromJson(obj.get("defaultKeyCode").getAsJsonObject());
				}
				if (JsonUtils.hasObject(obj, "keyCode"))
				{
					key = KeyCodeWrapper.fromJson(obj.get("keyCode").getAsJsonObject());
				}

				if (!id.isEmpty() && def != null && key != null)
				{
					return new KeybindWrapper(id, cat, transKey, def, key);
				}
			}
		}
		catch (Exception e)
		{
			// Error
		}

		return null;
	}

	@Override
    public String toString()
    {
        return "Keybind[" +
               "{id=" + this.id + "}" +
               ",{category=" + this.category + "}" +
               ",{translationKey=" + this.translationKey + "}" +
               ",{defaultKeyCode=" + this.defaultKeyCode.toString() + "}" +
               ",{keyCode=" +
               (this.keyCode != null ? this.keyCode.toString() : "[NOT-BOUND]") +
               "}";
    }

    public void runDebug()
    {
        System.out.printf("[DB] %s\n", this.toString());
    }
}
