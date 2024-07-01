package net.runelite.client.plugins.microbot.accountbuilder.Panel;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.accountbuilder.AccountBuilderPlugin;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
public class AccountBuilderPanel extends PluginPanel {
    private AccountBuilderPlugin accountBuilderPlugin;

    public AccountBuilderPanel(AccountBuilderPlugin accountBuilderPlugin){
        super(false);

        this.accountBuilderPlugin = accountBuilderPlugin;

        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        titlePanel.setLayout(new BorderLayout());

        JLabel title = new JLabel();
        title.setText("Basche's account builder");
        title.setForeground(Color.WHITE);
        titlePanel.add(title, BorderLayout.WEST);

        var tasks = accountBuilderPlugin.getScript().getTaskMap().keySet().stream()
                .sorted(Comparator.comparing(AccountBuilderTask::getName))
                .collect(Collectors.toList());
        var taskList = new JList<>(tasks.stream().map(AccountBuilderTask::getName).toArray(String[]::new));
        taskList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2){
                    var index = taskList.locationToIndex(e.getPoint());
                    accountBuilderPlugin.getScript().setNextTask(tasks.get(index));
                }
            }
        });

        var scrollableContainer = new JScrollPane(taskList);
        scrollableContainer.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(titlePanel, BorderLayout.NORTH);
        add(scrollableContainer, BorderLayout.CENTER);
    }
}
