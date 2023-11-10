package com.holonplatform.datastore.mongo.async.test;

import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.Defaults;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.packageresolver.Command;
import de.flapdoodle.embed.process.config.RuntimeConfig;
import de.flapdoodle.embed.process.runtime.Network;

public abstract class AbstractMongoDBTest {

	protected static final Logger logger = LoggerFactory.getLogger(AbstractMongoDBTest.class);

	private static final RuntimeConfig runtimeConfig = Defaults.runtimeConfigFor(Command.MongoD, logger).build();

	private static final MongodStarter starter = MongodStarter.getInstance(runtimeConfig);

	private static MongodExecutable _mongodExe;
	private static MongodProcess _mongod;

	private static MongoClient _mongo;

	@BeforeAll
	@BeforeClass
	public static void setUp() throws Exception {

		_mongodExe = starter.prepare(MongodConfig.builder().version(Version.Main.V4_4)
				.net(new Net("localhost", 12345, Network.localhostIsIPv6())).build());
		_mongod = _mongodExe.start();

		_mongo = MongoClients.create(MongoClientSettings.builder()
				.applyToClusterSettings(builder -> builder.hosts(Arrays.asList(new ServerAddress("localhost", 12345))))
				.build());
	}

	@AfterAll
	@AfterClass
	public static void tearDown() throws Exception {
		_mongod.stop();
		_mongodExe.stop();
	}

	protected static MongoClient getMongo() {
		return _mongo;
	}

}