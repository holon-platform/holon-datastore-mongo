package com.holonplatform.datastore.mongo.spring.boot.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.TransitionWalker;
import de.flapdoodle.reverse.transitions.Start;

public abstract class AbstractMongoSpringBootTest {

	protected static final Logger logger = LoggerFactory.getLogger(AbstractMongoSpringBootTest.class);

	private static TransitionWalker.ReachedState<RunningMongodProcess> _running;

	@BeforeAll
	public static void setUp() throws Exception {

		Mongod mongodConfig = Mongod.builder().net(Start.to(Net.class).initializedWith(Net.defaults().withPort(12345)))
				.build();

		_running = mongodConfig.start(Version.Main.V8_0);
	}

	@AfterAll
	public static void tearDown() throws Exception {
		_running.close();
	}

}