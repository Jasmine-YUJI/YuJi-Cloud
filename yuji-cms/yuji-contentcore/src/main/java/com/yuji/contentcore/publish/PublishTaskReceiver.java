package com.yuji.contentcore.publish;

import com.yuji.contentcore.config.CMSPublishConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;

import java.util.Map;
import java.util.Objects;

/**
 * 发布任务消费监听
 *
 * @author Liguoqiang
 */
@Slf4j
@RequiredArgsConstructor
public class PublishTaskReceiver implements StreamListener<String, MapRecord<String, String, String>> {

    private final static Logger logger = LoggerFactory.getLogger("publish");

    private final Map<String, IPublishTask<?>> publishTaskMap;

    private final StringRedisTemplate redisTemplate;

    @Setter
    @Getter
    private Consumer consumer;

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        String stream = message.getStream();
        if (Objects.nonNull(stream)) {
            try {
                Map<String, String> map = message.getValue();
                String type = MapUtils.getString(map, "type");
                IPublishTask<?> publishTask = publishTaskMap.get(IPublishTask.BeanPrefix + type);
                if (Objects.nonNull(publishTask)) {
                    publishTask.staticize(map);
                }
            } catch(Exception e) {
                logger.error("Publish err.", e);
            } finally {
                redisTemplate.opsForStream().acknowledge(stream, CMSPublishConfig.PublishConsumerGroup, message.getId().getValue());
                redisTemplate.opsForStream().delete(stream, message.getId().getValue());
            }
        }
    }
}
