package edu.kit.ipd.sdq.modsim.descomp.commands.database;

import edu.kit.ipd.sdq.modsim.descomp.services.SimulatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@ShellCommandGroup("database - general")
public class GeneralDatabaseCommands {
    @Autowired
    private SimulatorRepository repository;

    @ShellMethod("Clear Database")
    public void cleanAllDatabase() {
        repository.cleanAll();
    }
}
