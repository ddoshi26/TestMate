package server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by Dhairya on 10/27/2016.
 */
public class CommandListMap {
    public Map<String, ArrayList<String>> commandList = new Map<String, ArrayList<String>>() {

        @Override
        public int size() {
            return commandList.size();
        }

        @Override
        public boolean isEmpty() {
            return (commandList.size() == 0)? true:false;
        }

        @Override
        public boolean containsKey(Object key) {
            return commandList.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return commandList.containsValue(value);
        }

        @Override
        public ArrayList<String> get(Object key) {
            return commandList.get(key);
        }

        @Override
        public ArrayList<String> put(String key, ArrayList<String> value) {
            commandList.put(key, value);
            return value;
        }

        @Override
        public ArrayList<String> remove(Object key) {
            return commandList.remove(key);
        }

        @Override
        public void putAll(Map<? extends String, ? extends ArrayList<String>> m) {
            commandList.putAll(m);
        }

        @Override
        public void clear() {
            commandList.clear();
        }

        @Override
        public Set<String> keySet() {
            return commandList.keySet();
        }

        @Override
        public Collection<ArrayList<String>> values() {
            return commandList.values();
        }

        @Override
        public Set<Entry<String, ArrayList<String>>> entrySet() {
            return commandList.entrySet();
        }

        @Override
        public boolean equals(Object o) {
            return commandList.equals(o);
        }

        @Override
        public int hashCode() {
            return commandList.hashCode();
        }
    };

}
