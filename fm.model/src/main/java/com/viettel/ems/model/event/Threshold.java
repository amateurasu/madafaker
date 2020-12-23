package com.viettel.ems.model.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public interface Threshold {

    class PercentThreshold extends ApplicationEvent implements Threshold {
        @Getter
        private final double percent;

        public PercentThreshold(Object source, double percent) {
            super(source);
            this.percent = percent;
        }
    }

    class Remain extends ApplicationEvent implements Threshold {
        @Getter
        private final long remaining;
        @Getter
        private final long capacity;

        public Remain(Object source, long remaining, long capacity) {
            super(source);
            this.remaining = remaining;
            this.capacity = capacity;
        }
    }

    class Available extends ApplicationEvent implements Threshold {
        @Getter
        private final long available;
        @Getter
        private final long capacity;

        public Available(Object source, long available, long capacity) {
            super(source);
            this.available = available;
            this.capacity = capacity;
        }
    }
}
