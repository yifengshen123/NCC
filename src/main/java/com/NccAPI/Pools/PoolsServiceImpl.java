package com.NccAPI.Pools;

import com.NccPools.NccPoolData;
import com.NccPools.NccPools;

import java.util.ArrayList;

public class PoolsServiceImpl implements PoolsService {
    public ArrayList<NccPoolData> getPool(String apiKey){
        return new NccPools().getPool();
    }

    public NccPoolData getPool(String apiKey, Integer id){
        return new NccPools().getPool(id);
    }
}
