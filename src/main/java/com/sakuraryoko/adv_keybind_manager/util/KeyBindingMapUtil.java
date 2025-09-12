package com.sakuraryoko.adv_keybind_manager.util;

import java.util.Set;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class KeyBindingMapUtil
{
	@Nullable
	public static KeyBinding getByIdVanilla(String id)
	{
		return KeyBinding.keysById.getOrDefault(id, null);
	}

	@Nullable
	public static KeyBindingWrapper getById(String id)
	{
		KeyBinding keyBinding = getByIdVanilla(id);

		if (keyBinding == null)
		{
			return null;
		}

		return new KeyBindingWrapper(keyBinding);
	}

	@Nullable
	public static KeyBinding getByCodeVanilla(InputUtil.KeyCode keyCode)
	{
		return KeyBinding.keysByCode.getOrDefault(keyCode, null);
	}

	@Nullable
	public static KeyBindingWrapper getByCode(InputUtil.KeyCode keyCode)
	{
		KeyBinding keyBinding = getByCodeVanilla(keyCode);

		if (keyBinding == null)
		{
			return null;
		}

		return new KeyBindingWrapper(keyBinding);
	}

	public static Set<String> getCategoriesVanilla()
	{
		return KeyBinding.keyCategories;
	}

	public static Integer getCategoryOrderNumberVanilla(String cat)
	{
		return KeyBinding.categoryOrderMap.getOrDefault(cat, -1);
	}
}
