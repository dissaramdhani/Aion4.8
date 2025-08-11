package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

public class MembershipConfig {

	@Property(key = "gameserver.membership.types", defaultValue = "Premium")
	public static String[] MEMBERSHIP_TYPES;

	@Property(key = "gameserver.membership.gathering.allow_on_mount", defaultValue = "10")
	public static byte GATHERING_ALLOW_ON_MOUNT;

	@Property(key = "gameserver.instances.title.requirement", defaultValue = "10")
	public static byte INSTANCES_TITLE_REQ;

	@Property(key = "gameserver.instances.race.requirement", defaultValue = "10")
	public static byte INSTANCES_RACE_REQ;

	@Property(key = "gameserver.instances.level.requirement", defaultValue = "10")
	public static byte INSTANCES_LEVEL_REQ;

	@Property(key = "gameserver.instances.group.requirement", defaultValue = "10")
	public static byte INSTANCES_GROUP_REQ;

	@Property(key = "gameserver.instances.quest.requirement", defaultValue = "10")
	public static byte INSTANCES_QUEST_REQ;

	@Property(key = "gameserver.instances.cooldown", defaultValue = "10")
	public static byte INSTANCES_COOLDOWN;

	@Property(key = "gameserver.emotions.all", defaultValue = "10")
	public static byte EMOTIONS_ALL;

	@Property(key = "gameserver.quest.stigma.slot", defaultValue = "10")
	public static byte STIGMA_SLOT_QUEST;

	@Property(key = "gameserver.soulsickness.disable", defaultValue = "10")
	public static byte DISABLE_SOULSICKNESS;

	@Property(key = "gameserver.autolearn.stigma", defaultValue = "10")
	public static byte STIGMA_AUTOLEARN;

	@Property(key = "gameserver.quest.limit.disable", defaultValue = "10")
	public static byte QUEST_LIMIT_DISABLED;

	@Property(key = "gameserver.character.additional.enable", defaultValue = "10")
	public static byte CHARACTER_ADDITIONAL_ENABLE;

	@Property(key = "gameserver.character.additional.count", defaultValue = "8")
	public static byte CHARACTER_ADDITIONAL_COUNT;
}
