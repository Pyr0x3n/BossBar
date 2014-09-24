package com.pyr0x3n.bossbar;

import java.util.ArrayList;
import java.util.List;

import me.libraryaddict.Hungergames.Events.FeastAnnouncedEvent;
import me.libraryaddict.Hungergames.Events.FeastSpawnedEvent;
import me.libraryaddict.Hungergames.Events.GameStartEvent;
import me.libraryaddict.Hungergames.Events.InvincibilityWearOffEvent;
import me.libraryaddict.Hungergames.Events.PlayerWinEvent;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.boss.BossBarApi;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin implements Listener{

	private int i = 0;
	List<String> messages = new ArrayList<String>();

	@Override
	public void onEnable(){
		saveDefaultConfig();	
		getServer().getPluginManager().registerEvents(this, this);
		messages =  getConfig().getStringList("messages");
	}


	@Override
	public void onDisable() {
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		if(!this.getConfig().getBoolean("spectatorsSeeMessages"))
			if (HungergamesApi.getHungergames().currentTime >= 0) return;
		final Player player = e.getPlayer();
		if(this.getConfig().getBoolean("motd")){
			String message = ChatColor.translateAlternateColorCodes('&', 
					this.getConfig().getString("motdMessage").replaceAll("%player%",player.getName()));
			BossBarApi.setName(player, message, 100.0F);
		}

		int interval = getConfig().getInt("interval") * 20;
		final float barSize = 100F; // / messages.size();
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){

			@Override
			public void run() {
				BossBarApi.setName(player, ChatColor.translateAlternateColorCodes('&', messages.get(i)),  barSize);
				i++;
				if (i == messages.size()) i=0;
			}
		}, 60, interval);
	}

	
	public void stopAnnoucer(){
		this.getServer().getScheduler().cancelTasks(this);
		for (Player player : this.getServer().getOnlinePlayers())
			BossBarApi.removeBar(player);
	}

	@EventHandler
	public void onInvincibilityWearOffEvent(InvincibilityWearOffEvent e){
		if(this.getConfig().getBoolean("invincibilityWearOffEvent")) stopAnnoucer();
	}
	
	@EventHandler
	public void onGameStartEvent(GameStartEvent e){
		if(this.getConfig().getBoolean("gameStartEvent")) stopAnnoucer();
	}
	
	@EventHandler
	public void onFeastSpawnedEvent(FeastSpawnedEvent e){
		if(this.getConfig().getBoolean("feastSpawnedEvent")) stopAnnoucer();
	}
	
	@EventHandler
	public void onFeastAnnouncedEvent(FeastAnnouncedEvent e){
		if(this.getConfig().getBoolean("feastAnnouncedEvent")) stopAnnoucer();
	}	
	
	@EventHandler
	public void onPlayerWinEvent(PlayerWinEvent e){
		if(this.getConfig().getBoolean("playerWinEvent")) stopAnnoucer();
	}
}
