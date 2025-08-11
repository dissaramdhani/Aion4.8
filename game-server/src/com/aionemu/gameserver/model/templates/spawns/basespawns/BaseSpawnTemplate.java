package com.aionemu.gameserver.model.templates.spawns.basespawns;

import com.aionemu.gameserver.model.base.BaseOccupier;
import com.aionemu.gameserver.model.templates.spawns.SpawnGroup;
import com.aionemu.gameserver.model.templates.spawns.SpawnSpotTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;

/**
 * @author Source
 */
public class BaseSpawnTemplate extends SpawnTemplate {

	private int id;
	private BaseOccupier occupier;

	public BaseSpawnTemplate(SpawnGroup spawnGroup, SpawnSpotTemplate spot) {
		super(spawnGroup, spot);
	}

	public BaseSpawnTemplate(SpawnGroup spawnGroup, float x, float y, float z, byte heading, int randWalk, String walkerId, int staticId, int fly) {
		super(spawnGroup, x, y, z, heading, randWalk, walkerId, staticId, fly);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public BaseOccupier getOccupier() {
		return occupier;
	}

	public void setOccupier(BaseOccupier occupier) {
		this.occupier = occupier;
	}

}
