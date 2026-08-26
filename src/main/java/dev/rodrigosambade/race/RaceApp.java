package dev.rodrigosambade.race;
import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.util.random.RandomGenerator;
public final class RaceApp {
 public static void main(String[] args){SwingUtilities.invokeLater(RaceApp::show);} private static void show(){var frame=new JFrame("Liebre y tortuga");var hare=new JProgressBar(0,100);var tortoise=new JProgressBar(0,100);hare.setStringPainted(true);tortoise.setStringPainted(true);var status=new JLabel("Preparados…",SwingConstants.CENTER);var start=new JButton("Iniciar carrera");var panel=new JPanel(new GridLayout(0,1,8,8));panel.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));panel.add(new JLabel("Liebre"));panel.add(hare);panel.add(new JLabel("Tortuga"));panel.add(tortoise);panel.add(status);panel.add(start);frame.setContentPane(panel);frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);frame.setSize(520,260);frame.setLocationRelativeTo(null);frame.setVisible(true);start.addActionListener(e->{start.setEnabled(false);RaceEngine engine=new RaceEngine(100,Duration.ofMillis(100),RandomGenerator.getDefault());engine.start(snapshot->SwingUtilities.invokeLater(()->{hare.setValue(snapshot.hare());tortoise.setValue(snapshot.tortoise());status.setText(snapshot.winner()==null?"Corriendo…":"Ganador: "+snapshot.winner());})).whenComplete((winner,error)->{engine.close();SwingUtilities.invokeLater(()->start.setEnabled(true));});});}
}
