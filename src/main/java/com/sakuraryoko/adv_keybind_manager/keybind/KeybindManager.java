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

import java.util.HashSet;

import net.minecraft.client.options.KeyBinding;

public class KeybindManager
{
	private static final KeybindManager INSTANCE = new KeybindManager();
	public static KeybindManager getInstance() { return INSTANCE; }
	private final HashSet<KeyBindingWrapper> keybinds;

	public KeybindManager()
	{
		this.keybinds = new HashSet<>();
	}

	public void resync()
	{
		this.clear();

		KeybindUtil.MAP_BY_ID.forEach(this::addEach);
	}

	private void addEach(String id, KeyBinding keybind)
	{
		this.keybinds.add(new KeyBindingWrapper(keybind));
	}

	private void clear()
	{
		this.keybinds.clear();
	}
}
