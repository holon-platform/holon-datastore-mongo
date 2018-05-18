package com.holonplatform.datastore.mongo.sync.test;

import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.runtime.Network;

public abstract class AbstractMongoDBTest {

	protected static final Logger logger = LoggerFactory.getLogger(AbstractMongoDBTest.class);

	private static final IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
			.defaultsWithLogger(Command.MongoD, logger)
			//.daemonProcess(false)
			.build();

	private static final MongodStarter starter = MongodStarter.getInstance(runtimeConfig);

	private static MongodExecutable _mongodExe;
	private static MongodProcess _mongod;

	private static MongoClient _mongo;

	@BeforeClass
	public static void setUp() throws Exception {

		_mongodExe = starter.prepare(new MongodConfigBuilder().version(Version.Main.PRODUCTION)
				.net(new Net("localhost", 12345, Network.localhostIsIPv6())).build());
		_mongod = _mongodExe.start();

		_mongo = MongoClients.create(MongoClientSettings.builder()
				.applyToClusterSettings(builder -> builder.hosts(Arrays.asList(new ServerAddress("localhost", 12345))))
				.build());
	}

	@AfterClass
	public static void tearDown() throws Exception {
		_mongod.stop();
		_mongodExe.stop();
	}

	protected static MongoClient getMongo() {
		return _mongo;
	}

}