package com.NccAPI.Pools;

import com.NccAPI.NccAPI;
import com.NccPools.NccPoolData;
import com.NccPools.NccPools;

import java.util.ArrayList;

public class PoolsServiceImpl implements PoolsService {
    public ArrayList<NccPoolData> getPool(String apiKey){
        if (!new NccAPI().checkKey(apiKey)) return null;

        return new NccPools().getPool();
    }

    public NccPoolData getPool(String apiKey, Integer id){
        if (!new NccAPI().checkKey(apiKey)) return null;

        return new NccPools().getPool(id);
    }
}
