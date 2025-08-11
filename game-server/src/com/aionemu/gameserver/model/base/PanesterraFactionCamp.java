package com.aionemu.gameserver.model.base;

/**
 * @author Estrayl
 */
public class PanesterraFactionCamp extends PanesterraBase {

	public PanesterraFactionCamp(PanesterraBaseLocation loc) {
		super(loc);
	}

	@Override
	protected int getBossSpawnDelay() {
		return 10 * 60000;
	}

	@Override
	protected int getNpcSpawnDelay() {
		return 10 * 60000; // Retail delay
	}
}
