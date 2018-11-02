package com.bo.msgpush.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands.DistanceUnit;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoRadiusCommandArgs;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

/**
 * @author wangboc
 * 
 * @version 2018年2月9日 下午1:32:26
 * 
 * @notes redisTemplate简单封装:spring-data-redis
 */
@Service("redisClientService")
public class RedisClientService {

	private static Logger logger = LoggerFactory.getLogger(RedisClientService.class);

	/**
	 * 缓存键的前缀
	 */
	public static final String PREFIX_KEY_VALUE = "msgpush:value:";
	public static final String PREFIX_KEY_SET = "msgpush:set:";
	public static final String PREFIX_KEY_LIST = "msgpush:list:";
	public static final String PREFIX_KEY_HASH = "msgpush:hash:";
	public static final String PREFIX_KEY_GEO = "msgpush:geo:";

	@Resource(name = "stringRedisTemplate")
	private RedisTemplate<String, String> redisTemplate;

	public RedisTemplate<String, String> getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	/**
	 * 缓存value操作
	 *
	 * @param key
	 * @param value
	 * @param time(毫秒)
	 * @return
	 */
	public boolean cacheValue(String key, String value, long time) {
		try {
			key = PREFIX_KEY_VALUE + key;
			ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
			valueOps.set(key, value);
			if (time > 0) redisTemplate.expire(key, time, TimeUnit.MILLISECONDS);
			return true;
		} catch (Exception e) {
			logger.error("缓存[" + key + "]失败, value[" + value + "]", e);
		}
		return false;
	}

	/**
	 * 缓存value操作
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean cacheValue(String key, String value) {
		return cacheValue(key, value, -1);
	}

	/**
	 * 获取缓存value
	 *
	 * @param key
	 * @return
	 */
	public String getValue(String key) {
		try {
			key = PREFIX_KEY_VALUE + key;
			ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
			return valueOps.get(key);
		} catch (Exception e) {
			logger.error("获取缓存val失败, key[" + key + "], error[" + e + "]");
		}
		return null;
	}

	/**
	 * 缓存一个hash键值对到hash表
	 *
	 * @param key
	 * @param hashKey
	 * @param value
	 * @param time(毫秒)
	 * @return
	 */
	public boolean cacheHash(String key, String hashKey, String value, long time) {
		try {
			key = PREFIX_KEY_HASH + key;
			HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
			opsForHash.put(key, hashKey, value);
			if (time > 0) redisTemplate.expire(key, time, TimeUnit.MILLISECONDS);
			return true;
		} catch (Exception e) {
			logger.error("缓存Hash键值对失败: key[" + key + "], error[" + e + "]");
		}
		return false;
	}

	/**
	 * 缓存一个map中的键值对到hash表.
	 * 注意: map不会覆盖整个hash表，但map中的键值对会覆盖redis中存在的键值对
	 * map中元素个数不能为空，否则抛出异常
	 *
	 * @param key
	 * @param map
	 * @param time(毫秒)
	 * @return
	 */
	public boolean cacheHash(String key, Map<String, String> map, long time) {
		try {
			key = PREFIX_KEY_HASH + key;
			HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
			opsForHash.putAll(key, map);
			if (time > 0) redisTemplate.expire(key, time, TimeUnit.MILLISECONDS);
			return true;
		} catch (Exception e) {
			logger.error("缓存HashMap失败: key[" + key + "], error[" + e + "]");
		}
		return false;
	}

	/**
	 * 通过key获取一个map
	 *
	 * @param key
	 * @return
	 */
	public Map<String, String> getHashMap(String key) {
		try {
			key = PREFIX_KEY_HASH + key;
			HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
			return opsForHash.entries(key);
		} catch (Exception e) {
			logger.error("获取缓存HashMap失败: key[" + key + "], error[" + e + "]");
		}
		return null;
	}

	/**
	 * 获取key对应map中所有的keys
	 *
	 * @param key
	 * @return
	 */
	public Set<String> getHashKeys(String key) {
		try {
			key = PREFIX_KEY_HASH + key;
			HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
			return opsForHash.keys(key);
		} catch (Exception e) {
			logger.error("获取缓存HashKeys失败: key[" + key + "], error[" + e + "]");
		}
		return null;
	}

	/**
	 * 获取key对应map中所有的values
	 *
	 * @param key
	 * @return
	 */
	public List<String> getHashValues(String key) {
		try {
			key = PREFIX_KEY_HASH + key;
			HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
			return opsForHash.values(key);
		} catch (Exception e) {
			logger.error("获取缓存HashValues失败: key[" + key + "], error[" + e + "]");
		}
		return null;
	}

	/**
	 * 获取key对应的hash表中hashKey对应的值
	 *
	 * @param key
	 * @param hashKey
	 * @return
	 */
	public String getHashValue(String key, String hashKey) {
		try {
			key = PREFIX_KEY_HASH + key;
			HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
			return opsForHash.get(key, hashKey);
		} catch (Exception e) {
			logger.error("获取缓存HashValue失败: key[" + key + "], error[" + e + "]");
		}
		return null;
	}

	/**
	 * 获取key对应的hash表中指定的hashKey对应的值
	 *
	 * @param key
	 * @param hashKeys
	 * @return
	 */
	public List<String> getHashValues(String key, String... hashKeys) {
		try {
			key = PREFIX_KEY_HASH + key;
			HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
			return opsForHash.multiGet(key, Arrays.asList(hashKeys));
		} catch (Exception e) {
			logger.info("获取缓存HashValue失败: key[" + key + "], error[" + e + "]");
		}
		return null;
	}

	/**
	 * 删除key对应的hashMap中hashKey映射的值
	 *
	 * @param key
	 * @param hashKeys
	 * @return
	 */
	public boolean deleteHash(String key, String... hashKeys) {
		try {
			key = PREFIX_KEY_HASH + key;
			if (hashKeys != null && hashKeys.length != 0) {
				HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
				opsForHash.delete(key, hashKeys);
				return true;
			}
		} catch (Exception e) {
			logger.error("删除hash缓存失败: key[" + key + "], hashKeys[" + hashKeys + "], error[" + e + "]");
		}
		return false;
	}

	/**
	 * 缓存set
	 *
	 * @param key
	 * @param value
	 * @param time(毫秒)
	 * @return
	 */
	public boolean cacheSet(String key, String value, long time) {
		try {
			key = PREFIX_KEY_SET + key;
			SetOperations<String, String> valueOps = redisTemplate.opsForSet();
			valueOps.add(key, value);
			if (time > 0) redisTemplate.expire(key, time, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			logger.error("缓存[" + key + "]失败, value[" + value + "]", e);
		}
		return true;
	}

	/**
	 * 缓存set
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean cacheSet(String key, String value) {
		return cacheSet(key, value, -1);
	}

	/**
	 * 缓存set
	 *
	 * @param key
	 * @param value
	 * @param time(毫秒)
	 * @return
	 */
	public boolean cacheSet(String key, Set<String> value, long time) {
		try {
			key = PREFIX_KEY_SET + key;
			SetOperations<String, String> setOps = redisTemplate.opsForSet();
			setOps.add(key, value.toArray(new String[value.size()]));
			if (time > 0) redisTemplate.expire(key, time, TimeUnit.MILLISECONDS);
			return true;
		} catch (Exception e) {
			logger.error("缓存[" + key + "]失败, value[" + value + "]", e);
		}
		return false;
	}

	/**
	 * 缓存set
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean cacheSet(String key, Set<String> value) {
		return cacheSet(key, value, -1);
	}

	/**
	 * 获取缓存set数据
	 *
	 * @param key
	 * @return
	 */
	public Set<String> getSet(String key) {
		try {
			key = PREFIX_KEY_SET + key;
			SetOperations<String, String> setOps = redisTemplate.opsForSet();
			return setOps.members(key);
		} catch (Exception e) {
			logger.error("获取set缓存失败key[" + key + "], error[" + e + "]");
		}
		return null;
	}

	/**
	 * 缓存List
	 *
	 * @param key
	 * @param value
	 * @param time(毫秒)
	 * @return
	 */
	public boolean cacheList(String key, String value, long time) {
		try {
			key = PREFIX_KEY_LIST + key;
			ListOperations<String, String> listOps = redisTemplate.opsForList();
			listOps.rightPush(key, value);
			if (time > 0) redisTemplate.expire(key, time, TimeUnit.MILLISECONDS);
			return true;
		} catch (Exception e) {
			logger.error("缓存[" + key + "]失败, value[" + value + "]", e);
		}
		return false;
	}

	/**
	 * 缓存List
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean cacheList(String key, String value) {
		return cacheList(key, value, -1);
	}

	/**
	 * 缓存List
	 *
	 * @param key
	 * @param value
	 * @param time(毫秒)
	 * @return
	 */
	public boolean cacheList(String key, List<String> value, long time) {
		try {
			key = PREFIX_KEY_LIST + key;
			ListOperations<String, String> listOps = redisTemplate.opsForList();
			listOps.rightPushAll(key, value);
			if (time > 0) redisTemplate.expire(key, time, TimeUnit.MILLISECONDS);
			return true;
		} catch (Exception e) {
			logger.error("缓存[" + key + "]失败, value[" + value + "]", e);
		}
		return false;
	}

	/**
	 * 缓存List
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean cacheList(String key, List<String> value) {
		return cacheList(key, value, -1);
	}

	/**
	 * 获取缓存List
	 *
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public List<String> getList(String key, long start, long end) {
		try {
			key = PREFIX_KEY_LIST + key;
			ListOperations<String, String> listOps = redisTemplate.opsForList();
			return listOps.range(key, start, end);
		} catch (Exception e) {
			logger.error("获取list缓存失败key[" + key + "]" + ", error[" + e + "]");
		}
		return null;
	}

	/**
	 * 获取缓存List大小
	 *
	 * @param key
	 * @return
	 */
	public long getListSize(String key) {
		try {
			key = PREFIX_KEY_LIST + key;
			ListOperations<String, String> listOps = redisTemplate.opsForList();
			return listOps.size(key);
		} catch (Exception e) {
			logger.error("获取list长度失败key[" + key + "], error[" + e + "]");
		}
		return 0;
	}

	/**
	 * 移除List缓存
	 *
	 * @param key
	 * @return
	 */
	public boolean removeOneOfList(String key) {
		try {
			key = PREFIX_KEY_LIST + key;
			ListOperations<String, String> listOps = redisTemplate.opsForList();
			listOps.rightPop(key);
			return true;
		} catch (Exception e) {
			logger.error("移除list缓存失败key[" + key + "], error[" + e + "]");
		}
		return false;
	}

	/**
	 * 缓存地理位置信息
	 *
	 * @param key
	 * @param x
	 * @param y
	 * @param member
	 * @param time(毫秒) <= 0 不过期
	 * @return
	 */
	public boolean cacheGeo(String key, double x, double y, String member, long time) {
		try {
			key = PREFIX_KEY_GEO + key;
			GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
			geoOps.geoAdd(key, new Point(x, y), member);
			if (time > 0) redisTemplate.expire(key, time, TimeUnit.MILLISECONDS);
			return true;
		} catch (Exception e) {
			logger.error("缓存[" + key + "]" + "失败, point[" + x + "," + y + "], member[" + member + "]" + ", error[" + e + "]");
		}
		return false;
	}

	/**
	 * 缓存地理位置信息
	 *
	 * @param key
	 * @param memberCoordinateMap
	 * @param time(秒)             <= 0 不过期
	 * @return
	 */
	public boolean cacheGeo(String key, Map<String, Point> memberCoordinateMap, long time) {
		try {
			Set<Entry<String, Point>> entrySet = memberCoordinateMap.entrySet();
			for (Entry<String, Point> entry : entrySet) {
				boolean cacheRet = cacheGeo(key, entry.getValue().getX(), entry.getValue().getY(), entry.getKey(), time);
			}
			return true;
		} catch (Exception e) {
			logger.error("缓存[" + key + "]" + "失败" + ", error[" + e + "]");
		}
		return false;
	}

	/**
	 * 缓存地理位置信息
	 *
	 * @param key
	 * @param location
	 * @param time(秒)  <= 0 不过期
	 * @return
	 */
	public boolean cacheGeo(String key, GeoLocation<String> location, long time) {
		try {
			return cacheGeo(key, location.getPoint().getX(), location.getPoint().getY(), location.getName(), time);
		} catch (Exception e) {
			logger.error("缓存[" + key + "]" + "失败" + ", error[" + e + "]");
		}
		return false;
	}

	/**
	 * 缓存地理位置信息
	 *
	 * @param key
	 * @param locations
	 * @param time(秒)   <= 0 不过期
	 * @return
	 */
	public boolean cacheGeo(String key, Iterable<GeoLocation<String>> locations, long time) {
		try {
			for (GeoLocation<String> location : locations) {
				boolean cacheRet = cacheGeo(key, location.getPoint().getX(), location.getPoint().getY(), location.getName(), time);
			}
			return true;
		} catch (Exception e) {
			logger.error("缓存[" + key + "]" + "失败" + ", error[" + e + "]");
		}
		return false;
	}

	/**
	 * 移除地理位置信息
	 *
	 * @param key
	 * @param members
	 * @return
	 */
	public boolean deleteGeo(String key, String... members) {
		try {
			key = PREFIX_KEY_GEO + key;
			GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
			geoOps.geoRemove(key, members);
			return true;
		} catch (Exception e) {
			logger.error("移除[" + key + "]" + "失败" + ", error[" + e + "]");
		}
		return false;
	}

	/**
	 * 根据两个成员计算两个成员之间距离
	 *
	 * @param key
	 * @param member1
	 * @param member2
	 * @return
	 */
	public Distance distanceGeo(String key, String member1, String member2) {
		try {
			key = PREFIX_KEY_GEO + key;
			GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
			return geoOps.geoDist(key, member1, member2);
		} catch (Exception e) {
			logger.error("计算距离[" + key + "]" + "失败, member[" + member1 + "," + member2 + "], error[" + e + "]");
		}
		return null;
	}

	/**
	 * 根据key和member获取这些member的坐标信息
	 *
	 * @param key
	 * @param members
	 * @return
	 */
	public List<Point> getGeoPoints(String key, String... members) {
		try {
			key = PREFIX_KEY_GEO + key;
			GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
			return geoOps.geoPos(key, members);
		} catch (Exception e) {
			logger.error("获取坐标[" + key + "]" + "失败" + ", error[" + e + "]");
		}
		return null;
	}

	/**
	 * 通过给定的坐标和距离(m)获取范围类其它的坐标信息
	 *
	 * @param key
	 * @param x
	 * @param y
	 * @param distance(m)
	 * @return
	 */
	public GeoResults<GeoLocation<String>> radiusGeo(String key, double x, double y, double distance, Direction direction, long limit) {
		try {
			key = PREFIX_KEY_GEO + key;
			GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
			// 设置geo查询参数
			GeoRadiusCommandArgs geoRadiusArgs = GeoRadiusCommandArgs.newGeoRadiusArgs();
			geoRadiusArgs = geoRadiusArgs.includeCoordinates().includeDistance();// 查询返回结果包括距离和坐标
			if (Direction.ASC.equals(direction)) {// 按查询出的坐标距离中心坐标的距离进行排序
				geoRadiusArgs.sortAscending();
			} else if (Direction.DESC.equals(direction)) {
				geoRadiusArgs.sortDescending();
			}
			geoRadiusArgs.limit(limit);// 限制查询数量
			GeoResults<GeoLocation<String>> radiusGeo = geoOps.geoRadius(key, new Circle(new Point(x, y), new Distance(distance, DistanceUnit.KILOMETERS)), geoRadiusArgs);
			return radiusGeo;
		} catch (Exception e) {
			logger.error("通过坐标[" + x + "," + y + "]获取范围[" + distance + "km的其它位置信息失败]" + ", error[" + e + "]");
		}
		return null;
	}

	public boolean containsValueKey(String key) {
		return containsKey(PREFIX_KEY_VALUE + key);
	}

	public boolean containsHashKey(String key) {
		return containsKey(PREFIX_KEY_HASH + key);
	}

	public boolean containsSetKey(String key) {
		return containsKey(PREFIX_KEY_SET + key);
	}

	public boolean containsListKey(String key) {
		return containsKey(PREFIX_KEY_LIST + key);
	}

	public boolean containsGeoKey(String key) {
		return containsKey(PREFIX_KEY_GEO + key);
	}

	/**
	 * 判断缓存是否存在
	 *
	 * @param key
	 * @return
	 */
	private boolean containsKey(String key) {
		try {
			return redisTemplate.hasKey(key);
		} catch (Exception e) {
			logger.error("判断缓存是否存在失败: key[" + key + "], error[" + e + "]");
		}
		return false;
	}
	
	/**
	 * 根据模式串获取所有键值
	 * 
	 * @param pattern
	 * @return
	 */
	public Set<String> getKeys(String pattern) {
		Set<String> keys = new HashSet<>();
		try {
			return redisTemplate.keys(pattern);
		} catch (Exception e) {
			logger.error("获取缓存keys失败: pattern[" + pattern + "], error[" + e + "]");
		}
		return keys;
	}
	
	public boolean removeValue(String key) {
		return remove(PREFIX_KEY_VALUE + key);
	}

	public boolean removeHash(String key) {
		return remove(PREFIX_KEY_HASH + key);
	}
	
	public boolean removeSet(String key) {
		return remove(PREFIX_KEY_SET + key);
	}

	public boolean removeList(String key) {
		return remove(PREFIX_KEY_LIST + key);
	}

	public boolean removeGeo(String key) {
		return remove(PREFIX_KEY_GEO + key);
	}

	/**
	 * 移除key中所有缓存
	 *
	 * @param key
	 * @return
	 */
	private boolean remove(String key) {
		try {
			redisTemplate.delete(key);
			return true;
		} catch (Exception e) {
			logger.error("移除缓存失败: key[" + key + "], error[" + e + "]");
		}
		return false;
	}

	public boolean setExpireTimeForValue(String key, long time, TimeUnit timeUnit) {
		return setExpireTime(PREFIX_KEY_VALUE + key, time, timeUnit);
	}

	public boolean setExpireTimeForHash(String key, long time, TimeUnit timeUnit) {
		return setExpireTime(PREFIX_KEY_HASH + key, time, timeUnit);
	}
	
	public boolean setExpireTimeForSet(String key, long time, TimeUnit timeUnit) {
		return setExpireTime(PREFIX_KEY_SET + key, time, timeUnit);
	}

	public boolean setExpireTimeForList(String key, long time, TimeUnit timeUnit) {
		return setExpireTime(PREFIX_KEY_LIST + key, time, timeUnit);
	}

	public boolean setExpireTimeForGeo(String key, long time, TimeUnit timeUnit) {
		return setExpireTime(PREFIX_KEY_GEO + key, time, timeUnit);
	}
	
	/**
	 * 设置key对应的缓存过期时间
	 * 
	 * @param key
	 * @param time
	 * @param timeUnit
	 * @return
	 */
	private boolean setExpireTime(String key, long time, TimeUnit timeUnit) {
		try {
			if(time > 0) {
				redisTemplate.expire(key, time, timeUnit);
				return true;
			}
		} catch (Exception e) {
			logger.error("设置缓存时间失败: key[" + key + "], error[" + e + "]");
		}
		return false;
	}
	
	public Long getExpireTimeOfValue(String key) {
		return getExpireTime(PREFIX_KEY_VALUE + key);
	}

	public Long getExpireTimeOfHash(String key) {
		return getExpireTime(PREFIX_KEY_HASH + key);
	}
	
	public Long getExpireTimeOfSet(String key) {
		return getExpireTime(PREFIX_KEY_SET + key);
	}

	public Long getExpireTimeOfList(String key) {
		return getExpireTime(PREFIX_KEY_LIST + key);
	}

	public Long getExpireTimeOfGeo(String key) {
		return getExpireTime(PREFIX_KEY_GEO + key);
	}
	
	/**
	 * 获取key对应的缓存过期时间(秒)
	 *
	 * @param key
	 * @return
	 */
	private Long getExpireTime(String key) {
		Long expire = -1L;
		try {
			expire = redisTemplate.getExpire(key);
		} catch (Exception e) {
			logger.error("获取缓存剩余时间失败: key[" + key + "], error[" + e + "]");
		}
		return expire;
	}
	
	/**
	 * 分布式锁一般有三种实现方式：http://www.importnew.com/27477.html
	 *
	 * 1.数据库乐观锁；
	 * 2.基于Redis的分布式锁；
	 * 3.基于ZooKeeper的分布式锁
	 *
	 * Redis分布式锁: 为了确保分布式锁可用，我们至少要确保锁的实现同时满足以下四个条件：
	 *
	 * 1.互斥性。在任意时刻，只有一个客户端能持有锁。
	 * 2.不会发生死锁。即使有一个客户端在持有锁的期间崩溃而没有主动解锁，也能保证后续其他客户端能加锁。
	 * 3.具有容错性。只要大部分的Redis节点正常运行，客户端就可以加锁和解锁。
	 * 4.解铃还须系铃人。加锁和解锁必须是同一个客户端，客户端自己不能把别人加的锁给解了
	 */


	/**
	 * 获取一个lockExpireTime时间后过期的redis锁，如果获取不到，每隔tryInterval时间获取一次锁，timeout时间后，锁申请超时，获取锁失败
	 *
	 * @param key 锁的key
	 * @param reqId 请求标识
	 * @param timeout 等待超时时间(毫秒)
	 * @param tryInterval 多久重试获取锁(毫秒)
	 * @param lockExpireTime 锁过期时间(毫秒)
	 * @return
	 */
	public boolean acquireRedisLock(String key, String reqId, long timeout, long tryInterval, long lockExpireTime) {
		try {
			long startTime = System.currentTimeMillis();
			// 直到获取到锁为止
			while (true) {
				if (redisTemplate.opsForValue().setIfAbsent(key, reqId)) {
					// 设置锁的过期时间
					// TO-DO 若在这里程序突然崩溃，则无法设置过期时间，将发生死锁
					redisTemplate.opsForValue().set(key, reqId, lockExpireTime, TimeUnit.MILLISECONDS);
					return true;
				}
				//如果没有获取到，并且已经超时
				if (System.currentTimeMillis() - startTime > timeout) {
					return false;
				}
				//延迟一段时间
				Thread.sleep(tryInterval);
			}
		} catch (Exception e) {
			logger.error("获取redis分布式锁失败: key[" + key + "], requestId[ "+ reqId +"], error[" + e + "]");
		}
		return false;
	}

	/**
	 * 释放redis分布式锁
	 *
	 * @param key 锁对应的键
	 * @param reqId 请求标识
	 */
	public synchronized void releaseRedisLock(String key, String reqId) {
		try {
			// 判断加锁与解锁是不是同一个客户端
			if (isOriginalLocker(key, reqId)) {
				// TO-DO 若在此时，这把锁突然不是这个客户端的，则会误解锁
				redisTemplate.delete(key);
			}
		} catch (Exception e) {
			logger.error("释放redis分布式锁失败: key[" + key + "], requestId[ "+ reqId +"], error[" + e + "]");
		}
	}

	/**
	 * 判断是否是原配加锁客户端
	 *
	 * @param key 锁对应的键
	 * @param reqId 请求标识
	 * @return
	 */
	public boolean isOriginalLocker(String key, String reqId) {
		try {
			// 获取缓存的业务关键字
			String cacheBizKey =  redisTemplate.opsForValue().get(key);
			// 判断是否是原配客户端
			return reqId.equalsIgnoreCase(cacheBizKey);
		} catch (Exception e) {
			logger.error("校验redis分布式锁客户端失败: key[" + key + "], requestId[ "+ reqId +"], error[" + e + "]");
		}
		return false;
	}

}
