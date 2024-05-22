package me.sheepie.dashability;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public final class Dash_ability extends JavaPlugin implements Listener {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    @Override
    public void onEnable() {
        // Register the dash command
        this.getCommand("dash").setExecutor(this);

        // Register the event listener
        getServer().getPluginManager().registerEvents(this, this);

        // Log message to console on enable
        getLogger().info("DashAbility plugin has successfully enabled!");
    }

    @Override
    public void onDisable() {
        // Log message to console on disable
        getLogger().info("DashAbility plugin has successfully disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("dash")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.isOnGround()) {
                    dash(player);
                } else {
                    player.sendMessage("You need to be on the ground to use the dash ability.");
                }
            } else {
                sender.sendMessage("This command can only be used by a player.");
            }
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().getType() == Material.CHORUS_FLOWER) {
            if (player.isOnGround()) {
                dash(player);
            } else {
                player.sendMessage("You need to be on the ground to use the dash ability.");
            }
        }
    }

    private void dash(Player player) {
        UUID playerId = player.getUniqueId();

        // Cooldown logic
        if (cooldowns.containsKey(playerId)) {
            long timeLeft = (cooldowns.get(playerId) - System.currentTimeMillis()) / 1000;
            if (timeLeft > 0) {
                player.sendMessage("You must wait " + timeLeft + " seconds before using dash again.");
                return;
            }
        }

        // Get the direction the player is looking and multiply to create a dash effect
        Vector direction = player.getLocation().getDirection().normalize().multiply(2.5);

        // Limit the y component to a maximum of 2 blocks
        if (direction.getY() > 0.5) {
            direction.setY(0.5);
        }

        player.setVelocity(direction);

        // Set cooldown (e.g., 1.5 seconds)
        cooldowns.put(playerId, System.currentTimeMillis() + (1500));
    }
}
