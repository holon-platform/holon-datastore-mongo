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

import java.util.concurrent.CompletableFuture;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * A {@link Subscriber} adapter for {@link CompletableFuture}.
 *
 * @param <T> Type
 * 
 * @since 5.2.0
 */
public class CompletableFutureSubscriber<T> extends CompletableFuture<T> implements Subscriber<T> {

	private T value;

	/*
	 * (non-Javadoc)
	 * @see org.reactivestreams.Subscriber#onSubscribe(org.reactivestreams.Subscription)
	 */
	@Override
	public void onSubscribe(Subscription s) {
		s.request(1);
	}

	/*
	 * (non-Javadoc)
	 * @see org.reactivestreams.Subscriber#onNext(java.lang.Object)
	 */
	@Override
	public void onNext(T t) {
		value = t;
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
		complete(value);
	}

	public static <T> CompletableFuture<T> fromPublisher(Publisher<T> publisher) {
		final CompletableFutureSubscriber<T> future = new CompletableFutureSubscriber<>();
		publisher.subscribe(future);
		return future;
	}

}
