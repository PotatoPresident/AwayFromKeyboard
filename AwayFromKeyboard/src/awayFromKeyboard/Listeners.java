package awayFromKeyboard;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class Listeners implements Listener {
	private AwayFromKeyboard afk;

	public Listeners(AwayFromKeyboard plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.afk = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();

		int taskID = Bukkit.getScheduler().runTaskTimer(afk, new Runnable() {

			@Override
			public void run() {
				if (afk.timeWentAFK.get(uuid) == null) {
					afk.timeWentAFK.put(uuid, System.currentTimeMillis());
				}

				long timeAFK = System.currentTimeMillis() - afk.timeWentAFK.get(uuid);
				if (timeAFK > afk.getConfig().getInt("afkTime") * 1000 * 60) {
					if (!afk.playerIsAFK(player)) {
						afk.setAFK(player);
					}
				}

				// Player left, so stop monitoring them
				if (!player.isOnline()) {
					int id = afk.runnableMap.get(player.getUniqueId());
					Bukkit.getScheduler().cancelTask(id);
					afk.removeAFK(player);
				}
			}
		}, 20, 20).getTaskId(); // 1 second delay, 1 second period

		afk.runnableMap.put(uuid, taskID);
	}

	@EventHandler
	public void onPlayerMoveAFK(PlayerMoveEvent e) {
		Bukkit.getScheduler().runTaskAsynchronously(afk, new Runnable() {

			@Override
			public void run() {
				Player player = e.getPlayer();
				UUID uuid = player.getUniqueId();

				/* if player afk, and not within a buffer period (aka, they just typed /afk) */
				if (afk.playerIsAFK(player) && !afk.inBufferPeriod.get(uuid)) {
					afk.removeAFK(player);
				}

				afk.afkMap.put(uuid, false);
				afk.timeWentAFK.put(uuid, System.currentTimeMillis());
			}
		});

	}

	@EventHandler
	public void onPlayerChatAFK(AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();
		if (afk.playerIsAFK(player)) {
			afk.removeAFK(player);
		}
		afk.afkMap.put(player.getUniqueId(), false);
		afk.timeWentAFK.put(player.getUniqueId(), System.currentTimeMillis());
	}

	@EventHandler
	public void onPlayerCommandAFK(PlayerCommandPreprocessEvent e) {
		Player player = e.getPlayer();
		if (afk.playerIsAFK(player)) {
			afk.removeAFK(player);
		}
		afk.afkMap.put(player.getUniqueId(), false);
		afk.timeWentAFK.put(player.getUniqueId(), System.currentTimeMillis());
	}
}