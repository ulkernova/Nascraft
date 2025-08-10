package me.bounser.nascraft.managers.scheduler;

import me.bounser.nascraft.Nascraft;
import org.bukkit.Bukkit;
import java.util.concurrent.CompletableFuture;

/**
 * Factory for scheduler adapters that detects and creates the appropriate
 * adapter based on the server environment.
 */
public class SchedulerManager {
    
    private static SchedulerAdapter instance;
    private static boolean foliaDetected = false;
    
    /**
     * Initialize the scheduler adapter.
     * 
     * @param plugin The plugin instance
     * @return The appropriate scheduler adapter
     */
    public static SchedulerAdapter init(Nascraft plugin) {
        if (instance == null) {
            foliaDetected = detectFolia();
            
            if (foliaDetected) {
                plugin.getLogger().info("Folia detected! Using Folia scheduler adapter.");
                try {
                    instance = new FoliaSchedulerAdapter(plugin);
                } catch (Exception e) {
                    plugin.getLogger().severe("Failed to initialize Folia scheduler adapter. Falling back to Bukkit scheduler.");
                    e.printStackTrace();
                    instance = new BukkitSchedulerAdapter(plugin);
                    foliaDetected = false;
                }
            } else {
                plugin.getLogger().info("Using standard Bukkit scheduler adapter.");
                instance = new BukkitSchedulerAdapter(plugin);
            }
        }
        
        return instance;
    }
    
    /**
     * Get the scheduler adapter instance.
     * 
     * @return The scheduler adapter
     * @throws IllegalStateException if called before initialization
     */
    public static SchedulerAdapter getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SchedulerManager not initialized. Call init() first.");
        }
        return instance;
    }
    
    /**
     * Check if Folia is available.
     * 
     * @return true if Folia is detected, false otherwise
     */
    public static boolean isFolia() {
        return foliaDetected;
    }
    
    // Convenience forwarding methods
    public CompletableFuture<Void> runAsync(Runnable task) {
        return getInstance().runAsync(task);
    }
    
    public CompletableFuture<Void> runGlobal(Runnable task) {
        return getInstance().runGlobal(task);
    }
    
    public int scheduleAsyncRepeating(Runnable task, long initialDelayTicks, long periodTicks) {
        return getInstance().scheduleAsyncRepeating(task, initialDelayTicks, periodTicks);
    }
    
    public int scheduleGlobalRepeating(Runnable task, long initialDelayTicks, long periodTicks) {
        return getInstance().scheduleGlobalRepeating(task, initialDelayTicks, periodTicks);
    }
    
    public int scheduleAsyncDelayed(Runnable task, long delayTicks) {
        return getInstance().scheduleAsyncDelayed(task, delayTicks);
    }
    
    public int scheduleGlobalDelayed(Runnable task, long delayTicks) {
        return getInstance().scheduleGlobalDelayed(task, delayTicks);
    }
    
    public void cancelTask(int taskId) {
        getInstance().cancelTask(taskId);
    }
    
    /**
     * Detect if Folia is available.
     * 
     * @return true if Folia is detected, false otherwise
     */
    private static boolean detectFolia() {
        try {
            // Check if Folia classes are available
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            Class.forName("io.papermc.paper.threadedregions.scheduler.AsyncScheduler");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
} 