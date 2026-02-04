package com.gearshiftinteractive.gearshiftscript.interpreter;

import java.util.List;

public class NumberValue extends GearshiftValue {

    private double value;

    public NumberValue(double value) {
        this.value = value;
        declareField("__add", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Number.__add", args, 1, file, line);
                return new NumberValue(value + ((NumberValue) args.get(0)).value);
            }
        });
        declareField("__sub", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Number.__sub", args, 1, file, line);
                return new NumberValue(value - ((NumberValue) args.get(0)).value);
            }
        });
        declareField("__mul", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Number.__mull", args, 1, file, line);
                return new NumberValue(value * ((NumberValue) args.get(0)).value);
            }
        });
        declareField("__div", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Number.__div", args, 1, file, line);
                return new NumberValue(value / ((NumberValue) args.get(0)).value);
            }
        });
        declareField("__pow", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Number.__pow", args, 1, file, line);
                return new NumberValue(Math.pow(value, ((NumberValue) args.get(0)).value));
            }
        });
        declareField("__neg", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Number.__neg", args, 0, file, line);
                return new NumberValue(-value);
            }
        });
        declareField("__tostring", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Number.__tostring", args, 0, file, line);
                return new StringValue(Double.toString(value));
            }
        });
        declareField("__gt", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Number.__gt", args, 1, file, line);
                return new BooleanValue(value > ((NumberValue) args.getFirst()).value);
            }
        });
        declareField("__ls", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Number.__ls", args, 1, file, line);
                return new BooleanValue(value < ((NumberValue) args.getFirst()).value);
            }
        });
        declareField("__eq", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Number.__eq", args, 1, file, line);
                return new BooleanValue(value == ((NumberValue) args.getFirst()).value);
            }
        });
        declareField("__iterator", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Number.__iterator", args, 0, file, line);
                return new FunctionValue() {
                    int currentIndex = 0;
                    @Override
                    public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                        checkArgs("Number.__iterator()", args, 0, file, line);
                        if (currentIndex >= value) {
                            return new NullValue();
                        } else {
                            var result = Integer.valueOf(currentIndex);
                            currentIndex++;
                            return new NumberValue(result);
                        }
                    }
                };
            }
        });
    }
    public double checkDouble() {
        return value;
    }
    @Override
    public String getTypeName() {
        return "Number";
    }
    public double getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(value);
    }
}
