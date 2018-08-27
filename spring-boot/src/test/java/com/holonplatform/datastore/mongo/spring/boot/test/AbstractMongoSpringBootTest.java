package com.holonplatform.datastore.mongo.spring.boot.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public abstract class AbstractMongoSpringBootTest {

	protected static final Logger logger = LoggerFactory.getLogger(AbstractMongoSpringBootTest.class);

	private static final IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
			.defaultsWithLogger(Command.MongoD, logger).build();

	private static final MongodStarter starter = MongodStarter.getInstance(runtimeConfig);

	private static MongodExecutable _mongodExe;
	private static MongodProcess _mongod;

	@BeforeClass
	public static void setUp() throws Exception {

		_mongodExe = starter.prepare(new MongodConfigBuilder().version(Version.Main.PRODUCTION)
				.net(new Net("localhost", 12345, Network.localhostIsIPv6())).build());
		_mongod = _mongodExe.start();
	}

	@AfterClass
	public static void tearDown() throws Exception {
		_mongod.stop();
		_mongodExe.stop();
	}

}