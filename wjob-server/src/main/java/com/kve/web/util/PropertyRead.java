package com.kve.web.util;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * @author: hujing39
 * @date: 2022-03-07
 */

public class PropertyRead {
    private static Config config = ConfigFactory.load();

    public static String getKey(String key) {
        return config.getString(key).trim();
    }
}
