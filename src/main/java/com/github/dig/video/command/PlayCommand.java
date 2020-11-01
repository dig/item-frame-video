package com.github.dig.video.command;

import com.github.dig.video.ItemFramePlayer;
import com.github.dig.video.exception.VideoReadException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
            if (args.length == 1) {
                Player player = (Player) sender;
                File video = new File(plugin.getDataFolder(), args[0]);

                Optional<Entity> entityOptional = getClosestEntity(player, 10, EntityType.ITEM_FRAME);
                if (entityOptional.isPresent()) {
                    Entity entity = entityOptional.get();
                    ItemFrame itemFrame = (ItemFrame) entity;

                    if (video != null && video.exists()) {
                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                            try {
                                ItemFramePlayer framePlayer = new ItemFramePlayer(plugin, itemFrame, video);
                                Bukkit.getOnlinePlayers().forEach(framePlayer::addViewer);
                                Bukkit.getScheduler().runTask(plugin, () -> framePlayer.play());
                            } catch (FileNotFoundException | VideoReadException e) {
                                Bukkit.getScheduler().runTask(plugin,
                                        () -> sender.sendMessage(ChatColor.RED + "Unable to start player."));
                                log.log(Level.SEVERE, "Unable to start player", e);
                            }
                        });
                    } else {
                        sender.sendMessage(ChatColor.RED + "File does not exist.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "No item frame nearby.");
                }
            }
        }
        return true;
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
