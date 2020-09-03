package com.alyxferrari.afkmanager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.ArrayList;
@Mod(AfkManagerMod.MODID)
public class AfkManagerMod {
	public static final String MODID = "afkmanager";
	public static final Logger logger = LogManager.getLogger(MODID);
	public static final int afkDefinition = 2400;
	public final ArrayList<PlayerEntity> players;
	public final ArrayList<CustomVector3> previous;
	public final ArrayList<Integer> ticksAfk;
	public AfkManagerMod() {
		players = new ArrayList<PlayerEntity>();
		previous = new ArrayList<CustomVector3>();
		ticksAfk = new ArrayList<Integer>();
		MinecraftForge.EVENT_BUS.addListener(this::onPlayerConnect);
		MinecraftForge.EVENT_BUS.addListener(this::onServerTick);
		MinecraftForge.EVENT_BUS.addListener(this::onPlayerDisconnect);
		MinecraftForge.EVENT_BUS.addListener(this::onChat);
	}
	public void onPlayerConnect(PlayerLoggedInEvent event) {
		players.add(event.getPlayer());
		previous.add(new CustomVector3(event.getPlayer().lastTickPosX, event.getPlayer().lastTickPosY, event.getPlayer().lastTickPosZ));
		ticksAfk.add(0);
	}
	public void onPlayerDisconnect(PlayerLoggedOutEvent event) {
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).getEntityId() == event.getPlayer().getEntityId()) {
				players.remove(i);
				previous.remove(i);
				ticksAfk.remove(i);
				return;
			}
		}
		logger.warn("Non-fatal error: Player disconnected but no corresponding entry in the player array was found.");
	}
	public void onChat(ServerChatEvent event) {
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).getEntityId() == event.getPlayer().getEntityId()) {
				if (ticksAfk.get(i) > afkDefinition) {
					for (int y = 0; y < players.size(); y++) {
						players.get(y).sendMessage(new StringTextComponent("\u00A77* \u00A73" + players.get(y).getName().getString() + "\u00A77 is no longer AFK."), players.get(y).getUniqueID());
					}
				}
				ticksAfk.set(i, 0);
				return;
			}
		}
		logger.warn("Non-fatal error: Player sent a chat message but no corresponding entry in the player array was found.");
	}
	public void onServerTick(ServerTickEvent event) {
		for (int x = 0; x < players.size(); x++) {
			CustomVector3 pos = previous.get(x);
			PlayerEntity player = players.get(x);
			if (pos.x == player.lastTickPosX && pos.y == player.lastTickPosY && pos.z == player.lastTickPosZ) {
				ticksAfk.set(x, ticksAfk.get(x) + 1);
			} else {
				if (ticksAfk.get(x) > afkDefinition) {
					for (int y = 0; y < players.size(); y++) {
						players.get(y).sendMessage(new StringTextComponent("\u00A77* \u00A73" + players.get(y).getName().getString() + "\u00A77 is no longer AFK."), players.get(y).getUniqueID());
					}
				}
				ticksAfk.set(x, 0);
			}
			if (ticksAfk.get(x) == afkDefinition) {
				for (int y = 0; y < players.size(); y++) {
					players.get(y).sendMessage(new StringTextComponent("\u00A77* \u00A73" + players.get(y).getName().getString() + "\u00A77 is now AFK."), players.get(y).getUniqueID());
				}
			}
			previous.set(x, new CustomVector3(player.lastTickPosX, player.lastTickPosY, player.lastTickPosZ));
			logger.info(previous.get(0).x + ", " + previous.get(0).y + ", " + previous.get(0).z);
		}
	}
}