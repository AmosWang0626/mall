package cn.eyeo.mall.start.lottery;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 抽奖测试类
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2023/3/27
 */
public class LotteryDrawTests {

    private static final int TOTAL_PROBABILITY = 100;

    @Test
    public void lottery() {
        int[] array = {30, 40, 20, 1};

        probability(array);
    }

    private static void probability(int[] source) {
        int sum = Arrays.stream(source).sum();
        if (sum > TOTAL_PROBABILITY) {
            throw new IllegalArgumentException("总概率不能>100");
        }

        System.out.println("----------------------------------------------");
        System.out.println("原始数据: " + Arrays.toString(source));

        // 不满100%则补全
        int[] array = source;
        if (sum < TOTAL_PROBABILITY) {
            array = new int[source.length + 1];
            System.arraycopy(source, 0, array, 0, source.length);
            array[source.length] = TOTAL_PROBABILITY - sum;
        }
        System.out.println("补全后的数据: " + Arrays.toString(array));

        // 计算概率，测试1000次
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < 10000; i++) {
            // 单次获取概率结果
            int index = getIndex(array);
            // System.out.printf("中奖啦! 概率: %d, Index: %d%n", array[index], index);

            Integer value = map.getOrDefault(array[index], 0);
            map.put(array[index], value + 1);
        }

        System.out.println("----------------------------------------------");
        System.out.printf("看看结果吧! 概率: %s%n", map);
        System.out.println("----------------------------------------------");
    }

    private static int getIndex(int[] array) {
        int num = new Random().nextInt(100);
        for (int i = 0; i < array.length; i++) {
            if (num < array[i]) {
                return i;
            }
            num -= array[i];
        }
        return -1;
    }

}
