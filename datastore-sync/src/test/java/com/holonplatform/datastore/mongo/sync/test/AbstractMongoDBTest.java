package com.holonplatform.datastore.mongo.sync.test;

import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.TransitionWalker;
import de.flapdoodle.reverse.transitions.Start;

public abstract class AbstractMongoDBTest extends AbstractMongoDatastoreTest {

	protected static final Logger logger = LoggerFactory.getLogger(AbstractMongoDBTest.class);

	private static TransitionWalker.ReachedState<RunningMongodProcess> _running;
	private static MongoClient _mongo;

	@BeforeAll
	@BeforeClass
	public static void setUp() throws Exception {

		Mongod mongodConfig = Mongod.builder().net(Start.to(Net.class).initializedWith(Net.defaults().withPort(12345)))
				.build();

		_running = mongodConfig.start(Version.Main.V8_0);

		_mongo = MongoClients.create(MongoClientSettings.builder()
				.applyToClusterSettings(builder -> builder.hosts(Arrays.asList(new ServerAddress("localhost", 12345))))
				.build());
	}

	@AfterAll
	@AfterClass
	public static void tearDown() throws Exception {
		_running.close();
	}

	protected static MongoClient getMongo() {
		return _mongo;
	}

}