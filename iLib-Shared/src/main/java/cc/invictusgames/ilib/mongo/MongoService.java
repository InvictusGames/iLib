package cc.invictusgames.ilib.mongo;

import cc.invictusgames.ilib.configuration.defaults.MongoConfig;
import cc.invictusgames.ilib.utils.Timings;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 04.03.2020 / 18:31
 * iLib / cc.invictusgames.ilib.mongo
 */

public class MongoService {

    public static final ReplaceOptions REPLACE_OPTIONS = new ReplaceOptions().upsert(true);

    @Getter private static List<MongoService> services = new ArrayList<>();
    @Getter private static long lastExecution = -1;
    @Getter private static long lastError = -1;
    @Getter private static boolean down = false;
    @Getter private static long lastLatency = -1;
    @Getter private static long latencyTicks = 0;
    @Getter private static long averageLatency = -1;

    @Getter private MongoClient client;
    @Getter private MongoDatabase database;

    private final MongoConfig config;
    private final String databaseName;

    public MongoService(MongoConfig config, String databaseName) {
        this.config = config;
        this.databaseName = databaseName;
        services.add(this);
    }

    public boolean connect() {
        if (config.isAuthEnabled()) {
            MongoCredential credential = MongoCredential.createCredential(
                    config.getAuthUsername(),
                    config.getAuthDatabase(),
                    config.getAuthPassword().toCharArray()
            );
            client = new MongoClient(new ServerAddress(config.getHost(), config.getPort()),
                    Collections.singletonList(credential));
        } else {
            client = new MongoClient(config.getHost(), config.getPort());
        }
        try {
            database = client.getDatabase(this.databaseName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public <T> T executeCommand(MongoCommand<T> command) {
        lastExecution = System.currentTimeMillis();
        Timings timings = new Timings("mongo-command").startTimings();
        try {
            down = false;
            T t = command.execute(this.client, this.database);
            timings.stopTimings();
            lastLatency = timings.calculateDifference();
            averageLatency += timings.calculateDifference();
            ++latencyTicks;
            return t;
        } catch (Exception e) {
            e.printStackTrace();
            down = true;
            lastError = System.currentTimeMillis();
            timings.stopTimings();
            return null;
        }
    }

    public MongoCollection<Document> getCollection(String name) {
        return this.executeCommand((client, database) -> database.getCollection(name));
    }

    public static long getAverageLatency() {
        if (latencyTicks == 0) {
            return -1;
        }
        return averageLatency / latencyTicks;
    }

}
