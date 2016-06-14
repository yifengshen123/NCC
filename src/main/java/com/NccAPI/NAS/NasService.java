package com.NccAPI.NAS;

import com.NccNAS.NccNasData;
import com.NccNAS.NccNasType;

import java.util.ArrayList;

public interface NasService {
    NccNasData getNAS(String apiKey, Integer id);
    ArrayList<NccNasData> getNAS(String apiKey);

    ArrayList<NccNasType> getNASTypes(String apiKey);
}
