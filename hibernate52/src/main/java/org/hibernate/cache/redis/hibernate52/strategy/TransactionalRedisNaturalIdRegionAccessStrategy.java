/*
 * Copyright (c) 2016. Sunghyouk Bae <sunghyouk.bae@gmail.com>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.hibernate.cache.redis.hibernate52.strategy;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.internal.DefaultCacheKeysFactory;
import org.hibernate.cache.redis.hibernate52.regions.RedisNaturalIdRegion;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.EntityPersister;

/**
 * TransactionalRedisNaturalIdRegionAccessStrategy
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 11:14
 */
@Slf4j
public class TransactionalRedisNaturalIdRegionAccessStrategy
    extends AbstractRedisAccessStrategy<RedisNaturalIdRegion>
    implements NaturalIdRegionAccessStrategy {

  //@Getter
  //private final RedisClient redis;

  public TransactionalRedisNaturalIdRegionAccessStrategy(RedisNaturalIdRegion region,
                                                         //RedisClient redis,
                                                         SessionFactoryOptions options) {
    super(region, options);
    //this.redis = redis;
  }

  @Override
  public boolean afterInsert(SharedSessionContractImplementor session, Object key, Object value) {
    return false;
  }

  @Override
  public boolean afterUpdate(SharedSessionContractImplementor session, Object key, Object value, SoftLock lock) {
    return false;
  }

  @Override
  public Object get(SharedSessionContractImplementor session, Object key, long txTimestamp) {
    return region.get(key);
  }

  @Override
  public Object generateCacheKey(Object[] naturalIdValues,
                                 EntityPersister persister,
                                 SharedSessionContractImplementor session) {
    return DefaultCacheKeysFactory.createNaturalIdKey(naturalIdValues, persister, session);
  }

  @Override
  public Object[] getNaturalIdValues(Object cacheKey) {
    return DefaultCacheKeysFactory.getNaturalIdValues(cacheKey);
  }

  @Override
  public NaturalIdRegion getRegion() {
    return region;
  }

  @Override
  public boolean insert(SharedSessionContractImplementor session, Object key, Object value) {
    region.put(key, value);
    return true;
  }

  @Override
  public SoftLock lockItem(SharedSessionContractImplementor session, Object key, Object version) {
    region.remove(key);
    return null;
  }

  @Override
  public boolean putFromLoad(SharedSessionContractImplementor session,
                             Object key,
                             Object value,
                             long txTimestamp,
                             Object version,
                             boolean minimalPutOverride) {
    if (minimalPutOverride && region.contains(key)) {
      return false;
    }
    region.put(key, value);
    return true;
  }

  @Override
  public void remove(SharedSessionContractImplementor session, Object key) {
    region.remove(key);
  }

  @Override
  public void unlockItem(SharedSessionContractImplementor session, Object key, SoftLock lock) {
    region.remove(key);
  }

  @Override
  public boolean update(SharedSessionContractImplementor session, Object key, Object value) {
    region.put(key, value);
    return true;
  }
}
