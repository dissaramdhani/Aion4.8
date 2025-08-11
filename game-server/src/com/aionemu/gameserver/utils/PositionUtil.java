package com.aionemu.gameserver.utils;

import com.aionemu.gameserver.controllers.movement.CreatureMoveController;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.HouseObject;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.geometry.Point3D;
import com.aionemu.gameserver.model.templates.zone.Point2D;
import com.aionemu.gameserver.skillengine.properties.AreaDirections;

/**
 * Class with basic positional calculations.<br>
 * Thanks to:
 * <ul>
 * <li>http://geom-java.sourceforge.net/</li>
 * <li>http://local.wasp.uwa.edu.au/~pbourke/geometry/pointline/DistancePoint.java</li>
 * </ul>
 * 
 * @author Disturbing, SoulKeeper, ATracer, Wakizashi, Neon
 */
public class PositionUtil {

	private static final float MAX_ANGLE_DIFF = 90f;

	/**
	 * @return True if the object is behind the target.
	 */
	public static boolean isBehind(VisibleObject object, VisibleObject target) {
		return isBehind(object, target, MAX_ANGLE_DIFF);
	}

	/**
	 * @return True if the object is behind the target inside maxAngleDiff (e.g. ±90 degrees, meaning effective 180 degree coverage).
	 */
	public static boolean isBehind(VisibleObject object, VisibleObject target, float maxAngleDiff) {
		float angle1 = calculateAngleFrom(object, target);
		float angle2 = convertHeadingToAngle(target.getHeading());
		return checkAngleDiff(angle1, angle2, maxAngleDiff);
	}

	/**
	 * @return True if the object is in front of the target.
	 */
	public static boolean isInFrontOf(VisibleObject object, VisibleObject target) {
		return isInFrontOf(object, target, MAX_ANGLE_DIFF);
	}

	/**
	 * @return True if the object is in front of the target inside maxAngleDiff (e.g. ±90 degrees, meaning effective 180 degree coverage).
	 */
	public static boolean isInFrontOf(VisibleObject object, VisibleObject target, float maxAngleDiff) {
		if (maxAngleDiff >= 180) {
			return true;
		}
		float angle1 = calculateAngleFrom(target, object);
		float angle2 = convertHeadingToAngle(target.getHeading());
		return checkAngleDiff(angle1, angle2, maxAngleDiff);
	}

	/**
	 * @return True if both angles are within ±maxAngleDiff degrees of each other. The shortest distance between both angles will be checked, so the
	 *         effective difference between 345° and 5° will be 20 degrees instead of 340.
	 */
	private static boolean checkAngleDiff(float angle1, float angle2, float maxAngleDiff) {
		float angleDiff = Math.abs(angle1 - angle2);
		if (angleDiff > 180)
			angleDiff -= 360;
		return Math.abs(angleDiff) <= maxAngleDiff;
	}

	/**
	 * Calculates the angle where the target is located, relative to object's heading.<br>
	 * 0 degrees means directly looking at target and ±180 degrees means target stands behind object
	 * 
	 * <pre>
	 *       0 (head view)
	 *  -90     90
	 *      ±180  (back)
	 * </pre>
	 */
	public static float calculateAngleTowards(VisibleObject object, VisibleObject target) {
		float angle1 = convertHeadingToAngle(object.getHeading());
		float angle2 = calculateAngleFrom(object, target);
		float angleDiff = Math.abs(angle1 - angle2);
		return angleDiff > 180 ? angleDiff - 360 : angleDiff;
	}

	/**
	 * Get an angle between the line defined by two points and the horizontal axis
	 */
	public static float calculateAngleFrom(float obj1X, float obj1Y, float obj2X, float obj2Y) {
		float angleTarget = (float) Math.toDegrees(Math.atan2(obj2Y - obj1Y, obj2X - obj1X));
		return normalizeAngle(angleTarget);
	}

	/**
	 * Get an angle between the line defined by two objects and the horizontal axis
	 */
	public static float calculateAngleFrom(VisibleObject obj1, VisibleObject obj2) {
		return calculateAngleFrom(obj1.getX(), obj1.getY(), obj2.getX(), obj2.getY());
	}

	/**
	 * @param clientHeading
	 * @return Angle in degrees
	 */
	public static float convertHeadingToAngle(byte clientHeading) {
		return normalizeAngle(clientHeading * 3f);
	}

	/**
	 * @param float
	 *          - angle in degrees
	 * @return clientHeading
	 */
	public static byte convertAngleToHeading(float angle) {
		return (byte) (angle / 3);
	}

	/**
	 * @return The heading for the specified x and y coordinates
	 *         to look towards the specified x2 and y2 coordinates.
	 */
	public static byte getHeadingTowards(float x, float y, float x2, float y2) {
		return convertAngleToHeading(calculateAngleFrom(x, y, x2, y2));
	}

	/**
	 * @return The heading for obj1 to look towards the specified x and y coordinates.
	 */
	public static byte getHeadingTowards(VisibleObject obj1, float x, float y) {
		return getHeadingTowards(obj1.getX(), obj1.getY(), x, y);
	}

	/**
	 * @return The heading for obj1 to look towards obj2.
	 */
	public static byte getHeadingTowards(VisibleObject obj1, VisibleObject obj2) {
		return getHeadingTowards(obj1, obj2.getX(), obj2.getY());
	}

	public static float getDirectionalBound(VisibleObject object1, VisibleObject object2, boolean inverseTarget) {
		float angle = 90 - (inverseTarget ? calculateAngleTowards(object2, object1) : calculateAngleTowards(object1, object2));
		double radians = Math.toRadians(angle);
		float x1 = (float) (object1.getX() + object1.getObjectTemplate().getBoundRadius().getSide() * Math.cos(radians));
		float y1 = (float) (object1.getY() + object1.getObjectTemplate().getBoundRadius().getFront() * Math.sin(radians));
		float x2 = (float) (object2.getX() + object2.getObjectTemplate().getBoundRadius().getSide() * Math.cos(Math.PI + radians));
		float y2 = (float) (object2.getY() + object2.getObjectTemplate().getBoundRadius().getFront() * Math.sin(Math.PI + radians));
		float bound1 = (float) getDistance(object1.getX(), object1.getY(), x1, y1);
		float bound2 = (float) getDistance(object2.getX(), object2.getY(), x2, y2);
		return bound1 - bound2;
	}

	public static float getDirectionalBound(VisibleObject object1, VisibleObject object2) {
		return getDirectionalBound(object1, object2, false);
	}

	/**
	 * @return The distance between two points (2D coordinates)
	 */
	public static double getDistance(float x1, float y1, float x2, float y2) {
		float dx = x2 - x1;
		float dy = y2 - y1;

		return Math.sqrt(dx * dx + dy * dy);
	}

	/**
	 * Returns distance between two 3D points
	 * 
	 * @param point1
	 *          first point
	 * @param point2
	 *          second point
	 * @return distance between points
	 */
	public static double getDistance(Point3D point1, Point3D point2) {
		if (point1 == null || point2 == null)
			return 0;

		return getDistance(point1.getX(), point1.getY(), point1.getZ(), point2.getX(), point2.getY(), point2.getZ());
	}

	public static double getDistance(VisibleObject object, float x, float y, float z) {
		return getDistance(object.getX(), object.getY(), object.getZ(), x, y, z);
	}

	public static double getDistance(VisibleObject object, VisibleObject object2) {
		return getDistance(object, object2, true);
	}

	/**
	 * @return The distance between two objects. If centerToCenter is false, the dimensions of both objects are considered (distance between
	 *         both objects bound radius instead of the center).
	 */
	public static double getDistance(VisibleObject object, VisibleObject object2, boolean centerToCenter) {
		double distance = getDistance(object.getX(), object.getY(), object.getZ(), object2.getX(), object2.getY(), object2.getZ());
		if (!centerToCenter) {
			distance -= object.getObjectTemplate().getBoundRadius().getMaxOfFrontAndSide();
			distance -= object2.getObjectTemplate().getBoundRadius().getMaxOfFrontAndSide();
			if (distance < 0)
				distance = 0;
		}
		return distance;
	}

	/**
	 * @return The distance between two points (3D coordinates)
	 */
	public static double getDistance(float x1, float y1, float z1, float x2, float y2, float z2) {
		float dx = x1 - x2;
		float dy = y1 - y2;
		float dz = z1 - z2;

		// We should avoid Math.pow or Math.hypot due to performance reasons
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	/**
	 * @return True if two visible objects are within the given range of each other.
	 */
	public static boolean isInRange(VisibleObject object, VisibleObject object2, float range) {
		return isInRange(object, object2, range, true);
	}

	/**
	 * @return True if objects are in the given range of each other. If centerToCenter is false, the dimensions of both objects are considered (distance
	 *         between both objects bound radius instead of the center).
	 */
	public static boolean isInRange(VisibleObject object, VisibleObject object2, float range, boolean centerToCenter) {
		if (object.getWorldId() != object2.getWorldId() || object.getInstanceId() != object2.getInstanceId())
			return false;
		if (!centerToCenter) {
			range += object.getObjectTemplate().getBoundRadius().getMaxOfFrontAndSide();
			range += object2.getObjectTemplate().getBoundRadius().getMaxOfFrontAndSide();
		}
		return isInRange(object.getX(), object.getY(), object.getZ(), object2.getX(), object2.getY(), object2.getZ(), range);
	}

	public static boolean isInRange(VisibleObject obj, float x, float y, float z, float range) {
		return isInRange(obj.getX(), obj.getY(), obj.getZ(), x, y, z, range);
	}

	public static boolean isInRange(float x1, float y1, float z1, float x2, float y2, float z2, float range) {
		float dx = x1 - x2;
		float dy = y1 - y2;
		float dz = z1 - z2;
		return dx * dx + dy * dy + dz * dz < range * range;
	}

	public static boolean isInRangeLimited(VisibleObject object1, VisibleObject object2, float minRange, float maxRange) {
		if (object1.getWorldId() != object2.getWorldId() || object1.getInstanceId() != object2.getInstanceId())
			return false;

		float dx = object2.getX() - object1.getX();
		float dy = object2.getY() - object1.getY();
		float dz = object2.getZ() - object1.getZ();
		float distSquared = dx * dx + dy * dy + dz * dz;
		return !(distSquared < minRange * minRange || distSquared > maxRange * maxRange);
	}

	public static boolean isInAttackRange(Creature attacker, Creature target, float range) {
		if (attacker == null || target == null)
			return false;
		if (attacker.getMoveController().isInMove()) {
			float offset = calculateMaxDistanceOffset(attacker);
			if (attacker instanceof Player)
				offset *= 1.33f; // client sends inaccurate coordinates during movement (they're always behind actual position...)
			range += offset;
		}
		if (target.getMoveController().isInMove() && !(attacker instanceof Npc))
			range += calculateMaxDistanceOffset(target);
		return isInRange(attacker, target, range, false);
	}

	public static float calculateMaxCoveredDistance(Creature creature, long movementDurationMillis) {
		if (movementDurationMillis <= 0)
			return 0;
		int metersPerSecondInThousands = creature.getGameStats().getMovementSpeed().getCurrent();
		return metersPerSecondInThousands * movementDurationMillis / 1_000_000f;
	}

	private static float calculateMaxDistanceOffset(Creature creature) {
		float offset = CreatureMoveController.MOVE_CHECK_OFFSET;
		long lastMove = creature.getMoveController().getLastMoveUpdate();
		if (lastMove > 0) {
			long msSinceLastMove = Math.min(1000, System.currentTimeMillis() - lastMove); // cap ms to avoid huge atk ranges during lags
			offset += calculateMaxCoveredDistance(creature, msSinceLastMove);
		}
		return offset;
	}

	public static boolean isInTalkRange(Creature creature, Npc npc) {
		float range = npc.getObjectTemplate().getTalkDistance() + 1;
		return isInRange(npc, creature, range, false);
	}

	public static boolean isInTalkRange(Creature creature, HouseObject<?> houseObject) {
		float range = houseObject.getObjectTemplate().getTalkingDistance() + 1;
		return isInRange(houseObject, creature, range, false);
	}

	/**
	 * This method tests if {@code obj2} is within a cylinder, originating from {@code obj1} with the given {@code length}.
	 * Source: <a href="http://www.flipcode.com/archives/Fast_Point-In-Cylinder_Test.shtml">link</a>
	 */
	public static boolean isInsideAttackCylinder(VisibleObject obj1, VisibleObject obj2, float length, float radius, AreaDirections direction) {
		double radian = Math.toRadians(convertHeadingToAngle(obj1.getHeading()));
		if (direction == AreaDirections.BACK)
			radian += Math.PI;

		length += obj1.getObjectTemplate().getBoundRadius().getFront() + obj2.getObjectTemplate().getBoundRadius().getFront();
		radius += obj2.getObjectTemplate().getBoundRadius().getFront();

		float dx = (float) (Math.cos(radian) * length);
		float dy = (float) (Math.sin(radian) * length);

		float tdx = obj2.getX() - obj1.getX();
		float tdy = obj2.getY() - obj1.getY();
		float tdz = obj2.getZ() - obj1.getZ();
		float lengthSqr = length * length;

		float dot = tdx * dx + tdy * dy;
		if (dot < 0.0f || dot > lengthSqr)
			return false;

		// distance squared to the cylinder axis
		return (tdx * tdx + tdy * tdy + tdz * tdz) - (dot * dot / lengthSqr) <= (radius * radius);
	}

	/**
	 * Returns closest point on segment to point
	 * 
	 * @param sx1
	 *          segment x coord 1
	 * @param sy1
	 *          segment y coord 1
	 * @param sx2
	 *          segment x coord 2
	 * @param sy2
	 *          segment y coord 2
	 * @param px
	 *          point x coord
	 * @param py
	 *          point y coord
	 * @return closets point on segment to point
	 */
	public static Point2D getClosestPointOnSegment(float sx1, float sy1, float sx2, float sy2, float px, float py) {
		double xDelta = sx2 - sx1;
		double yDelta = sy2 - sy1;

		if ((xDelta == 0) && (yDelta == 0)) {
			throw new IllegalArgumentException("Segment start equals segment end");
		}

		double u = ((px - sx1) * xDelta + (py - sy1) * yDelta) / (xDelta * xDelta + yDelta * yDelta);

		Point2D closestPoint;
		if (u < 0) {
			closestPoint = new Point2D(sx1, sy1);
		} else if (u > 1) {
			closestPoint = new Point2D(sx2, sy2);
		} else {
			closestPoint = new Point2D((float) (sx1 + u * xDelta), (float) (sy1 + u * yDelta));
		}

		return closestPoint;
	}

	/**
	 * @return Normalized angle between 0 (inclusive) and 360 (exclusive) degrees.
	 */
	public static float normalizeAngle(float angle) {
		if (angle >= 360) {
			angle %= 360;
		} else if (angle < 0) {
			if (angle <= -360)
				angle %= 360;
			if (angle < 0)
				angle += 360;
		}
		return angle;
	}
}
