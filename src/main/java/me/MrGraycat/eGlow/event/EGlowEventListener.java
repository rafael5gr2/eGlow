package me.MrGraycat.eGlow.event;

import me.MrGraycat.eGlow.manager.DataManager;
import me.MrGraycat.eGlow.util.GlowPlayerUtil;
import me.MrGraycat.eGlow.config.EGlowMessageConfig.Message;
import me.MrGraycat.eGlow.EGlow;
import me.MrGraycat.eGlow.menu.Menu;
import me.MrGraycat.eGlow.manager.glow.IEGlowPlayer;
import me.MrGraycat.eGlow.util.Common.GlowDisableReason;
import me.MrGraycat.eGlow.util.packet.ProtocolVersion;
import me.MrGraycat.eGlow.util.chat.ChatUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;

public class EGlowEventListener implements Listener {

	public EGlowEventListener(EGlow instance) {
		if (ProtocolVersion.SERVER_VERSION.getMinorVersion() >= 13) {
			instance.registerEvents(new EGlowEventListener113AndAbove(instance));
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		GlowPlayerUtil.handlePlayerJoin(event.getPlayer());
	}

	@EventHandler
	public void onPlayerLick(PlayerKickEvent event) {
		GlowPlayerUtil.handlePlayerDisconnect(event.getPlayer(), false);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		GlowPlayerUtil.handlePlayerDisconnect(event.getPlayer(), false);
	}

	@EventHandler
	public void onMenuClick(InventoryClickEvent event) {
		InventoryHolder holder = event.getInventory().getHolder();

		if (holder == null) {
			return;
		}

		if (holder instanceof Menu) {
			event.setCancelled(true);

			if (event.getView().getBottomInventory().equals(event.getClickedInventory()) || event.getCurrentItem() == null) {
				return;
			}

			((Menu) holder).handleMenu(event);
		}
	}

	@EventHandler
	public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
		Player p = event.getPlayer();
		IEGlowPlayer glowPlayer = DataManager.getEGlowPlayer(p);

		if (glowPlayer != null) {
			if (glowPlayer.isInBlockedWorld()) {
				if (glowPlayer.isGlowing()) {
					glowPlayer.disableGlow(false);
					glowPlayer.setGlowDisableReason(GlowDisableReason.BLOCKEDWORLD, false);

					ChatUtil.sendMessage(p, Message.WORLD_BLOCKED.get(), true);
				}
			} else {
				if (glowPlayer.getGlowDisableReason().equals(GlowDisableReason.BLOCKEDWORLD)) {
					if (glowPlayer.setGlowDisableReason(GlowDisableReason.NONE, false)) {
						glowPlayer.activateGlow();
						ChatUtil.sendMessage(p, Message.WORLD_ALLOWED.get(), true);
					}
				}
			}
		}
	}

}