package de.myxrcrs.corndoors.util;

import javax.annotation.Nullable;

public interface RangeIterationConsumer {

    boolean iterate(int x,int y,int z)throws Exception;
}
