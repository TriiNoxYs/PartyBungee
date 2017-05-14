package fr.triinoxys.partybungee.events;

import java.io.IOException;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.score.Scoreboard;
import net.md_5.bungee.api.score.Team;
import net.md_5.bungee.event.EventHandler;
import fr.triinoxys.partybungee.Main;
import fr.triinoxys.partybungee.commands.PartyCmd;


public class ConnectAndDisconnectEvents implements Listener{
    
    private final Scoreboard sb = PartyCmd.sb;
	
    
    @EventHandler
    public void onPlayerJoinProxy(PostLoginEvent e){
      try{
          Main.updater.checkUpdate(true);
      }catch(IOException e1){
          e1.printStackTrace();
      }
        
        ProxiedPlayer player = e.getPlayer();
        
        for(Team parties : sb.getTeams()){
            if(parties.getPlayers().contains(player.getName())){
                player.setDisplayName(parties.getName());
            }
        }
        
        //TODO: DEBUG
        System.out.println("DEBUG: PLAYER_JOIN TEAMS: " + sb.getTeams().toString());
    }
    
    
	@EventHandler
	public void onServerSwitch(ServerSwitchEvent e){
		ProxiedPlayer player = e.getPlayer();
		
		Team party = sb.getTeam(player.getDisplayName());
        if(party == null) return;
		ProxiedPlayer leader = Main.proxy.getPlayer(party.getName());
        
        if(player == leader){
            for(String names : party.getPlayers()){
                ProxiedPlayer members = Main.proxy.getPlayer(names);
                if(members != leader) members.connect(leader.getServer().getInfo());
            }
        }
        
      //TODO: DEBUG
        System.out.println("DEBUG: SERV_SWITCH TEAMS: " + sb.getTeams().toString());
	}
	
	
	@EventHandler
    public void onPlayerLeaveProxy(PlayerDisconnectEvent e){
        ProxiedPlayer player = e.getPlayer();
        Team party = sb.getTeam(player.getDisplayName());
        if(party == null) return;
        
//        party.removePlayer(player.getName());
//        player.setDisplayName(player.getName());
//        
//        player.sendMessage(new ComponentBuilder("Vous avez quitt� la party.").color(ChatColor.GREEN).create());
//        for(String name : party.getPlayers()){
//            ProxiedPlayer member = Main.proxy.getPlayer(name);
//            if(member != player) member.sendMessage(new ComponentBuilder(player.getName() + " a quitt� la party.").color(ChatColor.GREEN).create());
//        }
        
        if(party.getPlayers().size() == 0){
            sb.removeTeam(party.getName());
            party = null;
        }
        
        //TODO: DEBUG
        System.out.println("DEBUG: PLAYER_LEAVE TEAMS: " + sb.getTeams().toString());
    }
	
}