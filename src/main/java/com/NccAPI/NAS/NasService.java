package com.NccAPI.NAS;

import com.NccNAS.NccNasData;
import com.NccNAS.NccNasType;

import java.util.ArrayList;

public interface NasService {
    NccNasData getNAS(String apiKey, Integer id);
    ArrayList<NccNasData> getNAS(String apiKey);
    ApiNasData getNAS(String login, String key);

    ApiNasTypeData getNasTypes(String login, String key);

    ArrayList<NccNasType> getNASTypes(String apiKey);

    Integer createNAS(String apiKey, String nasName, Integer nasType, Long nasIP, String nasSecret, Integer nasInterimInterval);

    Integer updateNAS(String apiKey, Integer id, String nasName, Integer nasType, Long nasIP, String nasSecret, Integer nasInterimInterval);

    Integer deleteNAS(String apiKey, Integer id);
}
