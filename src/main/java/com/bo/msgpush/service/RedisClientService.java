/**
 * 
 */
package com.bo.msgpush.service;

import java.util.Arrays;
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
 * @notes redisTemplate简单封装:spring-data-redis
 * 
 * @author wangboc
 * 
 * @version 2018年2月9日 下午1:32:26
 */
@Service("redisClientService")
public class RedisClientService {
	
	private static Logger logger = LoggerFactory.getLogger(RedisClientService.class);
	
	/** 缓存键的前缀 */
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
	 * @param time
	 * @return
	 */
	public boolean cacheValue(String key, String value, long time) {
		try {
			key = PREFIX_KEY_VALUE + key;
			ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
			valueOps.set(key, value);
			if(time > 0) redisTemplate.expire(key, time, TimeUnit.SECONDS);
			return true;
		} catch (Exception e) {
			logger.error("缓存[" + key + "]失败, value[" + value + "]", e);
		}
		return false;
	}

	/**
	 * 缓存value操作
	 * 
	 * @param k
	 * @param v
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
			logger.error("获取缓存val失败, key[" + key + ", error[" + e + "]");
		}
		return null;
	}
	
	/**
	 * 缓存一个hash键值对到hash表
	 * 
	 * @param key
	 * @param hashKey
	 * @param value
	 * @param time
	 * @return
	 */
	public boolean cacheHash(String key, String hashKey, String value, long time) {
		try {
			key = PREFIX_KEY_HASH + key;
			HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
			opsForHash.put(key, hashKey, value);
			if (time > 0)
				redisTemplate.expire(key, time, TimeUnit.SECONDS);
			return true;
		} catch (Exception e) {
			logger.error("缓存Hash键值对失败: key[" + key + ", error[" + e + "]");
		}
		return false;
	}

	/**
	 * 缓存一个map中的键值对到hash表. 
	 * 注意: map不会覆盖整个hash表，但map中的键值对会覆盖redis中存在的键值对
	 * 		map中元素个数不能为空，否则抛出异常
	 * @param key
	 * @param map
	 * @param time
	 * @return
	 */
	public boolean cacheHash(String key, Map<String, String> map, long time) {
		try {
			key = PREFIX_KEY_HASH + key;
			HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
			opsForHash.putAll(key, map);
			if (time > 0)
				redisTemplate.expire(key, time, TimeUnit.SECONDS);
			return true;
		} catch (Exception e) {
			logger.error("缓存HashMap失败: key[" + key + ", error[" + e + "]");
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
			logger.error("获取缓存HashMap失败: key[" + key + ", error[" + e + "]");
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
			logger.error("获取缓存HashKeys失败: key[" + key + ", error[" + e + "]");
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
			logger.error("获取缓存HashValues失败: key[" + key + ", error[" + e + "]");
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
			logger.error("获取缓存HashValue失败: key[" + key + ", error[" + e + "]");
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
	public List<String> getHashValues(String key, String...hashKeys) {
		try {
			key = PREFIX_KEY_HASH + key;
			HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
			return opsForHash.multiGet(key, Arrays.asList(hashKeys));
		} catch (Exception e) {
			logger.info("获取缓存HashValue失败: key[" + key + ", error[" + e + "]");
		}
		return null;
	}
	
	/**
	 * 删除key对应的hashMap中hashKey映射的值.或删除整个key对应的hashMap
	 * 
	 * @param key
	 * @param hashKeys
	 * @return
	 */
	public boolean deleteHash(String key, String... hashKeys) {
		try {
			key = PREFIX_KEY_HASH + key;
			if (hashKeys == null || hashKeys.length == 0) {
				redisTemplate.delete(key);
			} else {
				HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
				opsForHash.delete(key, hashKeys);
			}
			return true;
		} catch (Exception e) {
			logger.error("获取缓存失败key[" + key + ", error[" + e + "]");
		}
		return false;
	}
	
	/**
	 * 缓存set
	 * 
	 * @param key
	 * @param value
	 * @param time
	 * @return
	 */
	public boolean cacheSet(String key, String value, long time) {
		try {
			key = PREFIX_KEY_SET + key;
			SetOperations<String, String> valueOps = redisTemplate.opsForSet();
			valueOps.add(key, value);
			if (time > 0) redisTemplate.expire(key, time, TimeUnit.SECONDS);
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
	 * @param time
	 * @return
	 */
	public boolean cacheSet(String key, Set<String> value, long time) {
		try {
			key = PREFIX_KEY_SET + key;
			SetOperations<String, String> setOps = redisTemplate.opsForSet();
			setOps.add(key, value.toArray(new String[value.size()]));
			if (time > 0) redisTemplate.expire(key, time, TimeUnit.SECONDS);
			return true;
		} catch (Exception e) {
			logger.error("缓存[" + key + "]失败, value[" + value + "]", e);
		}
		return false;
	}
	
	/**
	 * 缓存set
	 * 
	 * @param k
	 * @param v
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
			logger.error("获取set缓存失败key[" + key + ", error[" + e + "]");
		}
		return null;
	}
	
	/**
	 * 缓存List
	 * 
	 * @param key
	 * @param value
	 * @param time
	 * @return
	 */
	public boolean cacheList(String key, String value, long time) {
		try {
			key = PREFIX_KEY_LIST + key;
			ListOperations<String, String> listOps = redisTemplate.opsForList();
			listOps.rightPush(key, value);
			if (time > 0)
				redisTemplate.expire(key, time, TimeUnit.SECONDS);
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
	 * @param k
	 * @param v
	 * @param time
	 * @return
	 */
	public boolean cacheList(String key, List<String> value, long time) {
		try {
			key = PREFIX_KEY_LIST + key;
			ListOperations<String, String> listOps = redisTemplate.opsForList();
			listOps.rightPushAll(key, value);
			if (time > 0)
				redisTemplate.expire(key, time, TimeUnit.SECONDS);
			return true;
		} catch (Exception e) {
			logger.error("缓存[" + key + "]失败, value[" + value + "]", e);
		}
		return false;
	}

	/**
	 * 缓存List
	 * 
	 * @param k
	 * @param v
	 * @return
	 */
	public boolean cacheList(String key, List<String> value) {
		return cacheList(key, value, -1);
	}

	/**
	 * 获取缓存List
	 * 
	 * @param k
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
			logger.error("移除list缓存失败key[" + key + ", error[" + e + "]");
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
	 * @param time(秒) <= 0 不过期
	 * @return
	 */
	public boolean cacheGeo(String key, double x, double y, String member, long time) {
		try {
			key = PREFIX_KEY_GEO + key;
			GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
			geoOps.geoAdd(key, new Point(x, y), member);
			if (time > 0)
				redisTemplate.expire(key, time, TimeUnit.SECONDS);
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
	 * @param time(秒) <= 0 不过期
	 * @return
	 */
	public boolean cacheGeo(String key, Map<String, Point> memberCoordinateMap, long time) {
		try {
			Set<Map.Entry<String, Point>> entrySet = memberCoordinateMap.entrySet();
			for(Entry<String, Point> entry : entrySet) {
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
	 * @param time(秒) <= 0 不过期
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
	 * @param time(秒) <= 0 不过期
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
	public boolean removeGeo(String key, String... members) {
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
			logger.error("判断缓存是否存在失败: key[" + key + ", error[" + e + "]");
		}
		return false;
	}

	public boolean removeValue(String k) {
		return remove(PREFIX_KEY_VALUE + k);
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
	 * @param k
	 * @return
	 */
	private boolean remove(String key) {
		try {
			redisTemplate.delete(key);
			return true;
		} catch (Exception e) {
			logger.error("移除缓存失败: key[" + key + ", error[" + e + "]");
		}
		return false;
	}

	/**
	 * 获取key对应的过期时间, 秒
	 * 
	 * @param key
	 * @return
	 */
	public Long getExpireTimeForValue(String key) {
		key = PREFIX_KEY_VALUE + key;
		Long expire = -2L;
		try {
			expire = redisTemplate.getExpire(key);
		} catch (Exception e) {
			logger.error("获取缓存剩余时间失败: key[" + key + ", error[" + e + "]");
		}
		return expire;
	}
	
}
