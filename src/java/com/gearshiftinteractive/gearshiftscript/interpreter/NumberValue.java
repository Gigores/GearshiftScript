package com.gearshiftinteractive.gearshiftscript.interpreter;

import java.util.List;

public class NumberValue extends GearshiftValue {

    private double value;

    public NumberValue(double value) {
        this.value = value;
        declareConstantField("__add", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Number.__add", args, 1, file, line);
                return new NumberValue(value + ((NumberValue) args.get(0)).value);
            }
        });
        declareConstantField("__sub", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Number.__sub", args, 1, file, line);
                return new NumberValue(value - ((NumberValue) args.get(0)).value);
            }
        });
        declareConstantField("__mul", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Number.__mull", args, 1, file, line);
                return new NumberValue(value * ((NumberValue) args.get(0)).value);
            }
        });
        declareConstantField("__div", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Number.__div", args, 1, file, line);
                return new NumberValue(value / ((NumberValue) args.get(0)).value);
            }
        });
        declareConstantField("__pow", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Number.__pow", args, 1, file, line);
                return new NumberValue(Math.pow(value, ((NumberValue) args.get(0)).value));
            }
        });
        declareConstantField("__neg", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Number.__neg", args, 0, file, line);
                return new NumberValue(-value);
            }
        });
        declareConstantField("__tostring", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Number.__tostring", args, 0, file, line);
                return new StringValue(Double.toString(value));
            }
        });
        declareConstantField("__gt", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Number.__gt", args, 1, file, line);
                return new BooleanValue(value > ((NumberValue) args.getFirst()).value);
            }
        });
        declareConstantField("__ls", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Number.__ls", args, 1, file, line);
                return new BooleanValue(value < ((NumberValue) args.getFirst()).value);
            }
        });
        declareConstantField("__eq", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Number.__eq", args, 1, file, line);
                return new BooleanValue(value == ((NumberValue) args.getFirst()).value);
            }
        });
        declareConstantField("__iterator", new FunctionValue() {
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
