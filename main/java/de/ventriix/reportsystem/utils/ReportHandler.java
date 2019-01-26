package de.ventriix.reportsystem.utils;

import de.ventriix.reportsystem.core.ReportSystem;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * (c) Ventriix 2019, All rights reserved.
 * Contact E-Mail: ventriix@variaty.eu
 **/
public class ReportHandler {

    private List<Report> allReports = new ArrayList<>();

    public class Report {
        private ProxiedPlayer sender, target;
        private String id, reason;
        private long timeCreated;

        public Report(String id, ProxiedPlayer sender, ProxiedPlayer target, String reason) {
            this.id = id;
            this.sender = sender;
            this.target = target;
            this.reason = reason;
            this.timeCreated = System.currentTimeMillis();
        }

        public String getId() {
            return id;
        }

        public String getReason() {
            return reason;
        }

        public ProxiedPlayer getSender() {
            return sender;
        }

        public ProxiedPlayer getTarget() {
            return target;
        }

        public long getTimeCreated() {
            return timeCreated;
        }
    }

    public void schedule() {
        ProxyServer.getInstance().getScheduler().schedule(ReportSystem.getInstance(), () -> {
            if(!allReports.isEmpty()) {
                for (Report report : allReports) {
                    long reportCreated = report.getTimeCreated();
                    long currentMillis = System.currentTimeMillis();

                    if ((currentMillis - reportCreated) > (120 * 1000)) {
                        allReports.remove(report);
                    }
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public Report createReport(ProxiedPlayer sender, ProxiedPlayer targetPlayer, String reason) {
        Report report = new Report(UUID.randomUUID().toString().substring(0, 8).replace("-", "").toUpperCase(), sender, targetPlayer, reason);
        allReports.add(report);
        return report;
    }

    public void notifyStaff(Report report) {
        for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
            if(all.hasPermission("reportsystem.notify")) {
                all.sendMessage(new TextComponent(ReportSystem.getPrefix() + "§c{targetDisplayName} §7» §c{reportReason}"
                .replace("{targetDisplayName}", report.getTarget().getDisplayName())
                .replace("{reportReason}", report.getReason())));

                TextComponent moreInfos = new TextComponent();
                moreInfos.setText("§a§l[ANNEHMEN]");
                moreInfos.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report accept {reportId}".replace("{reportId}", report.getId())));
                moreInfos.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aKlicke hier um den Report zu bearbeiten").bold(true).create()));

                all.sendMessage(moreInfos);
            }
        }
    }

    public Report getReportById(String id) {
        if(!allReports.isEmpty()) {
            for (Report report : allReports) {
                if(report.getId().equals(id)) {
                    return report;
                }
            }
        }

        return null;
    }

    public Report getReport(ProxiedPlayer player) {
        if(!allReports.isEmpty()) {
            for (Report report : allReports) {
                if(report.getTarget().equals(player) || report.getSender().equals(player)) {
                    return report;
                }
            }
        }

        return null;
    }

    public void deleteReport(Report report) {
        allReports.remove(report);
    }

    public boolean isReported(ProxiedPlayer targetPlayer) {
        if(!allReports.isEmpty()) {
            for (Report report : allReports) {
                if(report.getTarget().equals(targetPlayer)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isReportSender(ProxiedPlayer senderPlayer) {
        if(!allReports.isEmpty()) {
            for (Report report : allReports) {
                if(report.getSender().equals(senderPlayer)) {
                    return true;
                }
            }
        }

        return false;
    }
}
