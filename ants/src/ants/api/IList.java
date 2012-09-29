package ants.api;

import java.util.LinkedHashMap;

public interface IList extends IConfigurable {

    LinkedHashMap<String, IConfigurable> getItems(Context context);
}
