# PSP · Liebre y Tortuga

Reimplementación moderna de la práctica de concurrencia **LiebreTortuga** conservada en el material de PSP de 2018.

La versión original empleaba dos subclases de `Thread`, estado global estático y actualizaciones de Swing fuera del EDT. Esta versión separa el motor de carrera de la interfaz, usa un `ExecutorService`, estado atómico y publica los cambios visuales mediante `SwingUtilities.invokeLater`.

## Ejecutar

```bash
mvn verify
mvn -q -DskipTests package
java -cp target/classes dev.rodrigosambade.race.RaceApp
```

Java 21.
