package by.mrrockka

import java.util.random.RandomGenerator

class Random {

    companion object {
        @JvmStatic
        fun messageId(): Int {
            return RandomGenerator.getDefault().nextInt(0, 100);
        }

        @JvmStatic
        fun chatId(): Long {
            return RandomGenerator.getDefault().nextLong(0, 100);
        }
    }

}