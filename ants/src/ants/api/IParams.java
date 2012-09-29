package ants.api;

import java.util.List;
import java.util.Map;

public interface IParams extends IConfigurable {

    List<Map.Entry<String, Object>> getPairs(Context context);
}
