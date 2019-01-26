package de.ventriix.reportsystem.commands;

import de.ventriix.reportsystem.core.ReportSystem;
import de.ventriix.reportsystem.utils.ReportHandler;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * (c) Ventriix 2019, All rights reserved.
 * Contact E-Mail: ventriix@variaty.eu
 **/
public class Report_CMD extends Command {

    private List<ProxiedPlayer> cooldownList = new ArrayList<>();
    private List<String> reportReasons = Arrays.asList("Cheating", "Beleidigung", "Werbung", "Teaming", "Trolling", "Name", "Skin", "Bugusing");

    public Report_CMD() {
        super("report");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if(commandSender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) commandSender;

            if(args.length != 0) {
                if (!args[0].equalsIgnoreCase("accept")) {
                    if (args.length > 2 || args.length == 1) {
                        player.sendMessage(new TextComponent(ReportSystem.getPrefix() + "Mögliche Report-Gründe: §cCheating§7, §cBeleidigung§7, §cWerbung§7, §cTeaming§7, §cTrolling§7, §cName§7, §cSkin §7und §cBugusing"));
                        player.sendMessage(new TextComponent(ReportSystem.getPrefix() + "Syntax§8: §c/report <Spieler> <Grund>"));
                    } else {
                        if (ReportSystem.getInstance().hasPermission(player, "reportsystem.report.send")) {
                            if (!cooldownList.contains(player)) {
                                boolean reasonVerified = false;
                                String reasonFormatted = "";

                                for (String reason : reportReasons) {
                                    if (args[1].equalsIgnoreCase(reason)) {
                                        reasonVerified = true;
                                        reasonFormatted = reason;
                                        break;
                                    }
                                }

                                if (reasonVerified) {
                                    ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(args[0]);

                                    if (targetPlayer != null && !targetPlayer.hasPermission("reportsystem.report.bypass")) {
                                        if (!ReportSystem.getInstance().getReportHandler().isReported(targetPlayer)) {
                                            ReportHandler.Report report = ReportSystem.getInstance().getReportHandler().createReport(player, targetPlayer, reasonFormatted);
                                            ReportSystem.getInstance().getReportHandler().notifyStaff(report);

                                            cooldownList.add(player);
                                            ProxyServer.getInstance().getScheduler().schedule(ReportSystem.getInstance(), () -> {
                                                cooldownList.remove(player);
                                            }, 30, TimeUnit.SECONDS);

                                            player.sendMessage(new TextComponent(ReportSystem.getPrefix() + "§aVielen Dank! §7Dein Report wurde §aerfolgreich §7abgeschickt.\n\n§7ID§8: §c{reportId}\n§7Grund§8: §c{reportReason}\n§7Beschuldigter§8: §c{reportTargetDisplayName}"
                                                    .replace("{reportId}", report.getId())
                                                    .replace("{reportReason}", report.getReason())
                                                    .replace("{reportTargetDisplayName}", report.getTarget().getDisplayName())));
                                        } else {
                                            player.sendMessage(new TextComponent(ReportSystem.getPrefix() + "Dieser Spieler wurde bereits §czu oft §7reportet."));
                                        }
                                    } else {
                                        player.sendMessage(new TextComponent(ReportSystem.getPrefix() + "§cDieser Spieler ist entweder nicht online oder darf §cnicht reportet werden."));
                                    }
                                } else {
                                    player.sendMessage(new TextComponent(ReportSystem.getPrefix() + "Mögliche Report-Gründe: §cCheating§7, §cBeleidigung§7, §cWerbung§7, §cTeaming§7, §cTrolling§7, §cName§7, §cSkin §7und §cBugusing"));
                                }
                            } else {
                                player.sendMessage(new TextComponent(ReportSystem.getPrefix() + "§cBitte warte noch kurz."));
                            }
                        }
                    }
                } else if(args[0].equalsIgnoreCase("accept")){
                    if (ReportSystem.getInstance().hasPermission(player, "reportsystem.report.accept")) {
                        ReportHandler.Report report = ReportSystem.getInstance().getReportHandler().getReportById(args[1]);

                        if(report != null) {
                            ProxiedPlayer targetPlayer = report.getTarget();
                            player.connect(targetPlayer.getServer().getInfo());
                            player.sendMessage(new TextComponent(ReportSystem.getPrefix() + "Du wurdest mit dem Server §c{serverName} §7verbunden.".replace("{serverName}", targetPlayer.getServer().getInfo().getName()) +
                                    "\n§7Report-Grund§8: §c{reportReason}".replace("{reportReason}", report.getReason()) +
                                    "\n§7Beschuldigter§8: §c{reportTargetDisplayName}".replace("{reportTargetDisplayName}", report.getTarget().getDisplayName()) +
                                    "\n§7Reporter§8: §c{reporterDisplayName}".replace("{reporterDisplayName}", report.getSender().getDisplayName()) +
                                    "\n§7Report-ID§8: §c{reportId}".replace("{reportId}", report.getId())));

                            ReportSystem.getInstance().getReportHandler().deleteReport(report);
                        } else {
                            player.sendMessage(new TextComponent(ReportSystem.getPrefix() + "§cDieser Report ist nicht mehr gültig."));
                        }
                    }
                }
            } else {
                player.sendMessage(new TextComponent(ReportSystem.getPrefix() + "Mögliche Report-Gründe: §cCheating§7, §cBeleidigung§7, §cWerbung§7, §cTeaming§7, §cTrolling§7, §cName§7, §cSkin §7und §cBugusing"));
                player.sendMessage(new TextComponent(ReportSystem.getPrefix() + "Syntax§8: §c/report <Spieler> <Grund>"));
            }
        } else {
            commandSender.sendMessage(new TextComponent(ReportSystem.getPrefix() + "§fFür diese Aktion §cmusst §fdu ein Spieler sein!"));
        }
    }
}
