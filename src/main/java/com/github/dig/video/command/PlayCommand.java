package com.github.dig.video.command;

import com.github.dig.video.ItemFramePlayer;
import com.github.dig.video.exception.VideoReadException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.logging.Level;

@Log
@AllArgsConstructor
public class PlayCommand implements CommandExecutor {

    private final Plugin plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && sender.hasPermission("itemframevideo.play")) {
            if (args.length == 3) {
                Player player = (Player) sender;

                int width;
                int height;
                try {
                    width = Integer.parseInt(args[0]);
                    height = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid width or height.");
                    return true;
                }

                File video = new File(plugin.getDataFolder(), args[2]);
                if (video != null && video.exists()) {
                    Block topLeft = player.getTargetBlockExact(1);
                    if (topLeft != null && topLeft.getType() != Material.AIR) {
                        boolean useZ = true;
                        double yaw = player.getLocation().getYaw();
                        if ((yaw >= 135 || yaw <= -135)
                                || (yaw <= 45 || yaw <= -45)) {
                            useZ = false;
                        }

                        ItemFrame[][] itemFrames = createItemFrames(topLeft, width, height, useZ);
                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                            try {
                                ItemFramePlayer framePlayer = new ItemFramePlayer(plugin, itemFrames, video);
                                Bukkit.getOnlinePlayers().forEach(framePlayer::addViewer);
                                Bukkit.getScheduler().runTask(plugin, () -> framePlayer.play());
                            } catch (FileNotFoundException | VideoReadException e) {
                                Bukkit.getScheduler().runTask(plugin,
                                        () -> sender.sendMessage(ChatColor.RED + "Unable to start player."));
                                log.log(Level.SEVERE, "Unable to start player", e);
                            }
                        });
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "File does not exist.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /play <width> <height> <file>");
            }
        }
        return true;
    }

    private ItemFrame[][] createItemFrames(@NonNull Block topLeft, int width, int height, boolean useZ) {
        ItemFrame[][] itemFrames = new ItemFrame[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Location location = topLeft.getLocation().clone().add(useZ ? 0 : x, y, useZ ? x : 0);
                ItemFrame frame = location.getWorld().spawn(
                        location,
                        ItemFrame.class);
                itemFrames[x][y] = frame;
            }
        }
        return itemFrames;
    }

    private Optional<Entity> getClosestEntity(@NonNull Player player,
                                              double radius,
                                              EntityType entityType) {
        Entity entity = null;
        double distance = Double.MAX_VALUE;
        for (Entity ent : player.getNearbyEntities(radius, radius, radius)) {
            if (ent.getType() == entityType) {
                double dis = player.getLocation().distanceSquared(ent.getLocation());
                if (dis < distance) {
                    entity = ent;
                    distance = dis;
                }
            }
        }
        return Optional.ofNullable(entity);
    }
}
