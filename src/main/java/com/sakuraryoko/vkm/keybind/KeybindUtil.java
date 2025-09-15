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

import java.util.Map;
import java.util.Set;

import com.sakuraryoko.vkm.VanKeyMngr;
import com.sakuraryoko.vkm.config.Configs;
import fi.dy.masa.malilib.util.KeyCodes;
import org.jetbrains.annotations.Nullable;

//#if MC >= 11605
//$$ import net.minecraft.client.option.KeyBinding;
//#else
import net.minecraft.client.options.KeyBinding;
//#endif
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeybindUtil
{
//#if MC >= 11701
    //$$ protected static final Map<String, KeyBinding> MAP_BY_ID = KeyBinding.KEYS_BY_ID;
    //$$ protected static final Map<InputUtil.Key, KeyBinding> MAP_BY_CODE = KeyBinding.KEY_TO_BINDINGS;
    //$$ protected static final Set<String> SET_CAT_LIST = KeyBinding.KEY_CATEGORIES;
    //$$ protected static final Map<String, Integer> MAP_CAT_ORDER = KeyBinding.CATEGORY_ORDER_MAP;
//#elseif MC >= 11605
    //$$ protected static final Map<String, KeyBinding> MAP_BY_ID = KeyBinding.keysById;
    //$$ protected static final Map<InputUtil.Key, KeyBinding> MAP_BY_CODE = KeyBinding.keyToBindings;
    //$$ protected static final Set<String> SET_CAT_LIST = KeyBinding.keyCategories;
    //$$ protected static final Map<String, Integer> MAP_CAT_ORDER = KeyBinding.categoryOrderMap;
//#else
    protected static final Map<String, KeyBinding> MAP_BY_ID = KeyBinding.keysById;
    protected static final Map<InputUtil.KeyCode, KeyBinding> MAP_BY_CODE = KeyBinding.keysByCode;
    protected static final Set<String> SET_CAT_LIST = KeyBinding.keyCategories;
    protected static final Map<String, Integer> MAP_CAT_ORDER = KeyBinding.categoryOrderMap;
//#endif
    protected static final Map<String, InputUtil.KeyCode> MAP_BY_NAME = InputUtil.KeyCode.NAMES;

	@Nullable
    protected static KeyBinding getByIdVanilla(final String id)
	{
		return MAP_BY_ID.getOrDefault(id, null);
	}

    @Nullable
    protected static KeyBinding getByCodeVanilla(final InputUtil.KeyCode keyCode)
    {
        return MAP_BY_CODE.getOrDefault(keyCode, null);
    }

    protected static Set<String> getCategoriesVanilla()
    {
        return SET_CAT_LIST;
    }

    protected static Integer getCategoryOrderNumberVanilla(final String cat)
    {
        return MAP_CAT_ORDER.getOrDefault(cat, -1);
    }

    @Nullable
    protected static InputUtil.KeyCode getKeyCodeByName(final String name)
    {
        return MAP_BY_NAME.getOrDefault(name, null);
    }

    @Nullable
    protected static InputUtil.KeyCode getKeyCodeByType(final InputUtil.Type type, final int key)
    {
        return type.map.getOrDefault(key, null);
    }

    @Nullable
    public static String getKeycodeName(int keyCode)
    {
        return GLFW.glfwGetKeyName(keyCode, -1);
    }

    @Nullable
    public static String getScancodeName(int scanCode)
    {
        return GLFW.glfwGetKeyName(-1, scanCode);
    }

    @Nullable
    public static String getTypeName(InputUtil.Type type)
    {
        return type.name;
    }

    @Nullable
    protected static KeybindWrapper getById(final String id)
	{
		KeyBinding keyBinding = getByIdVanilla(id);

		if (keyBinding == null)
		{
			return null;
		}

		return new KeybindWrapper(keyBinding);
	}

	@Nullable
    protected static KeybindWrapper getByCode(final InputUtil.KeyCode keyCode)
	{
		KeyBinding keyBinding = getByCodeVanilla(keyCode);

		if (keyBinding == null)
		{
			return null;
		}

		return new KeybindWrapper(keyBinding);
	}

    protected static boolean updateByID(final String id, InputUtil.KeyCode newKeyCode)
    {
        try
        {
            MAP_BY_ID.get(id).setKeyCode(newKeyCode);
            KeyBinding.updateKeysByCode();
            return true;
        }
        catch (Exception err)
        {
            VanKeyMngr.LOGGER.error("updateByID: Exception while updating keybind '{}'; {}", id, err.getLocalizedMessage());
            return false;
        }
    }

    protected static boolean updateByCode(final InputUtil.KeyCode oldKeyCode, InputUtil.KeyCode newKeyCode)
    {
        try
        {
            // Extract the ID first
            return updateByID(MAP_BY_CODE.get(oldKeyCode).getId(), newKeyCode);
        }
        catch (Exception err)
        {
            VanKeyMngr.LOGGER.error("updateByCode: Exception while updating keybind '{}'; {}", oldKeyCode.getName(), err.getLocalizedMessage());
            return false;
        }
    }

    protected static void setPressed(KeyBinding keyBind, boolean toggle)
    {
        //#if MC >= 11502
        //$$ keyBind.setPressed(toggle);
        //#else
        keyBind.pressed = toggle;

        if (toggle)
        {
            ++keyBind.timesPressed;
        }
        //#endif
    }

    protected static boolean resetByID(final String id)
    {
        try
        {
            KeyBinding keybind = MAP_BY_ID.get(id);
            keybind.setKeyCode(keybind.getDefaultKeyCode());
            setPressed(keybind, false);
            MAP_BY_ID.put(id, keybind);
            KeyBinding.updateKeysByCode();
            return true;
        }
        catch (Exception err)
        {
            VanKeyMngr.LOGGER.error("resetByID: Exception while resetting keybind '{}'; {}", id, err.getLocalizedMessage());
            return false;
        }
    }

    protected static boolean resetByCode(InputUtil.KeyCode oldKeyCode)
    {
        try
        {
            // Extract the ID first
            return resetByID(MAP_BY_CODE.get(oldKeyCode).getId());
        }
        catch (Exception err)
        {
            VanKeyMngr.LOGGER.error("resetByCode: Exception while updating keybind '{}'; {}", oldKeyCode.getName(), err.getLocalizedMessage());
            return false;
        }
    }

    public static String buildConfigName(KeybindWrapper keybind)
    {
        final String cat = Configs.Generic.CATEGORY_COLOR_PREFIX.getStringValue();
        final String id = Configs.Generic.NAME_COLOR_PREFIX.getStringValue();

        return "["+cat+keybind.getTranslatedCategory()+"§r] ("+id+keybind.getTranslatedId()+"§r)";
    }

    public static String buildConfigString(KeybindWrapper keybind)
    {
        if (keybind.isDefault())
        {
            return KeyCodes.getNameForKey(keybind.getDefaultKeyCode().getKeyCode());
        }

        return KeyCodes.getNameForKey(keybind.getKeyCode().getKeyCode());
    }

    public static String buildConfigComment(KeybindWrapper keybind)
    {
        final String cat = Configs.Generic.CATEGORY_COLOR_PREFIX.getStringValue();
        final String id = Configs.Generic.NAME_COLOR_PREFIX.getStringValue();

        return "Minecraft Keybind:\nCategory: "+cat+keybind.getTranslatedCategory()+"§r\nName: "+id+keybind.getTranslatedId()+"§r";
    }

    public static String buildConfigPrettyName(KeybindWrapper keybind)
    {
        final String cat = Configs.Generic.CATEGORY_COLOR_PREFIX.getStringValue();
        final String id = Configs.Generic.NAME_COLOR_PREFIX.getStringValue();

        return "["+cat+keybind.getTranslatedCategory()+"§r] ("+id+keybind.getTranslatedId()+"§r)";
    }
}
