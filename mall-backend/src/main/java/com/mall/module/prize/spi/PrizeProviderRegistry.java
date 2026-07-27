package com.mall.module.prize.spi;

import com.mall.common.exception.BusinessException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 奖品发放 Provider 注册中心
 *
 * <p>采用 <b>Spring Bean + Java ServiceLoader</b> 双重注册机制：</p>
 * <ul>
 *   <li>内置实现（{@code CouponPrizeProvider}、{@code PointsPrizeProvider}）通过 Spring
 *       {@code @Component} 自动注入，依赖 {@code CouponService}/{@code PointsService}。</li>
 *   <li>外部扩展通过 {@code META-INF/services/com.mall.module.prize.spi.PrizeProvider}
 *       注册，由 {@link ServiceLoader} 加载，无需 Spring 容器。</li>
 * </ul>
 *
 * <h3>外部扩展示例</h3>
 * <pre>
 * // 1. 实现接口
 * public class GiftCardPrizeProvider implements PrizeProvider {
 *     public String getType() { return "GIFT_CARD"; }
 *     // ...
 * }
 *
 * // 2. 在 META-INF/services/com.mall.module.prize.spi.PrizeProvider 中写入
 * com.yourcompany.prize.GiftCardPrizeProvider
 * </pre>
 *
 * <p>内置实现优先级高于外部扩展（同类型时内置覆盖外部）。</p>
 */
@Component
public class PrizeProviderRegistry {

    private static final Logger log = LoggerFactory.getLogger(PrizeProviderRegistry.class);

    private final Map<String, PrizeProvider> providers = new ConcurrentHashMap<>();

    /** Spring 注入的内置 Provider（@Component 标注的实现类） */
    @Autowired
    private List<PrizeProvider> springProviders;

    @PostConstruct
    public void init() {
        // 1. 注册 Spring 管理的内置实现
        for (PrizeProvider p : springProviders) {
            providers.put(p.getType(), p);
            log.info("注册奖品 Provider [Spring]: type={}, class={}", p.getType(), p.getClass().getName());
        }

        // 2. 通过 ServiceLoader 加载外部 SPI 实现（putIfAbsent → 内置优先）
        ServiceLoader<PrizeProvider> loader = ServiceLoader.load(PrizeProvider.class);
        for (PrizeProvider p : loader) {
            String type = p.getType();
            if (!providers.containsKey(type)) {
                providers.put(type, p);
                log.info("注册奖品 Provider [SPI]: type={}, class={}", type, p.getClass().getName());
            } else {
                log.info("跳过外部 Provider (内置已注册): type={}, class={}", type, p.getClass().getName());
            }
        }

        log.info("奖品 Provider 注册完成，共 {} 种: {}", providers.size(), providers.keySet());
    }

    /**
     * 按类型获取 Provider
     *
     * @param type 奖品类型（如 "COUPON"、"POINTS"）
     * @return 对应的 Provider
     * @throws BusinessException 不支持的类型
     */
    public PrizeProvider get(String type) {
        PrizeProvider provider = providers.get(type);
        if (provider == null) {
            throw BusinessException.of("不支持的奖品类型: " + type);
        }
        return provider;
    }

    /** 获取所有已注册的类型 */
    public Set<String> getRegisteredTypes() {
        return Collections.unmodifiableSet(providers.keySet());
    }

    /** 获取所有已注册的 Provider */
    public Collection<PrizeProvider> getAllProviders() {
        return Collections.unmodifiableCollection(providers.values());
    }
}
