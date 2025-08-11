package admincommands;

import com.aionemu.gameserver.model.base.Base;
import com.aionemu.gameserver.model.base.BaseOccupier;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.BaseService;
import com.aionemu.gameserver.spawnengine.SpawnHandlerType;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

public class BaseCommand extends AdminCommand {

	private static final String COMMAND_LIST = "list";
	private static final String COMMAND_START = "start";
	private static final String COMMAND_STOP = "stop";
	private static final String COMMAND_CAPTURE = "capture";
	private static final String COMMAND_ASSAULT = "assault";

	public BaseCommand() {
		super("base");

		// @formatter:off
		setSyntaxInfo(
			"<list> - Lists all available base locations with their respective occupier",
			"<capture> [id] [occupier] - Captures the specified base with the specified new occupier.",
			"<assault> [id] - Spawns attacker NPCs for the specified base if available."
		);
		// @formatter:on
	}

	@Override
	public void execute(Player player, String... params) {
		if (params.length == 0) {
			sendInfo(player, "Not enough parameters.");
			return;
		}

		switch (params[0].toLowerCase()) {
			case COMMAND_LIST -> showBaseLocationList(player, params);
			case COMMAND_START -> startBase(player, params);
			case COMMAND_STOP -> stopBase(player, params);
			case COMMAND_CAPTURE -> captureBase(player, params);
			case COMMAND_ASSAULT -> assaultBase(player, params);
		}
	}

	protected void showBaseLocationList(Player player, String[] params) {
		BaseService.getInstance().getBaseLocations().values()
			.forEach(loc -> PacketSendUtility.sendMessage(player, "Base: %d belongs to %s".formatted(loc.getId(), loc.getOccupier())));
	}

	private void startBase(Player player, String[] params) {
		int baseId = parseBaseId(player, params);
		if (baseId == 0)
			return;

		if (BaseService.getInstance().isActive(baseId)) {
			sendInfo(player, "Unnecessary, it is already active. [id=%d]".formatted(baseId));
			return;
		}
		BaseService.getInstance().start(baseId);
	}

	private void stopBase(Player player, String[] params) {
		int baseId = parseBaseId(player, params);
		if (baseId == 0)
			return;

		if (!BaseService.getInstance().isActive(baseId)) {
			sendInfo(player, "Unnecessary, it is not active. [id=%d]".formatted(baseId));
			return;
		}
		BaseService.getInstance().stop(baseId);
	}

	protected void captureBase(Player player, String[] params) {
		int baseId = parseBaseId(player, params);
		if (baseId == 0)
			return;

		if (!BaseService.getInstance().isActive(baseId)) {
			sendInfo(player, "[id=%d] cannot only be captured if it is active".formatted(baseId));
			return;
		}

		BaseOccupier occupier = getOccupier(params[2].toUpperCase());
		if (occupier == null) {
			sendInfo(player, params[2] + " is not a valid occupier");
			return;
		}

		BaseService.getInstance().capture(baseId, occupier);
	}

	protected void assaultBase(Player player, String[] params) {
		int baseId = parseBaseId(player, params);
		if (baseId == 0)
			return;

		if (!BaseService.getInstance().isActive(baseId)) {
			sendInfo(player, "[id=%d] cannot only be assaulted if it is active".formatted(baseId));
			return;
		}

		BaseOccupier occupier = getOccupier(params[2].toUpperCase());
		if (occupier == null) {
			sendInfo(player, params[2] + " is not a valid occupier");
			return;
		}

		// assault
		Base<?> base = BaseService.getInstance().getActiveBase(baseId);
		if (base != null) {
			if (base.isUnderAssault())
				PacketSendUtility.sendMessage(player, "Assault is already active!");
			else
				base.spawnBySpawnHandler(SpawnHandlerType.ATTACKER, occupier);
		}
	}

	private int parseBaseId(Player admin, String[] params) {
		if (params.length < 2) {
			sendInfo(admin, "Not enough parameters");
			return 0;
		}

		int baseId;
		try {
			baseId = Integer.parseInt(params[1]);
		} catch (NumberFormatException e) {
			sendInfo(admin, "This baseId is not a number.");
			return 0;
		}

		if (!BaseService.getInstance().getBaseLocations().containsKey(baseId)) {
			sendInfo(admin, "This baseId does not exist.");
			return 0;
		}

		return baseId;
	}

	private BaseOccupier getOccupier(String param) {
		try {
			return BaseOccupier.valueOf(param);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}
