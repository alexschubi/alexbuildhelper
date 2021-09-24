package com.alexschubi.alexbuildhelper;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.Server.Spigot;
import org.bukkit.command.*;
import org.bukkit.event.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.*;
import org.bukkit.scheduler.BukkitRunnable;
import net.md_5.bungee.api.chat.*;

public class Main extends JavaPlugin implements Listener {

	private static Main instance;
	public static Object GLOBAL_d029356aa133700e250ba4c750a010c0;
	public static org.bukkit.scheduler.BukkitTask mtask = null;
	public static Integer mtaskId= null;
	
	@Override
	public void onEnable() {
		instance = this;
		getDataFolder().mkdir();
		getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {
	}

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String label, String[] commandArgs) {
		if (command.getName().equalsIgnoreCase("abh")) {
			try {
				if ((commandSender instanceof org.bukkit.entity.Player)) {
					switch (commandArgs[0]) {
						case "circle":
							Double radius = Double.valueOf(commandArgs[1]);
							Integer amount = 10000;
							try {amount = Integer.parseInt(commandArgs[3]);} catch (Exception e) {e.printStackTrace();}
							String mparticle = (org.bukkit.Particle.ELECTRIC_SPARK).name();
							try {
								mparticle = Particle.valueOf(commandArgs[2].toUpperCase()).name();
							} catch (Exception ex) {
								ex.printStackTrace();
							}
							//commandSender.sendMessage("do abh circle");
							Location vlocation = ((org.bukkit.entity.Player) commandSender).getLocation();
							Location blocation = new Location(vlocation.getWorld(), ((int) vlocation.getX()) + 0.5d - 2, ((int) vlocation.getY()) + 0.5d, ((int) vlocation.getZ()) + 0.5d);
							ArrayList<Location> locations =  getCircle(blocation, radius, amount);
							commandSender.sendMessage("got " + Integer.toString(locations.size()) + " circle-blocks of " + mparticle + " Particle");
							if(mtask==null) {
								spawnParticles(locations, commandSender, mparticle);
							} else if (mtask.isCancelled()){
								spawnParticles(locations, commandSender, mparticle);
							} else {
								mtask.cancel();
								Bukkit.getScheduler().cancelTask(mtask.getTaskId());
								spawnParticles(locations, commandSender, mparticle);
							}
							break;
						case "kill":
							commandSender.sendMessage(mtask.toString());
							if (!mtask.isCancelled()){
								mtask.cancel();
								Bukkit.getScheduler().cancelTask(mtask.getTaskId());
								commandSender.sendMessage("killed sucessfully all abh particles");
							}
							break;
						case "help":
							TextComponent message = new TextComponent("Particle IDs");	
							message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html#enum.constant.summary"));
							message.setColor(net.md_5.bungee.api.ChatColor.BLUE);
							commandSender.spigot().sendMessage(message);
							break;
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		return true;
	}
	
	public ArrayList<Location> getCircle(Location center, double radius, int amount){
        World world = center.getWorld();
        double increment = (2*Math.PI)/amount;
        Location tlocation = new Location(world, (int) center.getX() , (int) center.getY(), (int) center.getZ());
        ArrayList<Location> locations = new ArrayList<Location>();
        for(int i = 0;i < amount; i++){
        	double angle = i*increment;
        	int x = (int) (center.getX() + (radius * Math.cos(angle)));
        	int z = (int) (center.getZ() + (radius * Math.sin(angle)));
        	Location rlocation = new Location(world, x , center.getY(), z);
        	if (!tlocation.equals(rlocation)) {
        		locations.add(rlocation);
        	}
        	tlocation = rlocation;
        	
        }
        return locations;
    }
	

	public static void spawnParticles(ArrayList<Location> locations, CommandSender commandSender, String mparticle){
		BukkitRunnable mrunnable = new BukkitRunnable(){
			public void run() {
				try {
					locations.forEach((mlocation) -> 
						((org.bukkit.entity.Entity) (Object) commandSender).getWorld().spawnParticle(
							org.bukkit.Particle.valueOf(mparticle),
							(((Number) mlocation.getBlockX()).doubleValue() + 0.5d),
							(((Number) mlocation.getBlockY()).doubleValue() + 0.5d),
							(((Number) mlocation.getBlockZ()).doubleValue() + 0.5d),
							1,
							0d,
							0d,
							0d,
							0d,
							((java.lang.Object) null),
							false));
				} catch (Exception ex){
					ex.printStackTrace();
					cancel();
				}
			}
		};
		mtask = mrunnable.runTaskTimerAsynchronously((Plugin) Main.instance, 20l, 2l);
		mtaskId = mtask.getTaskId();
	}

	public static void procedure(String procedure, List procedureArgs) throws Exception {
	}

	public static Object function(String function, List functionArgs) throws Exception {
		return null;
	}

	public static List createList(Object obj) {
		List list = new ArrayList<>();
		if (obj.getClass().isArray()) {
			int length = java.lang.reflect.Array.getLength(obj);
			for (int i = 0; i < length; i++) {
				list.add(java.lang.reflect.Array.get(obj, i));
			}
		} else if (obj instanceof Collection<?>) {
			list.addAll((Collection<?>) obj);
		} else if (obj instanceof Iterator) {
			((Iterator<?>) obj).forEachRemaining(list::add);
		} else {
			list.add(obj);
		}
		return list;
	}

	public static void createResourceFile(String path) {
		Path file = getInstance().getDataFolder().toPath().resolve(path);
		if (Files.notExists(file)) {
			try (InputStream inputStream = Main.class.getResourceAsStream("/" + path)) {
				Files.createDirectories(file.getParent());
				Files.copy(inputStream, file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static Main getInstance() {
		return instance;
	}
}
