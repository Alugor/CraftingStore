package net.craftingstore.bukkit;

import net.craftingstore.bukkit.events.DonationReceivedEvent;
import net.craftingstore.core.CraftingStorePlugin;
import net.craftingstore.core.logging.CraftingStoreLogger;
import net.craftingstore.core.logging.impl.JavaLogger;
import net.craftingstore.core.models.donation.Donation;
import org.bukkit.Bukkit;
import org.bukkit.Server;

public class CraftingStoreBukkitImpl implements CraftingStorePlugin {

    private CraftingStoreBukkit bukkitPlugin;
    private JavaLogger logger;

    CraftingStoreBukkitImpl(CraftingStoreBukkit bukkitPlugin) {
        this.bukkitPlugin = bukkitPlugin;
        this.logger = new JavaLogger(bukkitPlugin.getLogger());
        this.logger.setDebugging(bukkitPlugin.getConfig().getBoolean("debug", false));
    }

    public boolean executeDonation(Donation donation) {
        if (donation.getPlayer().isRequiredOnline()) {
            if (Bukkit.getPlayerExact(donation.getPlayer().getUsername()) == null) {
                return false;
            }
        }
        Server server = bukkitPlugin.getServer();

        DonationReceivedEvent event = new DonationReceivedEvent(donation);
        server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        server.getScheduler().runTask(bukkitPlugin, () -> server.dispatchCommand(server.getConsoleSender(), donation.getCommand()));
        return true;
    }

    public CraftingStoreLogger getLogger() {
        return this.logger;
    }

    public void registerRunnable(Runnable runnable, int delay, int interval) {
        bukkitPlugin.getServer().getScheduler().runTaskTimerAsynchronously(bukkitPlugin, runnable, delay * 20, interval * 20);
    }

    public String getToken() {
        return bukkitPlugin.getConfig().getString("api-key");
    }

    @Override
    public String getVersion() {
        return bukkitPlugin.getDescription().getVersion();
    }

    @Override
    public String getPlatform() {
        return bukkitPlugin.getServer().getVersion();
    }
}
