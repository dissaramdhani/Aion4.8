package com.aionemu.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * @author Neon
 */
public class AdventDAO {

	public static int getLastReceivedDay(Player player) {
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT `last_day_received` FROM `advent` WHERE ? = account_id")) {
			stmt.setInt(1, player.getAccount().getId());
			try (ResultSet rs = stmt.executeQuery()) {
				rs.next();
				return rs.getInt("last_day_received");
			}
		} catch (SQLException e) {
			return 0;
		}
	}

	public static boolean storeLastReceivedDay(Player player, int dayOfMonth) {
		try (Connection con = DatabaseFactory.getConnection(); PreparedStatement stmt = con.prepareStatement("REPLACE INTO `advent` VALUES (?, ?)")) {
			stmt.setInt(1, player.getAccount().getId());
			stmt.setInt(2, dayOfMonth);
			stmt.execute();
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
}
