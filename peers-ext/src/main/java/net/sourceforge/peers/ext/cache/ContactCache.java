package net.sourceforge.peers.ext.cache;

import cn.hutool.cache.impl.FIFOCache;
import net.sourceforge.peers.ext.domain.DemoData;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ContactCache {

    private static ContactCache instance = new ContactCache();

    private final FIFOCache<String, DemoData> cache=new FIFOCache<>(5000);
    private ContactCache() {
    }

    public static ContactCache getInstance() {
        return instance;
    }
    public FIFOCache<String, DemoData> getCache() {

        return cache;
    }
    public void clear() {
        cache.clear();
    }

    public DemoData getValue(String key) {
        if (!cache.containsKey(key)) {
            return null;
        }
        return cache.get(key);
    }

    public void put(String key, DemoData o) {
        cache.put(key, o);
    }

    public List<DemoData> getData() {
        return cache.keySet().stream().map(key -> cache.get(key)).collect(Collectors.toList());
    }

    public String[][] getDataArray() {
        String arr[][] = new String[getCache().size()][4];
        Iterator<DemoData> iterator = getCache().iterator();
        int num = 0;
        while (iterator.hasNext()) {
            DemoData next = iterator.next();
            arr[num] = new String[]{next.getPhone(), next.getContractNo(), next.getName(), next.getIdNo()};
            num++;
        }
        return arr;
    }
}
