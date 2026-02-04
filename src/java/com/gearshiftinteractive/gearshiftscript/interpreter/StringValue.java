package com.gearshiftinteractive.gearshiftscript.interpreter;

import java.util.List;

public class StringValue extends GearshiftValue {
    private String value;

    public StringValue(String value) {
        this.value = value;
        declareFields();
    }
    private void declareFields() {
        declareField("__add", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("String.__add", args, 1, file, line);
                return new StringValue(value + ((StringValue) args.getFirst()).value);
            }
        });
        declareField("__mul", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("String.__mul", args, 1, file, line);
                return new StringValue(value.repeat((int) ((NumberValue) args.getFirst()).getValue()));
            }
        });
        declareField("__eq", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("String.__eq", args, 1, file, line);
                return new BooleanValue(value.equals(((StringValue) args.getFirst()).value));
            }
        });
        declareField("__get_index", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("String.__get_index", args, 1, file, line);
                int index = (int) ((NumberValue) args.get(0)).getValue();
                if (index < 0 || index >= value.length()) return new NullValue();
                return new StringValue(String.valueOf(value.charAt(index)));
            }
        });
        declareField("__assign_index", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("String.__assign_index", args, 2, file, line);
                int index = (int) ((NumberValue) args.get(0)).getValue();
                String newChar = ((StringValue) args.get(1)).value;
                if (index < 0 || index >= value.length()) throw new IndexOutOfBoundsError("Index out of bounds", file, line);
                value = value.substring(0, index) + newChar + value.substring(index + 1);
                return new NullValue();
            }
        });
        declareField("length", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("String.length", args, 0, file, line);
                return new NumberValue(value.length());
            }
        });
        declareField("__iterator", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("String.__iterator", args, 0, file, line);
                return new FunctionValue() {
                    int currentIndex = 0;
                    @Override
                    public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                        checkArgs("String.__iterator()", args, 0, file, line);
                        if (currentIndex >= value.length()) {
                            return new NullValue();
                        } else {
                            var result = new StringValue(String.valueOf(value.charAt(currentIndex)));
                            currentIndex++;
                            return result;
                        }
                    }
                };
            }
        });
        declareField("upper", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("String.upper", args, 0, file, line);
                return new StringValue(value.toUpperCase());
            }
        });
        declareField("lower", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("String.lower", args, 0, file, line);
                return new StringValue(value.toLowerCase());
            }
        });
        declareField("contains", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("String.contains", args, 1, file, line);
                return new BooleanValue(value.contains(((StringValue) args.get(0)).value));
            }
        });
        declareField("replace", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("String.replace", args, 2, file, line);
                return new StringValue(value.replace(((StringValue) args.get(0)).value, ((StringValue) args.get(1)).value));
            }
        });
    }

    public String checkString() {
        return value;
    }

    @Override
    public String getTypeName() {
        return "String";
    }

    @Override
    public String tojstring(String file, int line) {
        return value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
