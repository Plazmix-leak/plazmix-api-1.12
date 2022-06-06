package net.plazmix.utility.location;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

@UtilityClass
public class LocationUtil {

    /**
     * Преобразование локации в строку подобного вида:
     *  - 'world_name, x, y, z, yaw, pitch'
     */
    public String locationToString(Location location) {
        if (location == null) {
            return null;
        }

        return String.format("%s, %s, %s, %s, %s, %s", location.getWorld().getName(),
                location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    /**
     * Преобразование строки с координатами в саму локацию
     */
    public Location stringToLocation(String locString) {
        if (locString == null) {
            return null;
        }

        String[] locData = locString.split(", ");
        World world = Bukkit.getWorld(locData[0]);

        Objects.requireNonNull(world, "world");

        return new Location(world, Double.parseDouble(locData[1]), Double.parseDouble(locData[2]), Double.parseDouble(locData[3]),
                Float.parseFloat(locData[4]), Float.parseFloat(locData[5]));
    }

    public Location centralizeXYZ(boolean centerY, @NonNull Location location) {
        Location centralizedLocation = location.clone();

        centralizedLocation.setX(Math.floor(location.getBlockX()) + 0.5);
        centralizedLocation.setZ(Math.floor(location.getBlockZ()) + 0.5);

        if (centerY) {
            centralizedLocation.setY(Math.floor(location.getBlockY()) + 0.5);
        }

        return centralizedLocation;
    }

    public Location centralizeXZ(@NonNull Location location) {
        return centralizeXYZ(false, location);
    }

    /**
     * Проверка дистанции локаций
     */
    public boolean isDistance(Location location1, Location location2, double distance) {
        return location1.distance(location2) <= distance;
    }

    public final BlockFace[] axis = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
    public final BlockFace[] radial = { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };

    public BlockFace yawToFace(float yaw) {
        return yawToFace(yaw, true);
    }

    public BlockFace yawToFace(float yaw, boolean useSubCardinalDirections) {
        if (useSubCardinalDirections) {
            return radial[Math.round(yaw / 45f) & 0x7];
        } else {
            return axis[Math.round(yaw / 90f) & 0x3];
        }
    }

    /**
     * An array of all the block faces which face in a single direction (positive X, negative X, etc.)
     */
    public static final BlockFace[] PRIMARY_BLOCK_FACES = {BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};

    /**
     * Checks if a given block type is a hazard - whether it would damage the player if they were on top of it
     * @param type The type to check
     * @return Whether the block type is a hazard
     */
    public static boolean isHazard(Material type) {
        if (type.toString().contains("LAVA") || type.toString().contains("WATER")) {
            return true;
        }
        if (type.toString().contains("PORTAL") && !type.toString().endsWith("PORTAL_FRAME")) {
            return true;
        }
        if (type.toString().equals("MAGMA_BLOCK") || type.toString().equals("CAMPFIRE")) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether the given location is safe to teleport a player to - that a player would not be damaged as a result of being moved to this location
     * @param loc The location to check
     * @return Whether the given location is safe
     */
    public static boolean isSafe(Location loc) {
        Block under = loc.clone().subtract(0, 1, 0).getBlock();
        if (under.getType().isSolid()) {
            Block middle = loc.getBlock();
            Block above = loc.clone().add(0, 1, 0).getBlock();
            if (!isHazard(middle.getType()) && !isHazard(above.getType())) {
                if (!middle.getType().isSolid() && !above.getType().isSolid() && !middle.isLiquid() && !above.isLiquid()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the nearest safe location to the given location within the given distance passing the given predicate check
     * @param loc The location to find the nearest safe location to
     * @param maxDistance The maximum distance to check from this location
     * @param filter Used to filter safe locations that you still don't want to send the player to. Any locations this returns false for will be ignored.
     * @return The nearest safe location, or null if one was not found
     */
    public static Location getNearestSafeLocation(Location loc, int maxDistance, Predicate<Location> filter) {
        Vector direction = loc.getDirection();
        loc = loc.getBlock().getLocation().add(0.5, 0.1, 0.5);
        if (isSafe(loc) && filter.test(loc)) {
            loc.setDirection(direction);
            return loc;
        }
        Location nearest = null;
        double dist = 0;
        for (int y = 0; Math.abs(y) <= maxDistance; y = y == 0 ? 1 : -y - Math.min(Integer.signum(y), 0)) {
            for (int x = 0; Math.abs(x) <= maxDistance; x = x == 0 ? 1 : -x - Math.min(Integer.signum(x), 0)) {
                for (int z = 0; Math.abs(z) <= maxDistance; z = z == 0 ? 1 : -z - Math.min(Integer.signum(z), 0)) {
                    Location check = loc.clone().add(x, y, z);
                    if (isSafe(check) && filter.test(check)) {
                        check.setDirection(direction);
                        double distance = check.distanceSquared(loc);
                        if (nearest == null || distance < dist) {
                            nearest = check;
                            dist = distance;
                            if (dist <= 1) {
                                return nearest;
                            }
                        }
                    }
                }
            }
        }
        return nearest;
    }

    /**
     * Gets the nearest safe location to the given location within the given distance
     * @param loc The location to find the nearest safe location to
     * @param maxDistance The maximum distance to check from this location
     * @return The nearest safe location, or null if one was not found
     */
    public static Location getNearestSafeLocation(Location loc, int maxDistance) {
        return getNearestSafeLocation(loc, maxDistance, l -> true);
    }

    /**
     * Gets the Vector direction of a BlockFace. For use in versions below 1.13.
     * @param face The block face
     * @return The vector representing the direction
     */
    public static Vector getDirection(BlockFace face) {
        return new Vector(face.getModX(), face.getModY(), face.getModZ());
    }

    /**
     * Sets the location's coordinates to its block coordinates, then returns it
     * @param loc The location
     * @return The block location
     */
    public static Location toBlockLocation(Location loc) {
        loc.setX(loc.getBlockX());
        loc.setY(loc.getBlockY());
        loc.setZ(loc.getBlockZ());
        return loc;
    }

    /**
     * Gets the chunk X and Z of a location
     * @param loc The location to get the chunk coordinates of
     * @return An array containing the chunk coordinates [x, z]
     */
    public static int[] getChunkCoordinates(Location loc) {
        return new int[] {loc.getBlockX() >> 4, loc.getBlockZ() >> 4};
    }

    /**
     * Finds the fastest path between a starting and ending location using A*, then removes unneeded steps for straight
     * @param start The starting block
     * @param end The ending block
     * @param max The max number of locations to be checked - use to limit runtime
     * @param filter A filter to determine which blocks are passable
     * @return A List of locations leading from the start to the end, or the closest block if the path could not
     * be completed
     */
    public static List<Location> directPathfind(Block start, Block end, int max, Predicate<Block> filter) {
        List<Location> path = new ArrayList<>(pathfind(start, end, max, filter));
        for (int i = 0; i + 2 < path.size(); i += 2) {
            Location first = path.get(i);
            Location second = path.get(i + 2);
            if (Path.getPath(first, second, 0.25).stream().map(Location::getBlock).allMatch(filter)) {
                path.remove(i + 1);
                i -= 2;
            }
        }
        return path;
    }

    /**
     * Finds the fastest path between a starting and ending location using A*, then removes unneeded steps for straight
     * @param start The starting block
     * @param end The ending block
     * @param max The max number of locations to be checked - use to limit runtime
     * @return A List of locations leading from the start to the end, or the closest block if the path could not
     * be completed
     */
    public static List<Location> directPathfind(Block start, Block end, int max) {
        return directPathfind(start, end, max, b -> !b.getType().isSolid());
    }

    /**
     * Finds the fastest path between a starting and ending location using A*
     * @param start The starting block
     * @param end The ending block
     * @param max The max number of locations to be checked - use to limit runtime
     * @return A Deque of locations leading from the start to the end, or the closest block if the path could not
     * be completed
     */
    public static Deque<Location> pathfind(Block start, Block end, int max) {
        return pathfind(start, end, max, b -> !b.getType().isSolid());
    }

    /**
     * Finds the fastest path between a starting and ending location using A*
     * @param start The starting block
     * @param end The ending block
     * @param max The max number of locations to be checked - use to limit runtime
     * @param filter A filter to determine which blocks are passable
     * @return A Deque of locations leading from the start to the end, or the closest block if the path could not
     * be completed
     */
    public static Deque<Location> pathfind(Block start, Block end, int max, Predicate<Block> filter) {
        if (!start.getWorld().equals(end.getWorld())) {
            throw new IllegalArgumentException("Start and end must be in the same world");
        }
        Set<Block> nodes = new HashSet<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(n -> n.score));
        Set<Block> exclude = new HashSet<>();
        Node node = new Node(start, 0);
        node.score = score(node, start, end);
        nodes.add(node.block);
        queue.add(node);
        int iter = 0;
        Node least = node;
        int leastDist = distance(least.block, end);
        while (iter < max) {
            node = queue.poll();
            if (node == null) {
                return tracePath(least);
            }
            nodes.remove(node.block);
            int dist = distance(node.block, end);
            if (dist == 0 || (dist == 1 && !filter.test(end))) {
                return tracePath(node);
            } else {
                if (dist < leastDist) {
                    leastDist = dist;
                    least = node;
                }
            }
            exclude.add(node.block);
            getAdjacent(node, start, end, n -> {
                if (exclude.contains(n.block) || !filter.test(n.block)) {
                    exclude.add(n.block);
                    return;
                }
                if (nodes.add(n.block)) {
                    queue.add(n);
                }
            });
            iter++;
        }
        return tracePath(least);
    }

    private static Deque<Location> tracePath(Node node) {
        Deque<Location> path = new ArrayDeque<>();
        while (node != null) {
            path.addFirst(node.block.getLocation().add(.5, .5, .5));
            node = node.parent;
        }
        return path;
    }

    private static void getAdjacent(Node block, Block start, Block end, Consumer<Node> lambda) {
        lambda.accept(getRelative(block, start, end, 1, 0, 0));
        lambda.accept(getRelative(block, start, end, -1, 0, 0));
        lambda.accept(getRelative(block, start, end, 0, 1, 0));
        lambda.accept(getRelative(block, start, end, 0, -1, 0));
        lambda.accept(getRelative(block, start, end, 0, 0, 1));
        lambda.accept(getRelative(block, start, end, 0, 0, -1));
    }

    private static Node getRelative(Node block, Block start, Block end, int x, int y, int z) {
        Block b = block.block.getRelative(x, y, z);
        int score = score(block, start, end);
        Node node = new Node(b, score);
        node.parent = block;
        return node;
    }

    private static int score(Node node, Block start, Block end) {
        return distance(node.block, start) + distance(node.block, end) * 2;
    }

    private static int distance(Block first, Block second) {
        return Math.abs(first.getX() - second.getX())
                + Math.abs(first.getY() - second.getY())
                + Math.abs(first.getZ() - second.getZ());
    }

    private static class Node {

        public Block block;
        public int score;
        public Node parent;

        public Node(Block block, int score) {
            this.block = block;
            this.score = score;
        }

    }

    public static class Path {

        /**
         * Get the locations between the start and end location
         * @param start The start location
         * @param end The end location
         * @param step The step size to use
         * @return A list of all the locations between the locations
         */
        public static List<Location> getPath(Location start, Location end, double step) {
            List<Location> locs = new ArrayList<>();
            locs.add(start);
            Vector v = end.clone().subtract(start).toVector();
            v = v.normalize().multiply(step);
            Location current = start.clone();
            while (current.distance(end) > step) {
                locs.add(current.clone());
                current = current.add(v);
            }
            locs.add(end);
            return locs;
        }

        /**
         * Get the locations between the start and end location
         * @param start The start location
         * @param end The end location
         * @return A list of all the locations between the locations, equidistant
         */
        public static List<Location> getPath(Location start, Location end) {
            return getPath(start, end, 1);
        }

        /**
         * Get the locations from the start along a vector
         * @param start The start location
         * @param direction The vector indicating direction
         * @param distance The length of the path
         * @param step The step size to use
         * @return A list of all the locations between the locations, equidistant
         */
        public static List<Location> getPath(Location start, Vector direction, double distance, double step) {
            direction = direction.clone().normalize().multiply(distance);
            Location end = start.clone().add(direction);
            return getPath(start, end, step);
        }

        /**
         * Get the locations from the start along a vector
         * @param start The start location
         * @param direction The vector indicating direction
         * @param distance The max distance to step
         * @return A list of all the locations between the locations, equidistant
         */
        public static List<Location> getPath(Location start, Vector direction, double distance) {
            return getPath(start, direction, distance, 1);
        }

        /**
         * Get the locations from the start along a vector
         * @param start The start location
         * @param direction The vector indicating direction and length
         * @return A list of all the locations between the locations, equidistant
         */
        public static List<Location> getPath(Location start, Vector direction) {
            return getPath(start, direction, direction.length(), 1);
        }

        /**
         * Get the locations from the start along a vector
         * @param start The start location whose direction vector will be used for direction and length
         * @return A list of all the locations between the locations, equidistant
         */
        public static List<Location> getPath(Location start) {
            return getPath(start, start.getDirection(), start.getDirection().length(), 1);
        }

        /**
         * Get the locations from the start along a vector
         * @param start The start location whose direction vector will be used for direction and length
         * @param step The step size to use
         * @return A list of all the locations between the locations, equidistant
         */
        public static List<Location> getPath(Location start, double step) {
            return getPath(start, start.getDirection(), start.getDirection().length(), step);
        }

    }

}