package com.aionemu.gameserver.services;

import java.util.concurrent.TimeUnit;

import com.aionemu.commons.services.CronService;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.SiegeConfig;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.services.panesterra.PanesterraService;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * This class is used for miscellaneous long-time schedules like specific spawns.
 * 
 * @author Estrayl
 */
public class CronJobService {

	private static final CronJobService INSTANCE = new CronJobService();

	private CronJobService() {
		scheduleMoltenusSpawn();
		scheduleAhserionsFlight();
		scheduleIdianDepthPortalSpawns();
		scheduleLegionDominionCalculation();
	}

	public static CronJobService getInstance() {
		return INSTANCE;
	}

	private void scheduleMoltenusSpawn() {
		CronService.getInstance().schedule(new Runnable() {

			private Npc moltenus;

			@Override
			public void run() {
				if (moltenus != null && moltenus.isSpawned())
					return;

				SpawnTemplate template = switch (Rnd.get(1, 3)) {
					case 1 -> SpawnEngine.newSingleTimeSpawn(400010000, 251045, 2464.9199f, 1689f, 2882.221f, (byte) 0);
					case 2 -> SpawnEngine.newSingleTimeSpawn(400010000, 251045, 2263.4812f, 2587.1633f, 2879.5447f, (byte) 0);
					default -> SpawnEngine.newSingleTimeSpawn(400010000, 251045, 1692.96f, 1809.04f, 2886.027f, (byte) 0);
				};
				moltenus = (Npc) SpawnEngine.spawnObject(template, 1);
				// Despawn task
				ThreadPoolManager.getInstance().schedule(() -> {
					if (moltenus != null && !moltenus.isDead()) {
						moltenus.getController().delete();
						moltenus = null;
					}
				}, 3600 * 1000);
			}
		}, SiegeConfig.MOLTENUS_SPAWN_SCHEDULE);
	}

	private void scheduleAhserionsFlight() {
		CronService.getInstance().schedule(() -> PanesterraService.getInstance().startAhserionRaid(), SiegeConfig.AHSERION_START_SCHEDULE);
	}

	private void scheduleIdianDepthPortalSpawns() {
		new IdianDepthPortalSpawner().run(); // not a cronjob anymore, but let's keep it here
	}

	private void scheduleLegionDominionCalculation() {
		CronService.getInstance().schedule(() -> LegionDominionService.getInstance().startWeeklyCalculation(), "0 0 9 ? * WED *");
	}

	private static class IdianDepthPortalSpawner implements Runnable {

		private Npc asmodianUndergroundEntrance;
		private Npc elyosUndergroundEntrance;

		@Override
		public void run() {
			SpawnTemplate elyosSpawn = switch (Rnd.get(1, 4)) {
				case 1 -> SpawnEngine.newSingleTimeSpawn(600100000, 731631, 721.39f, 268.67f, 291.636f, (byte) 60); // Levinshor
				case 2 -> SpawnEngine.newSingleTimeSpawn(600100000, 731631, 332.40f, 1903.37f, 232.000f, (byte) 110); // Levinshor
				case 3 -> SpawnEngine.newSingleTimeSpawn(600090000, 731631, 1179.58f, 687.52f, 190.625f, (byte) 0); // Kaldor
				default -> SpawnEngine.newSingleTimeSpawn(210070000, 731631, 777.01f, 1479.86f, 457.375f, (byte) 30); // Cygnea
			};
			SpawnTemplate asmodianSpawn = switch (Rnd.get(1, 4)) {
				case 1 -> SpawnEngine.newSingleTimeSpawn(600100000, 731632, 1478.78f, 1844.20f, 225.987f, (byte) 45); // Levinshor
				case 2 -> SpawnEngine.newSingleTimeSpawn(600100000, 731632, 1870.49f, 41.64f, 244.711f, (byte) 15); // Levinshor
				case 3 -> SpawnEngine.newSingleTimeSpawn(600090000, 731632, 415.01f, 564.42f, 142.0f, (byte) 100); // Kaldor
				default -> SpawnEngine.newSingleTimeSpawn(220080000, 731632, 233.39f, 1137.03f, 225.875f, (byte) 105); // Enshar
			};
			if (asmodianUndergroundEntrance != null)
				asmodianUndergroundEntrance.getController().delete();
			if (elyosUndergroundEntrance != null)
				elyosUndergroundEntrance.getController().delete();
			elyosUndergroundEntrance = (Npc) SpawnEngine.spawnObject(elyosSpawn, 1);
			asmodianUndergroundEntrance = (Npc) SpawnEngine.spawnObject(asmodianSpawn, 1);
			ThreadPoolManager.getInstance().schedule(this, Rnd.get(3600, 18000), TimeUnit.MILLISECONDS);
		}
	}
}
