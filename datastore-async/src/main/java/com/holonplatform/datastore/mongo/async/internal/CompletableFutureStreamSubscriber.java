/*
 * Copyright 2016-2018 Axioma srl.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.datastore.mongo.async.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * A {@link Subscriber} adapter for {@link CompletableFuture} of results stream.
 *
 * @param <T> Type
 * 
 * @since 5.2.0
 */
public class CompletableFutureStreamSubscriber<T> extends CompletableFuture<Stream<T>> implements Subscriber<T> {

	private List<T> values = new ArrayList<>();

	/*
	 * (non-Javadoc)
	 * @see org.reactivestreams.Subscriber#onSubscribe(org.reactivestreams.Subscription)
	 */
	@Override
	public void onSubscribe(Subscription s) {
		s.request(Integer.MAX_VALUE);
	}

	/*
	 * (non-Javadoc)
	 * @see org.reactivestreams.Subscriber#onNext(java.lang.Object)
	 */
	@Override
	public void onNext(T t) {
		values.add(t);
	}

	/*
	 * (non-Javadoc)
	 * @see org.reactivestreams.Subscriber#onError(java.lang.Throwable)
	 */
	@Override
	public void onError(Throwable t) {
		completeExceptionally(t);
	}

	/*
	 * (non-Javadoc)
	 * @see org.reactivestreams.Subscriber#onComplete()
	 */
	@Override
	public void onComplete() {
		complete(values.stream());
	}

	public static <T> CompletableFuture<Stream<T>> fromPublisher(Publisher<T> publisher) {
		final CompletableFutureStreamSubscriber<T> future = new CompletableFutureStreamSubscriber<>();
		publisher.subscribe(future);
		return future;
	}

}
