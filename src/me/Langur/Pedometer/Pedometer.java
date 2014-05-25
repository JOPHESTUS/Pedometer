package me.Langur.Pedometer;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class Pedometer extends JavaPlugin implements Listener {
	
	private Scoreboard board;
	private Objective o;
	private HashMap<OfflinePlayer, Score> scores = new HashMap<OfflinePlayer, Score>();
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		
		p.setScoreboard(board);
		
		if(scores.get(p) == null) {
			scores.put(p, o.getScore(Bukkit.getServer().getOfflinePlayer(ChatColor.GREEN + "Steps:")));
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if(e.getFrom().getBlockX() == e.getTo().getBlockX() 
				&&
				e.getFrom().getBlockY() == e.getTo().getBlockY() 
				&&
				e.getFrom().getBlockZ() == e.getTo().getBlockZ()
				) return;
		
		scores.get(e.getPlayer()).setScore(scores.get(e.getPlayer()).getScore() + 1);
	}
	
	public void onEnable() {
		board = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
		
		o = board.registerNewObjective("pedometer", "dummy");
		o.setDisplayName(ChatColor.GOLD + "Pedometer");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		
		saveDefaultConfig();
		
		List<String> s = getConfig().getStringList("steps");
		
		for(String str : s) {
			String[] words = str.split(":");
			scores.put(Bukkit.getServer().getOfflinePlayer(words[0]), o.getScore(Bukkit.getServer().getOfflinePlayer(ChatColor.GREEN + "Steps:")));
			scores.get(Bukkit.getServer().getOfflinePlayer(words[0])).setScore(Integer.parseInt(words[1]));
		}
	}
	
	public void onDisable()  {
		List<String> s = getConfig().getStringList("steps");
		
		for(OfflinePlayer p : scores.keySet()) {
			s.add(p.getName() + ": " + scores.get(p).getScore());
		}
		
		getConfig().set("scores", s);
		saveConfig();
	}
}
