package club.revived.commons.velocity.motd;

import org.checkerframework.checker.units.qual.t;

import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.ProxyServer;

import club.revived.commons.distribution.game.PlayerManager;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class MotdManager {

  private final MiniMessage miniMessage = MiniMessage.miniMessage();

  private static MotdManager instance;
  private final ProxyServer server;

  private String motd;

  public MotdManager(final ProxyServer proxyServer) {
    this.server = proxyServer;
    instance = this;

    this.initListeners();
    this.initFetchTask();
  }

  private void initFetchTask() {
    final var task = new MotdFetchTask();

    task.start();
  }

  private void initListeners() {
    this.server.getEventManager().register(this.server, ProxyPingEvent.class, event -> {
      final var builder = event.getPing().asBuilder();
      final var playerCount = PlayerManager.getInstance().getOnlinePlayers()
          .size();

      builder.maximumPlayers(playerCount + 1);
      builder.onlinePlayers(playerCount);

      builder.description(miniMessage.deserialize(this.motd));

      event.setPing(builder.build());
    });
  }

  public String getMotd() {
    return motd;
  }

  public void setMotd(String motd) {
    this.motd = motd;
  }

  public static MotdManager getInstance() {
    if (instance == null) {
      throw new IllegalStateException("MotdManager is not initiated");
    }

    return instance;
  }
}
