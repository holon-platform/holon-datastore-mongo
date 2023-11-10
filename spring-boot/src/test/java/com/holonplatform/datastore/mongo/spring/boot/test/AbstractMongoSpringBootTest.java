package com.holonplatform.datastore.mongo.spring.boot.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public abstract class AbstractMongoSpringBootTest {

	protected static final Logger logger = LoggerFactory.getLogger(AbstractMongoSpringBootTest.class);

	private static final RuntimeConfig runtimeConfig = Defaults.runtimeConfigFor(Command.MongoD, logger).build();

	private static final MongodStarter starter = MongodStarter.getInstance(runtimeConfig);

	private static MongodExecutable _mongodExe;
	private static MongodProcess _mongod;

	@BeforeAll
	public static void setUp() throws Exception {

		_mongodExe = starter.prepare(MongodConfig.builder().version(Version.Main.V4_4)
				.net(new Net("localhost", 12345, Network.localhostIsIPv6())).build());
		_mongod = _mongodExe.start();
	}

	@AfterAll
	public static void tearDown() throws Exception {
		_mongod.stop();
		_mongodExe.stop();
	}

}