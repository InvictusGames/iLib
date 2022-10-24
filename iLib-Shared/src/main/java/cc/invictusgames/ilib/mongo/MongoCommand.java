package cc.invictusgames.ilib.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 04.03.2020 / 18:46
 * iLib / cc.invictusgames.ilib.mongo
 */

public interface MongoCommand<T> {

    public T execute(MongoClient client, MongoDatabase database);
}
