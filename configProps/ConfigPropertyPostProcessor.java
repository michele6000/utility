package configProps;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.lang.reflect.Field;
import java.util.Optional;

@Slf4j
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class ConfigPropertyPostProcessor implements BeanPostProcessor {

    @Autowired
    private ConfigHolderRepository configHolderRepository;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(ConfigProperty.class)) {
                ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);
                String key = annotation.value();
                Optional<ConfigHolderEntity> config = configHolderRepository.findById("core." + key);
                if (config.isPresent()) {
                    field.setAccessible(true);
                    try {
                        field.set(bean, config.get().getValue());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Failed to set field value", e);
                    }
                } else {
                    log.error("Rilevata proprietà core non valorizzata: core.{}", key);
                }
            }
        }
        return bean;
    }
}
