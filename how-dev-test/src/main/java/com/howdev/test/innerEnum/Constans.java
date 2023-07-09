package com.howdev.test.innerEnum;

/**
 * Constans class
 *
 * @author haozhifeng
 * @date 2023/06/13
 */
public class Constans {

    public enum LEVEL {
        /**
         * 
         */
        NORMAL(0, "mormal"),
        /**
         * 
         */
        WARNING(1, "warning"),
        /**
         * 
         */
        ERROR(2, "error");
        
        private final int level;
        private final String description;

        LEVEL(int level, String description) {
            this.level = level;
            this.description = description;
        }

        public int getLevel() {
            return level;
        }

        public String getDescription() {
            return description;
        }
    }

    

}
