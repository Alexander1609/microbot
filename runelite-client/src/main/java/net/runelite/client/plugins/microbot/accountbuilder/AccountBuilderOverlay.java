package net.runelite.client.plugins.microbot.accountbuilder;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.quests.AccountBuilderQuestTask;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AccountBuilderOverlay extends OverlayPanel {
    @Inject
    AccountBuilderOverlay(AccountBuilderPlugin plugin)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
        setPriority(OverlayPriority.HIGHEST);
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            var script = ((AccountBuilderPlugin) Objects.requireNonNull(getPlugin())).script;

            panelComponent.setPreferredSize(new Dimension(400, 300));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("AIO Account builder " + AccountBuilderScript.version)
                    .color(Color.ORANGE)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder().build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left(String.format("Current task: %s",  script.task != null ? script.task.getName() : "No task available"))
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left(String.format("Next task: %s", script.nextTask != null ? script.nextTask.getName() : "No next task available"))
                    .build());

            if (script.breakEndTime > System.currentTimeMillis())
                panelComponent.getChildren().add(LineComponent.builder()
                        .left(String.format("Breaking for: %s", getTimeSting(script.breakEndTime - System.currentTimeMillis())))
                        .build());
            else if (script.taskEndTime != 0)
                panelComponent.getChildren().add(LineComponent.builder()
                        .left(String.format("Time till next task: %s", getTimeSting(script.taskEndTime - System.currentTimeMillis())))
                        .build());
            else if (script.task != null && !(script.task instanceof AccountBuilderQuestTask))
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Preparing task..")
                        .build());

            if (script.nextBreakTime > System.currentTimeMillis())
                panelComponent.getChildren().add(LineComponent.builder()
                        .left(String.format("Next break in: %s", getTimeSting(script.nextBreakTime - System.currentTimeMillis())))
                        .build());

            panelComponent.getChildren().add(LineComponent.builder().build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left(Microbot.status)
                    .build());


        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }

    public static String getTimeSting(long duration) {
        long days = TimeUnit.MILLISECONDS.toDays(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60;

        if (days == 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d:%02d:%02d", days, hours, minutes, seconds);
        }
    }
}
