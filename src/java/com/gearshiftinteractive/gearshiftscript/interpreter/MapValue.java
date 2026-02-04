package com.gearshiftinteractive.gearshiftscript.interpreter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MapValue extends GearshiftValue {
    private final List<Map.Entry<GearshiftValue, GearshiftValue>> value = new LinkedList<>();
    public MapValue() {
        declareFields();
    }
    private void declareFields() {
        declareField("keyList", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Map.keyList", args, 0, file, line);
                var ret = new LinkedList<GearshiftValue>();
                for (var entry : value) {
                    ret.add(entry.getKey());
                }
                return new ListValue(ret);
            }
        });
        declareField("merge", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Map.merge", args, 1, file, line);
                value.addAll(((MapValue) args.getFirst()).value);
                return new NullValue();
            }
        });
        declareField("__get_index", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Map.__get_index", args, 1, file, line);
                for (var entry : value) {
                    if (((BooleanValue) entry.getKey().accessField("__eq", file, line).call(List.of(args.getFirst()), file, line)).getValue())
                        return entry.getValue();
                }
                return new NullValue();
            }
        });
        declareField("__assign_index", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Map.__assign_index", args, 2, file, line);
                for (int i = 0; i < value.size(); i++) {
                    if (((BooleanValue) value.get(i).getKey().accessField("__eq", file, line).call(List.of(args.getFirst()), file, line)).getValue()) {
                        value.remove(i);
                        value.add(i, Map.entry(args.getFirst(), args.get(1)));
                        return new NullValue();
                    }
                }
                for (var entry : value) {
                    if (((BooleanValue) entry.getKey().accessField("__eq", file, line).call(List.of(args.getFirst()), file, line)).getValue()) {
                        entry.setValue(args.get(1));
                        return new NullValue();
                    }
                }
                value.add(Map.entry(args.getFirst(), args.get(1)));
                return new NullValue();
            }
        });
        declareField("__tostring", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Map.__tostring", args, 0, file, line);
                var sb = new StringBuilder();
                sb.append("{ ");
                for (var key : value) {
                    sb.append(key.getKey().tojstring(file, line));
                    sb.append(": ");
                    sb.append(key.getValue().tojstring(file, line));
                    sb.append(", ");
                }
                if (sb.toString().length() > 1) {
                    sb.deleteCharAt(sb.length() - 1);
                    sb.deleteCharAt(sb.length() - 1);
                }
                sb.append(" }");
                return new StringValue(sb.toString());
            }
        });
    }
    @Override
    public String getTypeName() {
        return "Map";
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
