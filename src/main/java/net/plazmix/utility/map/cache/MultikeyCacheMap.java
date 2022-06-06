package net.plazmix.utility.map.cache;

import net.plazmix.utility.map.MultikeyMap;

public interface MultikeyCacheMap<I> extends MultikeyMap<I> {

    void cleanUp();
}
