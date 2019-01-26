package de.ventriix.reportsystem.listener;

import de.ventriix.reportsystem.core.ReportSystem;
import de.ventriix.reportsystem.utils.ReportHandler;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * (c) Ventriix 2019, All rights reserved.
 * Contact E-Mail: ventriix@variaty.eu
 **/
public class LeaveListener implements Listener {

    @EventHandler
    public void onLeave(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if(ReportSystem.getInstance().getReportHandler().isReported(player) || ReportSystem.getInstance().getReportHandler().isReportSender(player)) {
            ReportHandler.Report report = ReportSystem.getInstance().getReportHandler().getReport(player);
            ReportSystem.getInstance().getReportHandler().deleteReport(report);
        }
    }
}
