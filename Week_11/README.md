## 学习笔记
----
- 作业一 （必做）基于 Redis 封装分布式数据操作：<br>
- [x] 1. 在Java中实现一个简单的分布式锁
- [x] 2. 在Java中实现一个分布式计数器，模拟减库存

思路：<br>
分布式锁的重点：
1. 使用setNx，value为时间戳，并且设置有效期
2. 解锁的时候使用lua脚本保证原子性，判断时间戳，避免解错锁

分布式锁的代码实现
```java
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Implement distributed lock base on Redis.
 *
 * @author wuudongdong
 * @date 2020/01/06
 */
@Component
public class RedisManager {

    @Resource
    RedisTemplate<String, Object> redisTemplate;

    public Boolean lock(String lock, String val) {
        return redisTemplate.opsForValue().setIfAbsent(lock, val, 3000, TimeUnit.MILLISECONDS);
    }

    public void unLock(String lock, String val) {
        String lua = "local val = ARGV[1] local curr=redis.call('get', KEYS[1]) "
                + "if val==curr then redis.call('del', KEYS[1]) end return 'OK'";
        RedisScript<Object> redisScript = RedisScript.of(lua);
        redisTemplate.execute(redisScript, Collections.singletonList(lock), val);
    }

    public Boolean decrement(String key, Long value) {
        String lua = "local val = ARGV[1] local curr=redis.call('get', KEYS[1]) local result = curr-val "
                + "if result>=0 then redis.call('set', KEYS[1], result) return '1' else return '0' end";
        RedisScript<Integer> redisScript = RedisScript.of(lua, Integer.class);
        Integer result = redisTemplate.execute(redisScript, Collections.singletonList(key), value);
        return Objects.nonNull(result) && 1 == result;
    }
}
```

业务层最佳实践
```java
import com.example.redisdemo.manager.RedisManager;
import com.example.redisdemo.model.request.InventoryDecreaseRequest;
import com.example.redisdemo.service.InventoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Implement reduce inventory.
 *
 * @author wuudongdong
 * @date 2021/01/06
 */
@Service
public class InventoryServiceImpl implements InventoryService {

    @Value("${inventory.lock.key}")
    String lockKey;
    @Value("${inventory.item.prefix}")
    String prefix;
    @Resource
    RedisManager redisManager;

    @Override
    public Boolean reduceInventory(InventoryDecreaseRequest inventoryDecreaseRequest) {
        boolean isLocked = false;
        boolean isReduceSuccess = false;
        String timestamp = String.valueOf(System.nanoTime());
        try {
            isLocked = redisManager.lock(lockKey, timestamp);
            if (isLocked) {
                isReduceSuccess = redisManager.decrement(prefix + inventoryDecreaseRequest.getInventoryId(),
                        inventoryDecreaseRequest.getDecrease());
            }
            return isReduceSuccess;
        } finally {
            if (isLocked) {
                redisManager.unLock(lockKey, timestamp);
            }
        }
    }
}
```
作业链接：[redis-demo](https://github.com/wuudongdong/redis-demo)