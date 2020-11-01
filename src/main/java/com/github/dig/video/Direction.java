package com.github.dig.video;

import lombok.NonNull;
import org.bukkit.Location;

public enum Direction {

    SOUTH,
    WEST,
    NORTH,
    EAST;

    public static Direction getDirection(@NonNull Location location) {
        return getDirection(location.getYaw());
    }

    public static Direction getDirection(float yaw) {
        double rotation = (yaw - 90.0F) % 360.0F;
        if (rotation < 0.0D) {
            rotation += 360.0D;
        }

        if ((0.0D <= rotation) && (rotation < 45.0D)) {
            return WEST;
        }

        if ((45.0D <= rotation) && (rotation < 135.0D)) {
            return NORTH;
        }

        if ((135.0D <= rotation) && (rotation < 225.0D)) {
            return EAST;
        }

        if ((225.0D <= rotation) && (rotation < 315.0D)) {
            return SOUTH;
        }

        return WEST;
    }
}
