package com.aionemu.gameserver.model.base;

import com.aionemu.gameserver.ai.GeneralAIEvent;
import com.aionemu.gameserver.ai.eventcallback.OnDieEventListener;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team.TemporaryPlayerTeam;
import com.aionemu.gameserver.services.BaseService;

/**
 * @author Source, Rolandas
 */
public class BaseBossDeathListener extends OnDieEventListener {

	private final Base<?> base;

	public BaseBossDeathListener(Base<?> base) {
		this.base = base;
	}

	@Override
	public void onBeforeEvent(GeneralAIEvent event) {
		super.onBeforeEvent(event);
		if (!event.isHandled())
			return;

		AionObject winner = event.getSource().getOwner().getAggroList().getMostDamage();

		BaseOccupier winnerType = findOccupierType(event);
		if (winnerType == base.getOccupier())
			throw new BaseException("Base boss got killed by its own type! Boss killer: " + winner + ", Base ID: " + base.getId());

		BaseService.getInstance().capture(base.getId(), winnerType);
	}

	private BaseOccupier findOccupierType(GeneralAIEvent event) {
		AionObject winner = event.getSource().getOwner().getAggroList().getMostDamage();

		if (winner instanceof Player p) {
			return findOccupierType(p);
		} else if (winner instanceof TemporaryPlayerTeam) {
			Player leader = ((TemporaryPlayerTeam<?>) winner).getLeaderObject();
			return findOccupierType(leader);
		}
		return BaseOccupier.BALAUR;
	}

	private BaseOccupier findOccupierType(Player player) {
		if (base instanceof PanesterraFactionCamp)
			return BaseOccupier.PEACE; // If the soul anchor (boss) is destroyed, the camp will be eliminated
		if (base instanceof PanesterraBase && player.getPanesterraFaction() != null)
			return BaseOccupier.findBy(player.getPanesterraFaction());
		return BaseOccupier.findBy(player.getRace());
	}
}
