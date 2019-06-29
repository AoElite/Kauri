package cc.funkemunky.anticheat.api.data.logging;

import cc.funkemunky.anticheat.Kauri;
import cc.funkemunky.anticheat.api.checks.Check;
import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.database.Database;
import cc.funkemunky.api.database.DatabaseType;
import cc.funkemunky.api.utils.ConfigSetting;
import cc.funkemunky.api.utils.Init;
import cc.funkemunky.api.utils.Priority;
import lombok.Getter;
import lombok.val;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Init(priority = Priority.HIGH)
public class LoggerManager {
    @Getter
    private Map<UUID, Violation> violations = new ConcurrentHashMap<>();

    @Getter
    private Set<UUID> recentViolators = new LinkedHashSet<>();

    @ConfigSetting(path = "data.logging", name = "type")
    public String type = "FLATFILE";

    public LoggerManager() {
        Atlas.getInstance().getDatabaseManager().createDatabase("KauriLogs", DatabaseType.valueOf(type));
    }

    public void loadFromDatabase() {
        Database database = Atlas.getInstance().getDatabaseManager().getDatabase("KauriLogs");

        database.loadDatabase();

        database.getDatabaseValues().keySet().forEach(key -> {
            String[] toFormat = key.split(";");

            if (!toFormat[1].equals("banned")) {
                UUID uuid = UUID.fromString(toFormat[0]);

                int vl = (int) database.getDatabaseValues().get(key);

                Violation vls = violations.getOrDefault(uuid, new Violation());

                vls.addViolation(toFormat[1], vl);

                violations.put(uuid, vls);
            }
        });
    }

    public void saveToDatabase() {
        Database database = Atlas.getInstance().getDatabaseManager().getDatabase("KauriLogs");

        violations.keySet().forEach(key -> {
            val vls = violations.get(key);

            vls.getViolations().keySet().forEach(check -> {
                int vl = vls.getViolations().get(check);

                database.inputField(key.toString() + ";" + check, vl);
            });
        });
        database.saveDatabase();
    }

    public void addViolation(UUID uuid, Check check) {
        addAndGetViolation(uuid, check, 1);
    }

    public void addBan(UUID uuid, Check check) {
        Database database = Atlas.getInstance().getDatabaseManager().getDatabase("KauriLogs");

        database.inputField(uuid.toString() + ";banned", check.getName());
    }

    public boolean isBanned(UUID uuid) {
        Database database = Atlas.getInstance().getDatabaseManager().getDatabase("KauriLogs");
        return database.getDatabaseValues().keySet().stream().anyMatch(key -> key.equals(uuid.toString() + ";banned"));
    }

    public void removeBan(UUID uuid) {
        Database database = Atlas.getInstance().getDatabaseManager().getDatabase("KauriLogs");

        database.getDatabaseValues().remove(uuid.toString() + ";banned");

        Kauri.getInstance().getStatsManager().removeBan();
    }


    public String getBanReason(UUID uuid) {
        Database database = Atlas.getInstance().getDatabaseManager().getDatabase("KauriLogs");

        Optional<String> reasonOp = database.getDatabaseValues().keySet().stream().filter(key -> key.equals(uuid.toString() + ";banned")).findFirst();

        val key = reasonOp.orElse("none");

        if(!key.equals("none")) {
            return (String) database.getField(key);
        }
        return "none";
    }

    public int addAndGetViolation(UUID uuid, Check check) {
        return addAndGetViolation(uuid, check, 1);
    }

    public int addAndGetViolation(UUID uuid, Check check, int amount) {
        if (!violations.containsKey(uuid)) {
            violations.put(uuid, new Violation());
        }

        Violation vls = violations.get(uuid);

        vls.addViolation(check.getName(), amount);
        violations.put(uuid, vls);

        recentViolators.add(uuid);

        return vls.getViolation(check.getName());
    }

    public void clearLogs(UUID uuid) {
        Atlas.getInstance().getThreadPool().execute(() -> {
            Database database = Atlas.getInstance().getDatabaseManager().getDatabase("KauriLogs");
            database.getDatabaseValues().keySet().stream().filter(key -> key.startsWith(uuid.toString())).forEach(key -> database.getDatabaseValues().remove(key));
            database.saveDatabase();
        });
    }

    public Map<String, Integer> getViolations(UUID uuid) {
        return violations.getOrDefault(uuid, new Violation()).getViolations();
    }

    public int getViolations(Check check, UUID uuid) {
        Map<String, Integer> vls = getViolations(uuid);

        return vls.getOrDefault(check.getName(), 0);
    }
}