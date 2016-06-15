package com.NccAPI.Pools;

import com.NccPools.NccPoolData;

import java.util.ArrayList;

public interface PoolsService {
    ArrayList<NccPoolData> getPool(String apiKey);
    NccPoolData getPool(String apiKey, Integer id);
}
