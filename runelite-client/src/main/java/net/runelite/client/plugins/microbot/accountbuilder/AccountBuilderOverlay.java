package net.runelite.client.plugins.microbot.accountbuilder;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.natepainthelper.PaintFormat;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.util.Objects;

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

            if (script.taskEndTime != 0)
                panelComponent.getChildren().add(LineComponent.builder()
                        .left(String.format("Time till next task: %s", PaintFormat.ft(script.taskEndTime - System.currentTimeMillis())))
                        .build());
            else if (script.task != null)
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Preparing task..")
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
}
