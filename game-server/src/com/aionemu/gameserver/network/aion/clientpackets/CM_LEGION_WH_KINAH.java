package com.aionemu.gameserver.network.aion.clientpackets;

import java.util.Set;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.model.team.legion.LegionHistoryAction;
import com.aionemu.gameserver.model.team.legion.LegionMember;
import com.aionemu.gameserver.model.team.legion.LegionPermissionsMask;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.LegionService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
public class CM_LEGION_WH_KINAH extends AionClientPacket {

	private long amount;
	private byte actionType;

	public CM_LEGION_WH_KINAH(int opcode, Set<State> validStates) {
		super(opcode, validStates);
	}

	@Override
	protected void readImpl() {
		amount = readQ();
		actionType = readC();
	}

	@Override
	protected void runImpl() {
		Player activePlayer = getConnection().getActivePlayer();
		Legion legion = activePlayer.getLegion();
		if (legion != null) {
			LegionMember LM = LegionService.getInstance().getLegionMember(activePlayer.getObjectId());
			switch (actionType) {
				case 0:
					if (!LM.hasRights(LegionPermissionsMask.WH_WITHDRAWAL)) {
						// You do not have the authority to use the Legion warehouse.
						PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_WAREHOUSE_NO_RIGHT());
						return;
					}
					if (activePlayer.getStorage(StorageType.LEGION_WAREHOUSE.getId()).tryDecreaseKinah(amount)) {
						activePlayer.getInventory().increaseKinah(amount);
						LegionService.getInstance().addHistory(legion, activePlayer.getName(), LegionHistoryAction.KINAH_WITHDRAW, Long.toString(amount));
					}
					break;
				case 1:
					if (!LM.hasRights(LegionPermissionsMask.WH_DEPOSIT)) {
						// You do not have the authority to use the Legion warehouse.
						PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_WAREHOUSE_NO_RIGHT());
						return;
					}
					if (activePlayer.getInventory().tryDecreaseKinah(amount)) {
						activePlayer.getStorage(StorageType.LEGION_WAREHOUSE.getId()).increaseKinah(amount);
						LegionService.getInstance().addHistory(legion, activePlayer.getName(), LegionHistoryAction.KINAH_DEPOSIT, Long.toString(amount));
					}
					break;
			}
		}
	}
}
