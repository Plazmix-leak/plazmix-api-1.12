package net.plazmix.utility.location;

import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

/**
 * Used to rotate blocks and block sections when building or testing for the presence of a MultiBlockStructure
 * @author Redempt
 *
 */
public final class LocationRotator {
	
	private static final BlockFace[] BLOCK_FACES = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
	
	private static <T> int indexOf(T[] arr, T key) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].equals(key)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Rotates a BlockFace according to given values
	 * @param face The BlockFace to rotate
	 * @param rotation The number of clockwise rotations to apply
	 * @param mirror Whether or not to mirror over the X axis
	 * @return The rotated BlockFace
	 */
	public static BlockFace rotateBlockFace(BlockFace face, int rotation, boolean mirror) {
		rotation %= 4;
		if (rotation < 0) {
			rotation += 4;
		}
		int ind = indexOf(BLOCK_FACES, face);
		if (ind == -1) {
			return face;
		}
		if (mirror && (ind == 1 || ind == 3)) {
			ind = ind + 2;
		}
		ind = (ind + rotation) % 4;
		return BLOCK_FACES[ind];
	}
	
	/**
	 * Rotates a BlockFace according to given values
	 * @param face The BlockFace to rotate
	 * @param rotation The number of clockwise rotations to apply
	 * @return The rotated BlockFace
	 */
	public static BlockFace rotateBlockFace(BlockFace face, int rotation) {
		return rotateBlockFace(face, rotation, false);
	}
	
	private int rotation;
	private boolean mirrored;
	private double x = 0;
	private double z = 0;
	
	/**
	 * Constructs a new Rotator
	 * @param rotation The number of 90-degree clockwise rotations this Rotator applies
	 * @param mirrored Whether this Rotator should mirror over the X axis
	 */
	public LocationRotator(int rotation, boolean mirrored) {
		while (rotation < 0) {
			rotation += 4;
		}
		this.rotation = rotation % 4;
		this.mirrored = mirrored;
	}
	
	private <T> void rotate(T[] arr) {
		Object[] rot = new Object[4];
		for (int i = 0; i < 4; i++) {
			rot[i] = arr[i];
		}
		for (int i = 0; i < 4; i++) {
			int dir = (i + rotation) % 4;
			if (mirrored && (i == 0 || i == 2)) {
				dir = (dir + 2) % 4;
			}
			arr[i] = (T) rot[dir];
		}
	}
	
	/**
	 * Rotates a BlockFace according to this Rotator
	 * @param face The BlockFace to rotate
	 * @return The rotated BlockFace
	 */
	public BlockFace rotateBlockFace(BlockFace face) {
		return rotateBlockFace(face, rotation, mirrored);
	}
	
	/**
	 * Rotates a Vector according to this Rotator
	 * @param vector The Vector to rotate
	 * @return The rotated Vector
	 */
	public Vector rotateVector(Vector vector) {
		setLocation(vector.getX(), vector.getZ());
		return new Vector(getRotatedX(), vector.getY(), getRotatedZ());
	}
	
	/**
	 * Sets the relative coordinates this Rotator will rotate
	 * @param x The relative X coordinate
	 * @param z The relative Z coordinate
	 */
	public void setLocation(double x, double z) {
		this.x = mirrored ? -x : x;
		this.z = z;
	}
	
	/**
	 * @return The rotated relative block X
	 */
	public int getRotatedBlockX() {
		return (int) getRotatedX();
	}
	
	/**
	 * @return The rotated relative block Z
	 */
	public int getRotatedBlockZ() {
		return (int) getRotatedZ();
	}
	
	/**
	 * @return The rotated relative X
	 */
	public double getRotatedX() {
		switch (rotation) {
			case 0:
				return x;
			case 1:
				return -z;
			case 2:
				return -x;
			case 3:
				return z;
		}
		return 0;
	}
	
	/**
	 * @return The rotated relative Z
	 */
	public double getRotatedZ() {
		switch (rotation) {
			case 0:
				return z;
			case 1:
				return x;
			case 2:
				return -z;
			case 3:
				return -x;
		}
		return 0;
	}
	
	/**
	 * Gets a Rotator which will negate the operations of this Rotator
	 * @return The inverse Rotator
	 */
	public LocationRotator getInverse() {
		return new LocationRotator(-rotation, mirrored);
	}
	
	/**
	 * Gets a clone of this Rotator
	 * @return The clone of this Rotator
	 */
	public LocationRotator clone() {
		return new LocationRotator(rotation, mirrored);
	}
	
	/**
	 * Gets the rotation, in number of 90-degree clockwise rotations
	 * @return The rotation
	 */
	public int getRotation() {
		return rotation;
	}
	
	/**
	 * Sets the rotation
	 * @param rotation The rotation to set
	 */
	public void setRotation(int rotation) {
		this.rotation = rotation % 4;
	}
	
	/**
	 * Sets whether this rotator mirrors over the X axis
	 * @param mirrored Whether this rotator mirrors over the X axis
	 */
	public void setMirrored(boolean mirrored) {
		this.mirrored = mirrored;
	}
	
	/**
	 * Gets whether this rotator mirrors over the X axis
	 * @return Whether this rotator mirrors over the X axis
	 */
	public boolean isMirrored() {
		return mirrored;
	}
	
}