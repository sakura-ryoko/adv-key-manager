package com.sakuraryoko.adv_keybind_manager.keybind;

import java.util.HashSet;

import net.minecraft.client.options.KeyBinding;

import com.sakuraryoko.adv_keybind_manager.util.KeyBindingWrapper;

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

		KeyBinding.keysById.forEach(this::addEach);
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
