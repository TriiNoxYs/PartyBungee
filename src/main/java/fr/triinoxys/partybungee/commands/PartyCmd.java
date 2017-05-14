package fr.triinoxys.partybungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.score.Scoreboard;
import net.md_5.bungee.api.score.Team;
import fr.triinoxys.partybungee.Main;


public class PartyCmd extends Command{
    
    public static Scoreboard sb;
    
    private final BaseComponent[] ERROR_USAGE = new ComponentBuilder("Usage: /party <create | invite | kick | tp | tpall>").color(ChatColor.RED).create();
    private final BaseComponent[] ERROR_NO_PARTY = new ComponentBuilder("Vous n'§tes dans aucune party !").color(ChatColor.RED).create();
    private final BaseComponent[] ERROR_YOU_ARE_LEADER = new ComponentBuilder("Vous §tes le leader de la party !").color(ChatColor.RED).create();
    private final BaseComponent[] ERROR_YOU_ARE_NOT_LEADER = new ComponentBuilder("Vous n'§tes pas le leader de la party !").color(ChatColor.RED).create();
    private final BaseComponent[] ERROR_YOU_ALREADY_PARTY = new ComponentBuilder("Vous §tes d§j§ dans une party !").color(ChatColor.RED).create();

    public PartyCmd(){
        super("party", null, "p");
        
        sb = new Scoreboard();
    }
    
    @Override
    public void execute(CommandSender sender, String[] args){
        if(sender instanceof ProxiedPlayer){
            final ProxiedPlayer player = (ProxiedPlayer) sender;
            
            // CREATE
            if(args.length > 0){
                if(args[0].equalsIgnoreCase("create")){
                    for(Team parties : sb.getTeams()){
                        if(parties.getPlayers().contains(player.getName())){
                            player.sendMessage(ERROR_YOU_ALREADY_PARTY);
                            
                            //TODO: DEBUG
                            System.out.println("DEBUG: PARTY_INVITE TEAMS: " + sb.getTeams().toString());
                            
                            return;
                        }
                    }
                    
                    final Team party = new Team(player.getDisplayName());
                    sb.addTeam(party);
                    party.addPlayer(player.getName());
                    player.setDisplayName(party.getName());
                    
                    player.sendMessage(new ComponentBuilder("Vous avez cr§§ la party " + party.getName() + ".").color(ChatColor.GOLD).create());

                    //TODO: DEBUG
                    System.out.println("DEBUG: PARTY_CREATE TEAMS: " + sb.getTeams().toString());
                }
                else{
                    
                    // INVITE - ADD
                    Team party = sb.getTeam(player.getDisplayName());
                    if(party != null){
                        if(args[0].equalsIgnoreCase("invite") || args[0].equalsIgnoreCase("add")){
                            if(args.length > 1 ){
                                final ProxiedPlayer target = Main.proxy.getPlayer(args[1]);
                                
                                if(target != null){
                                    
                                    for(Team parties : sb.getTeams()){
                                        if(parties.getPlayers().contains(target.getName())){
                                            player.sendMessage(new ComponentBuilder(target.getName() + " est d§j§ dans une party !").color(ChatColor.RED).create());
                                            
                                            //TODO: DEBUG
                                            System.out.println("DEBUG: PARTY_INVITE TEAMS: " + sb.getTeams().toString());
                                            
                                            return;
                                        }
                                    }
                                    
                                    party.addPlayer(target.getName());
                                    target.setDisplayName(party.getName());
                                    
                                    player.sendMessage(new ComponentBuilder("Vous avez ajout§ " + target.getName() + " § votre party.").color(ChatColor.GOLD).create());
                                    target.sendMessage(new ComponentBuilder("Vous avez §t§ ajout§ § la party " + party.getName() + ".").color(ChatColor.GOLD).create());
                                        
                                    //TODO: DEBUG
                                    System.out.println("DEBUG: PARTY_INVITE TEAMS: " + sb.getTeams().toString());
                                }
                                else player.sendMessage(new ComponentBuilder(args[1] + " n'est pas connct§.").color(ChatColor.RED).create());
                            }
                            else player.sendMessage(new ComponentBuilder("Usage: /party add <joueur>").color(ChatColor.RED).create());
                        }
                        
                        // KICK - REMOVE
                        else if(args[0].equalsIgnoreCase("kick") || args[0].equalsIgnoreCase("remove")){
                            if(args.length > 1 ){
                                final ProxiedPlayer target = Main.proxy.getPlayer(args[1]);
                                
                                if(target !=null){
                                    party.removePlayer(target.getName());
                                    target.setDisplayName(target.getName());
                                    
                                    player.sendMessage(new ComponentBuilder("Vous avez exclu " + target.getName() + " de votre party.").color(ChatColor.GOLD).create());
                                    target.sendMessage(new ComponentBuilder("Vous avez §t§ exclu de la party " + party.getName() + ".").color(ChatColor.GOLD).create());
                                    
                                    //TODO: DEBUG
                                    System.out.println("DEBUG: TEAMS: " + sb.getTeams().toString());
                                }
                                else player.sendMessage(new ComponentBuilder(args[1] + " n'est pas connct§.").color(ChatColor.RED).create());
                            }
                            else player.sendMessage(new ComponentBuilder("Usage: /party remove <joueur>").color(ChatColor.RED).create());
                        }
                        
                       // LEAVE
                        else if(args[0].equalsIgnoreCase("leave")){
                                final ProxiedPlayer leader = Main.proxy.getPlayer(party.getName());
                                
                                if(player != leader){
                                   party.removePlayer(player.getName());
                                   player.setDisplayName(player.getName());
                                   
                                   player.sendMessage(new ComponentBuilder("Vous avez quitt§ la party.").color(ChatColor.GREEN).create());
                                   for(String name : party.getPlayers()){
                                       ProxiedPlayer member = Main.proxy.getPlayer(name);
                                       if(member != player) member.sendMessage(new ComponentBuilder(player.getName() + " a quitt§ la party.").color(ChatColor.GREEN).create());
                                   }
                                }
                                else player.sendMessage(ERROR_YOU_ARE_LEADER);
                        }
                        
                        // DISBAND
                        else if(args[0].equalsIgnoreCase("disband")){
                                final ProxiedPlayer leader = Main.proxy.getPlayer(party.getName());
                                
                                if(player == leader){
                                    for(String names : party.getPlayers()){
                                        ProxiedPlayer members = Main.proxy.getPlayer(names);
                                        
                                        party.removePlayer(names);
                                        members.sendMessage(new ComponentBuilder(leader.getName() + " a dissous la party !").color(ChatColor.RED).create());
                                    }
                                    
                                    sb.removeTeam(party.getName());
                                    party = null;
                                    
                                    player.sendMessage(new ComponentBuilder("Vous avez dissous la party !").color(ChatColor.GREEN).create());
                                }
                                else player.sendMessage(ERROR_YOU_ARE_NOT_LEADER);
                        }
                        
                        // TP
                        else if(args[0].equalsIgnoreCase("tp")){
                                final ProxiedPlayer leader = Main.proxy.getPlayer(party.getName());
                                
                                if(player != leader) player.connect(leader.getServer().getInfo());
                                else player.sendMessage(ERROR_YOU_ARE_LEADER);
                        }
                        
                        // TPALL
                        else if(args[0].equalsIgnoreCase("tpall")){
                            final ProxiedPlayer leader = Main.proxy.getPlayer(party.getName());
                            
                            if(player == leader){
                                for(String name : party.getPlayers()){
                                    final ProxiedPlayer member = Main.proxy.getPlayer(name);
                                    if(member != leader) member.connect(leader.getServer().getInfo());
                                }
                            }
                            else player.sendMessage(new ComponentBuilder("Vous devez §tre le leader de la party !").color(ChatColor.RED).create());
                        }
                        else player.sendMessage(ERROR_USAGE);
                    }
                    else player.sendMessage(ERROR_NO_PARTY);
                }
            }
            else player.sendMessage(ERROR_USAGE);
        }
        else sender.sendMessage(new ComponentBuilder("Vous devez etre un joueur pour executer cette commande.").color(ChatColor.RED).create());
    }
}
